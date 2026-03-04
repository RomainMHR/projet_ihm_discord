package main.java.com.ubo.tp.message.ihm.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class LoginViewFx extends GridPane {

    protected LoginController mController;

    protected TextField mTagField;
    protected PasswordField mPasswordField;
    protected Button mLoginButton;
    protected Button mRegisterButton;

    public LoginViewFx(LoginController controller) {
        this.mController = controller;
        this.initGUI();
    }

    protected void initGUI() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(25, 25, 25, 25));

        Label tagLabel = new Label("Tag (@...) :");
        this.add(tagLabel, 0, 1);

        mTagField = new TextField();
        this.add(mTagField, 1, 1);

        Label pwLabel = new Label("Mot de passe :");
        this.add(pwLabel, 0, 2);

        mPasswordField = new PasswordField();
        this.add(mPasswordField, 1, 2);

        mLoginButton = new Button("Se connecter");
        mLoginButton.setOnAction(e -> {
            mController.login(mTagField.getText(), mPasswordField.getText());
        });

        mRegisterButton = new Button("Créer un compte");
        mRegisterButton.setOnAction(e -> {
            mController.goToRegister();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(mLoginButton, mRegisterButton);
        this.add(hbBtn, 1, 4);
    }
}
