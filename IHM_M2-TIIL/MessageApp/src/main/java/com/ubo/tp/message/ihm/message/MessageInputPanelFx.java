package main.java.com.ubo.tp.message.ihm.message;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import main.java.com.ubo.tp.message.ihm.interfaces.IMessageInputView;

public class MessageInputPanelFx extends HBox implements IMessageInputView {

    protected MessageController mController;
    protected TextField mInputField;
    protected Button mSendButton;

    public MessageInputPanelFx(MessageController controller) {
        this.mController = controller;
        this.initGUI();
        this.mController.setView(this);
    }

    protected void initGUI() {
        this.setSpacing(10);
        this.setPadding(new Insets(10));

        mInputField = new TextField();
        mInputField.setOnAction(e -> sendMessage());
        HBox.setHgrow(mInputField, Priority.ALWAYS);

        mSendButton = new Button("Envoyer");
        mSendButton.setOnAction(e -> sendMessage());

        this.getChildren().addAll(mInputField, mSendButton);
    }

    protected void sendMessage() {
        String text = mInputField.getText();
        if (text != null && !text.trim().isEmpty()) {
            mController.sendMessage(text);
            mInputField.setText("");
        }
    }

    @Override
    public void focusInput() {
        javafx.application.Platform.runLater(() -> {
            mInputField.requestFocus();
        });
    }
}
