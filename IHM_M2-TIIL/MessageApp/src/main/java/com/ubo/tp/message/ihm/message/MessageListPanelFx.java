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
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    String dateStr = mDateFormat.format(new Date(msg.getEmissionDate()));
                    String prefix = String.format("[%s] %s: ", dateStr, msg.getSender().getName());

                    // Créer un TextFlow avec les @mentions en bleu gras
                    javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow();
                    javafx.scene.text.Text prefixText = new javafx.scene.text.Text(prefix);
                    prefixText.setStyle("-fx-font-weight: bold;");
                    textFlow.getChildren().add(prefixText);

                    // Parser le texte pour trouver les @mentions
                    String msgText = msg.getText();
                    main.java.com.ubo.tp.message.datamodel.User currentUser = mController.getConnectedUser();
                    String myName = (currentUser != null) ? currentUser.getName().toLowerCase() : "";
                    String myTag = (currentUser != null) ? currentUser.getUserTag().toLowerCase() : "";

                    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("@(\\w+)").matcher(msgText);
                    int lastEnd = 0;
                    while (matcher.find()) {
                        // Texte avant la mention
                        if (matcher.start() > lastEnd) {
                            textFlow.getChildren()
                                    .add(new javafx.scene.text.Text(msgText.substring(lastEnd, matcher.start())));
                        }
                        // La mention : colorée uniquement si c'est l'utilisateur connecté
                        javafx.scene.text.Text mentionText = new javafx.scene.text.Text(matcher.group());
                        String mentionName = matcher.group(1).toLowerCase();
                        if (mentionName.equals(myName) || mentionName.equals(myTag)) {
                            mentionText.setStyle("-fx-fill: #5865F2; -fx-font-weight: bold;");
                        }
                        textFlow.getChildren().add(mentionText);
                        lastEnd = matcher.end();
                    }
                    // Texte restant après la dernière mention
                    if (lastEnd < msgText.length()) {
                        textFlow.getChildren().add(new javafx.scene.text.Text(msgText.substring(lastEnd)));
                    }

                    setText(null);
                    setGraphic(textFlow);

                    // Menu contextuel si l'utilisateur est l'auteur du message
                    if (currentUser != null && msg.getSender().getUuid().equals(currentUser.getUuid())) {
                        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
                        javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem(
                                "Supprimer ce message");
                        deleteItem.setOnAction(ev -> {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText(null);
                            alert.setContentText("Voulez-vous supprimer ce message ?");
                            alert.showAndWait().ifPresent(res -> {
                                if (res == javafx.scene.control.ButtonType.OK) {
                                    mController.deleteMessage(msg);
                                }
                            });
                        });
                        contextMenu.getItems().add(deleteItem);
                        setContextMenu(contextMenu);
                    } else {
                        setContextMenu(null);
                    }
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
