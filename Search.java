/**
 * This class handles all search related operations
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;

public class Search {

	/**
	 * This method finds which file the hash is stored in
	 * 
	 * @param val hash value
	 * @return a call to the binarySearch method which will return a specific file
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
	 * This method finds the hash:password entry from indexed file
	 * 
	 * @param index specifies the file to be seached
	 * @param val the hash value to be searched
	 * @return the password:hash entry
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

	/**
	 * This method slices the given byte array with specified start and end values
	 * 
	 * @param arr array that will be sliced
	 * @param start where the slice will begin
	 * @param end where the slice will end
	 * @return sliced byte array
	 */
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
	 * Binary search, takes in array and target value. Returns index of
	 * closest lowest value.
	 * 
	 * @param arr array to be searched
	 * @param l left side of the array
	 * @param r right side of the array
	 * @param x target value
	 * @return index of closest valueS
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

	/**
	 * This method searches the hash value passed in as byte[] val
	 * 
	 * @param val the hash that will be searched
	 * @return a call the readString method which returns the string extracted from the byte array
	 */
	public static String searchAll(byte[] val) {
	 
	 byte [] entry = getEntryinFile(findIndexFile(val), val);
	 if (entry.length==0) {
		 return "Not Found";
	 }
	 return readString(getSliceOfArray(entry,0,4));
		
	}

	/**
	 * This method transforms a character array into a byte array by
	 * padding it with extra bits. The desired length of the byte array
	 * is specified by the integer num
	 * 
	 * @param array character array that will be padded
	 * @param num the fixed length of the character array we need
	 * @return padded byte array
	 */
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

	/**
	 * This method transforms the contents of a byte array
	 * into a string.
	 * 
	 * @param array byte array that will be transformed
	 * @return the string that is extracted from the byte array
	 */
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
