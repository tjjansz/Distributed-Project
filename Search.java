import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;

public class Search {
	/**
	 * 
	 * @param val
	 * @return
	 */
	private static int findIndexFile(byte[] val) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("index.ser"));
			BigInteger[] array = (BigInteger[]) in.readObject();
			in.close();
			return binarySearch(array, 0, array.length - 1, new BigInteger(1,val));
		} catch (Exception e) {
		}

		return -1;
	}
	/**
	 * 
	 * @param index
	 * @param val
	 * @return
	 */
	private static byte[] getEntryinFile(int index, byte[] val) {
		try {
			byte[] b = new byte[16+5];
			String inputFilename = index + ".dat";
			InputStream is = new FileInputStream(inputFilename);
			BigInteger target = new BigInteger(1, val);
			int readBytes = 0;

			while ((readBytes = is.read(b)) != -1) {
				
				byte [] hash =getSliceOfArray(b,5,21);
				
				BigInteger binteger = new BigInteger(1, hash);
				if (target.compareTo(binteger) == 0) {
					return b;
				}

			}
			is.close();

		} catch (IOException ioe) {
			System.out.println("Error " + ioe.getMessage());
		}
		return new byte[0];
	}

	private static byte[] getSliceOfArray(byte[] arr, int start, int end) {

// Get the slice of the Array 
		byte[] slice = new byte[end - start];

// Copy elements of arr to slice 
		for (int i = 0; i < slice.length; i++) {
			slice[i] = arr[start + i];
		}

// return the slice 
		return slice;
	}

	/**
	 * 
	 * @param arr
	 * @param l
	 * @param r
	 * @param x
	 * @return
	 */
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

	public static String searchAll(byte[] val) {
	 
	 byte [] entry = getEntryinFile(findIndexFile(val), val);
	 if (entry.length==0) {
		 return "Not Found";
	 }
	 return readString(getSliceOfArray(entry,0,4));
		
	}

	private static byte[] writePadChars(char array[], int num) {
		char[] output = new char[num];
		int numZero = num - array.length;
		byte[] bytearray = new byte[num];

		for (int i = 0; i < array.length; i++) {
			output[i] = array[i];
		}

		for (int i = 0; i < numZero; i++) {
			output[array.length + i] = (char) 0;
		}

		for (int i = 0; i < bytearray.length; i++) {
			bytearray[i] = (byte) output[i];
		}
		return bytearray;

	}

	private static String readString(byte[] array) {
		char[] temp = new char[array.length];
		int count = 0;
		char[] temp2;

		for (int i = 0; i < array.length; i++) {
			temp[i] = (char) array[i];
		}

		for (int i = 0; i < temp.length; i++) {
			if ((int) temp[i] != 0) {
				count++;
			}
		}
		temp2 = new char[count];
		for (int i = 0; i < count; i++) {
			temp2[i] = temp[i];
		}
		String string = new String(temp2);
		return string;
	}
	
}
