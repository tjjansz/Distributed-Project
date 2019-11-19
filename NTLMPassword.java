import jcifs.smb.NtlmPasswordAuthentication;
import java.io.FileWriter;
import java.io.File;

/**
 * NTLM passwords encoding.
 * 
 * This implementation depends on the JCIFS library.
 */
public class NTLMPassword {
    private static int counter = 0;
     private static int secondaryCounter = 0;
     private static String storage [] = new String [10000000];

    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public NTLMPassword() {
        //throw new UnsupportedOperationException("Can not instantiate this class.");
    }

    public static byte[] encodeBytes(String value){
         String s = (value != null) ? value : "";
        NtlmPasswordAuthentication test = new NtlmPasswordAuthentication("");
        byte[] hash = test.nTOWFv1(s);
        return hash;

    }
    public static String encode(String value) {
        String s = (value != null) ? value : "";
        NtlmPasswordAuthentication test = new NtlmPasswordAuthentication("");
        byte[] hash = test.nTOWFv1(s);
        return bytesToHex(hash).toUpperCase();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }



    public static void twoChar(){
                int counter = 0;
               String input = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        System.out.println(input.length());
         char[] charset = new char[input.length()]; 
  
        // Copy character by character into array 
        for (int i = 0; i < input.length(); i++) { 
            charset[i] = input.charAt(i); 
        } 

        for (int i=0;i<charset.length;i++){
            for (int j=0;j<charset.length;j++){
                String str = Character.toString(charset[i]) + Character.toString(charset[j]);
            System.out.println(str + ":" + encode(Character.toString(charset[i]+ charset[j])));
            counter++;
            }
        }
        System.out.println(counter);

    }


public static void printAllKLengthRec(char set[], String prefix, int k) 
{ 
    
    // Base case: k is 0, 
    // print prefix 
    if (k == 0) 
    { 
        
        storage[counter] = prefix+":"+encode(prefix);
        counter++;
        
        if (counter==10000000){

                       System.out.println("Writing to File");

try{
            FileWriter fw = new FileWriter("file.dat");
   

    for (int i = 0; i < storage.length; i++) {
      fw.write(storage[i] + "\n");
    }
    fw.close();
}catch(Exception e){}
                /*
                  try{
                secondaryCounter++;
            File file = new File("append.txt");
FileWriter fr = new FileWriter(file, true);
fr.write(secondaryCounter + " million\n");
fr.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            */
           /*
            System.out.println("Writing to file");
            FileWriter fw = new FileWriter("file.dat");
   

    for (int i = 0; i < storage.length; i++) {
      fw.write(strs[i] + "\n");
    }
    fw.close();
    */
  }
            /*
            try{
                secondaryCounter++;
            File file = new File("append.txt");
FileWriter fr = new FileWriter(file, true);
fr.write(secondaryCounter + " million\n");
fr.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            */
         
        
        return; 
    } 
  
    // One by one add all characters  
    // from set and recursively  
    // call for k equals to k-1 
    for (int i = 0; i < set.length; i++) 
    { 
        String newPrefix; 
          
        // Next character of input added 
        newPrefix = prefix + set[i]; 
          
        // k is decreased, because  
        // we have added a new character 
        printAllKLengthRec(set, newPrefix, k - 1); 
    } 
  
} 
    public static void printAllKLength(char set[], int k) 
{ 
    printAllKLengthRec(set, "", k); 
} 
    public static void oneChar(){
            String input = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        System.out.println(input.length());
         char[] charset = new char[input.length()]; 
  
        // Copy character by character into array 
        for (int i = 0; i < input.length(); i++) { 
            charset[i] = input.charAt(i); 
        } 

        for (int i=0;i<charset.length;i++){
            System.out.println(charset[i] +":" + encode(Character.toString(charset[i])));
            
        }
    }



}