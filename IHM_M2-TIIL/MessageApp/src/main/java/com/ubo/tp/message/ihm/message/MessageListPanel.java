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

        // Menu contextuel pour supprimer un message
        mMessageList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (javax.swing.SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) {
                    int row = mMessageList.locationToIndex(e.getPoint());
                    if (row >= 0) {
                        mMessageList.setSelectedIndex(row);
                        Message selected = mMessageList.getSelectedValue();
                        if (selected != null && mController.getConnectedUser() != null
                                && selected.getSender().getUuid().equals(mController.getConnectedUser().getUuid())) {
                            javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
                            javax.swing.JMenuItem deleteItem = new javax.swing.JMenuItem("Supprimer ce message");
                            deleteItem.addActionListener(evt -> {
                                int confirm = javax.swing.JOptionPane.showConfirmDialog(
                                        MessageListPanel.this,
                                        "Voulez-vous supprimer ce message ?",
                                        "Confirmation", javax.swing.JOptionPane.YES_NO_OPTION);
                                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                                    mController.deleteMessage(selected);
                                }
                            });
                            menu.add(deleteItem);
                            menu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });
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
                main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();

                // Échapper le texte brut avant de styliser les mentions
                String escapedText = escapeHTML(msg.getText());
                String styledText = styleMentions(escapedText, currentUser);

                // Utiliser un div avec word-wrap pour forcer le retour à la ligne
                // La largeur est approximative par rapport à la liste
                setText(String.format(
                        "<html><div style='width: 350px; word-wrap: break-word;'>[%s] <b>%s</b>: %s</div></html>",
                        dateStr, msg.getSender().getName(), styledText));
            }
            return this;
        }

        private String escapeHTML(String s) {
            if (s == null)
                return "";
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
                    "&#39;");
        }
    }

    /**
     * Colore uniquement la @mention de l'utilisateur connecté.
     */
    protected String styleMentions(String text, main.java.com.ubo.tp.message.datamodel.User currentUser) {
        if (currentUser == null) {
            return text;
        }
        String userName = currentUser.getName().toLowerCase();
        String userTag = currentUser.getUserTag().toLowerCase();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("@(\\w+)").matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String mention = matcher.group(1).toLowerCase();
            if (mention.equals(userName) || mention.equals(userTag)) {
                matcher.appendReplacement(sb, "<span style='color:#5865F2;font-weight:bold;'>@$1</span>");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
