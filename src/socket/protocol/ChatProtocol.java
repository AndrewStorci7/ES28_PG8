package socket.protocol;

import socket.borsa.ChannelsManager;
import socket.borsa.ThreadChannel;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public abstract class ChatProtocol {
    protected ChannelsManager manager;
    public ChatProtocol(ChannelsManager manager) {
        this.manager = manager;
        manager.setChatProtocol(this);
    }
    public abstract void startMessage(ThreadChannel ch);
    public abstract void parserMessage(ThreadChannel channel, String msg);
}
