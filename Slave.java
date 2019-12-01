import java.io.*;
import java.util.*;
import java.text.*; 

class Slave 
{ 
    BufferedReader reader;
    
    public static void main(String arg[])throws Exception 
    {
        File file = new File("nodeInfo.txt");
        if(file.exists()){
            Print("Exists");
        }
        else {
            Print("Not Exists, creating new node info file");
            file.createNewFile();
        }
        String str = "Hello";

            Date date = new Date(); 
            DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
            DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
            String dateStr = fordate.format(date);
            String timeStr = fortime.format(date);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(dateStr + "\n");
        writer.write(timeStr);
        
        
        String string[][] = new String[4][2];
        MakeArr(string);
        String unwrapped  = Unwrapper(string);
        writer.write(unwrapped);

        
        Scanner scanner = new Scanner(file);
        String line;
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            Print(line);
        }
        
        // FileWriter fr = new FileWriter(file, true);
        // fr.write(id+"\n");
        // fr.close();
        writer.close();
    }

    static public void Print(String str){
        System.out.println(str);
    }

    static public String Unwrapper(String[][] string){
        String str = "";
        for(int i = 0 ; i < 4; i++){
            str += "\n";
            str += string[i][0];
            str += ",";
            str += string[i][1];
        }
        return str;
    }

    static public void MakeArr(String[][] string){
        for(int i = 0 ; i < 4; i++){
            for(int j = 0; j < 2; j++){
                string[i][j] = Integer.toString(i + j*50);
            }
        }
    }
}