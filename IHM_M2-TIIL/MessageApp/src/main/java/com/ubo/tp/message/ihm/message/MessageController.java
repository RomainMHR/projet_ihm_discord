package main.java.com.ubo.tp.message.ihm.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageInputView;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageListView;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageMainView;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour l'envoi et la réception de messages.
 */
public class MessageController implements IDatabaseObserver {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected IMessageApp mMessageApp;
    protected IMessageInputView mView;
    protected IMessageListView mListView;
    protected IMessageMainView mMainView;

    /**
     * Destinataire actuellement sélectionné (Canal ou Utilisateur)
     */
    protected UUID mCurrentRecipientUuid;

    public MessageController(IMessageApp messageApp, DataManager dataManager, ISession session) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.mSession = session;
        this.mDataManager.addObserver(this);
    }

    public void setMainView(IMessageMainView view) {
        this.mMainView = view;
    }

    public void setView(IMessageInputView view) {
        this.mView = view;
    }

    public void setListView(IMessageListView view) {
        this.mListView = view;
        this.refreshListView();
    }

    /**
     * Permet de définir à qui le message sera envoyé et d'afficher ses messages.
     */
    public void setCurrentRecipient(UUID recipientUuid, String displayName) {
        this.mCurrentRecipientUuid = recipientUuid;
        if (this.mMainView != null) {
            this.mMainView.setChatTitle("Conversation : " + displayName);
        }
        this.refreshListView();
        if (this.mView != null) {
            this.mView.focusInput();
        }
    }

    protected void refreshListView() {
        if (mListView == null) {
            return;
        }
        if (mCurrentRecipientUuid == null) {
            mListView.updateMessageList(new ArrayList<>());
            return;
        }

        List<Message> conversationMessages = new ArrayList<>();
        Set<Message> allMessages = mDataManager.getMessages();

        // On peut afficher tous les messages envoyés vers le recipient actuel.
        // On récupère aussi les messages qu'on a envoyé au recipient (si c'est un
        // User).
        for (Message msg : allMessages) {
            boolean isToRecipient = msg.getRecipient().equals(mCurrentRecipientUuid);
            boolean isFromRecipientToUs = false;

            // Si c'est un chat 1-to-1, il faut aussi afficher les messages que l'autre nous
            // a envoyé
            if (mSession.getConnectedUser() != null) {
                isFromRecipientToUs = msg.getSender().getUuid().equals(mCurrentRecipientUuid)
                        && msg.getRecipient().equals(mSession.getConnectedUser().getUuid());
            }

            if (isToRecipient || isFromRecipientToUs) {
                conversationMessages.add(msg);
            }
        }

        // Tri chronologique
        Collections.sort(conversationMessages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        mListView.updateMessageList(conversationMessages);
    }

    /**
     * Envoie un message avec le texte saisi.
     */
    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        if (mCurrentRecipientUuid == null) {
            mMessageApp.showErrorMessage(
                    "Veuillez sélectionner un destinataire (canal ou utilisateur) avant d'envoyer un message.");
            return;
        }

        User sender = mSession.getConnectedUser();
        if (sender == null) {
            mMessageApp.showErrorMessage("Vous devez être connecté pour envoyer un message.");
            return;
        }

        Message newMessage = new Message(sender, mCurrentRecipientUuid, text.trim());
        mDataManager.sendMessage(newMessage);
    }

    // --- IDatabaseObserver ---

    @Override
    public void notifyMessageAdded(Message message) {
        this.refreshListView();
    }

    @Override
    public void notifyMessageDeleted(Message message) {
        this.refreshListView();
    }

    @Override
    public void notifyMessageModified(Message message) {
        this.refreshListView();
    }

    @Override
    public void notifyUserAdded(User user) {
    }

    @Override
    public void notifyUserDeleted(User user) {
    }

    @Override
    public void notifyUserModified(User user) {
    }

    @Override
    public void notifyChannelAdded(Channel channel) {
    }

    @Override
    public void notifyChannelDeleted(Channel channel) {
    }

    @Override
    public void notifyChannelModified(Channel channel) {
    }
}
