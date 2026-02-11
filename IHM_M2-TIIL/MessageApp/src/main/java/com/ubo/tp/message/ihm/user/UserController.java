package main.java.com.ubo.tp.message.ihm.user;

import java.util.HashSet;
import java.util.Set;

import main.java.com.ubo.tp.message.common.Constants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion des utilisateurs.
 */
public class UserController implements IDatabaseObserver {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected UserListPanel mView;

    public UserController(DataManager dataManager, ISession session) {
        this.mDataManager = dataManager;
        this.mSession = session;

        // On s'abonne aux changements de la base pour mettre à jour la liste
        this.mDataManager.addObserver(this);
    }

    public void setView(UserListPanel view) {
        this.mView = view;
        this.refreshView();
    }

    protected void refreshView() {
        if (mView != null) {
            Set<User> users = new HashSet<>();
            for (User user : mDataManager.getUsers()) {
                if (!user.getUuid().equals(Constants.UNKNONWN_USER_UUID)) {
                    users.add(user);
                }
            }
            mView.updateUserList(users);
        }
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
        this.refreshView();
    }

    @Override
    public void notifyUserDeleted(User user) {
        this.refreshView();
    }

    @Override
    public void notifyUserModified(User user) {
        this.refreshView();
    }

    @Override
    public void notifyChannelAdded(Channel channel) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyChannelDeleted(Channel channel) {
        // Pas d'action pour l'instant
    }

    @Override
    public void notifyChannelModified(Channel channel) {
        // Pas d'action pour l'instant
    }
}
