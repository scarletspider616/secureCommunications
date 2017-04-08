// please note that example code from Loyola Marymount University was used
// as a starting point for the thread class. 
// it can be found here: http://cs.lmu.edu/~ray/notes/javanetexamp

// crypto
import javax.crypto.KeyGenerator;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

// I/O
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class ServerThread extends Thread {
	// private FileManager fm;
	private int requestNumber;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	public ServerThread(Socket socket, int requestNumber) {
		this.socket = socket;
		this.requestNumber = requestNumber;
		System.out.println("Request num: " + String.valueOf(requestNumber) +
				" started new thread with socket: " + socket.toString());
		// fm = new FileManager();
	}

	public void run() {
		negotiateKey();

	}

	/**
	* Takes in the ALREADY HASHED username and password and checks if they 
	* exist in the shadow file
	**/

	private void negotiateKey() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
			keyGen.initialize(512); // 32-bit keys are used for this project
			KeyPair keys = keyGen.genKeyPair();
			PublicKey pubKey = keys.getPublic();
			PrivateKey privKey = keys.getPrivate();
		} catch (Exception e) {
			System.out.println(this.toString() + " failed. (Request num: " +
				String.valueOf(requestNumber) + ") Details: ");
			System.out.println(e.toString());
		}

	}

	private void establishSocket() throws IOException {
		in = new BufferedReader(
			new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		String inString = in.readLine();
		while (inString != null) {
			System.out.println(inString);
			inString = in.readLine();
		}
	}
	// public checkShadowFile(String user, String pass) {
	// 	fm.check
	// }
}