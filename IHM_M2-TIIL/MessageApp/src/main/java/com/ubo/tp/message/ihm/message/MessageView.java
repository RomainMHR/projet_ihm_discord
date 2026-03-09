package main.java.com.ubo.tp.message.ihm.message;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageMainView;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;

/**
 * Vue principale des messages (Liste + Input) gérée par le MessageController.
 */
public class MessageView extends JPanel implements IMessageMainView {

    protected MessageController mController;
    protected MessageListPanel mListPanel;
    protected MessageInputPanel mInputPanel;
    protected javax.swing.JLabel mTitleLabel;

    public MessageView(IMessageApp app, DataManager dataManager, ISession session) {
        this.setLayout(new BorderLayout());

        this.mController = new MessageController(app, dataManager, session);
        this.mController.setMainView(this);

        this.mTitleLabel = new javax.swing.JLabel("Sélectionnez une conversation", javax.swing.SwingConstants.CENTER);
        this.mTitleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        this.mTitleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barre de recherche
        javax.swing.JTextField searchField = new javax.swing.JTextField();
        searchField.setToolTipText("Rechercher un message...");
        searchField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 10, 5, 10),
                javax.swing.BorderFactory.createTitledBorder("🔍 Rechercher")));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                mController.setSearchFilter(searchField.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                mController.setSearchFilter(searchField.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                mController.setSearchFilter(searchField.getText());
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(mTitleLabel, BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.SOUTH);

        this.mListPanel = new MessageListPanel(mController);
        this.mInputPanel = new MessageInputPanel(mController);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(mListPanel, BorderLayout.CENTER);
        this.add(mInputPanel, BorderLayout.SOUTH);
    }

    public MessageController getController() {
        return mController;
    }

    @Override
    public void setChatTitle(String title) {
        this.mTitleLabel.setText(title);
    }
}
