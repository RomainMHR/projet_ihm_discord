package main.java.com.ubo.tp.message.ihm.message;

import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * Timestamp de démarrage du contrôleur (pour ignorer les messages existants).
     */
    protected long mStartTimestamp;

    public MessageController(IMessageApp messageApp, DataManager dataManager, ISession session) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.mSession = session;
        this.mStartTimestamp = System.currentTimeMillis();
        this.mDataManager.addObserver(this);
    }

    /**
     * Nettoie le contrôleur en le désinscrivant du DataManager.
     */
    public void dispose() {
        this.mDataManager.removeObserver(this);
    }

    public User getConnectedUser() {
        return mSession.getConnectedUser();
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

    /**
     * Filtre de recherche courant (texte libre).
     */
    protected String mSearchFilter = "";

    public void setSearchFilter(String filter) {
        this.mSearchFilter = (filter == null) ? "" : filter.trim().toLowerCase();
        this.refreshListView();
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

        for (Message msg : allMessages) {
            boolean isToRecipient = msg.getRecipient().equals(mCurrentRecipientUuid);
            boolean isFromRecipientToUs = false;

            if (mSession.getConnectedUser() != null) {
                isFromRecipientToUs = msg.getSender().getUuid().equals(mCurrentRecipientUuid)
                        && msg.getRecipient().equals(mSession.getConnectedUser().getUuid());
            }

            if (isToRecipient || isFromRecipientToUs) {
                // Appliquer le filtre de recherche
                if (mSearchFilter.isEmpty() || msg.getText().toLowerCase().contains(mSearchFilter)
                        || msg.getSender().getName().toLowerCase().contains(mSearchFilter)) {
                    conversationMessages.add(msg);
                }
            }
        }

        // Tri chronologique
        Collections.sort(conversationMessages, (m1, m2) -> Long.compare(m1.getEmissionDate(), m2.getEmissionDate()));

        mListView.updateMessageList(conversationMessages);
    }

    /**
     * Envoie un message avec le texte saisi.
     */
    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        if (text.length() > 200) {
            mMessageApp.showErrorMessage("Le message est trop long. Il ne doit pas dépasser 200 caractères.");
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

    /**
     * Supprime un message si l'utilisateur connecté en est l'auteur.
     */
    public void deleteMessage(Message message) {
        User currentUser = mSession.getConnectedUser();
        if (message == null || currentUser == null) {
            return;
        }
        if (!message.getSender().getUuid().equals(currentUser.getUuid())) {
            mMessageApp.showErrorMessage("Vous ne pouvez supprimer que vos propres messages.");
            return;
        }
        mDataManager.deleteMessage(message);
    }

    @Override
    public void notifyMessageAdded(Message message) {
        this.refreshListView();

        // Vérifier si une notification doit être affichée
        if (message == null) {
            return;
        }

        // Ignorer les anciens messages chargés au démarrage de l'application
        if (message.getEmissionDate() < mStartTimestamp) {
            return;
        }
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null) {
            return;
        }

        // --- Easter Eggs (déclenchés même pour ses propres messages) ---
        String msgText = message.getText().trim().toLowerCase();
        if (msgText.equals("/party") || msgText.equals("/flip") || msgText.equals("/earthquake")) {
            mMessageApp.triggerEasterEgg(msgText.substring(1));
        }

        // Ne pas notifier pour ses propres messages
        if (message.getSender().getUuid().equals(currentUser.getUuid())) {
            return;
        }

        String senderName = message.getSender().getName();

        // Cas 1 : Message direct (le message est dans un canal DM dont l'utilisateur
        // est membre)
        for (Channel c : mDataManager.getChannels()) {
            if (c.isDirectMessage() && c.getUuid().equals(message.getRecipient())
                    && c.getUsers().contains(currentUser)) {
                mMessageApp.showInformationMessage(
                        "💬 Nouveau message privé de " + senderName + " : " + truncate(message.getText(), 50));
                return;
            }
        }

        // Cas 2 : Mention dans un canal
        String myName = currentUser.getName().toLowerCase();
        String myTag = currentUser.getUserTag().toLowerCase();
        String msgContent = message.getText().toLowerCase();
        if (msgContent.contains("@" + myName) || msgContent.contains("@" + myTag)) {
            mMessageApp.showInformationMessage(
                    "🔔 " + senderName + " vous a mentionné : " + truncate(message.getText(), 50));
        }
    }

    /**
     * Tronque un texte à la longueur maximale donnée.
     */
    protected String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
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
