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
            javafx.stage.Stage toastStage = new javafx.stage.Stage();
            toastStage.initOwner(this.mStage);
            toastStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            toastStage.setAlwaysOnTop(true);

            javafx.scene.text.Text text = new javafx.scene.text.Text(message);
            text.setFill(javafx.scene.paint.Color.WHITE);
            text.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));

            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(text);
            root.setStyle("-fx-background-radius: 10; -fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 15px;");
            root.setOpacity(0);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            toastStage.setScene(scene);

            // Positionnement en bas au centre
            toastStage.setOnShown(event -> {
                double x = this.mStage.getX() + this.mStage.getWidth() / 2 - toastStage.getWidth() / 2;
                double y = this.mStage.getY() + this.mStage.getHeight() - toastStage.getHeight() - 50;
                toastStage.setX(x);
                toastStage.setY(y);
            });

            toastStage.show();

            // Animation d'apparition
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Fermeture automatique après 3 secondes
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(3));
            delay.setOnFinished(e -> {
                javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(300), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> toastStage.close());
                fadeOut.play();
            });
            delay.play();
        });
    }
}
