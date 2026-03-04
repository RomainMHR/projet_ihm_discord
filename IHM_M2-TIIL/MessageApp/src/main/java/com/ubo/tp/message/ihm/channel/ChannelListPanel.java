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
        this.add(titleLabel, BorderLayout.NORTH);

        // Liste des canaux
        mListModel = new DefaultListModel<>();
        mChannelList = new JList<>(mListModel);
        mChannelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mChannelList.setCellRenderer(new ChannelListCellRenderer()); // Custom renderer si besoin, sinon affichage par
                                                                     // défaut

        JScrollPane scrollPane = new JScrollPane(mChannelList);
        this.add(scrollPane, BorderLayout.CENTER);

        // Bouton de création
        JPanel bottomPanel = new JPanel();
        mCreateButton = new JButton("Créer un canal");
        mCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String channelName = JOptionPane.showInputDialog(ChannelListPanel.this, "Nom du nouveau canal :",
                        "Création de canal", JOptionPane.QUESTION_MESSAGE);
                if (channelName != null && !channelName.isEmpty()) {
                    mController.createChannel(channelName);
                }
            }
        });
        bottomPanel.add(mCreateButton);
        this.add(bottomPanel, BorderLayout.SOUTH);
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
                setText(((Channel) value).getName());
            }
            return this;
        }
    }
}
