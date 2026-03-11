package main.java.com.ubo.tp.message.ihm.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class RegisterViewFx extends GridPane {

    protected RegisterController mController;

    protected TextField mNameField;
    protected TextField mTagField;
    protected PasswordField mPasswordField;
    protected PasswordField mConfirmPasswordField;
    protected Button mRegisterButton;
    protected Button mCancelButton;

    public RegisterViewFx(RegisterController controller) {
        this.mController = controller;
        this.initGUI();
    }

    protected void initGUI() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(25, 25, 25, 25));

        Label nameLabel = new Label("Nom :");
        this.add(nameLabel, 0, 0);

        mNameField = new TextField();
        this.add(mNameField, 1, 0);

        Label tagLabel = new Label("Tag (@...) :");
        this.add(tagLabel, 0, 1);

        mTagField = new TextField();
        this.add(mTagField, 1, 1);

        Label pwLabel = new Label("Mot de passe :");
        this.add(pwLabel, 0, 2);

        mPasswordField = new PasswordField();
        this.add(mPasswordField, 1, 2);

        Label confirmPwLabel = new Label("Confirmer mdp :");
        this.add(confirmPwLabel, 0, 3);

        mConfirmPasswordField = new PasswordField();
        this.add(mConfirmPasswordField, 1, 3);

        mRegisterButton = new Button("S'inscrire");
        mRegisterButton.setOnAction(e -> {
            mController.register(mNameField.getText(), mTagField.getText(), mPasswordField.getText(),
                    mConfirmPasswordField.getText());
        });

        mCancelButton = new Button("Annuler");
        mCancelButton.setOnAction(e -> {
            mController.cancel();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(mRegisterButton, mCancelButton);
        this.add(hbBtn, 1, 5);
    }
}
