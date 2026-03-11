package main.java.com.ubo.tp.message.ihm.interfaces;

import java.util.List;
import main.java.com.ubo.tp.message.datamodel.Message;

public interface IMessageListView {
    void updateMessageList(List<Message> messages);
}
