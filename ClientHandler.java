/*
This class is the thread that is created by the Server when a new Client is accepted
Performs control flow with client and communicates with other threads via files and synchronized methods
*/
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
    boolean search;
    
    final static int CODE_INIT = 110; //To Server to initialize node
    final static int CODE_SEARCH_FINISHED = 111; //To Server block generation compelete and stored
    final static int CODE_GEN_FINISHED = 112; //To Server block generation compelete and stored
    final static int CODE_SEARCH = 113; //From Server sent hash to resolve
    final static int CODE_GENERATE = 114; //From Server sent block for work
    final static int CODE_SEARCH_FAILED = 115; //To Server hash resolution failed
    final static int CODE_GEN_FAILED = 116; //To Server block generation failed
    final static int CODE_READY = 117; //To Server to notify ready
    final static int CODE_STALL = 118; //From Server to notify no work
    
    //for generating queue list
    private static ArrayList <String> queueList = new ArrayList <String>();
	private static int counter = 0;
	private static boolean flag = false;
	private static String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    private String workerID;
    private String intent;

    // Method that generates the queue within a text file
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

    /**
     * Find the index of a given character
     * @param c character to be searched
     * @return index of the character OR return -1 if character is not found
     */
    private static int findIndexinCharset(char c) {
		for (int i = 0; i < charset.length(); i++) {
			if (c == charset.charAt(i)) {
				return i;
			}
		}
		return -1;
    }
    
    // Generates n permutations of character set hashes to be placed in a bucket
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

    // Get the next item in the queue
	static synchronized String getNextinQueue() {

		File file = new File("QL.txt");

		// returns null if file empty
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
            
        } catch(Exception e) {
            e.printStackTrace();
        }
            return line;
    }

    // Check if ID exits
    private static synchronized boolean idExists(String id) {
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

    // Add the ID into connections.txt file
    private static synchronized void addId(String id){
        File file = new File("connections.txt");
		try {
			FileWriter fr = new FileWriter(file, true);
			fr.write(id+"\n");
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * This method creates a file that contains a list of blocks
     * and checks if a certain block is completed by workers
     * @param start The name of the block
     * @return True or false depending on whether the block is completed or not
     * @throws IOException
     */
    public static synchronized Boolean checkCompleted(String start) throws IOException {
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
            String message = new String("Block starting with \"" + start + "\" has not yet been completed and will be assigned\n");
            PrintLine(message);
            return false;
        }
    }

    /**
     * Write the name of the block and the worker thread's ID into the BlockList.txt file
     * @param start the name of the block
     * @param workerID worker thread ID
     * @return  True when write operation is completed
     * @throws IOException
     */
    public static Boolean writeCompleted(String start, String workerID) throws IOException {
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

    // Implemented quicker print methods to save time
    public static void Print(String str){
      System.out.print(str);
    } 

    public static void PrintLine(String str){
      System.out.println(str);
    } 

    // Create a search.txt file if it doesn't already exist
    private static synchronized void checkSearch() throws IOException{
        File searchFile = new File("search.txt");
        if(!searchFile.exists()){
            PrintLine("Not Exists, creating new search file");
            searchFile.createNewFile();
        }
    }

    /**
     * Read the misses.txt file and increment the number of misses represented by count
     * @return Total number of misses
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static synchronized int incrementMisses() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("misses.txt")); 
        int count = Integer.parseInt(br.readLine()); 
        br.close();
        count++;
        FileWriter fw = new FileWriter("misses.txt",false);
        fw.write(count + "");
        fw.close();
        return count;
    }

    // Write the text to the search.txt file
    private static synchronized void writeToSearchFile(String text) throws IOException {
        File searchFile = new File("search.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(searchFile, false));  
        writer.write(text);
        writer.close();
    } 

    // Return true if the search.txt file is empty, false if it is not
    private static synchronized boolean isSearchEmpty(){
        File searchFile = new File("search.txt");
        if (searchFile.length()==0){
            return true;
        }
        return false;
    }

    // Returns true if the scanner in the search.txt file has another token
    private static synchronized boolean searchHasNext()throws FileNotFoundException {
        File searchFile = new File("search.txt");
         Scanner scn = new Scanner(searchFile); //For checking file
         boolean temp = scn.hasNext();
         scn.close();
         return temp;
    }

    // Return the string read from search.txt
    private static synchronized String searchString() throws FileNotFoundException {
        File searchFile = new File("search.txt");
        Scanner scn2 = new Scanner(searchFile); //For checking file
            String term = scn2.nextLine();
            scn2.close();
            return term;
    }

    private static synchronized void clearSearchFile() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("search.txt");
        pw.close();
    }
 
    // Return the number of connections by reading connections.txt file
    private static synchronized int getNumConnections()throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader("connections.txt"));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.search = search;
        
    } 
  
    @Override
    public void run()  
    { 
        try{
            String received; 
            String toreturn; 
            String currentStr = null;
            System.out.println("Reading init code");
            int initCode = dis.readInt();
            System.out.println("Reading id");
            workerID = dis.readUTF();
            addId(workerID);

            checkSearch();
            Print(workerID);
            
            while(true){
                if(!searchHasNext()){
                    try{
                        currentStr = getNextinQueue();
                        if(currentStr != null){
                            GenerateCmd(currentStr);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else if(searchHasNext()){
                    try{
                        String term = searchString();
                        SearchCmd(term);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void GenerateCmd(String currentStr) throws Exception{
        while (!searchHasNext())  
        { 
            System.out.println("Search File Empty. Awaiting code...");
            int code = dis.readInt();
 
            Print("Received code: " + codeToString(code) + "\n");

            switch(code){
                case CODE_GEN_FINISHED: //Took out CODE_SEARCH_FINISHED/FAILED option (never used)
                    writeCompleted(currentStr, workerID);
                    Print("Worker " + workerID + " generated block: " + currentStr + "\n");
                    return;
                case CODE_GEN_FAILED:
                    String failed = dis.readUTF();
                    //Add back to queue
                    break;
                case CODE_READY:
                    
                    if (currentStr == null){
                        Print("Queue is empty\n");
                        dos.writeInt(CODE_STALL);
                        return;
                    }
                    System.out.println("Writing generate code CODE_GENERATE");
                    dos.writeInt(CODE_GENERATE);
                    //Pop from work queue
                    //Check BL
                    //Send to client if not complete
                    dos.writeUTF(currentStr);
                    
                    //Wait for completion at dis.readInt()
                    
                    break;
                default:
                    break;
            }
        }

    }

    //waits for client to respond
    private void SearchCmd(String term) throws Exception{
        while (!isSearchEmpty())  
        { 
            int code = dis.readInt();
            Print(code + "\n");
            switch(code){
                case CODE_SEARCH_FINISHED: //Took out CODE_GEN_FINISHED/FAILED option (never used)
                    String decoded = dis.readUTF();

                    clearSearchFile();
                    createFile("misses.txt","0");
                    PrintLine("Retrieved cleartext value: " + decoded);
                    return; //Return once seach is completed
                case CODE_SEARCH_FAILED:
                    if (incrementMisses() == getNumConnections()){
                        System.out.println("Search String Not Found");
                        clearSearchFile();
                        createFile("misses.txt","0");
                    }
                    return;
                case CODE_READY:
                    dos.writeInt(CODE_SEARCH);
                    dos.writeUTF(term);
                    break;
                default:
                    break;
            }
        }
    }

    //creates file (synchronized) method
    private static synchronized void createFile(String filename, String content) {
		try {
            File file = new File(filename);
            FileWriter fw = new FileWriter(file,false);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

    // Return the string representation of the integer code
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