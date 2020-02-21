import java.net.*;
import java.io.*;

/* A class which implements an HTTP server that accepts GET requests, extracts the 
 */

public class HTTPAsk {
	public static void main( String[] args) throws IOException {
		int listenPort = Integer.parseInt(args[0]);	//get portnumber from commandline
		ServerSocket welcomeSocket = new ServerSocket(listenPort);	//create new socket with specified port
		
		//Wait for, accept tcp connections, and process HTTP GET requests
		while (true){
			//Datastructures needed for generating response
			String queryResponse;	//if the request is of the right form, this is what we get from the server we send a query to using TCPAsk
			String host = null;	//When we extract the hostname, this is where we will store it
			String port = null;	//store port here
			String data = null;

			//Logic to determine if we need to generate error message
			boolean err400 = false;
			boolean err404 = false;

			try {
				//Accept connection from client, and set up input and output streams for interacting with the client
        			Socket connectionSocket = welcomeSocket.accept();
        			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				
				//Read the request message	
				String readLine = inFromClient.readLine();
				String httpHeader = readLine;

				//discard the rest of the message, we are only interested in dissecting the URI
				//which is located in the header of the GET Request
				while (!readLine.isEmpty()){
					readLine = inFromClient.readLine();
				}
				try{
					//split the request by /, will hopefully split it into 3 parts p0: "GET " p1: "$URI HTTP" p3: "1.1"
					String[] headerParts = httpHeader.split("/");
					String p1 = headerParts[1];
					//split p1 by space, the first part of p1 contains the URI which is what we are after
					headerParts = p1.split(" ");
					String URI = headerParts[0];
					
					//If the URI contains the necessary information our server needs to set up a tcp connection
					//as specified by the task instructions
					if (URI.contains("ask") && URI.contains("hostname") && URI.contains("port")){
						//format of URI: ask?hostname=time.nist.gov&port=13, get rid of ask? by advancing the start of the string to index 4
						URI = URI.substring(4); 
						String[] arguments = URI.split("&");
						//hostnameA = "hostname=time.nist.gov"
						String hostnameA = arguments[0];
						String[] tempArg = hostnameA.split("=");
						//host = time.nist.gov
						host = tempArg[1];
						//portA = "port=13"
						String portA = arguments[1];
						tempArg = portA.split("=");
						//port = 13
						port = tempArg[1];

						//If our tcp request is to accept a string to send
						if (URI.contains("string")){
							//dataA = "string=datatosendtoserver"
							String dataA = arguments[2];
							tempArg = dataA.split("=");
							data = tempArg[1];
						}
					}
					else {
						err404 = true;
					}
				}
				catch (Exception e) {
					//the format of the request is wrong
					err400 = true;
				}
				//string holding the answer we get from the server we query using tcpAsk, and string
				//holding our response we will send to the asking client
				String tcpAskResponse = null;
				String response = null;
				if (!err400 && !err404){
					try{
						tcpAskResponse = TCPClient.askServer(host, Integer.parseInt(port), data);
					}
					//if tcpclient cannot successfully send the query
					catch (Exception e){
						err400 = true;
					}
				}
				//if theres been a 400 error, let the user know 
				else if (err400){
					response = "HTTP/1.1 400 bad request\r\n\r\n";
				}
				//print 404 error
				else if (err404){
					response = "HTTP/1.1 404 not foundn \r\n\r\n";
				}
				//if all has gone well, print 200 OK code, plus response we got from tcpAsk
				else {
					response = "HTTP/1.1 200 OK\r\n\r\n" + tcpAskResponse;
				}
				//send the response to the asking client and close the connection
				outToClient.writeBytes(response);
				connectionSocket.close();
			}	
			catch (IOException e) {
				System.err.println("Failed to read data from client");

			}
		}
	}
}
