package main.java.com.ubo.tp.message.ihm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe de la vue d'inscription.
 */
public class RegisterView extends JPanel {

    private static final long serialVersionUID = 1L;

    protected DataManager mDataManager;
    protected MessageApp mMessageApp;

    protected JTextField mNameField;
    protected JTextField mTagField;
    protected JPasswordField mPasswordField;
    protected JPasswordField mConfirmPasswordField;
    protected JButton mRegisterButton;
    protected JButton mCancelButton;

    public RegisterView(MessageApp messageApp, DataManager dataManager) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.initGUI();
    }

    protected void initGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(new JLabel("Nom :"), gbc);
        mNameField = new JTextField(20);
        gbc.gridx = 1;
        this.add(mNameField, gbc);

        // Tag
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(new JLabel("Tag (@...) :"), gbc);
        mTagField = new JTextField(20);
        gbc.gridx = 1;
        this.add(mTagField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(new JLabel("Mot de passe :"), gbc);
        mPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        this.add(mPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.add(new JLabel("Confirmer mdp :"), gbc);
        mConfirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        this.add(mConfirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        mRegisterButton = new JButton("S'inscrire");
        mRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        buttonPanel.add(mRegisterButton);

        mCancelButton = new JButton("Annuler");
        mCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mMessageApp.showLoginView();
            }
        });
        buttonPanel.add(mCancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        this.add(buttonPanel, gbc);
    }

    protected void register() {
        String name = mNameField.getText();
        String tag = mTagField.getText();
        String password = new String(mPasswordField.getPassword());
        String confirmPassword = new String(mConfirmPasswordField.getPassword());

        // SRS-MAP-USR-002 : Nom et Tag obligatoires
        if (name.isEmpty() || tag.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // SRS-MAP-USR-003 : Tag unique
        for (User user : mDataManager.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                JOptionPane.showMessageDialog(this, "Ce tag est déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Création et sauvegarde de l'utilisateur
        User newUser = new User(UUID.randomUUID(), tag, password, name);
        mDataManager.sendUser(newUser);

        JOptionPane.showMessageDialog(this, "Compte créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        mMessageApp.showLoginView();
    }
}
