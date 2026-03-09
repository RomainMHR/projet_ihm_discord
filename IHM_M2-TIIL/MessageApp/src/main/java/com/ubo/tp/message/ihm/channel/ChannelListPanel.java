package main.java.com.ubo.tp.message.ihm.channel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.interfaces.IChannelListView;

/**
 * Vue pour afficher la liste des canaux et en créer de nouveaux.
 */
public class ChannelListPanel extends JPanel implements IChannelListView {

    protected ChannelController mController;
    protected JList<Channel> mChannelList;
    protected DefaultListModel<Channel> mListModel;
    protected JButton mCreateButton;

    public ChannelListPanel(ChannelController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setLayout(new BorderLayout());

        // Titre
        JLabel titleLabel = new JLabel("Liste des canaux disponibles");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Barre de recherche
        javax.swing.JTextField searchField = new javax.swing.JTextField();
        searchField.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5),
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
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);

        // Liste des canaux
        mListModel = new DefaultListModel<>();
        mChannelList = new JList<>(mListModel);
        mChannelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mChannelList.setCellRenderer(new ChannelListCellRenderer()); // Custom renderer si besoin, sinon affichage par
                                                                     // défaut

        JScrollPane scrollPane = new JScrollPane(mChannelList);
        this.add(scrollPane, BorderLayout.CENTER);

        // MOUSE LISTENER POUR MENU CONTEXTUEL
        mChannelList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (javax.swing.SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) {
                    int row = mChannelList.locationToIndex(e.getPoint());
                    if (row >= 0) {
                        mChannelList.setSelectedIndex(row);
                        Channel selected = mChannelList.getSelectedValue();
                        if (selected != null) {
                            showContextMenu(e, selected);
                        }
                    }
                }
            }
        });

        // Bouton de création
        JPanel bottomPanel = new JPanel();
        mCreateButton = new JButton("Créer un canal");
        mCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel(new java.awt.GridLayout(0, 1));
                javax.swing.JTextField nameField = new javax.swing.JTextField();
                javax.swing.JCheckBox privateCheck = new javax.swing.JCheckBox("Canal privé");

                java.util.Set<main.java.com.ubo.tp.message.datamodel.User> allUsers = mController.getAllUsers();
                java.util.Vector<main.java.com.ubo.tp.message.datamodel.User> userVector = new java.util.Vector<>();
                main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();

                for (main.java.com.ubo.tp.message.datamodel.User u : allUsers) {
                    if (currentUser != null && !u.getUuid().equals(currentUser.getUuid())
                            && !u.getUuid().equals(main.java.com.ubo.tp.message.common.Constants.UNKNONWN_USER_UUID)) {
                        userVector.add(u);
                    }
                }

                javax.swing.JList<main.java.com.ubo.tp.message.datamodel.User> userList = new javax.swing.JList<>(
                        userVector);
                userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                JScrollPane listScroller = new JScrollPane(userList);
                listScroller.setPreferredSize(new java.awt.Dimension(200, 100));
                listScroller.setVisible(false);

                privateCheck.addActionListener(evt -> {
                    listScroller.setVisible(privateCheck.isSelected());
                    panel.revalidate();
                    panel.repaint();
                    // On pack la fenêtre parent
                    java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(panel);
                    if (win != null)
                        win.pack();
                });

                panel.add(new JLabel("Nom du canal :"));
                panel.add(nameField);
                panel.add(privateCheck);
                panel.add(listScroller);

                int result = JOptionPane.showConfirmDialog(ChannelListPanel.this, panel, "Nouveau canal",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    if (name != null && !name.isEmpty()) {
                        if (privateCheck.isSelected()) {
                            java.util.List<main.java.com.ubo.tp.message.datamodel.User> selected = userList
                                    .getSelectedValuesList();
                            mController.createPrivateChannel(name, new java.util.ArrayList<>(selected));
                        } else {
                            mController.createChannel(name);
                        }
                    }
                }
            }
        });
        bottomPanel.add(mCreateButton);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showContextMenu(java.awt.event.MouseEvent e, Channel channel) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();

        if (currentUser != null && channel.getCreator().getUuid().equals(currentUser.getUuid())) {
            javax.swing.JMenuItem deleteItem = new javax.swing.JMenuItem("Supprimer le canal");
            deleteItem.addActionListener(evt -> {
                int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce canal ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    mController.deleteChannel(channel);
                }
            });
            menu.add(deleteItem);

            if (channel.isPrivate()) {
                javax.swing.JMenuItem addItem = new javax.swing.JMenuItem("Ajouter un membre");
                addItem.addActionListener(evt -> {
                    java.util.Set<main.java.com.ubo.tp.message.datamodel.User> allUsers = mController.getAllUsers();
                    java.util.Vector<main.java.com.ubo.tp.message.datamodel.User> notInChannel = new java.util.Vector<>();
                    for (main.java.com.ubo.tp.message.datamodel.User u : allUsers) {
                        if (!u.getUuid().equals(main.java.com.ubo.tp.message.common.Constants.UNKNONWN_USER_UUID)
                                && !channel.getUsers().contains(u)) {
                            notInChannel.add(u);
                        }
                    }
                    if (notInChannel.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Aucun utilisateur disponible à ajouter.");
                        return;
                    }
                    main.java.com.ubo.tp.message.datamodel.User selected = (main.java.com.ubo.tp.message.datamodel.User) JOptionPane
                            .showInputDialog(this, "Sélectionnez l'utilisateur à ajouter:", "Ajout membre",
                                    JOptionPane.QUESTION_MESSAGE, null, notInChannel.toArray(), notInChannel.get(0));
                    if (selected != null) {
                        mController.addMemberToChannel(channel, selected);
                    }
                });
                menu.add(addItem);

                javax.swing.JMenuItem removeItem = new javax.swing.JMenuItem("Retirer un membre");
                removeItem.addActionListener(evt -> {
                    java.util.Vector<main.java.com.ubo.tp.message.datamodel.User> inChannel = new java.util.Vector<>();
                    for (main.java.com.ubo.tp.message.datamodel.User u : channel.getUsers()) {
                        if (!u.getUuid().equals(currentUser.getUuid())) { // Pas de retrait de soi-même via ce menu
                            inChannel.add(u);
                        }
                    }
                    if (inChannel.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Aucun autre utilisateur dans ce canal.");
                        return;
                    }
                    main.java.com.ubo.tp.message.datamodel.User selected = (main.java.com.ubo.tp.message.datamodel.User) JOptionPane
                            .showInputDialog(this, "Sélectionnez l'utilisateur à retirer:", "Retrait membre",
                                    JOptionPane.QUESTION_MESSAGE, null, inChannel.toArray(), inChannel.get(0));
                    if (selected != null) {
                        mController.removeMemberFromChannel(channel, selected);
                    }
                });
                menu.add(removeItem);
            }

        } else if (channel.isPrivate() && currentUser != null && channel.getUsers().contains(currentUser)) {
            javax.swing.JMenuItem leaveItem = new javax.swing.JMenuItem("Quitter le canal");
            leaveItem.addActionListener(evt -> {
                int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous quitter ce canal privé ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    mController.quitChannel(channel);
                }
            });
            menu.add(leaveItem);
        }

        if (menu.getComponentCount() > 0) {
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void updateChannelList(Set<Channel> channels) {
        mListModel.clear();
        for (Channel channel : channels) {
            mListModel.addElement(channel);
        }
    }

    public void addSelectionListener(javax.swing.event.ListSelectionListener listener) {
        mChannelList.addListSelectionListener(listener);
    }

    public Channel getSelectedChannel() {
        return mChannelList.getSelectedValue();
    }

    /**
     * Renderer simple pour afficher le nom du canal.
     */
    protected class ChannelListCellRenderer extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Channel) {
                Channel c = (Channel) value;
                String text = c.getName();
                if (c.isPrivate()) {
                    text += " (Privé)";
                }
                if (mController.hasUnreadMessages(c)) {
                    text = "* " + text;
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                    setForeground(new java.awt.Color(0, 120, 215));
                } else {
                    setFont(getFont().deriveFont(java.awt.Font.PLAIN));
                }
                setText(text);
            }
            return this;
        }
    }
}
