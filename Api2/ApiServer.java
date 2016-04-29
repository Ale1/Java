
import java.io.*;
import java.util.Scanner;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
 


class ApiServer{

    public static void main(String[] args) {
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(5541); // Server socket
            System.out.println("Server started. Listening to the port 5541");

            while (true) {
                // Always accept new clients
                Socket clientSocket = serverSocket.accept(); 
                new RequestHandler(clientSocket).start(); // Make a new thread and call it's run procedure
            } 

        } catch (IOException e) {
            System.out.println("Could not listen on port: 5541");
        }      
    }
}

   


class RequestHandler extends Thread {
    Socket clientSocket; 

    RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


     public static String jsonParser(String text){
    
        JSONParser parser = new JSONParser();
        String result = " error";
        
        try{
            // parsing and fetching of permalink value in response JSON
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



   static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }



    public void run() {

        try {
                URL myURL = new URL("https://api.mercadolibre.com/sites/MLA/search?category=MLA1648&limit=1&attributes=results");
                HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                String responseBody = "nothing";

           
                try {
                    try (Scanner scanner = new Scanner(inputStream)) {
                    responseBody = scanner.useDelimiter("\\A").next();
                    }
                     
                    outputStream.write(("HTTP/1.1 200 OK\n\n<html><body><a href=" + jsonParser(responseBody) +">item1</a></body></html>").getBytes());           
                    inputStream.close();
    
                }catch(Exception e) {
                    System.out.println("uh oh");
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

   


