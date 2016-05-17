

import java.net.*;
import java.io.*;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

 
public class EchoServer {
    public static void main(String[] args) throws IOException {
         
     
        int portNumber = 5541;
         
        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
            
            Socket clientSocket = serverSocket.accept();     
                              
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            
        ) {
            String query;
            while ((query = in.readLine()) != null) {           // (1) listen for client query

                String queryResults =  get(query);              // (2) get request to API , which returns query results
                String firstItem =  filter(queryResults);       // (3) filter first item 
                out.println(firstItem);                         // (4) print first item to output stream
            }

        } catch (Exception e) {
            System.out.println("Error1: " + e);
        }
    }



    public static String get(String query) {


        try {
                URL myURL = new URL("https://api.mercadolibre.com/sites/MLA/search?q=" + query );
                HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
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
    String link = "";
        
    try{
        Object obj = parser.parse(text); 
        JSONObject obj2 = (JSONObject)obj;
        JSONArray obj3 = (JSONArray)obj2.get("results");
        JSONObject obj4 = (JSONObject)obj3.get(0);
        link = obj4.get("permalink").toString();
      
      }catch(Exception e){
         System.out.println("Error4: " + e);
      }
      return link;
   }
}
