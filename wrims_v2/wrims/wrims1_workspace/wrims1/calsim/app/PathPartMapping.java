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
import java.util.*;
import java.io.*;
/**
  * A mapping from path parts to another path parts.
  *
  * @author Nicky Sandhu
  * @version $Id: PathPartMapping.java,v 1.1.2.2 2000/12/20 20:02:18 amunevar Exp $
  */
public class PathPartMapping{
  private Hashtable _map;
  /**
    *
    */
  public PathPartMapping(){
    _map = new Hashtable();
  }
  /**
    *
    */
  public PathPartMapping(String file) throws IOException{
    this();
    LineNumberReader reader = new LineNumberReader(new FileReader(file));
    String [] keys = new String[2];
    String [] vals = new String[2];
    while(true){
      String line = reader.readLine();
      if ( line  == null ) break;
      StringTokenizer st = new StringTokenizer(line,",");
      if ( st.countTokens() != 4 ) continue;
      keys[0] = (String) st.nextToken();
      keys[1] = (String) st.nextToken();
      vals[0] = (String) st.nextToken();
      vals[1] = (String) st.nextToken();
      this.addMap(keys,vals);
    }
    reader.close();
  }
  /**
    *
    */
  public void saveTo(String file) throws IOException{
    PrintWriter writer = new PrintWriter(new FileWriter(file));
    for(Enumeration e = _map.keys(); e.hasMoreElements(); ){
      String key = (String) e.nextElement();
      String val = (String) _map.get(key);
      String [] keys = createPlural(key);
      String [] vals = createPlural(val);
      for(int i=0; i < keys.length; i++){
	writer.print(keys[i]);
	writer.print(",");
      }
      for(int i=0; i < vals.length; i++){
	writer.print(vals[i]);
	writer.print(",");
      }
      writer.println();
    }
    writer.close();
  }
  /**
    *
    */
  public String createSingular(String [] keys){
    if ( keys == null ) return null;
    String key = "";
    for(int i=0; i < keys.length; i++){
      key += "/"+ ((keys[i] == null)? "" : keys[i].toUpperCase());
    }
    return key;
  }
  /**
    *
    */
  public String [] createPlural(String key){
    if ( key == null) return null;
    StringTokenizer st = new StringTokenizer(key,"/");
    String [] vals = new String[st.countTokens()];
    int count = 0;
    while ( st.hasMoreTokens() ){
      vals[count++] = (String) st.nextToken();
    }
    return vals;
  }
  /**
    *
    */
  public void addMap(String [] keys, String [] vals){
    if (keys == null || vals == null) return;
    String key = createSingular(keys);
    String val = createSingular(vals);
    _map.put(key,val);
  }
  /**
    *
    */
  public String [] getMap(String [] keys){
    if ( keys == null ) return null;
    String key = createSingular(keys);
    return createPlural((String) _map.get(key));
  }
}
