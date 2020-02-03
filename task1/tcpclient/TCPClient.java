package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    public static String askServer(String hostname, int port, String ToServer) throws  IOException {
        Socket clientSocket = new Socket(hostname, port);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(ToServer + '\n');
        return readOutput(clientSocket);

    }

    public static String askServer(String hostname, int port) throws  IOException {
        Socket clientSocket = new Socket(hostname, port);
        return readOutput(clientSocket);




    }
   public static String readOutput(Socket clientSocket) throws IOException{
       BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       clientSocket.setSoTimeout(2000);
       StringBuilder answer = new StringBuilder();
       String in = inFromServer.readLine();
       answer.append(in);
       try {
           while (in != null) {
               in = inFromServer.readLine();
               if (in != null){
               	answer.append('\n');
               	answer.append(in);
               }
            }
            inFromServer.close();
           clientSocket.close();
       }
       catch(SocketTimeoutException e){
           clientSocket.close();
           inFromServer.close();
       }
    return answer.toString();
   }
}

