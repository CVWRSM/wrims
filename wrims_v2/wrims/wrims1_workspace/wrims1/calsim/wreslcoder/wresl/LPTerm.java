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

package calsim.wreslcoder.wresl;

/**
 *  Defines one term of an LP constraint.  This consists of a name of a decision variable multiplied
 *  by a coefficent.  The coefficient is stored herein as a <code>String</code>.  The coefficient
 *  must not contain any decision variables.  The coefficient is stored as a <code>public</code>
 *  variable so that you may manipulate it externally.
 * @author Armin Munevar
 * @version $Id: LPTerm.java,v 1.1.2.2 2001/07/12 02:00:08 amunevar Exp $
 */
public class LPTerm {
    private String dvar=null;                    // string containing the dvar
    /**
      * The coefficient string
      */
    public String coef=new String("1");         // string containing an expression with no dvars

    /**
      * Creates a constant LP term having a coefficient of "1"
      */
    public LPTerm() {}

    /**
      * Creates an LP term consisting of the given components
      *
      * @param d the decision variable
      * @param c the coefficient
      */
    public LPTerm(String d, String c) {
        // optional constructor method.  Not required to use this one.
        if (d != null) dvar=d.toUpperCase();
        coef=c.toUpperCase();
    }

      /**
        *  Multiplies the coefficient of this LP term by -1.  This is done with some
        *  rudimentry intelligence to avoid things like -(-(-5))) but it is far from perfect.
        */
      public void negate() {
          if (coef.length()==0 || coef.equals("1")) {
              coef = new String("-1");
          } else if (coef.equals("-1")) {
              coef = new String("1");
          } else if (coef.equals("0")) {
            // do nothing
        //  } else if (coef.startsWith("-(") && coef.endsWith(")")) {
        //    coef = new String(coef.substring(2,coef.length()-3));
          } else {
	      coef = new String("-(" + coef + ")");
          }
      }

      /**
        *  Multiplies this term by the specified term.
        *
        *  @param t The multiplier
        *  @exception ParseException if both the multiplier and this term have non-NULL decision variable
        *                            components
        */
      public void multiply(LPTerm t) throws ParseException {
          if (t.coef.length()>0)
              if (coef.equals("1")) {
                  coef = t.coef;
              } else if (! t.coef.equals("1")) {
	          coef = coef.concat("*(" + t.coef + ")" );
              }
          if (t.dvar!=null) {
              // they specified a decision variable
              if (dvar == null) {
                 dvar = new String(t.dvar);
              } else {
                 throw new ParseException("Two dvars being nonlinearly combined: "
			+ dvar + ", " + t.dvar);
              }
          }
      }

      /**
          *  Divides this term by the specified term.
          *
          *  @param t The divisor
          *  @exception ParseException if the divisor has a non-NULL decision variable
          *                            component
          */

      public void divide(LPTerm t) throws ParseException {
          if (t.coef.length()>0)
              if (! t.coef.equals("1"))
		      coef = coef.concat("/(" + t.coef + ")" );
          if (t.dvar != null) {
              throw new ParseException("Trying to divide by a dvar :" + t.dvar);
          }
      }


     /**
       *  Set the decision variable component of this term to the specified string.
       *
       *  @param dv the new decision variable component.  Overwrites the old one.
       */
     public void setDvar(String dv) {
         dvar = dv.toUpperCase();
     }

     /**
       *  Retrieves the decision variable component of this term, possibly NULL.
       *
       *  @return the decision variable component
       */
     public String getDvar() {
         if (dvar==null) return null;
         return new String(dvar);
     }

}
