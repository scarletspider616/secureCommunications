// please note that example code from Loyola Marymount University was used
// as a starting point for the server class. 
// it can be found here: http://cs.lmu.edu/~ray/notes/javanetexamples/

package Server;

import java.net.ServerSocket;

public class FileServer {
	public static void main(String [] args) {
		System.out.println("initializing file server");
		ServerSocket serverSocket = null;
		int requestNumber = 0;
		try {
			serverSocket = new ServerSocket(16000);
		} catch (Exception e) {
			System.out.println(e.toString());
			System.exit(-1);
		}
		try {
			while (true) {
				new ServerThread(
					serverSocket.accept(), requestNumber).start();
					requestNumber++;
			}
		} catch (Exception e) {
			System.out.println("Fileserver Error: " + e.toString());
			System.exit(-1);
			try {
				serverSocket.close();
			} catch(Exception ex) {}
		}
		try {
			serverSocket.close();
		} catch(Exception ex) {}
	}
}
