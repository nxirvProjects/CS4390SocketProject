import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    private static final int PORT = 12345; //New server with port #
    private static final List<ClientSession> clients = Collections.synchronizedList(new ArrayList<>()); // List of client sessions
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Math Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public static synchronized void addClient(ClientSession session) {
        clients.add(session);
    }

    // Basically just removes a user when they disconnect the session
    public static synchronized void removeClient(String name) {
        clients.removeIf(client -> client.name.equals(name));
    }

    public static void printActiveClients() {
        System.out.println("---- ACTIVE CLIENTS ----");
        for (ClientSession session : clients) {
            long duration = (System.currentTimeMillis() - session.connectTime) / 1000;
            System.out.println("Name: " + session.name + " | Connected for: " + duration + " seconds");
        }
        System.out.println("------------------------");
    }

    static class ClientSession {
        String name;
        Socket socket;
        long connectTime;

        ClientSession(String name, Socket socket, long connectTime) {
            this.name = name;
            this.socket = socket;
            this.connectTime = connectTime;
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String clientName;
    private BufferedReader in;
    private PrintWriter out;
    private long connectTime;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            clientName = in.readLine();
            connectTime = System.currentTimeMillis();
            Server.addClient(new Server.ClientSession(clientName, clientSocket, connectTime));
            log("CONNECTED");
            out.println("ACK: Connected to Math Server as " + clientName);

            String input;
            while ((input = in.readLine()) != null) {
                if (input.equalsIgnoreCase("exit")) {
                    log("DISCONNECTED");
                    break;
                }
                try {
                    int result = evaluate(input);
                    out.println("Result: " + result);
                    log("REQUEST: " + input);
                } catch (Exception e) {
                    out.println("Invalid input. Try again.");
                    log("INVALID REQUEST: " + input);
                }
            }
        } catch (IOException e) {
            log("ERROR: Connection reset or IO issue");
        } finally {
            try {
                if (clientName != null) {
                    Server.removeClient(clientName);
                    log("SESSION CLOSED. Duration: " + getDuration() + " seconds");
                }
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String msg) {
        String timestamp = Server.sdf.format(new Date());
        System.out.println("[" + timestamp + "] [" + clientName + "] " + msg);
    }

    private long getDuration() {
        return (System.currentTimeMillis() - connectTime) / 1000;
    }

    // Takes in a string expression and evaluates the expression. It also recognizes of a string has spaces, and if it does gets rid of the spaces then finds the operation.
    private int evaluate(String expression) throws Exception {
        // Remove all spaces from the input
        expression = expression.replaceAll("\\s+", "");
    
        String[] tokens;
        String operator;
    
        // Detect the operator and split the operands
        if (expression.contains("+")) {
            tokens = expression.split("\\+");
            operator = "+";
        } else if (expression.contains("-")) {
            tokens = expression.split("-", 2); // handle negative numbers correctly
            operator = "-";
        } else if (expression.contains("*")) {
            tokens = expression.split("\\*");
            operator = "*";
        } else if (expression.contains("/")) {
            tokens = expression.split("/");
            operator = "/";
        } else {
            throw new IllegalArgumentException("Invalid operator");
        }
    
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Invalid format: must be two operands and one operator");
        }
    
        int a = Integer.parseInt(tokens[0]);
        int b = Integer.parseInt(tokens[1]);
    
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b; // integer division
            default:
                throw new IllegalArgumentException("Unknown operator");
        }
    }
}
