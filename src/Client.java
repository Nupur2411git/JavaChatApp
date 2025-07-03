import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    Socket socket;
    BufferedReader br;  // For reading messages from the server
    PrintWriter out;    // For sending messages to the server

    public Client() {
        try {
            // Connect to the server at localhost on port 7777
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connected to the server.");

            // Initialize the BufferedReader and PrintWriter for reading and writing to the socket
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);  // Enable auto-flushing

            // Start reading and writing threads
            startReading();
            startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reading messages from the server
    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    if (socket.isClosed()) {
                        System.out.println("Connection Closed..Reader Stopped.");
                        break;  // Exit the loop if socket is closed
                    }

                    String msg = br.readLine();  // Read the incoming message from the server
                    if (msg == null || msg.equals("exit")) { // Check for exit condition
                        System.out.println("Server terminated the chat.");
                        break;
                    }
                    System.out.println("Server: " + msg);  // Print the received message
                }
            } catch (Exception e) {
                System.out.println("Connection Closed..Reader Stopped.");
            } finally {
                try {
                    if (br != null) {
                        br.close();  // Close the BufferedReader
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // Close the socket if it's not already closed
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r1).start(); // Start the reading thread
    }

    // Writing messages to the server
    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in)); // For reading user input
                while (true) {
                    String content = userInputReader.readLine();  // Read the user input
                    if (socket.isClosed()) {
                        System.out.println("Connection closed, stopping writer...");
                        break;
                    }
                    out.println(content);  // Send the message to the server

                    if (content.equals("exit")) {  // If the user types "exit", close the connection
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();  // Close the PrintWriter
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // Close the socket if it's not already closed
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start(); // Start the writing thread
    }

    public static void main(String[] args) {
        new Client(); // Create a new client instance and start the communication
    }
}
