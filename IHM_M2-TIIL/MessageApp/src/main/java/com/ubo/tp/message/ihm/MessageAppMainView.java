package main.java.com.ubo.tp.message.ihm;

/**
 * Classe de la vue principale de l'application.
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView {

    protected Session mSession;

    protected JFrame mFrame;
    protected JMenuBar mMenuBar;
    protected JMenu mFileMenu;
    protected JMenu mHelpMenu;
    protected JMenu mLogoutMenu;
    protected JMenuItem mExitItem;
    protected JMenuItem mAboutItem;
    protected JMenuItem mLogoutItem;

    protected DataManager mDataManager;
    protected IMessageApp mMessageApp;

    public MessageAppMainView(IMessageApp messageApp, Session mSession, DataManager dataManager) {
        this.mMessageApp = messageApp;
        this.mSession = mSession;
        this.mDataManager = dataManager;
        this.initGUI();
    }

    protected void initGUI() {
        mFrame = new JFrame("MessageApp");
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setSize(800, 600);
        mFrame.setLayout(new BorderLayout());

        // Initialisation du menu
        this.initMenu();

        // Icone de l'application
        this.initIcon();

        // Centrage de la fenêtre
        mFrame.setLocationRelativeTo(null);

        // Initialisation du contenu
        this.initContent();
    }

    protected void initMenu() {
        mMenuBar = new JMenuBar();
        mFileMenu = new JMenu("Fichier");
        mHelpMenu = new JMenu("?");
        mLogoutMenu = new JMenu("Deconnexion");

        // Item Quitter
        mExitItem = new JMenuItem("Quitter");
        // Chargement de l'icône Quitter
        ImageIcon exitIcon = new ImageIcon("IHM_M2-TIIL/MessageApp/src/main/resources/images/exitIcon_20.png");
        mExitItem.setIcon(exitIcon);

        mExitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.java.com.ubo.tp.message.datamodel.User u = mSession.getConnectedUser();
                if (u != null) {
                    u.setOnline(false);
                    mDataManager.sendUser(u);
                }
                System.exit(0);
            }
        });

        // Item A propos
        mAboutItem = new JMenuItem("A propos");
        // Chargement de l'icône A propos (logo small)
        ImageIcon aboutIcon = new ImageIcon("IHM_M2-TIIL/MessageApp/src/main/resources/images/logo_20.png");
        mAboutItem.setIcon(aboutIcon);

        mAboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });

        mLogoutItem = new JMenuItem("Se déconnecter");
        mLogoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.java.com.ubo.tp.message.datamodel.User u = mSession.getConnectedUser();
                if (u != null) {
                    u.setOnline(false);
                    mDataManager.sendUser(u);
                }
                mSession.disconnect();
            }
        });

        mFileMenu.add(mExitItem);
        mHelpMenu.add(mAboutItem);
        mLogoutMenu.add(mLogoutItem);

        mMenuBar.add(mFileMenu);
        mMenuBar.add(mHelpMenu);

        // Menu Profil
        JMenu mProfileMenu = new JMenu("Profil");
        JMenuItem mChangeNameItem = new JMenuItem("Modifier mon nom");
        mChangeNameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.java.com.ubo.tp.message.datamodel.User user = mSession.getConnectedUser();
                if (user != null) {
                    String newName = JOptionPane.showInputDialog(mFrame, "Nouveau nom d'utilisateur :",
                            "Modifier mon nom", JOptionPane.QUESTION_MESSAGE);
                    if (newName != null && !newName.trim().isEmpty()) {
                        user.setName(newName.trim());
                        mDataManager.sendUser(user);
                        JOptionPane.showMessageDialog(mFrame, "Nom modifié avec succès !", "Succès",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        mProfileMenu.add(mChangeNameItem);

        JMenuItem mDeleteAccountItem = new JMenuItem("Supprimer mon compte");
        mDeleteAccountItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(mFrame,
                        "Êtes-vous sûr de vouloir supprimer votre compte définitivement ?",
                        "Confirmation de suppression",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    main.java.com.ubo.tp.message.datamodel.User user = mSession.getConnectedUser();
                    if (user != null) {
                        mDataManager.deleteUser(user);
                        mSession.disconnect();
                        JOptionPane.showMessageDialog(mFrame, "Votre compte a été supprimé.", "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        mProfileMenu.add(mDeleteAccountItem);

        mMenuBar.add(mProfileMenu);

        mMenuBar.add(mLogoutMenu);

        /*
         * // Menu de démo pour changer de panel
         * JMenu demoMenu = new JMenu("Démo");
         * JMenuItem userListItem = new JMenuItem("Liste utilisateurs");
         * userListItem.addActionListener(new ActionListener() {
         * 
         * @Override
         * public void actionPerformed(ActionEvent e) {
         * // Exemple d'appel à setMainPanel
         * setMainPanel(new UserListPanel(new HashSet<User>()));
         * }
         * });
         * demoMenu.add(userListItem);
         * mMenuBar.add(demoMenu);
         */

        mFrame.setJMenuBar(mMenuBar);
    }

    protected void initIcon() {
        // Chargement de l'icône de l'application (logo large)
        ImageIcon appIcon = new ImageIcon("IHM_M2-TIIL/MessageApp/src/main/resources/images/logo_50.png");
        mFrame.setIconImage(appIcon.getImage());
    }

    protected void showAboutDialog() {
        ImageIcon logoIcon = new ImageIcon("IHM_M2-TIIL/MessageApp/src/main/resources/images/logo_50.png");
        JOptionPane.showMessageDialog(mFrame,
                "UBO M2-TIIL\nDépartement Informatique",
                "A propos",
                JOptionPane.INFORMATION_MESSAGE, logoIcon);
    }

    protected JPanel mCurrentContent;

    protected void initContent() {
        // Création du contrôleur et de la vue des messages
        main.java.com.ubo.tp.message.ihm.message.MessageView messageView = new main.java.com.ubo.tp.message.ihm.message.MessageView(
                mMessageApp, mDataManager, mSession);

        // Création du contrôleur et de la vue des canaux
        main.java.com.ubo.tp.message.ihm.channel.ChannelController channelController = new main.java.com.ubo.tp.message.ihm.channel.ChannelController(
                mMessageApp, mDataManager, mSession);
        final main.java.com.ubo.tp.message.ihm.channel.ChannelListPanel channelListPanel = new main.java.com.ubo.tp.message.ihm.channel.ChannelListPanel(
                channelController);

        // Ajout de la liste des canaux à gauche
        mFrame.add(channelListPanel, BorderLayout.WEST);

        // Ajout de la liste des utilisateurs à droite
        main.java.com.ubo.tp.message.ihm.user.UserController userController = new main.java.com.ubo.tp.message.ihm.user.UserController(
                mDataManager, mSession);
        final main.java.com.ubo.tp.message.ihm.user.UserListPanel userListPanel = new main.java.com.ubo.tp.message.ihm.user.UserListPanel(
                userController);
        mFrame.add(userListPanel, BorderLayout.EAST);

        // Au centre : les messages
        this.setMainPanel(messageView);

        // Listeners pour mettre à jour les messages selon la sélection
        channelListPanel.addSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !channelListPanel.isUpdating()) {
                    main.java.com.ubo.tp.message.datamodel.Channel selected = channelListPanel.getSelectedChannel();
                    if (selected != null) {
                        messageView.getController().setCurrentRecipient(selected.getUuid(), selected.getName());
                        userController.setCurrentChannelFilter(selected);
                        channelController.markChannelAsRead(selected.getUuid());
                    } else {
                        userController.setCurrentChannelFilter(null);
                    }
                }
            }
        });

        userListPanel.addSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    main.java.com.ubo.tp.message.datamodel.User selectedUser = userListPanel.getSelectedUser();
                    if (selectedUser != null) {
                        main.java.com.ubo.tp.message.datamodel.Channel dmChannel = channelController
                                .findOrCreateDirectMessageChannel(selectedUser);
                        if (dmChannel != null) {
                            messageView.getController().setCurrentRecipient(dmChannel.getUuid(),
                                    selectedUser.getName());
                        }
                    }
                }
            }
        });

    }

    /**
     * Change le panneau central de la fenêtre principale.
     * 
     * @param newContent Le nouveau JPanel à afficher.
     */
    public void setMainPanel(JPanel newContent) {
        // Suppression de l'ancien contenu s'il existe
        if (mCurrentContent != null) {
            mFrame.remove(mCurrentContent);
        }

        // Mise à jour de la référence
        mCurrentContent = newContent;

        // Ajout du nouveau contenu
        mFrame.add(mCurrentContent, BorderLayout.CENTER);

        // Rafraîchissement de l'affichage
        mFrame.revalidate();
        mFrame.repaint();
    }

    public void show() {
        mFrame.setVisible(true);
    }
}
