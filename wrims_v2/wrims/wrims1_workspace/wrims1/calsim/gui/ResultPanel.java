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

package calsim.gui;
//import calsim.app.*;
import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
//import javax.swing.text.*;
//import javax.swing.border.*;
/**
 * The panel for the control tab.
 *
 * @author Yan-Ping Zuo
 * @version $Id: ResultPanel.java,v 1.1.2.11 2001/07/12 01:59:57 amunevar Exp $
 */

public class ResultPanel extends JPanel
{
  public static boolean DEBUG = true;
  /**
   * Constructor
   */
  public ResultPanel() {
    setLayout(new BorderLayout());
    add(createScrollPane(), BorderLayout.CENTER);
  }
  /**
   * Create the center panel
   */
  JScrollPane createScrollPane(){
    _textArea = new JTextArea(30,80);
    _textArea.setEditable(true);
    _textArea.setLineWrap(true);
    _textArea.setWrapStyleWord(true);
    final JScrollPane areaScrollPane = new JScrollPane(_textArea);
    areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    // CB added to better show progress
	_textArea.getDocument().addDocumentListener(new DocumentListener() {
		public void insertUpdate(DocumentEvent e) {
			_textArea.setCaretPosition(_textArea.getDocument().getLength());
		}

		public void removeUpdate(DocumentEvent e) {
			_textArea.setCaretPosition(_textArea.getDocument().getLength());
		}

		public void changedUpdate(DocumentEvent e) {}
	});

    return areaScrollPane;
  }
  /**
   * Get the text area
   */
  public JTextArea getTextArea() {
    return _textArea;
  }

  /**
   *
   */
  private JTextArea _textArea;
}


