package main.java.com.ubo.tp.message.ihm.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageListView;

public class MessageListPanelFx extends VBox implements IMessageListView {

    protected MessageController mController;
    protected ListView<Message> mMessageList;
    protected SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm");

    public MessageListPanelFx(MessageController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setListView(this);
    }

    protected void initGUI() {
        mMessageList = new ListView<>();
        VBox.setVgrow(mMessageList, Priority.ALWAYS);

        mMessageList.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    String dateStr = mDateFormat.format(new Date(msg.getEmissionDate()));
                    setText(String.format("[%s] %s: %s", dateStr, msg.getSender().getName(), msg.getText()));

                    // Menu contextuel si l'utilisateur est l'auteur du message
                    main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();
                    if (currentUser != null && msg.getSender().getUuid().equals(currentUser.getUuid())) {
                        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
                        javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem(
                                "Supprimer ce message");
                        deleteItem.setOnAction(ev -> {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText(null);
                            alert.setContentText("Voulez-vous supprimer ce message ?");
                            alert.showAndWait().ifPresent(res -> {
                                if (res == javafx.scene.control.ButtonType.OK) {
                                    mController.deleteMessage(msg);
                                }
                            });
                        });
                        contextMenu.getItems().add(deleteItem);
                        setContextMenu(contextMenu);
                    } else {
                        setContextMenu(null);
                    }
                }
            }
        });

        this.getChildren().add(mMessageList);
    }

    @Override
    public void updateMessageList(List<Message> messages) {
        Platform.runLater(() -> {
            mMessageList.getItems().clear();
            mMessageList.getItems().addAll(messages);
            if (!messages.isEmpty()) {
                mMessageList.scrollTo(messages.size() - 1);
            }
        });
    }
}
