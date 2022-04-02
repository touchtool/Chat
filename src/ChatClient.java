import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class ChatClient extends JFrame {
    public static final int PORT = 5455;
    public static final String IP = "127.0.0.1";
    private JTextField inputName;
    private JTextArea messagePane;
    private JTextField inputMessage;

    private Client client;
    public ChatClient(){
        client = new Client();
        client.getKryo().register(Message.class);
        client.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (object instanceof Message) {
                    Message message = (Message) object;
                    messagePane.append(message.senderName + " say " + message.text + "\n");
                }
            }
        });
        initGuis();
    }

    public void start(){
        client.start();
        try {
            client.connect(5000, IP, PORT);
        } catch (IOException e) {
            messagePane.append("Cannot connect to the server!");
            e.printStackTrace();
        }

        setVisible(true);

    }
    public void initGuis(){
        setTitle("Chat Client");
        inputName = new JTextField("No Name");
        messagePane = new JTextArea();
        messagePane.setPreferredSize(new Dimension(300, 300));
        messagePane.setEditable(false);
        messagePane.setBackground(Color.black);
        messagePane.setForeground(Color.GREEN);
        inputMessage = new JTextField();
        inputMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String msg = inputMessage.getText();
                    Message message = new Message();
                    message.text = msg;
                    message.senderName = inputName.getText();
                    client.sendTCP(message);
                    System.out.println(msg);
                    inputMessage.setText("");
                }
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(inputName, BorderLayout.NORTH);
        add(messagePane, BorderLayout.CENTER);
        add(inputMessage, BorderLayout.SOUTH);
        pack();
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
