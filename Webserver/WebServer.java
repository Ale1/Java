import java.io.*;
import java.net.*;
import java.util.*;


// USAGE:   go to "localhost:8000/<query>" in browser to make a Meli API search with query word.  
// EXAMPLE:   "localhost8000/ipod" will search MLA for all ipod items and return first 10 results. 


class WebServer{

    public static void main(String[] args) {
        ServerSocket serverSocket;
        int port_num = 8000;

        try {
            serverSocket = new ServerSocket(port_num); // create listening Server socket
            System.out.println("Server started. Listening to the port: " + port_num);


            while (true) {                                                  // always on listening for TCP connection requests

            Socket clientSocket = serverSocket.accept();                    // accept all incoming client connections
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(("<p> HELLO!</p>").getBytes());   

            BufferedReader in =  new BufferedReader (
                new InputStreamReader(clientSocket.getInputStream())        // parse incoming get request from client
            );
            parseParams(in.readLine()); // get first line of headers, and extract url query param (e.g "ipod"). 

                
            }          
        } catch (Exception e) {
            System.out.println("Could not listen on port: " + port_num + "because" + e);
        }      
    }

    public static void parseParams(String header){
        System.out.println(header.split(" ")[1].substring(1));       
    }


}