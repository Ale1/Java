// source: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java


import java.io.*;
import java.net.*;
 
public class ApiClient {
    public static void main(String[] args) throws IOException {
         
 
        String hostName = "localhost";
        int portNumber = 5541;
 
        try (
            Socket apiSocket = new Socket(hostName, portNumber);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(apiSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in))
        ) {
                System.out.println( in.readLine());
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }

}