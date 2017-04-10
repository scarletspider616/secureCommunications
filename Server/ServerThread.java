// please note that example code from Loyola Marymount University was used
// as a starting point for the thread class. 
// it can be found here: http://cs.lmu.edu/~ray/notes/javanetexamp
package Server;
import TEA.*;
// crypto
import javax.crypto.KeyGenerator;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;
import javax.crypto.KeyAgreement;
import java.security.InvalidKeyException;

// I/O
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.crypto.ShortBufferException;
import java.io.File;


public class ServerThread extends Thread {
	// private FileManager fm;
	private int requestNumber;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private PrivateKey secretKey;
	private PublicKey publicKey;
	private PublicKey clientKey;
	private byte[] sharedKey;
	
	public ServerThread(Socket socket, int requestNumber) {
		this.socket = socket;
		this.requestNumber = requestNumber;
		System.out.println("Request num: " + String.valueOf(requestNumber) +
				" started new thread with socket: " + socket.toString());
		// fm = new FileManager();
	}

	public void run() {
		createKeys();
		try {
			handshakeWithClient();
		} catch (Exception e) {}

		// authenticate
		try {
			getRequest();
		} catch (Exception e) {}

	}

	/**
	* Takes in the ALREADY HASHED username and password and checks if they 
	* exist in the shadow file
	**/

	private void createKeys() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
			keyGen.initialize(512); // 32-bit keys are used for this project
			KeyPair keys = keyGen.genKeyPair();
			publicKey = keys.getPublic();
			secretKey = keys.getPrivate();
		} catch (Exception e) {
			System.out.println(this.toString() + " failed. (Request num: " +
				String.valueOf(requestNumber) + ") Details: ");
			System.out.println(e.toString());
		}

	}

	private void handshakeWithClient() throws IOException, 
			ClassNotFoundException, NoSuchAlgorithmException, 
			InvalidKeyException, ShortBufferException {
		System.out.println("client no. " + String.valueOf(requestNumber) 
			+ ": handshake with client...");		
		// get client's public key
		in = new ObjectInputStream(socket.getInputStream());
		clientKey = (PublicKey) in.readObject();
		System.out.println("client no. " + String.valueOf(requestNumber) 
			+ ": public key: " + clientKey.toString());

		// send server's public key to client 
		out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(publicKey);

		// create shared key
		createSharedKey();
	}

	private void getRequest() throws IOException, ClassNotFoundException {
		// read in the request
		int [] request = (int []) in.readObject();
		String filename = TEADecrypt.decryptToString(request, sharedKey);

		// check if file exists in data dir
		filename = "data/" + filename;
		File file = new File(filename);

		if (!file.exists()) {
			// let the client know we can't find this file
			String error = "ERROR";
			
		}

	}

	private void createSharedKey() throws NoSuchAlgorithmException,
			InvalidKeyException, ShortBufferException {
		System.out.println("client no. " + String.valueOf(requestNumber) 
			+ ": generating shared key...");
		KeyAgreement sharedKeyGenerator = KeyAgreement.getInstance("DH");
		System.out.println("init generator...");
		sharedKeyGenerator.init(secretKey);
		System.out.println("generating secret...");
		sharedKeyGenerator.doPhase(clientKey, true);
		sharedKey = new byte[500];
		sharedKeyGenerator.generateSecret(sharedKey, 0);
	}
}