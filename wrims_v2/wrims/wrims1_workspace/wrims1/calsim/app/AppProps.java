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
import java.util.Properties;
/**
  * Properties for the application. These properties are stored in the
  * app.props file in the [user.dir]/.calsim directory. These directories
  * & files are created if they do not exist.
  *
  * @author Nicky Sandhu
  * @version $Id: AppProps.java,v 1.1.2.3 2000/12/20 20:02:06 amunevar Exp $
  */
public class AppProps{
  public static Properties props = new Properties();
  public static void initializeProps(){
    File appPropsDir = checkForPropsDir();
    File appPropsFile = new File(appPropsDir.getPath() + File.separator + "app.props");
    if ( ! appPropsFile.exists() ){
      // then look in jar file for defaults & save to props file
      try {
	props.load(Object.class.getResourceAsStream("/calsim/app/app.props"));
	save();
      } catch(IOException ioe){
	ioe.printStackTrace(System.err);
      }
    } else { // read from app.props file
      try {
	// always load defaults and then user customized properties
	props.load(Object.class.getResourceAsStream("/calsim/app/app.props"));
	props.load( new FileInputStream(appPropsFile));
	// make sure the version is matched or exists. If not load from
	// jar file first and then from props file again
	Properties jarprops = new Properties();
	jarprops.load(Object.class.getResourceAsStream("/calsim/app/app.props"));
	String propVersion = props.getProperty("AppProps.version");
	String jarPropVersion = jarprops.getProperty("AppProps.version");
	if ( propVersion == null ||
	     new Integer(propVersion).intValue() < new Integer(jarPropVersion).intValue() ){
	  // load the latest
	  props.load(Object.class.getResourceAsStream("/calsim/app/app.props"));
	  // override with users properties
	  props.load( new FileInputStream(appPropsFile));
	  // finally override the versionid for app props
	  props.put("AppProps.version",jarPropVersion);
	  // finally save all this work
	  save();
	}
      }catch(IOException ioe){
	ioe.printStackTrace(System.err);
      }
    }
  }
  /**
    *
    */
  public static File checkForPropsDir(){
    // first look in user.home for .calsim directory
    String appDir = System.getProperty("calsim.home") + File.separator + ".calsim";
    File appPropsDir = new File(appDir);
    if ( ! appPropsDir.exists() ) {
      // create this directory...
      appPropsDir.mkdir();
    }
    return appPropsDir;
  }
  /**
    *
    */
  public static void save(){
    // look for .calsim directory in user.home & a file called app.props
    File appPropsDir = checkForPropsDir();
    File appPropsFile = new File(appPropsDir.getPath() + File.separator + "app.props");
    // then look in jar file for defaults & save to props file
    try {
      props.store( new FileOutputStream(appPropsFile), "application user properties");
    } catch(IOException ioe){
      ioe.printStackTrace(System.err);
    }
  }
  static{
    initializeProps();
  }
  /**
    *
    */
  public static String getProperty(String key){ return props.getProperty(key); }
  /**
    *
    */
  public static void setProperty(String key, String val){ props.put(key,val); }
}
