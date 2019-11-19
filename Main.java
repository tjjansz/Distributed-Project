import java.util.ArrayList;
import java.io.File; 
import java.io.FileOutputStream; 
import java.io.OutputStream;
import java.nio.file.Files;
import java.math.BigInteger;
import java.util.LinkedList;

 import java.io.ByteArrayOutputStream;  
 import java.io.File;  
 import java.io.FileInputStream;  
 import java.io.FileOutputStream;  
 import java.io.IOException;  
 import java.io.InputStream;  
 import java.util.List;  
 import java.util.Map;  
 import java.util.zip.DataFormatException;  
 import java.util.zip.Deflater;  
 import java.util.zip.Inflater; 
 import org.apache.commons.lang3.ArrayUtils;

public class Main {
    private static BigInteger [] buckets;
    private static LinkedList <byte []> [] rootList;
    
    private static String input = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private static int findIndexinCharset(char c){
        for (int i=0;i<input.length();i++){
            if (c == input.charAt(i)){
                return i;
            }
        }
        return -1;
    }

static void instantiateBuckets(int numSegment){
    buckets = new BigInteger [numSegment];
    buckets[0] = BigInteger.valueOf(0);
  String hexStr = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
  BigInteger bigInt = new BigInteger(hexStr, 16);
  BigInteger bucketSize = bigInt.divide(BigInteger.valueOf(numSegment));
  
  for (int i=1;i<numSegment;i++){
      buckets[i] = buckets[i-1].add(bucketSize);
  }

  

  rootList = (LinkedList <byte []> [])new LinkedList [buckets.length];

  for (int i=0;i<rootList.length;i++){
      rootList[i] = new LinkedList <byte []> ();
  }


}
 static void placeInBucket(byte[] value){
     int index = binarySearch(buckets,0,buckets.length-1,new BigInteger(1, value));
     //System.out.println(index);
     rootList[index].add(value);
 }
 static int partition(BigInteger arr[], int low, int high) 
    { 
        BigInteger pivot = arr[high];  
        int i = (low-1); // index of smaller element 
        for (int j=low; j<high; j++) 
        { 
            // If current element is smaller than the pivot 
            if (arr[j].compareTo(pivot)==-1) 
            { 
                i++; 
  
                // swap arr[i] and arr[j] 
                BigInteger temp = arr[i]; 
                arr[i] = arr[j]; 
                arr[j] = temp; 
            } 
        } 
  
        // swap arr[i+1] and arr[high] (or pivot) 
        BigInteger temp = arr[i+1]; 
        arr[i+1] = arr[high]; 
        arr[high] = temp; 
  
        return i+1; 
    } 
  static void writeByteArraytoFile(byte [] bytes, String filename){
 try { 
     
            File file = new File(filename); 
            // Initialize a pointer 
            // in file using OutputStream 
            OutputStream 
                os 
                = new FileOutputStream(file, true); 
  
            // Starts writing the bytes in it 
            os.write(bytes); 
            
  
            // Close the file 
            os.close(); 
        } 
  
        catch (Exception e) { 
            System.out.println("Exception: " + e); 
        } 

  }
  static void printBigIntegerArray(BigInteger arr []){
      for(int i=0;i<arr.length;i++){
          System.out.println(arr[i].toString());
      }
  }
  
    /* The main function that implements QuickSort() 
      arr[] --> Array to be sorted, 
      low  --> Starting index, 
      high  --> Ending index */
    static void sort(BigInteger arr[], int low, int high) 
    { 
        if (low < high) 
        { 
            /* pi is partitioning index, arr[pi] is  
              now at right place */
            int pi = partition(arr, low, high); 
  
            // Recursively sort elements before 
            // partition and after partition 
            sort(arr, low, pi-1); 
            sort(arr, pi+1, high); 
        } 
    } 


    private String replaceCharUsingCharArray(String str, char ch, int index) {
    char[] chars = str.toCharArray();
    chars[index] = ch;
    return String.valueOf(chars);
}

    //not inclusive of start string
    public static void printPermutations( String start, int numIncrement){
        BigInteger [] array  = new BigInteger [numIncrement];
        ArrayList <Integer> indexes = new ArrayList <Integer>();
         char[] charset = new char[input.length()]; 
  
        // Copy character by character into array 
        for (int i = 0; i < input.length(); i++) { 
            charset[i] = input.charAt(i); 
        } 

        for (int i=start.length()-1;i>=0;i--){
            indexes.add(findIndexinCharset(start.charAt(i)));
        }
    /*
        for (int i=0;i<indexes.size();i++){
            System.out.print(indexes.get(i) + ", ");
        }
        System.out.println("\n");
        */
        /*
        char last = start.charAt(start.length()-1);

        int indexinCharset = findIndexinCharset(last);

        if (indexinCharset==(input.length-1)){
            //increment prevChar
        }
        else{
            int nextCharIndex = indexinCharset+1;
            char nextChar = charset[nextCharIndex];
            start = replaceCharUsingCharArray(start, nextChar, start.length-1);
        }
        
*/      
        for (int z =0;z<numIncrement;z++){
        boolean increase = false;
        for (int i=indexes.size()-1;i>=0;i--){
            
            if ((i==0)&& increase ==true){
                indexes.add(0);
                
            }
            if ((i==(indexes.size()-1))&&(indexes.get(i) == input.length()-1)){
                increase = true;
                indexes.set(i,0);
                if (i==0){
                    indexes.add(0);
                }
            }
            else if(i==(indexes.size()-1)){
                indexes.set(i,indexes.get(i)+1);
                increase = false;
               
                break;
            }
            else if(increase ==true){
                if (indexes.get(i) == input.length()-1){
                increase = true;
                indexes.set(i,0);
                }
                else{
                    indexes.set(i,indexes.get(i)+1);
                increase = false;
                break;
                }
            }
           
        }

        char [] str = new char [indexes.size()];
for (int i=indexes.size()-1;i>=0;i--){
str[i] = input.charAt(indexes.get(i));

}
String string = new String(str);
  File file = new File("file.dat"); 
    NTLMPassword ntlm = new NTLMPassword();
  byte [] bytes = ntlm.encodeBytes(string);
  placeInBucket(bytes);
  //array[z] = new BigInteger(1,bytes);


  /*
 try { 
     
  
            // Initialize a pointer 
            // in file using OutputStream 
            OutputStream 
                os 
                = new FileOutputStream(file, true); 
  
            // Starts writing the bytes in it 
            os.write(bytes); 
            
  
            // Close the file 
            os.close(); 
        } 
  
        catch (Exception e) { 
            System.out.println("Exception: " + e); 
        } 

        */


    }
    /*
    System.out.println("Sorting");
        sort(array, 0, array.length-1);
        printBigIntegerArray(array);*/
    }
    private static byte[][] convertToArray(LinkedList <byte []> list){
        byte [][] arr = new byte[list.size()][];
 arr=list.toArray(arr);
 return arr;
    }

    private static byte [] convert1d(byte [] [] input){
        int z=0;
        byte [] arr = new byte [input.length*input[0].length];
        for (int i=0;i<input.length;i++){
            for (int j=0;j<input[0].length;j++){
                arr[z] = input[i][j];
               z++; 
            }
        }
        return arr;
    }


   private static int binarySearch(BigInteger arr[], int l, int r, BigInteger x) { 
        
        if (r >= l) { 
             int mid = l + (r - l) / 2; 
             
            // System.out.println(mid);
            // If the element is present at the 
            // middle itself 
            if (arr[mid].compareTo(x)==0) 
                return mid; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (arr[mid].compareTo(x) == 1) 
                return binarySearch(arr, l, mid - 1, x); 
  
            // Else the element can only be present 
            // in right subarray 
            return binarySearch(arr, mid + 1, r, x); 
        }
        else{ 
        int mid = l + (r - l) / 2;

        if (mid > arr.length-1){
            return arr.length-1;
        }

        if (mid<0){
            return 0;
        }
        // We reach here when element is not present 
        // in array 
         if (arr[mid].compareTo(x)==0) 
                return mid; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (arr[mid].compareTo(x) == 1){ 
                if (mid > 0){
                return (mid-1);
                }
                return 0;
            }
            // Else the element can only be present 
            // in right subarray 
            return mid;
        }
    } 
    public static void writeToFiles(){
        
        for (int i=0;i<buckets.length;i++){
            writeByteArraytoFile(convert1d(convertToArray(rootList[i])), i+".dat");
        }
        


    }

    public static void verifyFile(String inputFilename, String outputFilename){
     
        LinkedList <BigInteger> temp = new LinkedList <BigInteger> ();

     try{
                    byte[] b = new byte[16];
                    InputStream is = new FileInputStream(inputFilename);
                    OutputStream os = new FileOutputStream(outputFilename);

                    int readBytes = 0;

                    while ((readBytes  = is.read(b)) != -1) {
                       BigInteger binteger =  new BigInteger(1,b);
                        System.out.println(binteger.toString());
                      temp.add(new BigInteger(1,b));
                    }
                    is.close();
                    os.close();

            }catch(IOException ioe){
                System.out.println("Error "+ioe.getMessage());
            }        

    }
   public static void main (String argvs []){

      // int arr [] = {1,3,6,8,9,11,16};
       // int n = arr.length; 
       // int x = 0; 
//System.out.println(binarySearch(arr, 0, n - 1, x)); 
        NTLMPassword ntlm = new NTLMPassword();
        //System.out.println(ntlm.encode("tommytripod"));
        
        //not inclusive of start string
        //TODO Will not work for empty start string.

      instantiateBuckets(2000);
      System.out.println(buckets[1999].toString()+"\n");
       
        printPermutations("a",5000000);
      //  System.out.println  ("Converting");
        
       writeToFiles();


      verifyFile("1999.dat","asd.txt");

       /*
        File file = new File("file.dat");
byte[] fileContent = new byte [0];
    try{
       fileContent = Files.readAllBytes(file.toPath());
    }catch(Exception e){
        e.printStackTrace();
    }
    try{
compress(fileContent);
    }catch(Exception e){
        e.printStackTrace();
    }
    System.out.println(fileContent.length);
    */
/*
        String input = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
       
         char[] charset = new char[input.length()]; 
  
        // Copy character by character into array 
        for (int i = 0; i < input.length(); i++) { 
            charset[i] = input.charAt(i);
            System.out.println((int) charset[i]); 
        } 

        ntlm.printAllKLength(charset, "", );
        */
     
       /*
       String input = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
       
         char[] charset = new char[input.length()]; 
  
        // Copy character by character into array 
        for (int i = 0; i < input.length(); i++) { 
            charset[i] = input.charAt(i);
            System.out.println((int) charset[i]); 
        } 

     printAllKLength(charset, 5);
*/
    }
}
