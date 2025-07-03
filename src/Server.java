import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connections...");
            socket = server.accept();
            System.out.println("Client connected...");

            // Initialize the reader and writer for communication
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);  // true enables auto-flush

            // Start reading and writing in separate threads
            startReading();
            startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method for reading from the client
    public void startReading() {
        Runnable r1 = () -> {
            try {
                String msg;
                while (true) {
                    if (socket.isClosed()) {
                        System.out.println("Socket closed, stopping reader...");
                        break;  // Exit the loop if socket is closed
                    }

                    // Read the message from the client
                    msg = br.readLine();
                    if (msg == null || msg.equals("exit")) {
                        System.out.println("Client terminated the chat.");
                        break;  // Exit the loop if client sends "exit"
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (Exception e) {
               
                 System.out.println("Connection Closed. Reader Closed");
                  
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // Ensure socket is closed after reading finishes
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }

    // Method for writing to the client
    public void startWriting() {
        Runnable r2 = () -> {
            try {
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String content = userInputReader.readLine();
                    if (socket.isClosed()) {
                        System.out.println("Connection closed. Writer closed.");
                        break;  // Exit the loop if socket is closed
                    }

                    // Send message to the client
                    out.println(content);
                    if (content.equals("exit")) {
                        socket.close();  // Close the socket if client sends "exit"
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();  // Ensure socket is closed after writing finishes
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        new Server();  // Start the server
    }
}
