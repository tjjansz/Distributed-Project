import java.io.*; 
import java.io.DataInputStream; 
import java.net.*;
import java.util.*;
 
class TCPClient { 
    public static void main(String arg[])throws Exception { 
        try {
            Scanner scn = new Scanner(System.in);

            //InetAddress ip = InetAddress.getByName("localhost");
            Socket server=new Socket("127.0.0.1", 5056); 
            DataInputStream dis=new DataInputStream(server.getInputStream());         
            DataOutputStream dos=new DataOutputStream(server.getOutputStream());

            PrintLine("Welcome to our server! Input <quit> to exit!"); // Messages to client on starts
            System.out.println("Login---------------------- ");
            Print("Please input your username:"); // Receive and send username. 
            String toSend = scn.nextLine();
            System.out.println("Your username: " + toSend);
            dos.writeUTF(toSend);

            while(true){
                int message = dis.readInt();
                if(message == 999){
                    break;
                }
                PrintLine(Integer.toString(message));
                dos.writeInt(1);
            }

            scn.close(); 
            dis.close(); 
            dos.close(); 
		}catch (UnknownHostException e){
			System.out.println("Sock:"+e.getMessage()); 
		}catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){
			System.out.println("IO:"+e.getMessage());
		}
    }
    public static void clearScreen() throws IOException, InterruptedException{  
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 
    }
    public static void Print(String str){ //Implemented quicker print methods to save time
      System.out.print(str);
    } 
    public static void PrintLine(String str){
      System.out.println(str);
    } 
}