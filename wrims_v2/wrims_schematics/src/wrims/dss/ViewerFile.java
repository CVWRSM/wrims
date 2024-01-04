package wrims.dss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ViewerFile {
	String fileName = null;
	public HashMap var2value = new HashMap();

	public ViewerFile(String fn) {
		fileName = fn;
	}

	public void write(HashMap hm) {
		String lines[] = { "# comment",
				"# variable and value are separated by whitespace",
				"# value obviously cannot contain whitespace", "",
				"# Specify dss files to operate on",
				"# DV Variables: BaseDV, Comp1DV, Comp2DV, Comp3DV",
				"# SV Variables: BaseSV, Comp1SV, Comp2SV, Comp3SV",

				"" };

		BufferedWriter output = null;

		try {
			output = new BufferedWriter(new FileWriter(fileName));

			for (int i = 0; i < lines.length; i++)
				output.write(lines[i] + "\n");

			String var = null;
			// String val = null;

			Set set = hm.keySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				var = (String) iter.next();
				// System.out.println ( " "+var+" )  "+hm.get(var));
				output.write(var + "\t" + hm.get(var) + "\n");
			}
			output.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void read() {

		String[] temp = null;

		try {
			File file = new File(fileName);
			// System.out.println( "file: "+file);
			BufferedReader input = new BufferedReader(new FileReader(file));

			String line = null;
			String var = null;
			String val = null;

			while ((line = input.readLine()) != null) {
				if (line.startsWith("#"))
					continue;

				temp = line.split("\t+");

				if (temp.length != 2)
					continue;

				var = temp[0].trim();
				val = temp[1].trim();

				// System.out.println(line);
				// System.out.println(var+" "+val);
				var2value.put(var, val);
			}
			input.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * CB Eclipse created a Run Configuration behind my back so I commented main
	 * out!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! public static void main
	 * (String args[]) {
	 * 
	 * String fn = "D:/workspace/hecdss/wrims/dss/data/test.dsv"; //String fn =
	 * "data/test.dsv"; ViewerFile vf = new ViewerFile(fn); vf.read(); //
	 * System.out.println("HASH: "+vf.var2value);
	 * 
	 * String outfn = "D:/workspace/hecdss/wrims/dss/data/testout.dsv";
	 * ViewerFile vf2 = new ViewerFile(outfn); vf2.write(vf.var2value); }
	 */
}
