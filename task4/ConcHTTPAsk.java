import java.net.*;
import java.io.*;

/*
 * A class which starts a server on port specified by first argument, and concurrently handles HTTP requests
 */

public class ConcHTTPAsk {

	public static void main (String[] args) throws IOException{
		int listenPort = Integer.parseInt(args[0]);
		ServerSocket welcomeSocket = new ServerSocket(listenPort);
		//wait for new requests. When a request is received, start a thread for it and send it off to do the work.
		while (true){
			Socket query = welcomeSocket.accept();
			startThread(query);
		}
	
	}
	// Method for starting a new thread
	private static void startThread(Socket query){
		getSendQuery httpQuery = new getSendQuery(query);
		Thread tid = new Thread(httpQuery);
		tid.start();
	}


}
