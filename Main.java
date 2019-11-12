import java.util.ArrayList;

public class Main {
    private static String input = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private static int findIndexinCharset(char c){
        for (int i=0;i<input.length();i++){
            if (c == input.charAt(i)){
                return i;
            }
        }
        return -1;
    }

    private String replaceCharUsingCharArray(String str, char ch, int index) {
    char[] chars = str.toCharArray();
    chars[index] = ch;
    return String.valueOf(chars);
}

    //not inclusive of start string
    public static void printPermutations( String start, int numIncrement){
        
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
System.out.println(string);
        }


    }
   public static void main (String argvs []){

        NTLMPassword ntlm = new NTLMPassword();
        //System.out.println(ntlm.encode("tommytripod"));
        
        //not inclusive of start string
        //TODO Will not work for empty start string.
        printPermutations("a",1000);

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