package main.java.com.ubo.tp.message.ihm.message;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.com.ubo.tp.message.ihm.interfaces.IMessageInputView;

/**
 * Vue pour la saisie et l'envoi d'un message.
 */
public class MessageInputPanel extends JPanel implements IMessageInputView {

    protected MessageController mController;
    protected JTextField mInputField;
    protected JButton mSendButton;

    public MessageInputPanel(MessageController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setLayout(new BorderLayout(5, 5)); // Espacement léger

        mInputField = new JTextField();
        // Permettre d'envoyer avec la touche Entrée
        mInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        this.add(mInputField, BorderLayout.CENTER);

        mSendButton = new JButton("Envoyer");
        mSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        this.add(mSendButton, BorderLayout.EAST);
    }

    protected void sendMessage() {
        String text = mInputField.getText();
        if (text != null && !text.trim().isEmpty()) {
            mController.sendMessage(text);
            mInputField.setText(""); // Vider le champ après un envoi éventuel
        }
    }

    /**
     * Permet de donner le focus au champ texte.
     */
    @Override
    public void focusInput() {
        mInputField.requestFocusInWindow();
    }
}
