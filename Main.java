
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
    final static int CODE_INIT = 110; //To Server to initialize node
    final static int CODE_SEARCH_FINISHED = 111; //To Server block generation compelete and stored
    final static int CODE_GEN_FINISHED = 112; //To Server block generation compelete and stored
    final static int CODE_SEARCH = 113; //From Server sent hash to resolve
    final static int CODE_GENERATE = 114; //From Server sent block for work
    final static int CODE_SEARCH_FAILED = 115; //To Server hash resolution failed
    final static int CODE_GEN_FAILED = 116; //To Server block generation failed
    final static int CODE_READY = 117; //To Server to notify ready
    
    
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
		HashGenerator test = new HashGenerator(200*1024);
        String id = test.getId();
		 
        try{ 
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
            dos.writeInt(CODE_INIT); //CODE_INIT
            dos.writeUTF(id);

            dos.writeInt(CODE_READY);//CODE_READY
            while (true)  
            {     
               int code = dis.readInt();
               Print(Integer.toString(code) + "\n");
               switch(code){
                    case CODE_SEARCH:
                        String target = dis.readUTF();
                        //Generate rainbow fragment
                        if (true){
                            dos.writeInt(CODE_SEARCH_FINISHED);
                            dos.writeUTF("decoded text");
                        }else{
                            dos.writeInt(CODE_SEARCH_FAILED);
                        }
                        break;
                    case CODE_GENERATE:
                        String start = dis.readUTF();
                        //Generate rainbow fragment
                        Print(start);
                        try{
                            test.generate(start); 
		                    test.writeToFiles();
                            dos.writeInt(CODE_GEN_FINISHED);
                            Print("Finished");
                        }catch (Exception e) {
                            dos.writeInt(CODE_GEN_FAILED);
                            dos.writeUTF(start);
                        }
                        break;
                    default:
                        break;
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
    static void Print(String str){
        System.out.println(str);
    }
}

