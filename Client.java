import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            out.println(name);
            System.out.println(in.readLine()); // Read ACK

            // Send 3 math operations
            for (int i = 0; i < 3; i++) {
                Thread.sleep(new Random().nextInt(3000) + 1000); // Random wait
                System.out.print("Enter math operation (e.g., 3 + 4): ");
                String expr = scanner.nextLine();
                out.println(expr);
                System.out.println(in.readLine());
            }

            // Optionally allow user to type 'exit'
            while (true) {
                System.out.print("Send more? (y/n): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("n")) {
                    out.println("exit");
                    break;
                }
                else if (!choice.equalsIgnoreCase("y")) {
                        System.out.println("Invalid input. Please enter 'y' or 'n'");
                        continue;
                } 
                System.out.print("Enter math operation: ");
                String expr = scanner.nextLine();
                out.println(expr);
                System.out.println(in.readLine());
            }

            socket.close();
            scanner.close();
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
