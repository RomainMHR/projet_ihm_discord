package main.java.com.ubo.tp.message.ihm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;
import main.java.com.ubo.tp.message.ihm.login.LoginController;
import main.java.com.ubo.tp.message.ihm.login.LoginViewFx;
import main.java.com.ubo.tp.message.ihm.login.RegisterController;
import main.java.com.ubo.tp.message.ihm.login.RegisterViewFx;

/**
 * Classe principale JavaFX de l'application.
 */
public class MessageAppFx extends Application implements ISessionObserver, IMessageApp {

    // Instance statique du DataManager partagé avec Swing
    private static DataManager sharedDataManager;

    public static void setSharedDataManager(DataManager dataManager) {
        sharedDataManager = dataManager;
    }

    protected DataManager mDataManager;
    protected Session mSession;
    protected Stage mStage;

    @Override
    public void start(Stage primaryStage) {
        this.mStage = primaryStage;
        this.mDataManager = sharedDataManager;

        // Création d'une nouvelle session spécifique à JavaFX (pour connexion
        // indépendante)
        this.mSession = new Session();
        this.mSession.addObserver(this);

        this.mStage.setTitle("MessageApp - JavaFX");
        this.mStage.setOnCloseRequest(event -> {
            Platform.exit();
        });

        this.showLoginView();
        this.mStage.show();
    }

    @Override
    public void showLoginView() {
        LoginController controller = new LoginController(this, mDataManager, mSession);
        LoginViewFx loginView = new LoginViewFx(controller);
        Scene scene = new Scene(loginView, 400, 300);
        scene.getStylesheets().add(new java.io.File(
                "c:/Users/romai/Documents/GitHub/projet_ihm_discord/IHM_M2-TIIL/MessageApp/style.css").toURI()
                .toString());
        this.mStage.setScene(scene);
        this.mStage.setTitle("MessageApp JavaFX - Connexion");
    }

    @Override
    public void showRegisterView() {
        RegisterController controller = new RegisterController(this, mDataManager);
        RegisterViewFx registerView = new RegisterViewFx(controller);
        Scene scene = new Scene(registerView, 400, 400);
        scene.getStylesheets().add(new java.io.File(
                "c:/Users/romai/Documents/GitHub/projet_ihm_discord/IHM_M2-TIIL/MessageApp/style.css").toURI()
                .toString());
        this.mStage.setScene(scene);
        this.mStage.setTitle("MessageApp JavaFX - Inscription");
    }

    @Override
    public void showMainView() {
        MessageAppMainViewFx mainView = new MessageAppMainViewFx(this, mSession, mDataManager);
        Scene scene = new Scene(mainView, 800, 600);
        scene.getStylesheets().add(new java.io.File(
                "c:/Users/romai/Documents/GitHub/projet_ihm_discord/IHM_M2-TIIL/MessageApp/style.css").toURI()
                .toString());
        this.mStage.setScene(scene);
        this.mStage.setTitle("MessageApp JavaFX - " + mSession.getConnectedUser().getName());
    }

    @Override
    public void notifyLogin(User user) {
        Platform.runLater(() -> {
            this.showMainView();
        });
    }

    @Override
    public void notifyLogout() {
        Platform.runLater(() -> {
            System.out.println("Utilisateur déconnecté (JavaFX)");
            this.showLoginView();
        });
    }

    @Override
    public void showErrorMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void showInformationMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
