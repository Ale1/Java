
import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;



class AsyncServer {
    public static void main( String[] args) {
		    ServerSocket serverSocket;
        int port = 8000;

        try{

            serverSocket = new ServerSocket(port);                                                   // create listening Server socket

            while (true){
         
                Thread thread = new Thread( new ConnectionHandler( serverSocket.accept() )  );       // handle multiple connections by creating thread for each client connection
                thread.start(); 
        	
            }
        
        }catch(Exception e){
            System.out.println("Error1:" + e);
        }
    }
}



class ConnectionHandler implements Runnable{
    private Socket clientSocket;

    ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
	

    public void run(){
        try{

            BufferedReader in =  new BufferedReader (
                new InputStreamReader(clientSocket.getInputStream())        			         // (1)    parse incoming get request from client
            );

            String query = parseParams(in.readLine());                        				     // (2)    get first line of headers, and extract url query param (e.g "ipod").
          	ArrayList<URL> queryURLS = makeQueryURLS(query);								               // (3)    build 10 query URL (one for each country)
          	for( URL queryURL: queryURLS){  												                       // (4) for each country create requesthandler thread 
          		  Thread thread = new Thread( new RequestHandler(clientSocket, queryURL));
          		  thread.start();																                             // (5) requestHandler does get request and writes response asynchronously     	
            }                           

		    }catch(Exception e){
            System.out.println("Error2:" + e);
        }
    }


    public static String parseParams(String header){
        return header.split(" ")[1].substring(1);    
    }


    public static ArrayList<URL> makeQueryURLS(String query){
        ArrayList<URL> queryURLS = new ArrayList<URL>();
        try{

	    	    String[] countries = {"MLA","MLB","MBO","MCO","MLC","MEC","MPY","MPE","MLU","MLV"};
	    	
	    	    for(String country : countries){
            queryURLS.add(new URL("https://api.mercadolibre.com/sites/" + country + "/search?q="+ query));
            }

        }catch(Exception e){ 
	    	  System.out.println("Error "+ e);
        }
    
        return queryURLS;

    }

}



class RequestHandler implements Runnable {
	Socket clientSocket;
	URL queryURL;

	RequestHandler(Socket clientSocket, URL queryURL){
		this.clientSocket = clientSocket;
		this.queryURL = queryURL;
	}


	public void run() {

		String item = filter(getRequest());												// (6) make get request and filter response
		System.out.println(item);
		htmlWriter(item); 																        // (7) write response to output stream

	}


	public String getRequest(){

      	try {
                HttpURLConnection connection = (HttpURLConnection)queryURL.openConnection();
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


    public String filter(String text){
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

    public void htmlWriter(String item){
        try{

            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(("<p><a href=" + item +">link </a></p>").getBytes());

        }catch(Exception e){
            System.out.println("Error3:" + e);
        }
    }
}


