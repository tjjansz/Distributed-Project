import org.zeromq.ZMQ;
import jcifs.smb.NtlmPasswordAuthentication;

public class ZServer {

    public static void main(String[] args) throws Exception {
        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:5555");
        //My code start
        System.out.println("Waiting for connections...");

        while(true) {
            byte[] reply = responder.recv(0);
            String replyString = new String(reply, ZMQ.CHARSET);
            //int replyInt = Integer.parseInt(replyString);
            String[] arr = new String[2];

            //System.out.println("Received \"" + new String(reply, ZMQ.CHARSET) + "\""); STRINGS
            System.out.println("Received \"" + replyString + "\"");
			
			String string = new String(replyString);
			NTLMPassword ntlm = new NTLMPassword();
			String encoded = ntlm.encode(string);


            
            //responder.send("World", 0);

            //String response = Primes(replyInt);
            System.out.println("Sending \"" + replyString + " hashed"  + "\"");
            
            //responder.send(response.getBytes(ZMQ.CHARSET), 0);
			responder.send(encoded.getBytes(ZMQ.CHARSET), 0); 			
        }

 
        //responder.close();
        //context.term();
    }

    public static String Primes(int num){
        String returnString = "2 ";

        if (num < 2){
            returnString = "There are no primes below 2";
            return returnString;
        }

        for (int i = 2; i <= num; i++){
            
            for(int j = 2; j < i; j++){
                if (i % j == 0){
                    break;
                }
                else if (j + 1 == i) {
                    returnString += i + " ";
                }
            }
            
            
        }
        return returnString;
    }
    
}