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

    public MessageAppMainView(Session mSession, DataManager dataManager) {
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
                mSession.disconnect();
            }
        });

        mFileMenu.add(mExitItem);
        mHelpMenu.add(mAboutItem);
        mLogoutMenu.add(mLogoutItem);

        mMenuBar.add(mFileMenu);
        mMenuBar.add(mHelpMenu);
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
        // Création du contrôleur et de la vue des canaux
        main.java.com.ubo.tp.message.ihm.channel.ChannelController channelController = new main.java.com.ubo.tp.message.ihm.channel.ChannelController(
                mDataManager, mSession);
        main.java.com.ubo.tp.message.ihm.channel.ChannelListPanel channelListPanel = new main.java.com.ubo.tp.message.ihm.channel.ChannelListPanel(
                channelController);

        // Utilisation de la méthode pour définir le panneau
        this.setMainPanel(channelListPanel);

        // Ajout de la liste des utilisateurs à droite
        main.java.com.ubo.tp.message.ihm.user.UserController userController = new main.java.com.ubo.tp.message.ihm.user.UserController(
                mDataManager, mSession);
        main.java.com.ubo.tp.message.ihm.user.UserListPanel userListPanel = new main.java.com.ubo.tp.message.ihm.user.UserListPanel(
                userController);
        mFrame.add(userListPanel, BorderLayout.EAST);
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
