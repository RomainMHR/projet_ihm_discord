package main.java.com.ubo.tp.message.ihm.interfaces;

import java.util.Set;
import main.java.com.ubo.tp.message.datamodel.Channel;

public interface IChannelListView {
    void updateChannelList(Set<Channel> channels);
}
