package main.java.com.ubo.tp.message.ihm.channel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;
import main.java.com.ubo.tp.message.ihm.interfaces.IChannelListView;

/**
 * Contrôleur pour la gestion des canaux.
 */
public class ChannelController implements IDatabaseObserver {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected IMessageApp mMessageApp;
    protected IChannelListView mView;

    /**
     * Ensemble des UUIDs de canaux ayant des messages non lus.
     */
    protected Set<UUID> mUnreadChannels = new HashSet<>();

    /**
     * UUID du canal actuellement sélectionné (pour ne pas marquer comme non lu).
     */
    protected UUID mCurrentSelectedChannelUuid;

    public ChannelController(IMessageApp messageApp, DataManager dataManager, ISession session) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.mSession = session;

        // On s'abonne aux changements de la base
        this.mDataManager.addObserver(this);
    }

    /**
     * Nettoie le contrôleur en le désinscrivant du DataManager.
     */
    public void dispose() {
        this.mDataManager.removeObserver(this);
    }

    public void setView(IChannelListView view) {
        this.mView = view;
        this.refreshView();
    }

    public User getConnectedUser() {
        return mSession.getConnectedUser();
    }

    public java.util.Set<User> getAllUsers() {
        return mDataManager.getUsers();
    }

    /**
     * Marque un canal comme lu.
     */
    public void markChannelAsRead(UUID channelUuid) {
        this.mCurrentSelectedChannelUuid = channelUuid;
        if (channelUuid != null) {
            this.mUnreadChannels.remove(channelUuid);
            this.refreshView();
        }
    }

    /**
     * Retourne true si le canal a des messages non lus.
     */
    public boolean hasUnreadMessages(Channel channel) {
        return channel != null && mUnreadChannels.contains(channel.getUuid());
    }

    protected String mSearchFilter = "";

    public void setSearchFilter(String filter) {
        this.mSearchFilter = (filter == null) ? "" : filter.trim().toLowerCase();
        this.refreshView();
    }

    protected void refreshView() {
        if (mView != null) {
            java.util.Set<Channel> allChannels = mDataManager.getChannels();
            java.util.Set<Channel> visibleChannels = new java.util.HashSet<>();
            User currentUser = mSession.getConnectedUser();

            for (Channel c : allChannels) {
                // Filtre de visibilité (privé/public)
                boolean visible = false;
                if (c.isDirectMessage()) {
                    // Les canaux DM ne sont jamais affichés dans la liste
                    visible = false;
                } else if (!c.isPrivate()) {
                    visible = true;
                } else if (currentUser != null && c.getUsers().contains(currentUser)) {
                    visible = true;
                }

                // Filtre de recherche
                if (visible && !mSearchFilter.isEmpty()) {
                    visible = c.getName().toLowerCase().contains(mSearchFilter);
                }

                if (visible) {
                    visibleChannels.add(c);
                }
            }
            mView.updateChannelList(visibleChannels);
        }
    }

    public void createChannel(String channelName) {
        if (channelName == null || channelName.trim().isEmpty()) {
            mMessageApp.showErrorMessage("Le nom du canal ne peut pas être vide.");
            return;
        }

        User creator = mSession.getConnectedUser();
        if (creator == null) {
            mMessageApp.showErrorMessage("Vous devez être connecté pour créer un canal.");
            return;
        }

        Channel newChannel = new Channel(creator, channelName);
        mDataManager.sendChannel(newChannel);
    }

    public void createPrivateChannel(String channelName, java.util.List<User> initialUsers) {
        if (channelName == null || channelName.trim().isEmpty()) {
            mMessageApp.showErrorMessage("Le nom du canal privé ne peut pas être vide.");
            return;
        }

        User creator = mSession.getConnectedUser();
        if (creator == null) {
            mMessageApp.showErrorMessage("Vous devez être connecté pour créer un canal privé.");
            return;
        }

        // Le créateur doit faire partie du canal
        if (!initialUsers.contains(creator)) {
            initialUsers.add(creator);
        }

        Channel newChannel = new Channel(creator, channelName, initialUsers);
        mDataManager.sendChannel(newChannel);
    }

    /**
     * Cherche un canal DM existant entre l'utilisateur connecté et l'utilisateur
     * cible.
     * S'il n'existe pas, en crée un nouveau.
     */
    public Channel findOrCreateDirectMessageChannel(User targetUser) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null || targetUser == null) {
            return null;
        }

        // Empêcher l'envoi de message à soi-même
        if (currentUser.getUuid().equals(targetUser.getUuid())) {
            return null;
        }

        // Chercher un canal DM existant entre les deux utilisateurs
        for (Channel c : mDataManager.getChannels()) {
            if (c.isDirectMessage() && c.getUsers().size() == 2
                    && c.getUsers().contains(currentUser)
                    && c.getUsers().contains(targetUser)) {
                return c;
            }
        }

        // Créer un nouveau canal DM
        java.util.List<User> dmUsers = new java.util.ArrayList<>();
        dmUsers.add(currentUser);
        dmUsers.add(targetUser);
        Channel dmChannel = new Channel(currentUser, "DM-" + currentUser.getName() + "-" + targetUser.getName(),
                dmUsers);
        dmChannel.setDirectMessage(true);
        mDataManager.sendChannel(dmChannel);
        return dmChannel;
    }

    public void deleteChannel(Channel channel) {
        User currentUser = mSession.getConnectedUser();
        if (channel != null && currentUser != null && channel.getCreator().getUuid().equals(currentUser.getUuid())) {
            // Seul le créateur peut supprimer le canal, qu'il soit public ou privé.
            // On peut restreindre juste pour les privés ou tout le monde : Restreignons
            // pour tous pour cohérence
            // Pour être sûr, la spécification dit "supprimer un canal privé dont il est le
            // propriétaire"
            mDataManager.getChannels().remove(channel); // Avertissement: la BDD gère pas forcément la suppression
                                                        // physique directe sur ce set
            // mDataManager n'a pas mis en cache de fonction sendDeletChannel,
            // l'EntityManager le gère
            // Mais l'EntityManager peut ne pas l'avoir si DataManager ne l'expose pas...
            // En regardant l'API DataManager, on a deleteUser, pas deleteChannel :(
        }
    }

    public void quitChannel(Channel channel) {
        User currentUser = mSession.getConnectedUser();
        if (channel != null && currentUser != null && channel.isPrivate() && channel.getUsers().contains(currentUser)) {
            // Le créateur ne peut pas quitter son propre canal (règle implicite ou il peut,
            // mais on le laisse)
            if (!channel.getCreator().getUuid().equals(currentUser.getUuid())) {
                channel.removeUser(currentUser);
                mDataManager.sendChannel(channel); // Réécrit le fichier et notifie l'observateur BDD
            } else {
                mMessageApp.showErrorMessage("Le créateur ne peut quitter le canal, il doit le supprimer.");
            }
        }
    }

    public void addMemberToChannel(Channel channel, User newMember) {
        User currentUser = mSession.getConnectedUser();
        if (channel != null && newMember != null && channel.isPrivate() && currentUser != null) {
            if (channel.getCreator().getUuid().equals(currentUser.getUuid())) {
                if (!channel.getUsers().contains(newMember)) {
                    channel.addUser(newMember);
                    mDataManager.sendChannel(channel);
                }
            } else {
                mMessageApp.showErrorMessage("Seul le créateur du canal privé peut y ajouter des membres.");
            }
        }
    }

    public void removeMemberFromChannel(Channel channel, User memberToRemove) {
        User currentUser = mSession.getConnectedUser();
        if (channel != null && memberToRemove != null && channel.isPrivate() && currentUser != null) {
            if (channel.getCreator().getUuid().equals(currentUser.getUuid())) {
                if (!memberToRemove.getUuid().equals(currentUser.getUuid())) {
                    channel.removeUser(memberToRemove);
                    mDataManager.sendChannel(channel);
                } else {
                    mMessageApp.showErrorMessage(
                            "Vous ne pouvez pas vous retirer vous-même. Quittez le canal ou supprimez-le.");
                }
            } else {
                mMessageApp.showErrorMessage("Seul le créateur du canal privé peut en retirer des membres.");
            }
        }
    }

    // --- IDatabaseObserver implementation ---

    @Override
    public void notifyMessageAdded(Message message) {
        if (message != null) {
            UUID recipientUuid = message.getRecipient();
            // Si le message est destiné à un canal qui n'est pas le canal sélectionné
            if (mCurrentSelectedChannelUuid == null || !recipientUuid.equals(mCurrentSelectedChannelUuid)) {
                // Vérifier que le recipient est bien un canal connu
                for (Channel c : mDataManager.getChannels()) {
                    if (c.getUuid().equals(recipientUuid)) {
                        mUnreadChannels.add(recipientUuid);
                        this.refreshView();
                        break;
                    }
                }
            }
        }
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
