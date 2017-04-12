// Adapted from insertion sort in first project

package TEA;
// JNI tutorial used as reference: 
// https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html
import java.util.Scanner;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import TEA.*;

public class TEADecrypt {
	// private int[] result;
	static {
		System.loadLibrary("TEADecrypt");
	}

	// public InsertionSort(int[] inputData) {
	// 	result = new InsertionSort().runInsertionSort(inputData);
	// } 

	// public int[] getSortedResult() {
	// 	return this.result;
	// }

	// native method
	private native int[] runTEADecrypt(int[] value, int[] key);

	// Driver (as main for initial testing only)
	public static void main(String [] args) {
		byte[] byteKey = {(byte) 0xefbf, (byte) 0xbd38, (byte) 0xefbf, 
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
		String string = "Hello World!!!";
		// byte[] byteValue = new byte[string.length() + 
		// 			(4-(string.length()%4))]; // add necessary padding
		// byteValue  = byteValue + pad;

		// String test = "hello world!!";
		// int [] encrypted = TEAEncrypt.encrypt(test, byteKey);
		// int [] result = decrypt(encrypted, byteKey);
		// System.out.println(convertIntArrayToString(result));

		// encrypt(value, key);
		// TEAEncrypt.encrypt(value.getInt(), key.getInt());
		// int[] sortThis = {-15, 4, 99, 0, 87, 32, 1, -1}; // 4 initial testing only
		// int[] result = new TEAEncrypt().runTEAEncrypt();
		// for (int r:result) {
		// 	System.out.println(r);
		// }
	}

		public static String decryptToString(String value, String key) {
		return convertIntArrayToString(decrypt(convertStringToIntArray(value), 
					convertStringToIntArray(key)));
	}

	public static String decryptToString(String value, byte[] key) {
		return convertIntArrayToString(decrypt(convertStringToIntArray(value), 
					convertStringToIntArray(new String(key, 
						Charset.forName("UTF-8")))));
	}

	public static String decryptToString(int[] value, int[] key) {
		int [] result = decrypt(value, key);
		return convertIntArrayToString(result);
	}

	public static String decryptToString(int [] value, byte[] key) {
		int [] result = decrypt(value  ,
			convertStringToIntArray(new String(
				key, Charset.forName("UTF-8"))));
		return convertIntArrayToString(result);
	}
	public static int[] decrypt(String value, String key) {
		return decrypt(convertStringToIntArray(value), 
					convertStringToIntArray(key));
	}

	public static int[] decrypt(String value, byte[] key) {
		return decrypt(convertStringToIntArray(value), 
					convertStringToIntArray(new String(key, 
						Charset.forName("UTF-8"))));
	}

	public static int[] decrypt(int[] value, int[] key) {
		int [] result = new int[value.length];
		for (int i = 0; i < value.length-1; i+=2) {
			int [] temp = new TEADecrypt().runTEADecrypt(
				new int[] {value[i], value[i+1]}, key);
			result[i] = temp[0];
			result[i+1] = temp[1];
		}
		return result;
	}

	public static int[] decrypt(int [] value, byte[] key) {
		int [] result = decrypt(value  ,
			convertStringToIntArray(new String(
				key, Charset.forName("UTF-8"))));
		return result;
	}


	public static int[] convertStringToIntArray(String string) {
		while (string.getBytes().length % 8 != 0) {
			string = string + " ";
		}
		byte[] byteValue = string.getBytes();
		ByteBuffer value = ByteBuffer.wrap(byteValue);

		int [] input = new int[byteValue.length/4];
		int i = 0;
		while(value.hasRemaining()) {
			input[i] = value.getInt();
			i++;

		}
		return input;
	}

	public static String convertIntArrayToString(int [] array) {
		String result = "";
		for (int i: array) {
			result = result + convertIntToChar(i);
		}
		return result.trim();
	}

	// http://stackoverflow.com/questions/5328996/java-change-int-to-ascii
	private static String convertIntToChar(int input) {
		int length = 4;
    	StringBuilder builder = new StringBuilder(length);
    	for (int i = length - 1; i >= 0; i--) {
        	builder.append((char) ((input >> (8 * i)) & 0xFF));
    	}
    	return builder.toString();
	}
}