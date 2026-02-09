package main.java.com.ubo.tp.message.ihm;

/**
 * Classe de la vue principale de l'application.
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView {

    protected JFrame mFrame;
    protected JMenuBar mMenuBar;
    protected JMenu mFileMenu;
    protected JMenu mHelpMenu;
    protected JMenuItem mExitItem;
    protected JMenuItem mAboutItem;

    public MessageAppMainView() {
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
    }

    protected void initMenu() {
        mMenuBar = new JMenuBar();
        mFileMenu = new JMenu("Fichier");
        mHelpMenu = new JMenu("?");

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

        mFileMenu.add(mExitItem);
        mHelpMenu.add(mAboutItem);

        mMenuBar.add(mFileMenu);
        mMenuBar.add(mHelpMenu);

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

    public void show() {
        mFrame.setVisible(true);
    }
}
