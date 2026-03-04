package main.java.com.ubo.tp.message.ihm.message;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageListView;

/**
 * Vue pour afficher la liste des messages d'une conversation.
 */
public class MessageListPanel extends JPanel implements IMessageListView {

    protected MessageController mController;
    protected JList<Message> mMessageList;
    protected DefaultListModel<Message> mListModel;
    protected SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");

    public MessageListPanel(MessageController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setListView(this);
    }

    protected void initGUI() {
        this.setLayout(new BorderLayout());

        mListModel = new DefaultListModel<>();
        mMessageList = new JList<>(mListModel);
        mMessageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mMessageList.setCellRenderer(new MessageListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(mMessageList);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void updateMessageList(List<Message> messages) {
        mListModel.clear();
        for (Message message : messages) {
            mListModel.addElement(message);
        }
        // Scroll to bottom
        if (!mListModel.isEmpty()) {
            mMessageList.ensureIndexIsVisible(mListModel.getSize() - 1);
        }
    }

    /**
     * Renderer pour afficher le message avec son expéditeur et sa date.
     */
    protected class MessageListCellRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Message) {
                Message msg = (Message) value;
                String dateStr = mDateFormat.format(new Date(msg.getEmissionDate()));
                setText(String.format("[%s] %s: %s", dateStr, msg.getSender().getName(), msg.getText()));
            }
            return this;
        }
    }
}
