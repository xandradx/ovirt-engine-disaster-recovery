package helpers;

import play.Logger;

public class ShellHelper {

	public static int executeCommand(String command) {

        Logger.debug("Executing command: %s", command);

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
