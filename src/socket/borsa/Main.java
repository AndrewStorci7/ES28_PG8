package socket.borsa;

import socket.protocol.DefaultMrChatProtocol;
import java.io.IOException;

public class Main {
    public static void main(String [] argv) throws IOException {
        ChannelsManager manager = new ChannelsManager();
        DefaultMrChatProtocol protocol = new DefaultMrChatProtocol(manager);
        MrChatServer server = new MrChatServer(10000, manager);
        server.start();
    }
}
