import java.io.*;
import java.net.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


// USAGE EXAMPLE:  go to  "localhost8000/ipod" in browser to search MLA for all ipod items and return first result. 


class WebServer{

    public static void main(String[] args) {
        ServerSocket serverSocket;
        int port_num = 8000;
        String baseURL = "https://api.mercadolibre.com/sites/MLA/search?q=";

        try {
            serverSocket = new ServerSocket(port_num); // create listening Server socket
            System.out.println("Server started. Listening to the port: " + port_num);


            while (true) {                                                                     // (0) always on listening for TCP connection requests

                Socket clientSocket = serverSocket.accept();                                   // (1)    accept incoming client connection
                OutputStream outputStream = clientSocket.getOutputStream();  

                BufferedReader in =  new BufferedReader (
                    new InputStreamReader(clientSocket.getInputStream())                       // (2)    parse incoming get request from client
                );
                String query = parseParams(in.readLine());                                     // (3)    get first line of headers, and extract url query param (e.g "ipod").
                URL queryURL = new URL(baseURL + query);                                       // (4)    build query URL
                String response = getRequest(queryURL);                                        // (5)    execute get request to MELI API with query param
                String item = filter(response);                                                // (6)    filter response to get 1 item
                outputStream.write(("<p><a href=" + item +">link </a></p>").getBytes());       // (7)    write response to output stream
                
            }          
        } catch (Exception e) {
            System.out.println("Could not listen on port: " + port_num + "because" + e);
        }      
    }

    public static String parseParams(String header){
        return header.split(" ")[1].substring(1);    
    }


   public static String getRequest(URL query) {


        try {
                HttpURLConnection connection = (HttpURLConnection)query.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                InputStream response = connection.getInputStream();
                return streamToText(response); 

        } 
        catch (Exception e) { 
                System.out.println("Error3: " + e);
        }
        return null; 
    }

    public static String streamToText(InputStream stream){
        String text;
        try (Scanner scanner = new Scanner(stream)) 
        {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;

    }

    public static String filter(String text){
        JSONParser parser = new JSONParser();
        String item = "";
        
        try{
            Object obj = parser.parse(text); 
            JSONObject obj2 = (JSONObject)obj;
            JSONArray obj3 = (JSONArray)obj2.get("results");
            JSONObject obj4 = (JSONObject)obj3.get(0);
            item = obj4.get("permalink").toString();
      
        }catch(Exception e){
             System.out.println("Error4: " + e);
        }
    return item;
   }


}