// Adapted from insertion sort in first project


// JNI tutorial used as reference: 
// https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html
import java.util.Scanner;
import java.io.File;

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