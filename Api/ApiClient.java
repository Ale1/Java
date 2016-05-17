// source: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java


import java.io.*;
import java.net.*;
 
public class EchoClient {
    public static void main(String[] args) throws Exception {
         
        String hostName = "localhost";
        int portNumber = 5541;
 
        try (
            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out =
                new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                System.out.println(in.readLine());
            }
        } catch (Exception e) {
            System.err.println("error:" + e);
            System.exit(1);
        } 
    }
}
