
import java.math.BigInteger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

public class Main {
	//creates file structure and generates first block
	static HashGenerator init() {
		 HashGenerator hgen = new HashGenerator(200*1024);
		 hgen.generate(""); 
		 hgen.writeToFiles();
		 return hgen;
	}
	
	static String search(String hexStr) {
		BigInteger bigInt = new BigInteger(hexStr, 16);
		return Search.searchAll(bigInt.toByteArray());
	}
	public static void main(String argvs[]) {
		HashGenerator test = new HashGenerator(-1);
		 try
        { 
            Scanner scn = new Scanner(System.in); 
              
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost"); 
      
            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 5056); 
      
            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
      
            // the following loop performs the exchange of 
            // information between client and client handler 
            while (true)  
            { 
                while (true){
               System.out.println("Sending Id");
                String tosend = test.getId();
                dos.writeUTF(tosend); 
                System.out.println(dis.readUTF());
                }
                  
                // If client sends exit,close this connection  
                // and then break from the while loop 
                
            } 
              
            // closing resources 
            /*
            scn.close(); 
            dis.close(); 
            dos.close(); 
            */
        }catch(Exception e){ 
            e.printStackTrace();
            
        } 
	}
}

