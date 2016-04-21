import java.net.*;
import java.io.*;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


class Api{

  public static String execute (String[] args) {

    String responseBody = "poop";

    try {
        URL myURL = new URL("https://api.mercadolibre.com/sites/MLA/search?category=MLA1648&limit=1&attributes=results");
        HttpURLConnection connection = (HttpURLConnection)myURL.openConnection();
        connection.setRequestMethod("GET");

        // para que devuela el JSON, omitir estos para que devuelva HTML:
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        InputStream response = connection.getInputStream();
      

        try (Scanner scanner = new Scanner(response)) {
          responseBody = scanner.useDelimiter("\n").next();
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
    
    return responseBody;
  } 
}




public class SimpleHttpServer {

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/listado", new ListHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  static class ListHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      Api myApi = new Api(); 
      String apiResponse = "<html><body>" + myApi.execute(null) + "</body></html>";

      t.sendResponseHeaders(200, apiResponse.getBytes().length);
      OutputStream output = t.getResponseBody();
      output.write(apiResponse.getBytes());
      output.close();
    }
  }
}








