package main.java.com.ubo.tp.message.ihm;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;

import main.java.com.ubo.tp.message.ihm.channel.ChannelController;
import main.java.com.ubo.tp.message.ihm.channel.ChannelListPanelFx;
import main.java.com.ubo.tp.message.ihm.user.UserController;
import main.java.com.ubo.tp.message.ihm.user.UserListPanelFx;
import main.java.com.ubo.tp.message.ihm.message.MessageViewFx;

public class MessageAppMainViewFx extends BorderPane {

    protected IMessageApp mMessageApp;
    protected Session mSession;
    protected DataManager mDataManager;

    public MessageAppMainViewFx(IMessageApp messageApp, Session session, DataManager dataManager) {
        this.mMessageApp = messageApp;
        this.mSession = session;
        this.mDataManager = dataManager;
        this.initGUI();
    }

    protected void initGUI() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Fichier");
        MenuItem exitItem = new MenuItem("Quitter JavaFX");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("?");
        MenuItem aboutItem = new MenuItem("A propos");
        aboutItem.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("A propos");
            alert.setHeaderText(null);
            alert.setContentText("UBO M2-TIIL\nDépartement Informatique (JavaFX)");
            alert.showAndWait();
        });
        helpMenu.getItems().add(aboutItem);

        Menu profileMenu = new Menu("Profil");
        MenuItem changeNameItem = new MenuItem("Modifier mon nom");
        changeNameItem.setOnAction(e -> {
            main.java.com.ubo.tp.message.datamodel.User user = mSession.getConnectedUser();
            if (user != null) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Modifier mon nom");
                dialog.setHeaderText(null);
                dialog.setContentText("Nouveau nom d'utilisateur :");
                dialog.showAndWait().ifPresent(newName -> {
                    if (!newName.trim().isEmpty()) {
                        user.setName(newName.trim());
                        mDataManager.sendUser(user);
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Succès");
                        alert.setHeaderText(null);
                        alert.setContentText("Nom modifié avec succès !");
                        alert.showAndWait();
                    }
                });
            }
        });

        MenuItem deleteAccountItem = new MenuItem("Supprimer mon compte");
        deleteAccountItem.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer votre compte définitivement ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    main.java.com.ubo.tp.message.datamodel.User user = mSession.getConnectedUser();
                    if (user != null) {
                        mDataManager.deleteUser(user);
                        mSession.disconnect();
                        Alert infoAlert = new Alert(AlertType.INFORMATION);
                        infoAlert.setTitle("Information");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Votre compte a été supprimé.");
                        infoAlert.showAndWait();
                    }
                }
            });
        });

        profileMenu.getItems().addAll(changeNameItem, deleteAccountItem);

        Menu logoutMenu = new Menu("Déconnexion");
        MenuItem logoutItem = new MenuItem("Se déconnecter");
        logoutItem.setOnAction(e -> mSession.disconnect());
        logoutMenu.getItems().add(logoutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu, profileMenu, logoutMenu);
        this.setTop(menuBar);

        MessageViewFx messageView = new MessageViewFx(mMessageApp, mDataManager, mSession);

        ChannelController channelController = new ChannelController(mMessageApp, mDataManager, mSession);
        ChannelListPanelFx channelListPanel = new ChannelListPanelFx(channelController);

        UserController userController = new UserController(mDataManager, mSession);
        UserListPanelFx userListPanel = new UserListPanelFx(userController);

        this.setLeft(channelListPanel);
        this.setRight(userListPanel);
        this.setCenter(messageView);

        channelListPanel.setOnChannelSelected(channel -> {
            if (channel != null) {
                messageView.getController().setCurrentRecipient(channel.getUuid(), channel.getName());
                userController.setCurrentChannelFilter(channel);
                channelController.markChannelAsRead(channel.getUuid());
            } else {
                userController.setCurrentChannelFilter(null);
            }
        });

    }
}
