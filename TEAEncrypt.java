// Adapted from insertion sort in first project


// JNI tutorial used as reference: 
// https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html
import java.util.Scanner;
import java.io.File;
import java.nio.ByteBuffer;

public class TEAEncrypt {
	// private int[] result;
	private static int memCount;
	static {
		System.loadLibrary("Encrypt");
	}

	// public InsertionSort(int[] inputData) {
	// 	result = new InsertionSort().runInsertionSort(inputData);
	// } 

	// public int[] getSortedResult() {
	// 	return this.result;
	// }

	// native method
	private native int[] runTEAEncrypt(int[] value, int[] key);

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
		String string = "Hello World!!";
		// byte[] byteValue = new byte[string.length() + 
		// 			(4-(string.length()%4))]; // add necessary padding
		// byteValue  = byteValue + pad;

		while (string.length() %8 != 0) {
			string = string + " ";
		}
		byte[] byteValue = string.getBytes();
		System.out.println(byteValue.length);
		ByteBuffer key = ByteBuffer.wrap(byteKey);
		ByteBuffer value = ByteBuffer.wrap(byteValue);

		

		while(value.hasRemaining()) {
			System.out.println(value.getInt());
		}

		// TEAEncrypt.encrypt(value.getInt(), key.getInt());
		// int[] sortThis = {-15, 4, 99, 0, 87, 32, 1, -1}; // 4 initial testing only
		// int[] result = new TEAEncrypt().runTEAEncrypt();
		// for (int r:result) {
		// 	System.out.println(r);
		// }
	}

	public static int[] encrypt(int[] value, int[] key) {
		// mem count is stored in the last element of the array
		int [] result = new TEAEncrypt().runTEAEncrypt(value, key);
		return result;
	}
}