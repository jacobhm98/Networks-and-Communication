import java.net.*;
import java.io.*;

public class HTTPAsk {
    public static void main( String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);	//get portnumber from commandline
        ServerSocket welcomeSocket = new ServerSocket(port);	//create new socket with specified port

        while (true){
        	Socket connectionSocket = welcomeSocket.accept();	//accept new client
        	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));	//inputstream
        	DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());	//outputstream
        	String lineFromClient = inFromClient.readLine();	//first line read from client
        	StringBuilder tempResult = new StringBuilder();	//where we build our response
        	String headerMsg = inFromClient.readLine();
        	boolean error404 = false;
        	boolean error400 = false;
        	String host = null;
        	String destPort = null;
        	String string = null;

        	while(!lineFromClient.isEmpty()){	//while we are still receiving information
        		tempResult = tempResult.append(lineFromClient);	//add line to our string
        		tempResult = tempResult.append("\r\n");		//prep for next line
        		lineFromClient = inFromClient.readLine();	//read next line
        	}

        	try{
        		String[] request = headerMsg.split("/");
        		String[] filteredRequest = request[1].split(" ");
        		String finalRequest = filteredRequest[0];

        		if (finalRequest.contains("ask") && finalRequest.contains("hostname") && finalRequest.contains("port")){ //check if the request contains the necessary subparts
        			String[] newRequest = finalRequest.split("?"); 
        			String tempRequest = newRequest[1];
        			String[] arguments = tempRequest.split("&");
        			host = arguments[0];
        			destPort = arguments[1];
        			String[] tempHost = host.split("=");
        			host = tempHost[1];
        			String[] tempPort = destPort.split("=");
        			destPort = tempPort[1];

        			if(finalRequest.contains("string")){
        				String[] tempString = arguments[2].split("=");
                        string = tempString[1];
        			}

        		}
        		else{
        			error404 = true;

        		}
        }
        catch(Exception e){
        	error400 = true;
        }
        String clientResponse = null;
        String responseMessage;
        if(!error400 && !error404){
         	int portNumber = Integer.parseInt(destPort);
        	clientResponse = TCPClient.askServer(host, portNumber, string);
        }
        if(error404){
        	responseMessage = "HTTP/1.1 404 not found\r\n\r\n404 error";
        }
        if(error400){
        	responseMessage = "HTTP/1.1 400 bad request\r\n\r\n400 error";
        }
        else{
        	responseMessage = "HTTP/1.1 200 OK\r\n\r\n" + clientResponse;
        }
        outToClient.writeBytes(responseMessage);
        connectionSocket.close();	//close the socket and wait for new response
        }
    }
}

