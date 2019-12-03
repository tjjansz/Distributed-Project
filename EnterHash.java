import java.io.FileWriter;  
import java.util.Scanner;

public class EnterHash {

    public static void main (String argvs []){
	while(true){

        System.out.println("Enter the hash you want to decrypt: ");
        Scanner scanner = new Scanner(System.in);
        String hex = scanner.nextLine();

   try{    
           FileWriter fw=new FileWriter("search.txt");    
           fw.write(hex);    
           fw.close();    
          }catch(Exception e){System.out.println(e);}    
    }
}
}
