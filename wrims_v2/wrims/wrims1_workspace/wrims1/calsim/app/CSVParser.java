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

package calsim.app;
import java.io.*;
import java.util.*;
/**
  * This class parses a comma delimited file
  *
  * @author Nicky Sandhu ,Armin Munevar
  * @version $Id: CSVParser.java,v 1.1.2.6 2001/07/12 01:58:22 amunevar Exp $
  */
public class CSVParser{
  private String _file;
  private LineNumberReader _reader;
  private String _delimiter;
  /**
    * opens a file delimited by "," for reading
    */
  public CSVParser(String file){
    try {
      _file = file;
      _reader = new LineNumberReader(new FileReader(file));
      _delimiter = ",";
    }catch(IOException ioe){
      throw new RuntimeException("IOError: " + ioe.getMessage());
    }
  }
  /**
    * opens a file delimited by a user specified delimiter for reading
    */
  public CSVParser(String file, String delimiter){
    try {
      _file = file;
      _reader = new LineNumberReader(new FileReader(file));
      _delimiter = delimiter;
    }catch(IOException ioe){
      throw new RuntimeException("IOError: " + ioe.getMessage());
    }
  }
  /**
    * @return an array of strings split on the delimiter or null
    */
  public String [] nextLine(){
    try {
      String line = _reader.readLine();
      if ( line == null ) {
				_reader.close();
				return null;
      }
      	line = line.trim();
      	String [] tokens = split(line,_delimiter);
      	return tokens;
    } catch(IOException ioe){
     	System.err.println("Error occured in reading file: " + _file);
     	System.err.println("Error msg: " + ioe.getMessage());
     	return null;
    }
  }
  /**
    * @return an array of strings split on the delimiter or null
    */
  public void close(){
    try {
			_reader.close();
    } catch(IOException ioe){
     	System.err.println("Error occured in closing file: " + _file);
     	System.err.println("Error msg: " + ioe.getMessage());
    }
  }
  // save time on creating lots of vector objects only
  // to be discarded a short time later
  private static Vector _sv = new Vector();
  /**
    *
    */
  public String [] split(String line,String delimiter){
    if ( line == null ) return null;
    int cindex = 0; // current position
    _sv.removeAllElements();
    while(true){
      if ( cindex >= line.length() ) break;
      // get index of delimiter from current position
      int sindex = line.indexOf(delimiter,cindex);
      // if not found get last part of string and break
      if ( sindex == -1 ) {
	_sv.addElement(line.substring(cindex,line.length()).trim());
	break;
      }
      _sv.addElement(line.substring(cindex,sindex).trim());
      cindex = sindex + delimiter.length();
    }
    if ( _sv.size() == 0 ) return null;
    String [] sarray = new String[_sv.size()];
    _sv.copyInto(sarray);
    return sarray;
  }
}
