/*
 * This class' primary function is to generate large volume of hashes and the related storage functions/procedures
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;
import jcifs.smb.NtlmPasswordAuthentication;

public class HashGenerator {
	private final int blockSize = 115;
	private final String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	private int numBuckets;
	
	private BigInteger[] bucketRanges;
	private LinkedList<byte[]>[] buckets;

	//paritionsize is the specified allocation size on hard disk by user in megabytes
	public HashGenerator(int parititionSize) {
		this.numBuckets = parititionSize / blockSize;
		instantiateBuckets();
	}
	

	
	


	//instantiates buckets data structure
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
	try{
	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("index.ser"));
        out.writeObject(bucketRanges);
        out.flush();
        out.close();
	}catch(Exception e){
		e.printStackTrace();
	}

	}
	
	//generates hashes and places in buckets LinkedList data structure. Generates from starting string to 
	//a specified incremented number. For input ("a", 3) Would generate hashes for [b,c,d] in this case.
	//it is not inclusive of starting value
	public void generate(String start, int numIncrement) {
		BigInteger[] array = new BigInteger[numIncrement];
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		if (start.length()==0) {
			
			String string = ""+ charset.charAt(0);
			NTLMPassword ntlm = new NTLMPassword();
			byte[] bytes = ntlm.encodeBytes(string);
			placeInBucket(bytes);
			numIncrement--;
			generate(string, numIncrement);
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
				if ((i==-1)&& increase == false){
					break;
				}
				//if last character of working string & last character of charset
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

			placeInBucket(bytes);

		}
	}
	
	//places byte array into bucket (LinkedList)
	private void placeInBucket(byte[] value) {
		int index = binarySearch(this.bucketRanges, 0, this.bucketRanges.length - 1, new BigInteger(1, value));
		// System.out.println(index);
		
		buckets[index].add(value);
	}

	//writes contents of buckets to file
	public void writeToFiles() {

		for (int i = 0; i < this.bucketRanges.length; i++) {
			writeByteArraytoFile(convert1d(convertToArray(buckets[i])), i + ".dat");
		}

	}
	
	//reads in specified file; returns BigInteger array of values to be analyzed
	public LinkedList <BigInteger> verifyFile(String inputFilename) {

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
	
	//returns buckets
	public LinkedList<byte[]>[] getBuckets(){
		
		return buckets;
	}

	/*
	 * 
	 * HELPER FUNCTIONS
	 */
	
	//writes byte array to file
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
	
	//converts LinkedList of byte arrays to two dimensional byte array
	private byte[][] convertToArray(LinkedList<byte[]> list) {
		byte[][] arr = new byte[list.size()][];
		arr = list.toArray(arr);
		return arr;
	}
	
	//finds index of given character in charset
	private int findIndexinCharset(char c) {
		for (int i = 0; i < charset.length(); i++) {
			if (c == charset.charAt(i)) {
				return i;
			}
		}
		return -1;
	}
	
	
	//converts two dimensional byte array to single dimensional
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

	//modified binary search, takes in array and target value. Returns index of closest lowest value.
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

}
