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
 * Classe de la vue d'inscription.
 */
public class RegisterView extends JPanel {

        private static final long serialVersionUID = 1L;

        protected RegisterController mController;

        protected JTextField mNameField;
        protected JTextField mTagField;
        protected JPasswordField mPasswordField;
        protected JPasswordField mConfirmPasswordField;
        protected JButton mRegisterButton;
        protected JButton mCancelButton;

        public RegisterView(RegisterController controller) {
                this.mController = controller;
                this.initGUI();
        }

        protected void initGUI() {
                this.setLayout(new GridBagLayout());

                // Name
                this.add(new JLabel("Nom :"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                mNameField = new JTextField(20);
                this.add(mNameField, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

                // Tag
                this.add(new JLabel("Tag (@...) :"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                mTagField = new JTextField(20);
                this.add(mTagField, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

                // Password
                this.add(new JLabel("Mot de passe :"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                mPasswordField = new JPasswordField(20);
                this.add(mPasswordField, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

                // Confirm Password
                this.add(new JLabel("Confirmer mdp :"),
                                new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                mConfirmPasswordField = new JPasswordField(20);
                this.add(mConfirmPasswordField, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
                                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

                // Buttons
                JPanel buttonPanel = new JPanel();
                mRegisterButton = new JButton("S'inscrire");
                mRegisterButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                mController.register(mNameField.getText(), mTagField.getText(),
                                                new String(mPasswordField.getPassword()),
                                                new String(mConfirmPasswordField.getPassword()));
                        }
                });
                buttonPanel.add(mRegisterButton);

                mCancelButton = new JButton("Annuler");
                mCancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                mController.cancel();
                        }
                });
                buttonPanel.add(mCancelButton);

                this.add(buttonPanel, new GridBagConstraints(0, 4, 2, 1, 0, 0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
        }
}
