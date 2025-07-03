import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GUIServer extends JFrame {
    private ServerSocket server;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton emojiButton;
    private JButton exitButton;

    private final String HISTORY_FILE = "chat_history_server.txt";

    public GUIServer() {
        setTitle("Server Chat Application");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set up chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Set up message input area and buttons
        messageField = new JTextField();
        sendButton = new JButton("Send");
        emojiButton = new JButton("😊");
        exitButton = new JButton("Exit");

        // Set up bottom panel to contain message input and buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);

        // Set up button panel for emoji, send, and exit buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(emojiButton);
        buttonPanel.add(sendButton);
        buttonPanel.add(exitButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Add components to the main frame
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        sendButton.addActionListener(e -> sendMessage());
        emojiButton.addActionListener(e -> showEmojiPicker());
        exitButton.addActionListener(e -> exitServer());

        setVisible(true);
        setupServer();
    }

    private void setupServer() {
        try {
            server = new ServerSocket(7777);
            appendToChat("Server is waiting for connection...");
            socket = server.accept();
            appendToChat("Client connected!");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::startReading).start();
        } catch (IOException e) {
            appendToChat("Error setting up server: " + e.getMessage());
        }
    }

    private void startReading() {
        try {
            String msg;
            while ((msg = br.readLine()) != null) {
                String formattedMsg = "Client: " + msg;
                appendToChat(formattedMsg);
                saveToHistory(formattedMsg);
            }
        } catch (IOException e) {
            appendToChat("Connection closed.");
        }
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);
            String formattedMsg = "Me: " + msg;
            appendToChat(formattedMsg);
            saveToHistory(formattedMsg);
            messageField.setText("");
        }
    }

    private void appendToChat(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        chatArea.append("[" + timestamp + "] " + message + "\n");
    }

    private void showEmojiPicker() {
        JPopupMenu emojiMenu = new JPopupMenu();
        String[] emojis = { "😊", "😂", "❤️", "👍", "😢", "😎", "🎉" };

        for (String emoji : emojis) {
            JMenuItem item = new JMenuItem(emoji);
            item.addActionListener(e -> messageField.setText(messageField.getText() + emoji));
            emojiMenu.add(item);
        }

        emojiMenu.show(emojiButton, 0, emojiButton.getHeight());
    }

    private void saveToHistory(String message) {
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exitServer() {
        try {
            if (br != null) br.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (server != null) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        appendToChat("Server has been closed.");
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIServer::new);
    }
}
