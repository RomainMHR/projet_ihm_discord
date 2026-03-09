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
import main.java.com.ubo.tp.message.ihm.interfaces.IUserListView;

/**
 * Contrôleur pour la gestion des utilisateurs.
 */
public class UserController implements IDatabaseObserver {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected IUserListView mView;

    public UserController(DataManager dataManager, ISession session) {
        this.mDataManager = dataManager;
        this.mSession = session;

        // On s'abonne aux changements de la base pour mettre à jour la liste
        this.mDataManager.addObserver(this);
    }

    protected Channel mCurrentChannelFilter;

    public void setView(IUserListView view) {
        this.mView = view;
        this.refreshView();
    }

    public void setCurrentChannelFilter(Channel channel) {
        this.mCurrentChannelFilter = channel;
        this.refreshView();
    }

    protected String mSearchFilter = "";

    public void setSearchFilter(String filter) {
        this.mSearchFilter = (filter == null) ? "" : filter.trim().toLowerCase();
        this.refreshView();
    }

    protected void refreshView() {
        if (mView != null) {
            Set<User> users = new HashSet<>();

            if (mCurrentChannelFilter != null && mCurrentChannelFilter.isPrivate()) {
                users.addAll(mCurrentChannelFilter.getUsers());
            } else {
                for (User user : mDataManager.getUsers()) {
                    if (!user.getUuid().equals(Constants.UNKNONWN_USER_UUID)) {
                        users.add(user);
                    }
                }
            }

            // Appliquer le filtre de recherche
            if (!mSearchFilter.isEmpty()) {
                Set<User> filtered = new HashSet<>();
                for (User u : users) {
                    if (u.getName().toLowerCase().contains(mSearchFilter)
                            || u.getUserTag().toLowerCase().contains(mSearchFilter)) {
                        filtered.add(u);
                    }
                }
                users = filtered;
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
        if (mCurrentChannelFilter != null && mCurrentChannelFilter.getUuid().equals(channel.getUuid())) {
            this.mCurrentChannelFilter = channel;
            this.refreshView();
        }
    }
}
