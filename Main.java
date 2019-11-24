
import java.math.BigInteger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;

public class Main {
		public static int findIndexFile(byte [] val){
			try{
			 ObjectInputStream in = new ObjectInputStream(new FileInputStream("index.ser"));
			  BigInteger[] array = (BigInteger[]) in.readObject();
			  in.close();
			  return binarySearch(array, 0, array.length-1, new BigInteger(val));
        
			}catch(Exception e){}
			return -1;
		
	}

		public static boolean verifyFile(int index, byte[] val) {

		try {
			byte[] b = new byte[16];
			String inputFilename = index + ".dat";
			InputStream is = new FileInputStream(inputFilename);
			BigInteger target = new BigInteger(1, val);
			int readBytes = 0;

			while ((readBytes = is.read(b)) != -1) {
				BigInteger binteger = new BigInteger(1, b);
				System.out.println(binteger.toString());
				if (target.compareTo(binteger)==0){
					return true;
				}
				
			}
			is.close();
		

		} catch (IOException ioe) {
			System.out.println("Error " + ioe.getMessage());
		}
		return false;

	}

		private static int binarySearch(BigInteger arr[], int l, int r, BigInteger x) {

		if (r >= l) {
			int mid = l + (r - l) / 2;

			if (arr[mid].compareTo(x) == 0)
				return mid;


			if (arr[mid].compareTo(x) == 1)
				return binarySearch(arr, l, mid - 1, x);

	
			return binarySearch(arr, mid + 1, r, x);
		} else {
			int mid = l + (r - l) / 2;

			if (mid > arr.length - 1) {
				return arr.length - 1;
			}

			if (mid < 0) {
				return 0;
			}

			if (arr[mid].compareTo(x) == 0)
				return mid;

			if (arr[mid].compareTo(x) == 1) {
				if (mid > 0) {
					return (mid - 1);
				}
				return 0;
			}
		
			return mid;
		}
	}

	public static void main(String argvs[]) {
		/*
		HashGenerator hgen = new HashGenerator(200*1024);
		
		hgen.generate("", 5000000);
		hgen.writeToFiles();
		*/

		
		NTLMPassword ntlm = new NTLMPassword();
			byte[] bytes = ntlm.encodeBytes("ab");
			BigInteger temp = new BigInteger(1,bytes);
			System.out.println(temp.toString() + "\n");
		System.out.println(verifyFile(findIndexFile(bytes),bytes));
		
		//findIndexFile();
	}
}
