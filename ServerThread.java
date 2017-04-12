// please note that example code from Loyola Marymount University was used
// as a starting point for the thread class. 
// it can be found here: http://cs.lmu.edu/~ray/notes/javanetexamp
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
import java.util.Scanner;


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
	private boolean wantMore;
	
	public ServerThread(Socket socket, int requestNumber) {
		this.socket = socket;
		this.requestNumber = requestNumber;
		System.out.println("Request num: " + String.valueOf(requestNumber) +
				" started new thread with socket: " + socket.toString());
		wantMore = true;
		// fm = new FileManager();
	}

	public void run() {
		createKeys();
		try {
			handshakeWithClient();
		} catch (Exception e) {}

		if(authenticate()) {

			try {
				while(wantMore) {
					getRequest();
				}
			} catch (Exception e) {}
		}

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

	private boolean authenticate() {
		System.out.println("authenticating " + String.valueOf(requestNumber));
		try {
			String user = TEADecrypt.decryptToString((int []) in.readObject(),
							sharedKey);
			if (ShadowTableGenerator.checkUsername(user)) {
				out.writeObject(TEAEncrypt.encrypt("y", sharedKey));
				String pass = TEADecrypt.decryptToString((int []) in.readObject(),
								sharedKey);
				if(ShadowTableGenerator.checkPassword(user, pass)) {
					System.out.println("auth sucessful");
					out.writeObject(TEAEncrypt.encrypt("y", sharedKey));
					return true;
				}

			}
			out.writeObject(TEAEncrypt.encrypt("n", sharedKey));
			return false;
		} catch (Exception e) {}
		return false;
	}

	private void getRequest() throws IOException, ClassNotFoundException {
		System.out.println("waiting for request");
		// read in the request
		int [] request = (int []) in.readObject();
		String filename = TEADecrypt.decryptToString(request, sharedKey);

		// check if file exists in data dir
		filename = "data/" + filename;
		System.out.println(filename);
		File file = new File(filename);

		if (!file.exists()) {
			// let the client know we can't find this file
			String error = "ERROR";
			int [] encrypted = TEAEncrypt.encrypt(error, sharedKey);
			out.writeObject(encrypted);
			System.out.println(filename + " does not exist.");
			wantMore = checkIfClientWantsMore();
			return;
		}
		String message = "Sending file";
		System.out.println(message);
		int [] encryptedMessage = TEAEncrypt.encrypt(message, sharedKey);
		out.writeObject(encryptedMessage);
		// otherwise lets encrypt and send off the file!
		String fileDump = readFile(filename);
		int [] data = TEAEncrypt.encrypt(fileDump, sharedKey);
		out.writeObject(data);
		wantMore = checkIfClientWantsMore();
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

	private String readFile(String filename) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (Exception e) {}

		String result = "";
		while (scanner.hasNextLine()) {
			result = result + scanner.nextLine() + "\n";
		}
		return result;
	}

	private Boolean checkIfClientWantsMore() {
		System.out.println("waiting for client" + String.valueOf(
					requestNumber));
		String decrypted = "";
		try {
			int [] encrypted = (int []) in.readObject();
			decrypted = TEADecrypt.decryptToString(encrypted, sharedKey);
		} catch (Exception e) {
			e.toString();
			return false;
		}
		if(decrypted.equals("MORE")) {
			return true;
		}
		else {
			return false;
		}
	}
}
