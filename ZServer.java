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

            System.out.println("Sending \"" + replyString + " hashed"  + "\"");
            
            //responder.send(response.getBytes(ZMQ.CHARSET), 0);
			responder.send(encoded.getBytes(ZMQ.CHARSET), 0); 			
        }

 
        //responder.close();
        //context.term();
    }

    
}