import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUIClient {
    private JFrame frame;
    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton, exitButton, emojiButton;
    private StyledDocument doc;
    private PrintWriter out;
    private BufferedReader br;
    private Socket socket;
    private String nickname;

    public GUIClient(String nickname) {
        this.nickname = nickname;
        createGUI();
        connectToServer();
        startReading();
    }

    private void createGUI() {
        frame = new JFrame("Chat Client - " + nickname);
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        exitButton = new JButton("Exit");
        emojiButton = new JButton("Emoji");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(emojiButton);
        buttonPanel.add(sendButton);
        buttonPanel.add(exitButton);

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        exitButton.addActionListener(e -> closeConnection());
        emojiButton.addActionListener(e -> showEmojiPicker());

        frame.setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 7777);
            out = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            appendMessage("[System] Connected to server.", Color.GRAY);
        } catch (Exception e) {
            showError("Unable to connect to server.");
        }
    }

    private void startReading() {
        Thread readThread = new Thread(() -> {
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    appendReceivedMessage(msg);
                }
            } catch (IOException e) {
                appendMessage("[System] Disconnected.", Color.RED);
            }
        });
        readThread.start();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String time = new SimpleDateFormat("HH:mm").format(new Date());
            String formatted = nickname + " [" + time + "]: " + text;
            out.println(formatted);  // Send the message with timestamp
            appendMessage(formatted, Color.BLUE);  // Display sent message with timestamp
            inputField.setText("");
        }
    }

    private void appendMessage(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                Style style = doc.addStyle("", null);
                StyleConstants.setForeground(style, color);
                doc.insertString(doc.getLength(), message + "\n", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    private void appendReceivedMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                String time = new SimpleDateFormat("HH:mm").format(new Date());
                String formattedMessage = "[Server][" + time + "]: " + message;
                appendMessage(formattedMessage, Color.BLACK);  // Display received message with timestamp
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Emoji Picker
    private void showEmojiPicker() {
        JPopupMenu emojiMenu = new JPopupMenu();
        String[] emojis = { "😊", "😂", "❤️", "👍", "😢", "😎", "🎉" };

        // Add each emoji to the menu
        for (String emoji : emojis) {
            JMenuItem item = new JMenuItem(emoji);
            item.addActionListener(e -> inputField.setText(inputField.getText() + emoji));  // Append emoji to input field
            emojiMenu.add(item);
        }

        // Show the emoji menu near the emoji button
        emojiMenu.show(emojiButton, 0, emojiButton.getHeight());
    }

    private void closeConnection() {
        try {
            if (out != null) out.println(nickname + " has left the chat.");
            if (socket != null && !socket.isClosed()) socket.close();
            frame.dispose();
        } catch (IOException e) {
            showError("Error closing connection.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        String nickname = JOptionPane.showInputDialog(null, "Enter your nickname:", "Welcome", JOptionPane.PLAIN_MESSAGE);
        if (nickname != null && !nickname.trim().isEmpty()) {
            new GUIClient(nickname.trim());
        } else {
            JOptionPane.showMessageDialog(null, "Nickname required!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
