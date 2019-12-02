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
    
    final int CODE_INIT = 110; //To Server to initialize node
    final int CODE_SEARCH_FINISHED = 111; //To Server block generation compelete and stored
    final int CODE_GEN_FINISHED = 112; //To Server block generation compelete and stored
    final int CODE_SEARCH = 113; //From Server sent hash to resolve
    final int CODE_GENERATE = 114; //From Server sent block for work
    final int CODE_SEARCH_FAILED = 115; //To Server hash resolution failed
    final int CODE_GEN_FAILED = 116; //To Server block generation failed
    final int CODE_READY = 117; //To Server to notify ready
    

    //for generating queue list
    private static ArrayList <String> queueList = new ArrayList <String>();
	private static int counter = 0;
	private static boolean flag = false;
	private static String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private String workerID;
    private String intent;


    private static void queueListBuilder(){
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



	static String getNextinQueue() {

		File file = new File("QL.txt");

		//returns null if file empty
		if (file.length()==0){
			return null;
		}

		System.out.println("Running");
		String line = null;
		ArrayList<String> list = new ArrayList<String>();
		System.out.println("Reading File");
		try {
			Scanner s = new Scanner(new File("QL.txt"));

			while (s.hasNextLine()) {
				list.add(s.nextLine());
			}

			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		line = list.get(0);
		list.remove(0);


	try {
		FileWriter fw = new FileWriter("QL.txt",false);
		
		for (int i=0;i<list.size();i++) {
			fw.write(list.get(i) +"\n");
		}
		fw.close();
		
	}catch(Exception e) {
		e.printStackTrace();
	}

	return line;
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
        try{
            String received; 
            String toreturn; 
            String currentStr = null;
            
            int initCode = dis.readInt();
            workerID = dis.readUTF();
            intent = dis.readUTF();

            

            File searchFile = new File("search.txt");
            if(!searchFile.exists()){
                PrintLine("Not Exists, creating new search file");
                searchFile.createNewFile();
            }
            Scanner scn = new Scanner(searchFile); //For checking file

            if(intent.equals("SEARCH")){
                String term = dis.readUTF();
                String text = (term + "\n");
            
                BufferedWriter writer = new BufferedWriter(new FileWriter(searchFile, false));  
                writer.write(text);
                writer.close();
            }
            else if(intent.equals("GEN")){

            }
            Print(workerID);
            
            while (!scn.hasNext())  
            { 
                Print("Scn doesnt have next");

                int code = dis.readInt();
                Print(code + "\n");
                switch(code){
                    case CODE_SEARCH_FINISHED:
                        String decoded = dis.readUTF();
                        PrintLine(decoded);
                        break;
                    case CODE_GEN_FINISHED:
                        writeCompleted(currentStr, workerID);
                        break;
                    case CODE_SEARCH_FAILED:
                        break;
                    case CODE_GEN_FAILED:
                        String failed = dis.readUTF();
                        //Add back to queue
                        break;
                    case CODE_READY:
                        dos.writeInt(CODE_GENERATE);
                        //Pop from work queue
                        //Check BL
                        //Send to client if not complete
                        currentStr = getNextinQueue();
                        dos.writeUTF(currentStr);
                        //Wait for completion at dis.readInt()
                        
                        break;
                    default:
                        break;
                }


            }
            scn.close();
            while (!(searchFile.length() == 0))  
            { 
                Scanner scn2 = new Scanner(searchFile); //For checking file
                String term = scn2.nextLine();

                int code = dis.readInt();
                Print(code + "\n");
                switch(code){
                    case CODE_SEARCH_FINISHED:
                        String decoded = dis.readUTF();
                        PrintWriter pw = new PrintWriter("search.txt");
                        pw.close();
                        PrintLine(decoded);
                        break;
                    case CODE_GEN_FINISHED:
                        
                        writeCompleted(currentStr, workerID);
                        break;
                    case CODE_SEARCH_FAILED:
                        break;
                    case CODE_GEN_FAILED:
                        String failed = dis.readUTF();
                        //Add back to queue
                        break;
                    case CODE_READY:
                        dos.writeInt(CODE_SEARCH);
                        dos.writeUTF(term);
                        break;
                    default:
                        break;
                }


            }
            // Print("Scn does have next"); 
            
            
            // try
            // { 
            //     // closing resources 
            //     this.dis.close(); 
            //     this.dos.close(); 
                
            // }catch(IOException e){ 
            //     e.printStackTrace(); 
                
            // }


        
        }catch(IOException e){
            e.printStackTrace();
        }
    }
} 