/*
 * This class' primary function is to generate large volume of hashes and the related storage functions/procedures
 */

import java.util.UUID;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;
import jcifs.smb.NtlmPasswordAuthentication;
import java.util.Scanner;

public class HashGenerator {
	private final int blockSize = 115;
	private final String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	private int numBuckets;
	private final int entriesPerBlock = 5000000;
	private BigInteger[] bucketRanges;
	private LinkedList<byte[]>[] buckets;
	private String id;
	private String start;
	private String end;

	public String getEnd() {
		return this.end;
	}

	// Constructor
	// parititionSize the specified allocation size on hard disk by user in megabytes
	public HashGenerator(int parititionSize) {
		if (parititionSize > 0) {
			this.numBuckets = parititionSize / blockSize;
			generateId();
			instantiateBuckets();
		} else {
			File tmpDir = new File("index.ser");

			boolean exists = tmpDir.exists();

			if (exists) {
				loadProfile();
			} else {
				instantiateBuckets();
			}
		}
	}

	// 
	private void loadProfile() {
		bucketRanges = (BigInteger[]) ReadObjectFromFile("index.ser");
		buckets = (LinkedList<byte[]>[]) new LinkedList[bucketRanges.length];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new LinkedList<byte[]>();
		}

		File file = new File("id.txt");
		Scanner sc;
		try {
			sc = new Scanner(file);
			this.id = sc.nextLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getId() {
		return this.id;
	}

	// Generate an ID for each node and write it into id.txt file
	private void generateId() {
		// generate ID
		UUID uuid = UUID.randomUUID();
		this.id = uuid.toString();

		File file = new File("id.txt");
		try {
			FileWriter fr = new FileWriter(file, false);
			fr.write(this.id);
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Instantiates buckets data structure
	private void instantiateBuckets() {
		bucketRanges = new BigInteger[this.numBuckets];
		bucketRanges[0] = BigInteger.valueOf(0);
		String hexStr = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		BigInteger bigInt = new BigInteger(hexStr, 16);
		BigInteger bucketSize = bigInt.divide(BigInteger.valueOf(this.numBuckets));

		for (int i = 1; i < this.numBuckets; i++) {
			bucketRanges[i] = bucketRanges[i - 1].add(bucketSize);
		}
		buckets = (LinkedList<byte[]>[]) new LinkedList[bucketRanges.length];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new LinkedList<byte[]>();
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("index.ser"));
			out.writeObject(bucketRanges);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Generate buckets which are made of linked lists
	public void generate(String start) {
		this.start = start;
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new LinkedList<byte[]>();
		}

		generateFunc(start, entriesPerBlock);
	}

	/**
	 * Generates hashes and places in buckets LinkedList data structure. Generates
	 * from starting string to a specified incremented number. For input ("a", 3)
	 * Would generate hashes for [b,c,d] in this case. It is not inclusive of
	 * starting value
	 * 
	 * @param start The starting string
	 * @param numIncrement Defines the upper limit for the hashes that are generated
	 */
	private void generateFunc(String start, int numIncrement) {
		BigInteger[] array = new BigInteger[numIncrement];
		ArrayList<Integer> indexes = new ArrayList<Integer>();

		if (start.length() == 0) {

			String string = "" + charset.charAt(0);
			NTLMPassword ntlm = new NTLMPassword();
			byte[] bytes = ntlm.encodeBytes(string);
			placeInBucket(bytes, string);
			numIncrement--;
			generateFunc(string, numIncrement);
			return;
		}

		for (int i = start.length() - 1; i >= 0; i--) {
			indexes.add(findIndexinCharset(start.charAt(i)));
		}

		for (int z = 0; z < numIncrement; z++) {
			boolean increase = false;
			//
			for (int i = indexes.size() - 1; i >= -1; i--) {

				if ((i == -1) && increase == true) {
					indexes.add(0);
					increase = false;

				}
				if ((i == -1) && increase == false) {
					break;
				}
				// if last character of working string & last character of charset
				if ((i == (indexes.size() - 1)) && (indexes.get(i) == this.charset.length() - 1)) {

					increase = true;
					indexes.set(i, 0);

				} else if (i == (indexes.size() - 1)) {

					indexes.set(i, indexes.get(i) + 1);
					increase = false;

					break;
				} else if (increase == true) {

					if (indexes.get(i) == this.charset.length() - 1) {
						increase = true;
						indexes.set(i, 0);
					} else {
						indexes.set(i, indexes.get(i) + 1);
						increase = false;
						break;
					}
				}

			}

			char[] str = new char[indexes.size()];
			for (int i = indexes.size() - 1; i >= 0; i--) {
				str[i] = this.charset.charAt(indexes.get(i));

			}
			String string = new String(str);
			NTLMPassword ntlm = new NTLMPassword();
			byte[] bytes = ntlm.encodeBytes(string);

			placeInBucket(bytes, string);
			if (z == numIncrement - 1) {
				this.end = string;
			}
		}
	}

	/**
	 * Places byte array into bucket (LinkedList)
	 * 
	 * @param value byte array to be placed within the bucket
	 * @param clearText 
	 */
	private void placeInBucket(byte[] value, String clearText) {
		int index = binarySearch(this.bucketRanges, 0, this.bucketRanges.length - 1, new BigInteger(1, value));
		
		byte[] clearTextBytes = writePadChars(clearText.toCharArray(), 5);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(clearTextBytes);
			outputStream.write(value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		buckets[index].add(outputStream.toByteArray());
	}

	// Writes contents of buckets to file
	public void writeToFiles() {

		for (int i = 0; i < this.bucketRanges.length; i++) {
			writeByteArraytoFile(convert1d(convertToArray(buckets[i])), i + ".dat");
		}
		updateBlockFile();

	}

	// Update the blocks.txt file with the start and end string values
	private void updateBlockFile() {
		File file = new File("blocks.txt");
		try {
			FileWriter fr = new FileWriter(file, true);
			fr.write(this.start + "-" + this.end+"\n");
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in specified file; returns BigInteger array of values to be analyzed
	 * 
	 * @param inputFilename
	 * @return
	 */
	public LinkedList<BigInteger> verifyFile(String inputFilename) {

		LinkedList<BigInteger> temp = new LinkedList<BigInteger>();

		try {
			byte[] b = new byte[16];
			InputStream is = new FileInputStream(inputFilename);

			int readBytes = 0;

			while ((readBytes = is.read(b)) != -1) {
				BigInteger binteger = new BigInteger(1, b);
				temp.add(new BigInteger(1, b));
			}
			is.close();

		} catch (IOException ioe) {
			System.out.println("Error " + ioe.getMessage());
		}
		return temp;

	}

	// Return buckets
	public LinkedList<byte[]>[] getBuckets() {

		return buckets;
	}

	// HELPER FUNCTIONS

	/**
	 * Writes byte array to file
	 * 
	 * @param bytes byte array to be written to the file
	 * @param filename the file name where the byte array will be written
	 */
	static void writeByteArraytoFile(byte[] bytes, String filename) {
		try {

			File file = new File(filename);
			OutputStream os = new FileOutputStream(file, true);

			// Starts writing the bytes in it
			os.write(bytes);

			// Close the file
			os.close();
		}

		catch (Exception e) {
			System.out.println("Exception: " + e);
		}

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
	private byte[] writePadChars(char array[], int num) {
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
	 * Converts LinkedList of byte arrays to two dimensional byte array
	 * 
	 * @param list byte linked list to be converted into 2D byte array
	 * @return 2D byte array is returned after the conversion
	 */
	private byte[][] convertToArray(LinkedList<byte[]> list) {
		byte[][] arr = new byte[list.size()][];
		arr = list.toArray(arr);
		return arr;
	}

	/**
	 * Finds index of given character in charset
	 * 
	 * @param c
	 * @return index is returned if found; if the index isn't found -1 is returned
	 */
	private int findIndexinCharset(char c) {
		for (int i = 0; i < charset.length(); i++) {
			if (c == charset.charAt(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Converts two dimensional byte array to single dimensional
	 * 
	 * @param input 2D byte array
	 * @return single dimensional array
	 */
	private byte[] convert1d(byte[][] input) {
		int z = 0;
		byte[] arr = new byte[input.length * input[0].length];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length; j++) {
				arr[z] = input[i][j];
				z++;
			}
		}
		return arr;
	}

	/**
	 * Modified binary search, takes in array and target value. Returns index of
	 * closest lowest value.
	 * 
	 * @param arr array to be searched
	 * @param l left side of the array
	 * @param r right side of the array
	 * @param x target value
	 * @return index of closest value
	 */
	private int binarySearch(BigInteger arr[], int l, int r, BigInteger x) {

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
	 * This method reads an object from the specifed file path
	 * 
	 * @param filepath file path as string
	 * @return Object that will be read
	 */
	private Object ReadObjectFromFile(String filepath) {
		try {
			FileInputStream fileIn = new FileInputStream(filepath);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);

			Object obj = objectIn.readObject();

			objectIn.close();
			return obj;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
