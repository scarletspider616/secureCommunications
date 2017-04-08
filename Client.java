//http://stackoverflow.com/questions/36346734/receiving-diffie-hellman-key-over-sockets-error

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
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.crypto.ShortBufferException;

// JSON


public class Client {
	private String user;
	private String pass;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private PrivateKey secretKey;
	private PublicKey publicKey;
	private PublicKey serverKey;
	private Key sharedKey;

	// public Client(String user, String pass) {
	// 	// constrcutor for client
	// 	this.user = user;
	// 	this.pass = pass;
	// 	try {
	// 		this.socket = new Socket("localhost", 16000);
	// 		establishSocket();
	// 	} catch(Exception e) {
	// 		System.out.println("Client error connecting to host. Dets:");
	// 		System.out.println(e.toString());
	// 		System.out.println("exiting...");
	// 		System.exit(-1);
	// 	}
	// }

	public Client() {
		try {
			System.out.println("attempting to communicate with server...");
			this.socket = new Socket("localhost", 16000);
		} catch(Exception e) {
			System.out.println("Client error connecting to host. Dets:");
			System.out.println(e.toString());
			System.out.println("exiting...");
			System.exit(-1);
		}
	}

	public static void main(String [] args) {
		Client client = new Client();
		try {
			client.generateKeys();
			client.handshakeWithServer();
		} catch (Exception e) {
			System.out.println("Client request failed.");
			System.exit(-1);
		}
	}

	private void generateKeys() throws NoSuchAlgorithmException, IOException {
		System.out.println("generating keys...");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		keyGen.initialize(512);
		KeyPair keys = keyGen.genKeyPair();
		secretKey = keys.getPrivate();
		publicKey = keys.getPublic();
	}

	private void handshakeWithServer() throws IOException, 
			ClassNotFoundException, NoSuchAlgorithmException, 
			InvalidKeyException, ShortBufferException {
		System.out.println("handshake with server...");
		out = new ObjectOutputStream(socket.getOutputStream());

		// send public key to server
		out.writeObject(publicKey);
		System.out.println("Sending: " + publicKey);

		// get public key from server
		in = new ObjectInputStream(socket.getInputStream());
		serverKey = (PublicKey) in.readObject();
		System.out.println("Received server key: ");
		System.out.println(serverKey);

		// create shared key
		createSharedKey();

	}

	private void createSharedKey() throws NoSuchAlgorithmException,
			InvalidKeyException, ShortBufferException {
		System.out.println("generating shared key...");
		KeyAgreement sharedKeyGenerator = KeyAgreement.getInstance("DH");
		sharedKeyGenerator.init(secretKey);
		sharedKeyGenerator.doPhase(serverKey, true);
		byte [] sharedKey = new byte[500];
		sharedKeyGenerator.generateSecret(sharedKey, 0);
		String s = new String(sharedKey);
		System.out.println(s);
	}
}