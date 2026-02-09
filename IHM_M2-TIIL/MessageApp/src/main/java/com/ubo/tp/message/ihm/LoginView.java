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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tag
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(new JLabel("Tag (@...) :"), gbc);
        mTagField = new JTextField(20);
        gbc.gridx = 1;
        this.add(mTagField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(new JLabel("Mot de passe :"), gbc);
        mPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        this.add(mPasswordField, gbc);

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

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(buttonPanel, gbc);
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
