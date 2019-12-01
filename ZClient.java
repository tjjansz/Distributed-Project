import org.zeromq.ZMQ;
import java.util.Scanner;

public class ZClient {
	//java -classpath C:\Users\100631883\Desktop\Tools\JeroMQ\jeromq.jar; ZServer
	//java -classpath C:\Users\100631883\Desktop\Tools\JeroMQ\jeromq.jar; ZClient
	//javac -classpath C:\Users\100631883\Desktop\Tools\JeroMQ\jeromq.jar *.java

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:5555");

        Scanner scn = new Scanner(System.in);


        System.out.println("Please enter a string value: ");
        String strVal = scn.nextLine();
        //String request = String.valueOf(intVal);;

        System.out.println("Sending \"" + strVal + "\"");

        System.out.println("Hashed value of " + strVal + ": ");
        requester.send(strVal.getBytes(ZMQ.CHARSET), 0);
        System.out.println(new String(requester.recv(0), ZMQ.CHARSET));

        requester.close();
        context.term();
    }
}