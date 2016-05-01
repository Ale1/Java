
import java.io.*;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
 

public class ApiServer {


    public static String get_url(String text){
    
      JSONParser parser = new JSONParser();
      String result = "crap";
        
      try{
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


    public static String fetch(String arg) {

        String return_string = "nothing"; 

        try {
                URL myURL = new URL("https://api.mercadolibre.com/sites/MLA/search?category=MLA1648&limit=1&attributes=results");
                HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                InputStream response = connection.getInputStream();

                try (Scanner scanner = new Scanner(response)) {
                    String responseBody = scanner.useDelimiter("\\A").next();

                    return_string = get_url(responseBody);
                }

        } 
        catch (MalformedURLException e) { 
                // new URL() failed
                System.out.println("woopsie");
        } 
        catch (IOException e) {   
                // openConnection() failed
                System.out.println("Woopsie doopsie");
        }
        return return_string;
    }



    public static void main(String[] args) throws IOException {
         
     
        int portNumber = 5541;
         
        try (
            ServerSocket serverSocket =
                new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();     
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String item_url = ApiServer.fetch("hello");
            out.println(item_url);
            
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }




}


