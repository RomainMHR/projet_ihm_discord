package main.java.com.ubo.tp.message.ihm.user;

import java.util.Set;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.interfaces.IUserListView;

public class UserListPanelFx extends BorderPane implements IUserListView {

    protected UserController mController;
    protected ListView<User> mUserList;

    public UserListPanelFx(UserController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setMinWidth(200);

        Label titleLabel = new Label("Utilisateurs (JavaFX)");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10px;");

        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPromptText("Rechercher un utilisateur...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> mController.setSearchFilter(newVal));

        javafx.scene.layout.VBox topBox = new javafx.scene.layout.VBox(2, titleLabel, searchField);
        topBox.setPadding(new javafx.geometry.Insets(5));
        this.setTop(topBox);

        mUserList = new ListView<>();
        mUserList.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (@" + item.getUserTag() + ")");
                }
            }
        });

        // On désactive la sélection pour empêcher les conversations privées
        mUserList.setSelectionModel(null);

        this.setCenter(mUserList);
    }

    @Override
    public void updateUserList(Set<User> users) {
        Platform.runLater(() -> {
            mUserList.getItems().clear();
            mUserList.getItems().addAll(users);
        });
    }

}
