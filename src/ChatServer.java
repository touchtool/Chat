import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends JFrame {
    public static final int PORT = 5455;
    private Server server;
    private JTextArea screen;
    private String name;
    private String msg;
    private List<Message> chatHistory;

    public  ChatServer(){
        server = new Server();
        server.getKryo().register(Message.class);
        server.addListener(new Listener(){

            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (object instanceof Message) {
                    Message message = (Message) object;
                    msg = message.text;
                    name = message.senderName;
                    chatHistory = new ArrayList<Message>();
                    chatHistory.add(message);
                    screen.append("Receive message " + message.text + " from " + message.senderName + "\n");
                    server.sendToAllTCP(message);
//                    chatHistory = new ArrayList<String>();
//                    chatHistory.add(message.senderName + " say " + message.text + "\n");
                }
            }

            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                screen.append("New client connected! \n");
                if (chatHistory != null) {
                    if (chatHistory.size() != 0) {
                        for (int i = 0; i < chatHistory.size(); i++) {
//                            server.sendToAllTCP(chatHistory.get(i));
                            server.sendToTCP(connection.getID(), chatHistory.get(i));
                        }
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                screen.append("Client disconnected. \n");
            }
        });
        initGuis();
    }

    public void start(){
        setVisible(true);
        screen.append("Server started \n");

        server.start();
        try {
            server.bind(PORT);
        } catch (IOException e) {
            screen.append("Cannot bind to the port \n");
            e.printStackTrace();
        }
    }
    public void initGuis(){
        setTitle("Server Screen");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        screen = new JTextArea();
        screen.setPreferredSize(new Dimension(480, 480));
        screen.setBackground(Color.black);
        screen.setForeground(Color.GREEN);
        screen.setEditable(false);
        add(screen);
        pack();
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
