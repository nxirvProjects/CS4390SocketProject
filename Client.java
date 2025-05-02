import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        if(args.length > 0 && !args[0].isEmpty()){ // if no IP address is provided, use local host
            host = args[0];
        }
        String[] queries = { // for standby mode
                "3 + 4",
                "4 * 5",
                "20 / 5",
                "6 - 3",
                "7 + 3",
                "20 * 5",
                "3 * 8",
                "300 * 9",
                "100 / 10",
                " 28 * 56",
                "1999 * 999"

        };
        String[] names ={ // for standby mode
                "Steve",
                "Stevette",
                "Joe",
                "Jerry",
                "Sherry",
                "Karen",
                "Jeff",
                "Mary",
                "Lincoln",
                "Vishnu",
                "Martin",
                "Andrew"
        };

        try {
            Socket socket = new Socket(host, 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            // automatic mode
            if(name.equalsIgnoreCase("standby")){

                Random rand = new Random();
                int randomIndex = rand.nextInt(names.length);
                out.println(names[randomIndex]);
                System.out.println(in.readLine()); // Read ACK

                for (int i = 0; i < 5; i++) {
                    Thread.sleep(new Random().nextInt(3000) + 3000); // Random wait
                    System.out.print("Enter math operation (e.g., 3 + 4): ");
                    int randomexprIdx = rand.nextInt(queries.length);
                    String randomeexp = queries[randomexprIdx];
                    out.println(randomeexp);
                    System.out.println(in.readLine());
                }
                out.println("exit");

                socket.close();
                scanner.close();

            }else{
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

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
