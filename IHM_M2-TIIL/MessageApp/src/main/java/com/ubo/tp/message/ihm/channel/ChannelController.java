package main.java.com.ubo.tp.message.ihm.channel;

import java.util.Set;

import javax.swing.JOptionPane;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion des canaux.
 */
public class ChannelController implements IDatabaseObserver {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected ChannelListPanel mView;

    public ChannelController(DataManager dataManager, ISession session) {
        this.mDataManager = dataManager;
        this.mSession = session;

        // On s'abonne aux changements de la base pour mettre à jour la liste
        this.mDataManager.addObserver(this);
    }

    public void setView(ChannelListPanel view) {
        this.mView = view;
        this.refreshView();
    }

    protected void refreshView() {
        if (mView != null) {
            mView.updateChannelList(mDataManager.getChannels());
        }
    }

    public void createChannel(String channelName) {
        if (channelName == null || channelName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mView, "Le nom du canal ne peut pas être vide.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User creator = mSession.getConnectedUser();
        if (creator == null) {
            JOptionPane.showMessageDialog(mView, "Vous devez être connecté pour créer un canal.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Channel newChannel = new Channel(creator, channelName);
        mDataManager.sendChannel(newChannel);
    }

    // --- IDatabaseObserver implementation ---

    @Override
    public void notifyMessageAdded(Message message) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyMessageDeleted(Message message) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyMessageModified(Message message) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyUserAdded(User user) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyUserDeleted(User user) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyUserModified(User user) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyChannelAdded(Channel channel) {
        this.refreshView();
    }

    @Override
    public void notifyChannelDeleted(Channel channel) {
        this.refreshView();
    }

    @Override
    public void notifyChannelModified(Channel channel) {
        this.refreshView();
    }
}
