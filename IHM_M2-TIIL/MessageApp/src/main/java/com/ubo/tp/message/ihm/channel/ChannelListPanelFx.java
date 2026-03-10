package main.java.com.ubo.tp.message.ihm.channel;

import java.util.Set;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.interfaces.IChannelListView;

public class ChannelListPanelFx extends BorderPane implements IChannelListView {

    protected ChannelController mController;
    protected ListView<Channel> mChannelList;
    private Consumer<Channel> onChannelSelected;
    private boolean mIsUpdating = false;

    public ChannelListPanelFx(ChannelController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setMinWidth(200);
        this.setPadding(new Insets(10));

        Label titleLabel = new Label("Canaux (JavaFX)");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10px;");

        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPromptText("Rechercher un canal...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> mController.setSearchFilter(newVal));

        VBox topBox = new VBox(2, titleLabel, searchField);
        topBox.setPadding(new Insets(5));
        this.setTop(topBox);

        mChannelList = new ListView<>();
        mChannelList.setCellFactory(param -> new ListCell<Channel>() {
            @Override
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    String text = item.getName();
                    if (item.isPrivate()) {
                        text += " (Privé)";
                    }
                    if (mController.hasUnreadMessages(item)) {
                        text = "* " + text;
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #0078D7;");
                    } else {
                        setStyle("");
                    }
                    setText(text);

                    // Menu contextuel
                    javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
                    main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();

                    if (currentUser != null && item.getCreator().getUuid().equals(currentUser.getUuid())) {
                        javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem(
                                "Supprimer le canal");
                        deleteItem.setOnAction(ev -> {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText(null);
                            alert.setContentText("Voulez-vous vraiment supprimer ce canal ?");
                            alert.showAndWait().ifPresent(res -> {
                                if (res == javafx.scene.control.ButtonType.OK) {
                                    mController.deleteChannel(item);
                                }
                            });
                        });
                        contextMenu.getItems().add(deleteItem);

                        if (item.isPrivate()) {
                            javafx.scene.control.MenuItem addItem = new javafx.scene.control.MenuItem(
                                    "Ajouter un membre");
                            addItem.setOnAction(ev -> {
                                java.util.List<main.java.com.ubo.tp.message.datamodel.User> notInChannel = new java.util.ArrayList<>();
                                for (main.java.com.ubo.tp.message.datamodel.User u : mController.getAllUsers()) {
                                    if (!u.getUuid()
                                            .equals(main.java.com.ubo.tp.message.common.Constants.UNKNONWN_USER_UUID)
                                            && !item.getUsers().contains(u)) {
                                        notInChannel.add(u);
                                    }
                                }
                                if (notInChannel.isEmpty()) {
                                    javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                                            javafx.scene.control.Alert.AlertType.INFORMATION);
                                    a.setContentText("Aucun utilisateur disponible.");
                                    a.show();
                                    return;
                                }
                                javafx.scene.control.ChoiceDialog<main.java.com.ubo.tp.message.datamodel.User> dialog = new javafx.scene.control.ChoiceDialog<>(
                                        notInChannel.get(0), notInChannel);
                                dialog.setTitle("Ajout membre");
                                dialog.setHeaderText(null);
                                dialog.setContentText("Sélectionnez l'utilisateur :");
                                dialog.showAndWait().ifPresent(selected -> {
                                    mController.addMemberToChannel(item, selected);
                                });
                            });
                            contextMenu.getItems().add(addItem);

                            javafx.scene.control.MenuItem removeItem = new javafx.scene.control.MenuItem(
                                    "Retirer un membre");
                            removeItem.setOnAction(ev -> {
                                java.util.List<main.java.com.ubo.tp.message.datamodel.User> inChannel = new java.util.ArrayList<>();
                                for (main.java.com.ubo.tp.message.datamodel.User u : item.getUsers()) {
                                    if (!u.getUuid().equals(currentUser.getUuid())) {
                                        inChannel.add(u);
                                    }
                                }
                                if (inChannel.isEmpty()) {
                                    javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                                            javafx.scene.control.Alert.AlertType.INFORMATION);
                                    a.setContentText("Aucun autre utilisateur dans ce canal.");
                                    a.show();
                                    return;
                                }
                                javafx.scene.control.ChoiceDialog<main.java.com.ubo.tp.message.datamodel.User> dialog = new javafx.scene.control.ChoiceDialog<>(
                                        inChannel.get(0), inChannel);
                                dialog.setTitle("Retrait membre");
                                dialog.setHeaderText(null);
                                dialog.setContentText("Sélectionnez l'utilisateur :");
                                dialog.showAndWait().ifPresent(selected -> {
                                    mController.removeMemberFromChannel(item, selected);
                                });
                            });
                            contextMenu.getItems().add(removeItem);
                        }

                    } else if (item.isPrivate() && currentUser != null && item.getUsers().contains(currentUser)) {
                        javafx.scene.control.MenuItem leaveItem = new javafx.scene.control.MenuItem("Quitter le canal");
                        leaveItem.setOnAction(ev -> {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText(null);
                            alert.setContentText("Voulez-vous quitter ce canal privé ?");
                            alert.showAndWait().ifPresent(res -> {
                                if (res == javafx.scene.control.ButtonType.OK) {
                                    mController.quitChannel(item);
                                }
                            });
                        });
                        contextMenu.getItems().add(leaveItem);
                    }

                    if (contextMenu.getItems().isEmpty()) {
                        setContextMenu(null);
                    } else {
                        setContextMenu(contextMenu);
                    }
                }
            }
        });

        mChannelList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (mIsUpdating)
                return;
            if (onChannelSelected != null) {
                onChannelSelected.accept(newVal);
            }
        });

        this.setCenter(mChannelList);

        Button createBtn = new Button("Créer un canal");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> {
            javafx.scene.control.Dialog<Boolean> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Création de canal");
            dialog.setHeaderText("Nouveau canal");

            javafx.scene.control.ButtonType createButtonType = new javafx.scene.control.ButtonType("Créer",
                    javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, javafx.scene.control.ButtonType.CANCEL);

            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
            nameField.setPromptText("Nom du canal");

            javafx.scene.control.CheckBox privateCheck = new javafx.scene.control.CheckBox("Canal privé");

            ListView<main.java.com.ubo.tp.message.datamodel.User> userListView = new ListView<>();
            userListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
            userListView.setVisible(false);
            userListView.setManaged(false);

            java.util.Set<main.java.com.ubo.tp.message.datamodel.User> allUsers = mController.getAllUsers();
            main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();
            for (main.java.com.ubo.tp.message.datamodel.User u : allUsers) {
                if (currentUser != null && !u.getUuid().equals(currentUser.getUuid())
                        && !u.getUuid().equals(main.java.com.ubo.tp.message.common.Constants.UNKNONWN_USER_UUID)) {
                    userListView.getItems().add(u);
                }
            }

            userListView.setCellFactory(param -> new ListCell<main.java.com.ubo.tp.message.datamodel.User>() {
                @Override
                protected void updateItem(main.java.com.ubo.tp.message.datamodel.User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getName() + " (@" + user.getUserTag() + ")");
                    }
                }
            });

            privateCheck.setOnAction(evt -> {
                boolean isPriv = privateCheck.isSelected();
                userListView.setVisible(isPriv);
                userListView.setManaged(isPriv);
                if (dialog.getDialogPane().getScene() != null
                        && dialog.getDialogPane().getScene().getWindow() != null) {
                    dialog.getDialogPane().getScene().getWindow().sizeToScene();
                }
            });

            grid.add(new Label("Nom du canal :"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(privateCheck, 1, 1);
            grid.add(userListView, 1, 2);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> nameField.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    String name = nameField.getText();
                    if (name != null && !name.isEmpty()) {
                        if (privateCheck.isSelected()) {
                            mController.createPrivateChannel(name,
                                    new java.util.ArrayList<>(userListView.getSelectionModel().getSelectedItems()));
                        } else {
                            mController.createChannel(name);
                        }
                    }
                    return true;
                }
                return null;
            });

            dialog.showAndWait();
        });

        VBox bottomBox = new VBox(createBtn);
        bottomBox.setPadding(new Insets(5));
        this.setBottom(bottomBox);
    }

    @Override
    public void updateChannelList(Set<Channel> channels) {
        Platform.runLater(() -> {
            mIsUpdating = true;
            Channel selected = mChannelList.getSelectionModel().getSelectedItem();
            mChannelList.getItems().clear();
            mChannelList.getItems().addAll(channels);
            if (selected != null) {
                for (Channel c : channels) {
                    if (c.getUuid().equals(selected.getUuid())) {
                        mChannelList.getSelectionModel().select(c);
                        break;
                    }
                }
            }
            mIsUpdating = false;
        });
    }

    public void setOnChannelSelected(Consumer<Channel> listener) {
        this.onChannelSelected = listener;
    }
}
