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
                } else {
                    String dateStr = mDateFormat.format(new Date(msg.getEmissionDate()));
                    setText(String.format("[%s] %s: %s", dateStr, msg.getSender().getName(), msg.getText()));
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
