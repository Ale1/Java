
import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
 
// DESCRIPTION
// an improved web server that asynchronously fetches 10 items from different meli country sites using the API. 

class WebServer{

    public static void main(String[] args) {
        ServerSocket serverSocket;
        int port_num = 8080;

        try {
            serverSocket = new ServerSocket(port_num); // create listening Server socket
            System.out.println("Server started. Listening to the port: " + port_num);

            while (true) {  // always on listening for TCP connection requests
                // each accepted connection starts a new  ConnectionHandler thread:       
                ConnectionHandler thread = new ConnectionHandler(serverSocket.accept());
                thread.start();
            }
                
        } catch (Exception e) {
            System.out.println("Could not listen on port: " + port_num + " because " + e);
        }      
    }
}

class ConnectionHandler  extends Thread {
    private Socket clientSocket;

    ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void run(){
        try{
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write (("<html><body><p> YOUR ITEMS</p>").getBytes());

            // list of meli country codes
            String[] countries = {"MLB", "MLA", "MCO"};
            List<CountryHandler> country_threads = new ArrayList<CountryHandler>();
            for(String country : countries){
                // each ConnectionHandler creates individual CountryHandler threads for each country:
                CountryHandler country_thread  = new CountryHandler(clientSocket, country);  // Make new thread for each request 
                country_thread.start();//and call it's run procedure
                country_threads.add( country_thread); // add to threads array, used later for join operation;
            } 

            // "join" makes it wait for all threads to complete.
            for (CountryHandler country_thread : country_threads) {
                try{country_thread.join();} catch(Exception e) { System.out.println("join error");}      
            }

            outputStream.write(("<p> LIST COMPLETE - HAVE A NICE DAY! </p></body></html>").getBytes());
            outputStream.close();
 
        }catch (Exception e){
            System.out.println("connection handler error");

        }       
    } 
}


class CountryHandler extends Thread {
    private Socket clientSocket;
    private String country;
    private OutputStream outputStream;

    CountryHandler(Socket clientSocket, String country) {
        this.clientSocket = clientSocket;
        this.country = country;
        try{ this.outputStream = clientSocket.getOutputStream();}catch(Exception e){ System.out.println("woopsie");}
    }

    public void run() {

        //generate 10 requests with different offsets
        int[] offsets = {1,2,3,4,5,6,7,8,9,10};
        List<RequestHandler> threads = new ArrayList<RequestHandler>();

        //each countryHandler thread creates a new thread for each of the 10 requests:   
        for(int x : offsets){
            RequestHandler thread  = new RequestHandler(clientSocket, country, x);  // Make new thread for each request 
            thread.start();//and call it's run procedure
            threads.add( thread); // add to threads array, used later for join operation;
        } 


        //  makes it wait for all offset threads to complete.
            for (RequestHandler thread : threads) {
                  try{thread.join();} catch(Exception e) { System.out.println("join error");}      
            }

            try{ this.outputStream = clientSocket.getOutputStream();
                    outputStream.write(
                        ("<p> finished all  " + country +" requests </p></body></html>").getBytes()
                    ); 
            }catch(Exception e) {System.out.println("woops: " + e);}         

    }
}


class RequestHandler extends Thread {
    private Socket clientSocket;
    private int offset;
    private String country;

    RequestHandler(Socket clientSocket,  String country, int offset) {
        this.clientSocket = clientSocket;
        this.offset = offset;
        this.country = country;                 
    }


    private void htmlWriter(String response_url) {

        try { 
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(("<p><a href=" + response_url +">item "+ country + offset + "</a></p>").getBytes());           
    
        }
        catch(Exception e) {
            System.out.println("error processing request for item " + country + offset + " : " + e );
        }
    }


    public void run() {

        try {
                URL myURL = new URL("https://api.mercadolibre.com/sites/"+country+ "/search?category="+country+"1648&limit=1&offset="+offset+"&attributes=results");
                HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                InputStream inputStream = connection.getInputStream();

                String responseBody = ServerUtils.parseInputStream(inputStream); // inputstream to text
                String response_url = ServerUtils.jsonParser(responseBody);  // text to json, then fetch url

                htmlWriter(response_url); // write result to output stream

        } 
        catch (Exception e) {   
                System.out.println("GET request failed: " + e);
        }
    }
}


abstract class ServerUtils {

    public static String jsonParser(String text){
    
        JSONParser parser = new JSONParser();
        String result = "";
        
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


    public static String parseInputStream(InputStream stream) {

        String parsedStream;
        try (Scanner scanner = new Scanner(stream)) {
                parsedStream = scanner.useDelimiter("\\A").next();
        }
        return parsedStream;
   }


}



   


