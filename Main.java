
import java.math.BigInteger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;

public class Main {

	public static void main(String argvs[]) {
		/*
		  HashGenerator hgen = new HashGenerator(200*1024);
		  hgen.generate("", 5000000); hgen.writeToFiles();
		 */
		
		 NTLMPassword ntlm = new NTLMPassword(); 
		 byte[] bytes =ntlm.encodeBytes("abc"); 
		 BigInteger temp = new BigInteger(1,bytes);
		 System.out.println(Search.searchAll(bytes));
		 
	}
}
