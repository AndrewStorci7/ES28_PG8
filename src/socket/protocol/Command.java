package socket.protocol;

import socket.borsa.ThreadChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Command {
    public void execute(ThreadChannel channel, Matcher match);
}
