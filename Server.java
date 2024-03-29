/**
 * Java implementation of the Server side 
 */
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

public class Server  
{     
    // For generating queue list
    private static ArrayList <String> queueList = new ArrayList <String>();
	private static int counter = 0;
	private static boolean flag = false;
	private static String charset = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

	// Create text file where the queue list items will be written
	private static void queueListBuilder(){
        
		File tmpDir = new File("QL.txt");
		boolean exists = tmpDir.exists();

		if (!exists || tmpDir.length()==0) {
            System.out.println("Building Queue List");
            generateFunc("",5000000);
			File file = new File("QL.txt");
			try {
				FileWriter fr = new FileWriter(file);
				
				for (int i=0;i<queueList.size();i++) {
					fr.write(queueList.get(i)+"\n");
				}
				
				fr.close();
                System.out.println("...Done");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method returns the index found in charset
	 * @param c character to be searched
	 * @return the index where the char is located
	 */
    private static int findIndexinCharset(char c) {
		for (int i = 0; i < charset.length(); i++) {
			if (c == charset.charAt(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Generates clear text from starting string to a specified incremented number. For input ("a", 3)
	 * Would generate [b,c,d]. It is not inclusive of the starting value
	 * 
	 * @param start The starting string
	 * @param numIncrement Defines the upper limit for the clear text that are generated
	 */
	private static void generateFunc(String start, int numIncrement) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		
		if (start.length() == 0) {
            counter++;
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
			
			if (z == numIncrement-1) {
				counter++;
                System.out.println(counter);
                queueList.add(string);
			
				if (counter == 250) {
					return;
				}
                generateFunc(string, 5000000);
			}
			
		}
	}

	/**
	 * This method creates a file with a specific file name and writes contents
	 * passed into the method as string
	 * 
	 * @param filename the file to be created
	 * @param content the content that will be written in the file
	 */
	private static void createFile(String filename, String content) {
		try {
            File file = new File(filename);
            FileWriter fw = new FileWriter(file,false);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    public static void main(String[] args) throws IOException  
    { 
        // Server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5056); 

        queueListBuilder();
		createFile("connections.txt","");

        createFile("misses.txt","0");
        File searchFile = new File ("search.txt");

        if(!searchFile.exists()){
            System.out.println("Not Exists, creating new search file");
            searchFile.createNewFile();
        }

		System.out.println("Waiting for Connections...");
		
        // Running infinite loop for getting client request 
        while (true)  
        { 
            Socket s = null; 
            try 
            { 
                // Socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // Obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this client"); 
  
                // Create a new thread object 
                Thread t = new ClientHandler(s, dis, dos); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  





 