package main.java.com.ubo.tp.message.ihm.channel;

import java.util.Set;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.interfaces.IChannelListView;

public class ChannelListPanelFx extends BorderPane implements IChannelListView {

    protected ChannelController mController;
    protected ListView<Channel> mChannelList;
    private Consumer<Channel> onChannelSelected;

    public ChannelListPanelFx(ChannelController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setMinWidth(200);

        Label titleLabel = new Label("Canaux (JavaFX)");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10px;");
        this.setTop(titleLabel);

        mChannelList = new ListView<>();
        mChannelList.setCellFactory(param -> new ListCell<Channel>() {
            @Override
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        mChannelList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (onChannelSelected != null) {
                onChannelSelected.accept(newVal);
            }
        });

        this.setCenter(mChannelList);

        Button createBtn = new Button("Créer un canal");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Création de canal");
            dialog.setHeaderText(null);
            dialog.setContentText("Nom du nouveau canal :");
            dialog.showAndWait().ifPresent(name -> mController.createChannel(name));
        });

        VBox bottomBox = new VBox(createBtn);
        bottomBox.setPadding(new Insets(5));
        this.setBottom(bottomBox);
    }

    @Override
    public void updateChannelList(Set<Channel> channels) {
        Platform.runLater(() -> {
            mChannelList.getItems().clear();
            mChannelList.getItems().addAll(channels);
        });
    }

    public void setOnChannelSelected(Consumer<Channel> listener) {
        this.onChannelSelected = listener;
    }
}
