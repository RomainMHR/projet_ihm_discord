package main.java.com.ubo.tp.message.ihm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe de la vue de connexion.
 */
public class LoginView extends JPanel {

    private static final long serialVersionUID = 1L;

    protected DataManager mDataManager;
    protected ISession mSession;
    protected MessageApp mMessageApp;

    protected JTextField mTagField;
    protected JPasswordField mPasswordField;
    protected JButton mLoginButton;
    protected JButton mRegisterButton;

    public LoginView(MessageApp messageApp, DataManager dataManager, ISession session) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.mSession = session;
        this.initGUI();
    }

    protected void initGUI() {
        this.setLayout(new GridBagLayout());

        // Tag
        this.add(new JLabel("Tag (@...) :"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        mTagField = new JTextField(20);
        this.add(mTagField, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Password
        this.add(new JLabel("Mot de passe :"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        mPasswordField = new JPasswordField(20);
        this.add(mPasswordField, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        // Buttons
        JPanel buttonPanel = new JPanel();
        mLoginButton = new JButton("Se connecter");
        mLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        buttonPanel.add(mLoginButton);

        mRegisterButton = new JButton("Créer un compte");
        mRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mMessageApp.showRegisterView();
            }
        });
        buttonPanel.add(mRegisterButton);

        this.add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    protected void login() {
        String tag = mTagField.getText();
        String password = new String(mPasswordField.getPassword());

        if (tag.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recherche de l'utilisateur
        User foundUser = null;
        for (User user : mDataManager.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                foundUser = user;
                break;
            }
        }

        if (foundUser != null && foundUser.getUserPassword().equals(password)) {
            // SRS-MAP-USR-004 : Connexion
            mSession.connect(foundUser);
            JOptionPane.showMessageDialog(this, "Connexion réussie ! Bienvenue " + foundUser.getName(), "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            mMessageApp.showMainView();
        } else {
            JOptionPane.showMessageDialog(this, "Identifiant ou mot de passe incorrect.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
