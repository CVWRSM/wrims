/*

Copyright (C) 1998, 2000 State of California, Department of Water Resources.

This program is licensed to you under the terms of the GNU General Public
License, version 2, as published by the Free Software Foundation.

You should have received a copy of the GNU General Public License along
with this program; if not, contact Dr. Sushil Arora, below, or the
Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA DEPARTMENT
OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE CALIFORNIA DEPARTMENT OF WATER RESOURCES OR ITS
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

For more information, contact:

Dr. Sushil Arora
California Dept. of Water Resources
Office of State Water Project Planning, Hydrology and Operations Section
1416 Ninth Street
Sacramento, CA  95814
916-653-7921
sushil@water.ca.gov

*/


package calsim.wreslcoder;

import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import calsim.wreslcoder.wresl.*;

/**
 * Scans a Wresl input heirarchy to obtain a list of all the files.
 * Operates quickly using the <CODE>WreslParser</CODE> token manager.
 * @author Armin Munevar
 * @version $Id: WreslIncScanner.java,v 1.1.2.5 2001/07/12 02:00:04 amunevar Exp $
 */

public class WreslIncScanner implements WreslParserConstants {

	private WreslParserTokenManager tm;
	private File ourFile;
	private FileInputStream fis;

 	/**
  	*  Constructor method.
  	*
		*  @param wreslFile the file to be scanned
   	*  @execption FileNotFoundException
	*/
	public WreslIncScanner( File wreslFile) throws FileNotFoundException {
		fis = new FileInputStream( wreslFile);
	 	ASCII_CharStream in = new ASCII_CharStream(fis, 1, 1);
		tm = new WreslParserTokenManager( in);
		ourFile = wreslFile;
 	}

    /**
      *  Constructor method.
      *
      *  @param wreslFilename the file to be scanned
      *  @execption FileNotFoundException
      */
	public WreslIncScanner( String wreslFilename) throws FileNotFoundException {
		this( new File( WreslParser.nameWithExtension( wreslFilename)));
 	}

    /**
      *  Obtains a list of strings that are the absolute pathnames of
      *  included files that are newer than the specified file.
      *
      *  @param   compareFileName   The file name to be used as basis for comparison.
      *                             File need not exist.
      *  @return  A Vector of String objects representing the absolute pathnames of each file
      *           that is newer than the specified file.
      */
    public Vector getNewerList(String compareFileName)	throws FileNotFoundException {
		Vector newerFileList = new Vector();
		long compare = new File(compareFileName).lastModified();
		Vector fileList = getIncFileList();
		for (Enumeration e = fileList.elements(); e.hasMoreElements();) {
	    	File f = (File) e.nextElement();
	    	if (f.lastModified() > compare)	newerFileList.addElement(f.getAbsolutePath());
		}
		return newerFileList;
    }


    /**
      *  Scans the file and returns a list of the Wresl input files used.
      *  The list includes the top level file and all of its include family.
      *
      *  @return a String Vector containing this list
      *
      *  @exception TokenMgrError          A grave parse error occurred.  Rare but possible.
      *  @exception FileNotFoundException  Some included file was not found.
      */

    public Vector getIncFileList() throws FileNotFoundException, TokenMgrError {
		Token t;
		Vector fileList = new Vector();

		// First and foremost, note the current file in the list of files!
		fileList.addElement(ourFile);

        try {
	    	while (EOF != (t = tm.getNextToken()).kind) {
				if ( t.kind == INCLUDE) {
		    		Token fileToken = tm.getNextToken();
					if ( fileToken.image == "[" ) {
					    fileToken = tm.getNextToken(); // scope token
					    fileToken = tm.getNextToken(); // "]" token
					    fileToken = tm.getNextToken(); // finally the filename
					}
		    		File subFile = new File(ourFile.getParent(),
						WreslParser.nameWithExtension(fileToken.image.substring( 1,
			        	fileToken.image.length()-1)));

		    		// Open another WreslIncScanner object on this included file
		    		WreslIncScanner subScanner = new WreslIncScanner( subFile);
		    		Vector subVector = subScanner.getIncFileList();

		    		// Append the files found within to our own list
		    		for ( Enumeration e = subVector.elements() ; e.hasMoreElements();) {
					fileList.addElement((File) e.nextElement());
		    		}
				}
	    	}
		} catch (TokenMgrError e) {
				try {
				  fis.close();
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
	    	throw new TokenMgrError( "In " + ourFile + ": " + e.getMessage(), -1);
		}
		return fileList;
    }

    /**
      * Retrieves a list of modification dates of the include files.
      *
      * @return a hashtable consisting of the filenames and a corresponding <code>Long</code> value.
      */
    public Hashtable getFileDates() throws FileNotFoundException {
		Hashtable hasher = new Hashtable();
		for ( Enumeration e = getIncFileList().elements() ; e.hasMoreElements();) {
	    	File theFile = (File) e.nextElement();
	    	hasher.put( theFile, new Long( theFile.lastModified()));
		}
        return hasher;
    }


    /**
      *  For debugging.  Scans a file and prints the list of files newer than a given file.
      *
      *  @param arg0    The input Wresl file
      *  @param arg1    The object file to compare against
      */
    public static void main( String arg[]) {
        if ( arg.length == 2) {
	    	try {
				WreslIncScanner inc = new WreslIncScanner( arg[0]);
				Vector names = inc.getNewerList(arg[1]);
				for ( Enumeration e = names.elements() ; e.hasMoreElements();) {
		    		String name = (String) e.nextElement();
		    		System.out.println( name);
				}
	    	} catch ( FileNotFoundException ee) {
	        	System.out.println( ee.toString());
	    	}
        } else {
	    	System.out.println( "Specify top wresl file (with .wresl) and the compare file.");
		}
    }

}
