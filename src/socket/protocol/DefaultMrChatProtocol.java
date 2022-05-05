package socket.protocol;

import socket.borsa.ChannelsManager;
import socket.borsa.ThreadChannel;
import socket.protocol.ChatProtocol;

import java.util.*;
import java.util.regex.*;

public class DefaultMrChatProtocol extends ChatProtocol {

    private Map<String, Command> commands;

    {
        commands = new HashMap<String, Command>();
        commands.put("user", new User());
        commands.put("view_list", new ViewList());
        commands.put("edit",  new Edit());
        commands.put("time", new Time());
        commands.put("view", new View());
        commands.put("quit", new Quit());
    }

    public DefaultMrChatProtocol(ChannelsManager manager) {
        super(manager);
    }

    public void parserMessage(ThreadChannel ch, String str) {
        if(str.charAt(0)!='/') {
            ch.send("Sintassi errata");
        } else {
            Pattern pattern = Pattern.compile("\\/([^\\s]+)\\s(.*)");
            Matcher match = pattern.matcher(str);
            match.find();
            String command = match.group(1);
            if (commands.containsKey(command)) {
                Command cmd = commands.get(command);
                cmd.execute(ch, match);
            } else {
                ch.send("Comando sconosciuto");
            }
        }
    }

    protected void broadcast(String name, String msg, boolean mysend) {
        Set <String> set =  manager.getAllName();
        for(String str : set) {
            if(!str.equalsIgnoreCase(name) || mysend) {
                ThreadChannel channel = manager.getChannel(str);
                if(channel.isLogin()) channel.send(msg);
            }
        }
    }

    public void startMessage(ThreadChannel ch) {
        ch.send("BORSA NAZIONALE STORGALLI" +
                "\nCOMANDI:" +
                "\n/user <nome> -> per accedere al canale" +
                "\n/view_list -> per vedere la lista completa delle azioni" +
                "\n/edit <nome_azione> -> per cambiare il valore" +
                "\n/view <nome_azione> -> per visualizzare il valore" +
                "\n/quit -> per uscire" +
                "\n************************************");
    }

    private class View implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            if(!channel.isLogin()) {
                String name= match.group(2);
                if (name.length()==0) {
                    channel.send("Sintassi del comando errata");
                } else if(manager.addChannel(name.toLowerCase(), channel) && name.length() <= 4) {
                    broadcast(name, name + ": " + valutazione(name), false);
                    channel.setName(name);
                    channel.setLogin(true);
                    channel.send(name + ": " + valutazione(name));
                } else {
                    channel.send("Nome dell'azione inserita non esistente o troppo lunga (deve essere di 4 caratteri)");
                }
            } else {
                channel.send("Nome non esistente");
            }
        }

        private float valutazione(String azione) {
            float value = 0.0f;
            if(azione.equalsIgnoreCase("appl"))
                value = 34.0f;
            else if(azione.equalsIgnoreCase("goog"))
                value = 21.32f;
            else if(azione.equalsIgnoreCase("msft"))
                value = 143.76f;
            else if(azione.equalsIgnoreCase("csco"))
                value = 12.75f;

            return value;
        }
    }

    private class Edit implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            if (channel.isLogin()) {
                broadcast(channel.getName().toLowerCase(),
                        "#"+channel.getName()+" "+match.group(2), true);
            } else {
                channel.send("Non ti sei ancora loggato");
            }
        }
    }

    private class Time implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            channel.send(new Date().toString());
        }
    }

    private class Quit implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            channel.send("By By");
            manager.removeChannel(channel.getName().toLowerCase());
            channel.closeChannel();
        }
    }

    private class ViewList implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            Set <String> names = manager.getAllName();
            if (names.isEmpty()) {
                channel.send("Nessun utente collegato");
            } else {
                channel.send("********** Lista delle azioni **********");
                for(String name:names)
                    channel.send(name);
            }
        }
    }

    private class User implements Command {
        public void execute(ThreadChannel channel, Matcher match) {
            if(!channel.isLogin()) {
                String name= match.group(2);
                if (name.length()==0) {
                    channel.send("Sintassi del comando errata");
                } else if(manager.addChannel(name.toLowerCase(), channel)) {
                    broadcast(name, "l'utente" + ": ", false);
                    channel.setName(name);
                    channel.setLogin(true);
                    channel.send(name + ": " );
                } else {
                    channel.send("Nome dell'azione inserita non esistente o troppo lunga (deve essere di 4 caratteri)");
                }
            } else {
                channel.send("Nome non esistente");
            }
        }
    }
}
