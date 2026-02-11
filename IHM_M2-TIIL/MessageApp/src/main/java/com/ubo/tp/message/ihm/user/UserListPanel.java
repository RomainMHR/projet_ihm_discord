package main.java.com.ubo.tp.message.ihm.user;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Vue pour afficher la liste des utilisateurs.
 */
public class UserListPanel extends JPanel {

    protected UserController mController;
    protected JList<User> mUserList;
    protected DefaultListModel<User> mListModel;

    public UserListPanel(UserController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setLayout(new BorderLayout());
        // Taille préférée pour la barre latérale
        this.setPreferredSize(new Dimension(200, 0));

        // Titre
        JLabel titleLabel = new JLabel("Utilisateurs");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        // Liste des utilisateurs
        mListModel = new DefaultListModel<>();
        mUserList = new JList<>(mListModel);
        mUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mUserList.setCellRenderer(new UserListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(mUserList);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void updateUserList(Set<User> users) {
        mListModel.clear();
        for (User user : users) {
            mListModel.addElement(user);
        }
    }

    /**
     * Renderer simple pour afficher le nom de l'utilisateur.
     */
    protected class UserListCellRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof User) {
                User user = (User) value;
                setText(user.getName() + " (@" + user.getUserTag() + ")");
            }
            return this;
        }
    }
}
