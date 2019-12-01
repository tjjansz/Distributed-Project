import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.lang.*; 
// ClientHandler class 
class ClientHandler extends Thread
{ 

    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 

    //for generating queue list
    private static ArrayList <String> queueList = new ArrayList <String>();
	private static int counter = 0;
	private static boolean flag = false;
	private static String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";


    private static queueListBuilder(){
        generateFunc("",5000000);
		File tmpDir = new File("QL.txt");

		boolean exists = tmpDir.exists();

		if (!exists) {
			File file = new File("QL.txt");
			try {
				FileWriter fr = new FileWriter(file);
				
				for (int i=0;i<queueList.size();i++) {
					fr.write(queueList.get(i)+"\n");
				}
				
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    private static int findIndexinCharset(char c) {
		for (int i = 0; i < charset.length(); i++) {
			if (c == charset.charAt(i)) {
				return i;
			}
		}
		return -1;
	}
	private static void generateFunc(String start, int numIncrement) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		if (start.length() == 0) {
			queueList.add("");
			String string = "" + charset.charAt(0);
			numIncrement--;
			flag = true;
			generateFunc(string, numIncrement);
			return;
		}

		for (int i = start.length() - 1; i >= 0; i--) {
			indexes.add(findIndexinCharset(start.charAt(i)));
		}

		for (int z = 0; z < numIncrement; z++) {
			boolean increase = false;
			//
			for (int i = indexes.size() - 1; i >= -1; i--) {
				if ((i == -1) && increase == true) {
					indexes.add(0);
					increase = false;
				}
				if ((i == -1) && increase == false) {
					break;
				}
				// if last character of working string & last character of charset
				if ((i == (indexes.size() - 1)) && (indexes.get(i) ==charset.length() - 1)) {
					increase = true;
					indexes.set(i, 0);

				} else if (i == (indexes.size() - 1)) {

					indexes.set(i, indexes.get(i) + 1);
					increase = false;

					break;
				} else if (increase == true) {

					if (indexes.get(i) == charset.length() - 1) {
						increase = true;
						indexes.set(i, 0);
					} else {
						indexes.set(i, indexes.get(i) + 1);
						increase = false;
						break;
					}
				}

			}

			char[] str = new char[indexes.size()];
			for (int i = indexes.size() - 1; i >= 0; i--) {
				str[i] = charset.charAt(indexes.get(i));

			}
			String string = new String(str);
			
			if (z == 0) {
				counter++;
				if (flag) {
					flag = false;
				}
				else {
					queueList.add(string);
				}
				if (counter == 250) {
					return;
				}
			}
			else if (z == numIncrement -1) {
				generateFunc(string, 5000000);
			}
		}
	}

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

    public static Boolean checkCompleted(String start) throws IOException {
        File file = new File("BlockList.txt");
        if(!file.exists()){
            PrintLine("Not Exists, creating new block list file");
            file.createNewFile();
        }
        Scanner scanner = new Scanner(file);
        String clearText, IDs;
        PrintLine("Checking Block List for the block that starts with: \"" + start + "\"...");
        while(true){
            while(scanner.hasNextLine()){
                clearText = scanner.nextLine();
                IDs = scanner.nextLine();
                if(clearText.equals(start)){
                    String message = new String("\"" + start + "\" marked as completed in block list by workers with ID: " + IDs + "\n");
                    PrintLine(message);
                    return true;
                }
            }
            String message = new String("Block stating with \"" + start + "\" has not yet been completed and will be assigned\n");
            PrintLine(message);
            return false;
        }
    }

    public static Boolean writeCompleted(String start, String workerID) throws IOException{
        String textToAppend = (start + "\n" + workerID);
        File file = new File("BlockList.txt");
        if(!file.exists()){
            PrintLine("Not Exists, creating new block list file");
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(
            new FileWriter(file, true)  //Set true for append mode
        );  
        writer.newLine();   //Add new line
        writer.write(textToAppend);
        writer.close();
        return true;
    }
    public static void Print(String str){ //Implemented quicker print methods to save time
      System.out.print(str);
    } 
    public static void PrintLine(String str){
      System.out.println(str);
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

        try{
            if(!checkCompleted("a")){
                writeCompleted("a", "123e4567-e89b-12d3-a456-426655440000");
            };
            if(!checkCompleted("thomas")){
                writeCompleted("thomas", "123e4567-e89b-12d3-a456-426655440000");
            };
            if(!checkCompleted("m12lk")){
                writeCompleted("m12lk", "123e4567-e89b-12d3-a456-426655440000");
            };
            if(!checkCompleted("new")){
                writeCompleted("new", "123e4567-e89b-12d3-a456-426655440000");
            };
        }catch(IOException e){
            e.printStackTrace(); 
        }
        
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