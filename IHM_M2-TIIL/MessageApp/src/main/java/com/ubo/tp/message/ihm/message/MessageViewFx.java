package main.java.com.ubo.tp.message.ihm.message;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageMainView;

public class MessageViewFx extends BorderPane implements IMessageMainView {

    protected MessageController mController;
    protected MessageListPanelFx mListPanel;
    protected MessageInputPanelFx mInputPanel;
    protected Label mTitleLabel;

    public MessageViewFx(IMessageApp app, DataManager dataManager, ISession session) {
        this.mController = new MessageController(app, dataManager, session);
        this.mController.setMainView(this);

        this.mTitleLabel = new Label("Sélectionnez une conversation");
        this.mTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox topBox = new VBox(this.mTitleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        this.setTop(topBox);

        this.mListPanel = new MessageListPanelFx(mController);
        this.mInputPanel = new MessageInputPanelFx(mController);

        this.setCenter(this.mListPanel);
        this.setBottom(this.mInputPanel);
    }

    public MessageController getController() {
        return mController;
    }

    @Override
    public void setChatTitle(String title) {
        javafx.application.Platform.runLater(() -> {
            this.mTitleLabel.setText(title);
        });
    }
}
