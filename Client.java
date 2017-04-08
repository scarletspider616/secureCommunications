//http://stackoverflow.com/questions/36346734/receiving-diffie-hellman-key-over-sockets-error

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


public class Client {
	private String user;
	private String pass;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public Client(String user, String pass) {
		// constrcutor for client
		this.user = user;
		this.pass = pass;
		try {
			this.socket = new Socket("localhost", 16000);
			establishSocket();
		} catch(Exception e) {
			System.out.println("Client error connecting to host. Dets:");
			System.out.println(e.toString());
			System.out.println("exiting...");
			System.exit(-1);
		}
	}

	public Client() {
		try {
			this.socket = new Socket("localhost", 16000);
			establishSocket();
		} catch(Exception e) {
			System.out.println("Client error connecting to host. Dets:");
			System.out.println(e.toString());
			System.out.println("exiting...");
			System.exit(-1);
		}
	}

	public static void main(String [] args) {
		Client client = new Client();
		System.out.println("Client initalized. Please ensure server is " +
			"running");
		try {
			client.sendRequest();
		} catch (Exception e) {
			System.out.println("Client request failed.");
			System.exit(-1);
		}
	}

	private void sendRequest() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		keyGen.initialize(512);
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
}