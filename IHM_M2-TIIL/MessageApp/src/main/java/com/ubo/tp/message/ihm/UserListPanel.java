package main.java.com.ubo.tp.message.ihm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import main.java.com.ubo.tp.message.datamodel.User;
/*
 * public class UserListPanel extends JPanel {
 * 
 * public UserListPanel(Set<User> users) {
 * this.setLayout(new GridBagLayout());
 * 
 * JLabel title = new JLabel("Liste des utilisateurs");
 * this.add(title, new GridBagConstraints(0, 0, 1, 1, 0, 0,
 * GridBagConstraints.CENTER, GridBagConstraints.NONE,
 * new Insets(10, 10, 10, 10), 0, 0));
 * 
 * JTextArea userListArea = new JTextArea();
 * userListArea.setEditable(false);
 * 
 * StringBuilder sb = new StringBuilder();
 * for (User user : users) {
 * sb.append(user.getName()).append(" (@").append(user.getUserTag()).append(
 * ")\n");
 * }
 * userListArea.setText(sb.toString());
 * 
 * this.add(new JScrollPane(userListArea), new GridBagConstraints(0, 1, 1, 1, 1,
 * 1, GridBagConstraints.CENTER,
 * GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
 * }
 * }
 */