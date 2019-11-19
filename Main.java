
public class Main {

	public static void main(String argvs[]) {
		HashGenerator hgen = new HashGenerator(200*1024);
		hgen.generate("", 5000000);
		hgen.writeToFiles();
	}
}
