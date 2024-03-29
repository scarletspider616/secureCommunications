import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.nio.charset.Charset;

import TEA.*;



public class ShadowTableGenerator {


	// since this is made for demonstration purposes no attempt is 
	// made to hide this key. TES is NOT one-way, however the purpose
	// of this excersize is NOT to create password tables. For 
	// actual password tables, please see: 
	// http://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
	private static byte[] key = {(byte) 0xefbf, (byte) 0xbd38, (byte) 0xefbf, 
		(byte) 0xbdef, (byte) 0xbfbd, (byte) 0x0cef, (byte) 0xbfbd, 
		(byte) 0x0eef, (byte) 0xbfbd, (byte) 0xefbf, (byte) 0xbd26, 
		(byte) 0x69cb, (byte) 0x8719, (byte) 0x5451, (byte) 0xefbf, 
		(byte) 0xbd0d, (byte) 0x7303, (byte) 0xefbf, (byte) 0xbdef, 
		(byte) 0xbfbd, (byte) 0xefbf, (byte) 0xbd1d, (byte) 0x54ef, 
		(byte) 0xbfbd, (byte) 0x0d43, (byte) 0x1226, (byte) 0x02ef, 
		(byte) 0xbfbd, (byte) 0x0a7f, (byte) 0xefbf, (byte) 0xbd06, 
		(byte) 0x7975, (byte) 0xefbf, (byte) 0xbdef, (byte) 0xbfbd, 
		(byte) 0xefbf, (byte) 0xbd76, (byte) 0x0639, (byte) 0xefbf, 
		(byte) 0xbd15, (byte) 0x1aef, (byte) 0xbfbd, (byte) 0x07ef, 
		(byte) 0xbfbd, (byte) 0x1aef, (byte) 0xbfbd, (byte) 0x52ef, 
		(byte) 0xbfbd, (byte) 0x3647, (byte) 0xd1b2, (byte) 0xefbf, 
		(byte) 0xbd71, (byte) 0x074d, (byte) 0x1400, (byte) 0x750a}; 

	public static void main(String [] args) {
		welcomeWarning();
		createShadowFile();

		// for testing only 
		// System.out.println(checkPassword("user2", "abcdefg"));
	}

	private static void welcomeWarning() {
		System.out.println("Hey! Please note that this will overwrite" +
			" any existing file called '.passwords' in the cur dir.\n" + 
			"It will also erase the input file (passwords.txt).");
		System.out.println("also note that commas are not allowed in \n" + 
			"usernames or passwords.");
		System.out.println("Press enter to confirm you are okay with this,\n" +
			"and that your passwords file is in the current dir labeled " + 
			"'passwords.txt'\nin the format: 'username,password\\n' (plaintext)");
		try {
			System.in.read();
		} catch(Exception e) {}
	}
	private static void createShadowFile() {
		// http://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
		ArrayList<ArrayList<String>> lines = 
			new ArrayList<ArrayList<String>>();
		ArrayList<String> data = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(
				new FileReader("passwords.txt"))) {
    		String line;
    		while ((line = br.readLine()) != null) {
    			data.add(line);
    		}
    	} catch (Exception e) {
    		System.out.println(e.toString());
    	}
    	lines = hashAndSalt(data);
    	writeValues(lines);
    }

    private static ArrayList<ArrayList<String>> hashAndSalt(ArrayList<String> lines) {
    	ArrayList<ArrayList<String>> results = 
    		new ArrayList<ArrayList<String>>();
    	
    	for (String line:lines) {
    		ArrayList<String> temp = new ArrayList<String>();
    		// extract data
    		String [] words = line.split(",");
    		String username = words[0];
    		String password = words[1];

    		// generate 32-bit salt
    		//http://stackoverflow.com/questions/18142745/how-do-i-generate-a-salt-in-java-for-salted-hash
    		int[] random = new int[4];
            Random randomGenerator = new Random();
            for (int i = 0; i < 4; i++) {
                random[i] = randomGenerator.nextInt();
            }
            String salt = getHexString(random);


	   		// hash the password + salt
    		int[] hash = TEAEncrypt.encrypt(password+salt, key);
    		String hashedPass = getHexString(hash);
    		hashedPass = hashedPass + "\n";
    		temp.add(username);
    		temp.add(salt);
    		temp.add(hashedPass);
    		results.add(temp);
    	}
    	return results;
    }

    // modified version of code I used in first project
    private static void writeValues(ArrayList<ArrayList<String>> writeValues) {
		// outputFilename = "../" + outputFilename;
		// System.out.println("Random Values Produced: ");
		// for(int r: writeValues) {
		// 	System.out.print(r);
		// 	System.out.print(" ");
		// }
		// System.out.println(" ");
		// based on:
		// http://stackoverflow.com/questions/12350248/java-difference-between-filewriter-and-bufferedwriter
		String outputFilename = ".passwords";
		try {
		    BufferedWriter bw = new BufferedWriter(
		    	new FileWriter(outputFilename));
		    // String output = "";
		    for(ArrayList<String> line: writeValues) {
		    	for(String word: line) {
		    		bw.write(word + ":");
		    	}
		    	bw.write("\n");
		    	bw.flush();
		    }
		 } catch (Exception e) {
			e.printStackTrace();
		}
	    System.out.println(
	    	"Data available in  " + outputFilename);
	}

	public static Boolean checkPassword(String user, String pass) {
		// find user name in file
		// http://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
		ArrayList<String> lines = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(
				new FileReader(".passwords"))) {
    		String line;
    		while ((line = br.readLine()) != null) {
    			lines.add(line);
    		}
    	} catch (Exception e) {
    		System.out.println(e.toString());
    	}
    	ArrayList<String> usernames = new ArrayList<String>();
    	for (String line: lines) {
    		String [] words = line.split(":");
            try {
    	       usernames.add(words[0]);
           } catch (Exception e) {}
    	}      
    	// should we make this case insensitive in the future? 
    	// anyways now that we have the usernames lets check if our input
    	// is an existing user
    	Boolean isUser = false;
    	for (String username: usernames) {
    		if (user.equals(username)) {
                // System.out.println(username);
    			isUser = true;
    			break;
    		}
    	}
    	if (!isUser) return false;

    	// now check passwords
    	String salt = "";
    	String hash = "";
    	for (String line: lines) {
    		String [] words = line.split(":");
            try {
        		if (words[0].equals(user)) {
        			salt = words[1];
        			hash = words[2];
        		}
            } catch (Exception e) {}
    	}
    	int[] newHash = TEAEncrypt.encrypt(pass + salt, key);
    	String checkHash = getHexString(newHash);
    	// System.out.println(checkHash);
    	// System.out.println(hash);
    	return checkHash.equals(hash);

	}

    public static boolean checkUsername(String user) {
        ArrayList<String> lines = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(".passwords"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        ArrayList<String> usernames = new ArrayList<String>();
        for (String line: lines) {
            String [] words = line.split(":");
            try {
               usernames.add(words[0]);
           } catch (Exception e) {}
        }      
        // should we make this case insensitive in the future? 
        // anyways now that we have the usernames lets check if our input
        // is an existing user
        Boolean isUser = false;
        for (String username: usernames) {
            if (user.equals(username)) {
                // System.out.println(username);
                isUser = true;
                break;
            }
        }
        return isUser;
    }

    private static String getHexString(int [] ints) {
        String hex = "";
        for (int i: ints) {
            hex = hex + Integer.toHexString(i);
        }
        return hex;
    }
}
    