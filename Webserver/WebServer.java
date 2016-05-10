
import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
 

class WebServer{

    public static void main(String[] args) {
        ServerSocket serverSocket;
        int port_num = 8080;

        try {
            serverSocket = new ServerSocket(port_num); // create listening Server socket
            System.out.println("Server started. Listening to the port: " + port_num);


            while (true) {  // always on listening for TCP connection requests
                ConnectionHandler thread = new ConnectionHandler(serverSocket.accept()); // start thread to handle client
                System.out.println("accepted connection");
                thread.start();
            }          

        } catch (IOException e) {
            System.out.println("Could not listen on port: " + port_num + "because" + e);
        }      
    }
}


class ConnectionHandler extends Thread {
    Socket clientSocket;

    ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    public void run() {
        try{
            System.out.println("processing client connection requests...");
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write (("<html><body><p> YOUR MLA ITEMS</p>").getBytes());

            //generate 10 requests with different offsets 
            int[] offsets = {1,2,3,4,5,6,7,8,9,10};
            List<RequestHandler> threads = new ArrayList<RequestHandler>();

            for(int x : offsets){
                RequestHandler thread  = new RequestHandler(clientSocket, x);  // Make new thread for each item request 
                thread.start();//and call it's run procedure
                threads.add(thread); // add to threads array, used later for join operation;
            } 
             // "join" makes it wait for all threads to complete.
            for (RequestHandler thread : threads) {
                try{
                    thread.join();
                } 
                catch(Exception e) { 
                    System.out.println("join error");
                }  
            }
            
            outputStream.write(("</td></table><p> FINISHED MLA</p></body></html>").getBytes());
            outputStream.close();


        }
        catch(Exception e) {
            System.out.println("error with connection handler");
        }
    }  
} 


class RequestHandler extends Thread {
    
    Socket clientSocket;
    int offset;

    RequestHandler(Socket clientSocket,  int offset) {
        this.clientSocket = clientSocket;
        this.offset = offset;
                    
    }


    public void htmlWriter(String response_url) {

        try { 
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(("<p><a href=" + response_url +">item "+ offset + "</a></p>").getBytes());           
            System.out.println("Done processing request (item "+ offset +")");
    
        }
        catch(Exception e) {
            System.out.println("error processing request for item " + offset + " : " + e );
        }
    }


    public static String jsonParser(String text){
    
        JSONParser parser = new JSONParser();
        String result = " error";
        
        try{
            // parsing and fetching of permalink value in response JSON
            // note: funciona pero es muy feo esto.  debe haber una forma mas directa.  
            Object obj = parser.parse(text); 
            JSONObject obj2 = (JSONObject)obj;
            JSONArray obj3 = (JSONArray)obj2.get("results");
            JSONObject obj4 = (JSONObject)obj3.get(0);
            result = obj4.get("permalink").toString();
      
        }catch(ParseException pe){
            System.out.println("Parse error");
        }
        return result;
   }


   public String parseInputStream(InputStream stream) {

        String parsedStream;
        try (Scanner scanner = new Scanner(stream)) {
                parsedStream = scanner.useDelimiter("\\A").next();
        }
        return parsedStream;
   }


    public void run() {

        System.out.println("new thread processing request on server (item: " + offset + ")");

        try {
                URL myURL = new URL("https://api.mercadolibre.com/sites/MLA/search?category=MLA1648&limit=1&offset="+offset+"&attributes=results");
                HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                InputStream inputStream = connection.getInputStream();

                String responseBody = parseInputStream(inputStream); // inputstream to text
                String response_url = jsonParser(responseBody);  // text to json, then fetch url

                htmlWriter(response_url); // write response to outputStream


        } 
        catch (MalformedURLException e) { 
                System.out.println("new URL() failed");
        } 
        catch (IOException e) {   
                System.out.println("openConnection() failed");
        }
    }

}

   


