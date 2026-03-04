package main.java.com.ubo.tp.message.ihm.login;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Classe de la vue de connexion.
 */
public class LoginView extends JPanel {

    private static final long serialVersionUID = 1L;

    protected LoginController mController;

    protected JTextField mTagField;
    protected JPasswordField mPasswordField;
    protected JButton mLoginButton;
    protected JButton mRegisterButton;

    public LoginView(LoginController controller) {
        this.mController = controller;
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
                mController.login(mTagField.getText(), new String(mPasswordField.getPassword()));
            }
        });
        buttonPanel.add(mLoginButton);

        mRegisterButton = new JButton("Créer un compte");
        mRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mController.goToRegister();
            }
        });
        buttonPanel.add(mRegisterButton);

        this.add(buttonPanel, new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }
}
