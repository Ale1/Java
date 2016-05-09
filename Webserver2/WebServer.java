
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
        int port_num = 8000;

        try {
            serverSocket = new ServerSocket(port_num); // create listening Server socket
            System.out.println("Server started. Listening to the port: " + port_num);

            while (true) {  // always on listening for TCP connection requests       
                ConnectionHandler thread = new ConnectionHandler(serverSocket.accept());
                thread.start();
            }
                

        } catch (IOException e) {
            System.out.println("Could not listen on port: " + port_num + " because " + e);
        }      
    }
}

class ConnectionHandler  extends Thread {
    Socket clientSocket;

    ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void run(){
        try{
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write (("<html><body><p> YOUR ITEMS</p>").getBytes());

            // list of meli country codes
            String[] countries = {"MLB", "MLA", "MCO","MLM"};
            List<CountryHandler> country_threads = new ArrayList<CountryHandler>();
            for(String country : countries){
                CountryHandler country_thread  = new CountryHandler(clientSocket, country);  // Make new thread for each request 
                country_thread.start();//and call it's run procedure
                country_threads.add( country_thread); // add to threads array, used later for join operation;
            } 

            // "join" makes it wait for all threads to complete.
            for (CountryHandler country_thread : country_threads) {
                try{country_thread.join();} catch(Exception e) { System.out.println("join error");}      
            }

            outputStream.write(("</td></table><p> LIST COMPLETE - HAVE A NICE DAY! </p></body></html>").getBytes());  
        }catch (Exception e){
            System.out.println("connection handler error");

        }       
    } 
}



class CountryHandler extends Thread {
    Socket clientSocket;
    String country;
    OutputStream outputStream;

    CountryHandler(Socket clientSocket, String country) {
        this.clientSocket = clientSocket;
        this.country = country;
        try{ this.outputStream = clientSocket.getOutputStream();}catch(Exception e){ System.out.println("woopsie");}
    }

    public void run() {

        //generate 10 requests with different offsets
        int[] offsets = {1,2,3,4,5,6,7,8,9,10};
        List<RequestHandler> threads = new ArrayList<RequestHandler>();

             
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
                        ("</td></table><p> finished all  " + country +" requests </p></body></html>").getBytes()
                    ); 
                }catch(Exception e) {System.out.println("woops");}         

    }
}




class RequestHandler extends Thread {
    
    Socket clientSocket;
    int offset;
    String country;

    RequestHandler(Socket clientSocket,  String country, int offset) {
        this.clientSocket = clientSocket;
        this.offset = offset;
        this.country = country;
                    
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
                URL myURL = new URL("https://api.mercadolibre.com/sites/"+country+ "/search?category="+country+"1648&limit=1&offset="+offset+"&attributes=results");
                HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                String responseBody = parseInputStream(inputStream); // inputstream to text
                String response_url = jsonParser(responseBody);  // text to json, then fetch url

           
                try { 
                    outputStream.write(("<p><a href=" + response_url +">item "+ country + offset + "</a></p>").getBytes());           
                    System.out.println("Done processing request (item "+ country + offset +")");
    
                }catch(Exception e) {
                    System.out.println("error processing request for item " + country + offset );
                }

        } 
        catch (MalformedURLException e) { 
                System.out.println("new URL() failed");
        } 
        catch (IOException e) {   
                System.out.println("openConnection() failed");
        }
    }

}

   


