package helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellHelper {

	public static int executeCommand(String command) {
		StringBuffer output = new StringBuffer();
		 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();

            return p.exitValue();
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return -1;
	}
	
}
