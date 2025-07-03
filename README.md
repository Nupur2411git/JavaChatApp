# 💬 Java Chat Application with GUI

This is a simple Java-based chat application that supports both **console-based** and **GUI-based** (Swing) client-server messaging. It demonstrates the use of Java Socket Programming along with Java Swing for UI components.

---

## 🚀 Features

- 📡 Real-time two-way communication between server and client
- 🖥️ GUI interface for both client and server (Swing-based)
- 📜 Chat history saved to a text file
- 🔌 Uses TCP sockets for reliable communication
- 💡 Easy to switch between GUI and console versions

---

## 🛠️ Technologies Used

- Java
- Java Sockets (Networking)
- Java Swing (GUI)
- File I/O (for chat history)

---

## 🏗️ Project Structure

JavaChatApp/
├── src/
│ ├── Client.java # Console-based client
│ ├── Server.java # Console-based server
│ ├── GUIClient.java # GUI-based client using Swing
│ └── GUIServer.java # GUI-based server using Swing
├── chat_history_server.txt # Text file for storing chat logs
└── LICENSE (optional)

## ▶️ How to Run

### Step 1: Compile the Code

```bash
cd src
javac *.java
```
### Step 2: Run the Server
```bash
java Server          # Run console version
java GUIServer       # Run GUI version
```
### Step 3: Run the Client
```bash
java Client          # Run console version
java GUIClient       # Run GUI version
```
✅ Make sure to start the server before connecting with the client.

### 📄 License
This project is licensed under the MIT License.

### ✨ Author
Nupur – Nupur2411git

### 🙌 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check issues page if you want to contribute.
