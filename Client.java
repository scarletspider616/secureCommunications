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
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Console;

// Encryption
import TEA.*;

// misc
import java.util.ArrayList;


public class Client {
	private String user;
	private String pass;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private PrivateKey secretKey;
	private PublicKey publicKey;
	private PublicKey serverKey;
	private byte[] sharedKey;

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

		client.authenticate();
		String wantMore = "y";
		Scanner input = new Scanner(System.in);
		while (wantMore.equals("y")) {
			System.out.print("Enter filename to request: ");
			try {
				client.sendRequest(input.nextLine());
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			System.out.print("Another request? [y/N]: ");
			wantMore = input.nextLine();
			if (wantMore.equals("y")) {
				client.continueSession();
			}
		}
		client.sendEndOfSessionRequest();

	}

	private void generateKeys() throws NoSuchAlgorithmException, IOException {
		System.out.println("generating keys...");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		keyGen.initialize(512);
		KeyPair keys = keyGen.genKeyPair();
		secretKey = keys.getPrivate();
		publicKey = keys.getPublic();
	}

	// https://www.tutorialspoint.com/java/io/console_readpassword.htm
	private void authenticate() {
		// System.out.print("username: ");
		// Scanner scanner = new Scanner(System.in);
		// String user = scanner.nextLine();
		// System.out.println("password: ");
		// String pass = scanner.nextLine();
		Console console = null;
		String user = null;
		String pass = null;
		try {
			console = System.console();
			if (console != null) {
				user = console.readLine("username: ");
				pass = new String(console.readPassword("password: "));
			}

		} catch (Exception e) {}
		try {
			out.writeObject(TEAEncrypt.encrypt(user, sharedKey));
			String ack = TEADecrypt.decryptToString((int []) in.readObject(), 
						sharedKey);
			if (ack.equals("y")) {
				System.out.println("sending pass..");
				out.writeObject(TEAEncrypt.encrypt(pass, sharedKey));
				ack = TEADecrypt.decryptToString((int []) in.readObject(), 
						sharedKey);
				System.out.println(ack);
				if (ack.equals("y")) {
					System.out.println("Successful");
					return;
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println("Login failed, quitting...");
			System.exit(-1);
		}
		System.out.println("Login failed, quitting...");
		System.exit(-1);

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
		sharedKey = new byte[500];
		sharedKeyGenerator.generateSecret(sharedKey, 0);
	}

	/**
	* Sends a request to the server to retrieve a file
	**/
	private void sendRequest(String filename) throws IOException, 
			ClassNotFoundException{
		System.out.println("Requesting " + filename + "...");
		// encrypt the filename
		int [] message = TEAEncrypt.encrypt(
			filename, sharedKey);

		// send encrypted data
		out.writeObject(message);
		System.out.println("Sent request...");
		System.out.println("waiting for response from server");

		// get encrypted response
		int [] response = (int []) in.readObject();
		String strResponse = TEADecrypt.decryptToString(response, sharedKey);
		if (strResponse.equals("ERROR")) {
			System.out.println("File not found!");
			return;
		}
		else {
			receiveFile(filename);
		}
	}

	private void receiveFile(String filename) {
		System.out.println("Please specify the dir (enter for curr)");
		Scanner scanner = new Scanner(System.in);
		String path = scanner.nextLine() + filename;
		int [] fileData = null;
		try {
			fileData = (int []) in.readObject();
		} catch (Exception e) {}
		String fileDump = TEADecrypt.decryptToString(fileData, sharedKey);

		// http://stackoverflow.com/questions/12350248/java-difference-between-filewriter-and-bufferedwriter
		System.out.println("writing file...");
		try {
		    BufferedWriter bw = new BufferedWriter(
		    	new FileWriter(filename));
		    // String output = "";
		    bw.write(fileDump);
		    bw.flush();
		 } catch (Exception e) {
			e.printStackTrace();
		}
	    System.out.println(
	    	"File is avaialable at " + path);

	}

	private void sendEndOfSessionRequest() {
		System.out.println("Ending session...");
		String unencrypted = "DONE";
		int [] encrypted = TEAEncrypt.encrypt(unencrypted, sharedKey);
		try {
			out.writeObject(encrypted);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private void continueSession() {
		String unencrypted = "MORE";
		int [] encrypted = TEAEncrypt.encrypt(unencrypted, sharedKey);
		try {
			out.writeObject(encrypted);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}