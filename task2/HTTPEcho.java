import java.net.*;
import java.io.*;

public class HTTPEcho {
    public static void main( String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);	//get portnumber from commandline
        ServerSocket welcomeSocket = new ServerSocket(port);	//create new socket with specified port

        while (true){
        	Socket connectionSocket = welcomeSocket.accept();	//accept new client
        	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));	//inputstream
        	DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());	//outputstream
        	String lineFromClient = inFromClient.readLine();	//first line read from client
        	StringBuilder tempResult = new StringBuilder();	//where we build our response

        	while(!lineFromClient.isEmpty()){	//while we are still receiving information
        		tempResult = tempResult.append(lineFromClient);	//add line to our string
        		tempResult = tempResult.append("\r\n");		//prep for next line
        		lineFromClient = inFromClient.readLine();	//read next line
        	}

        	String httpResponse = tempResult.toString();	//turn response into string
        	outToClient.writeBytes(httpResponse);	//write it to our client
        	connectionSocket.close();	//close the socket and wait for new response
        }
    }
}

