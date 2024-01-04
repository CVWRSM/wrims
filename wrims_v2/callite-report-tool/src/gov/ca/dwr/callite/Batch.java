package gov.ca.dwr.callite;

import java.io.File;

public class Batch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			System.out.println("Processing Inp file named: \n"+args[0]);
			
			Report report = new Report(args[0]);

			
			String msgs = Utils.getMessages();
			if (msgs.length() > 0) {
				System.out.println(msgs);
			}
			
			System.out.println("Done!\n");

		}
		catch (Exception ex) {
			// ignore
		}

	}
}
