/*
Client-side(node) that connects to Server
*/
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
    final static int CODE_STALL = 118; //From Server to notify no work
    
    
	// Creates file structure and generates first block
	static HashGenerator init() {
		 HashGenerator hgen = new HashGenerator(200*1024);
		 hgen.generate(""); 
		 hgen.writeToFiles();
		 return hgen;
	}
    
    /**
     * Searches the hash given in hex value
     * @param hexStr hash to be searched
     * @return either "not found" or the result exists and the result of readString method is returned
     */
	static String search(String hexStr) {
		BigInteger bigInt = new BigInteger(hexStr, 16);
		return Search.searchAll(bigInt.toByteArray());
	}

    public static void main(String argvs[]) {
		HashGenerator test = new HashGenerator(200*1024);
        String id = test.getId();
		 
        try{ 
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost"); 
      
            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 5056); 
      
            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      
            // the following loop performs the exchange of 
            // information between client and client handler 
            System.out.println("writing init code");
            dos.writeInt(CODE_INIT); //CODE_INIT
            System.out.println("writing id");
            dos.writeUTF(id);

            System.out.println("writing ready code");
            dos.writeInt(CODE_READY);
            
            while (true)  
            {     
                System.out.println("Waiting for code...");
                int code = dis.readInt();
                System.out.println("Reading code");
                Print("Received code: " + codeToString(code) + "\n");
                switch(code){
                    case CODE_SEARCH:
                        String target = dis.readUTF();

                        //Generate rainbow fragment          
                        System.out.println("Starting Search");
			            String result = search(target);
                        
                        System.out.println("Done Search");
                        if (!result.equals("Not Found")){
                            dos.writeInt(CODE_SEARCH_FINISHED);
                            dos.writeUTF(result);
                            Print("Finished and retrieved string: " + result + "\n");
                            dos.writeInt(CODE_READY);
                        }else{
                            dos.writeInt(CODE_SEARCH_FAILED);
                            dos.writeInt(CODE_READY);
                        }
                        break;
                    case CODE_GENERATE:
                        String start = dis.readUTF();
                        //Generate rainbow fragment
                        Print(start);
                        try{
                            Print("Generating Block");
                            test.generate(start); 
		                    test.writeToFiles();
                            Print("Finished");
                            dos.writeInt(CODE_GEN_FINISHED);
                            dos.writeInt(CODE_READY);
                        }catch (Exception e) {
                            dos.writeInt(CODE_GEN_FAILED);
                            dos.writeUTF(start);
                        }
                        break;
                    case CODE_STALL:
                        dos.writeInt(CODE_READY);
                        break;
                    default:
                        break;
                }
                
            } 

        }catch(Exception e){ 
            e.printStackTrace();
            
        } 
    }
    
    static void Print(String str){
        System.out.println(str);
    }

    // Returns the string representation of the integer code
    static String codeToString(int code){
        switch(code){
            case 110:
                return "CODE_INIT";
            case 111:
                return "CODE_SEARCH_FINISHED";
            case 112:
                return "CODE_GEN_FINISHED";
            case 113:
                return "CODE_SEARCH";
            case 114:
                return "CODE_GENERATE";
            case 115:
                return "CODE_SEARCH_FAILED";
            case 116:
                return "CODE_GEN_FAILED";  
            case 117:
                return "CODE_READY";
            case 118:
                return "CODE_STALL";
            default:
                return "NOT_A_CODE";
        }
    }
}