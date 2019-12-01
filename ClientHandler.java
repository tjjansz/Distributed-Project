import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
// ClientHandler class 
class ClientHandler extends Thread  
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
      
    private static synchronized boolean idExists(String id){
        try {
			Scanner scanner = new Scanner(new File("nodes.txt"));
			while (scanner.hasNextLine()) {
				if (id.equals(scanner.nextLine())){
                    return true;
                }
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        return false;
    }
    private static synchronized void addId(String id){
        File file = new File("nodes.txt");
		try {
			FileWriter fr = new FileWriter(file, true);
			fr.write(id+"\n");
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

 
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    @Override
    public void run()  
    { 
        String received; 
        String toreturn; 
        while (true)  
        { 
            try { 
                
                  
                // receive the answer from client 
                received = dis.readUTF(); 
                  while (!received.equals("Exit")){
                  if (!idExists(received)){
                      dos.writeUTF(received);
                      addId(received);
                  }
                  else{
                    dos.writeUTF("exists");  
                  }
                  try{
                  Thread.sleep(1000);
                  }catch(Exception e){}
                  }
             
                  
                // creating Date object 
                Date date = new Date(); 
                DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
                DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
                fordate.format(date); 
                fortime.format(date); 
                  
                // write on output stream based on the 
                // answer from the client 
                switch (received) { 
                  
                    case "Date" : 
                        toreturn = fordate.format(date); 
                        dos.writeUTF(toreturn); 
                        break; 
                          
                    case "Time" : 
                        toreturn = fortime.format(date); 
                        dos.writeUTF(toreturn); 
                        break; 
                          
                    default: 
                        dos.writeUTF("Invalid input"); 
                        break; 
                } 
            } catch (IOException e) { 
                System.out.println(e);
            break;
            } 
        } 
          
        try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
            
        }


    }

} 