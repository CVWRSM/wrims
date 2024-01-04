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
import java.util.*;
import java.io.PrintWriter;

/**
 *  Defines one row of an LP constraint.  Also, a portion of a row that is to be
 *  linearly combined with another one.
 *
 *  There is no mathematics and no numbers used here, only strings.
 *
 *  Two uses for this object:
 *  <p>
 *  (1) example where X and Y are decision variables and a,b, and c are constants:
 *        a*X + b*Y = c
 *  <p>
 *  (2) same example as (1) but you called keepLeft() after construction:
 *        a*X + b*Y + c
 * @author Armin Munevar
 * @version $Id: LPExpression.java,v 1.1.2.2 2001/07/12 02:00:07 amunevar Exp $
 */
public class LPExpression {
      private Hashtable lhs = new Hashtable(4);          // a linked list of left-hand-side terms
      private String rhs = new String("0");              // right-hand-side terms concatenated
      private boolean put_rhs_terms_on_right = true;     // true for LP constraints, false for bare expressions

      /**
        *  Creates an <code>LPExpression</code> instance with a constant RHS term of 0 and no LP terms.
        */
      public LPExpression() {}

      /**
        *  Creates an <code>LPExpression</code> instance with a a RHS term of 0 and the supplied LP Term
        *
        *  @param lpt The desired LP Term to insert into this expression
        */
      public LPExpression(LPTerm lpt) {
          // optional constructor that also inserts a term
          add(lpt);
      }

      /**
        *  Creates an <code>LPExpression</code> instance cloned from an existing one.
        *
        *  @param a An <code>LPExression</code> to copy
        */
      public LPExpression(LPExpression a) {
          // optional constructor that copies an existing one
          rhs = new String(a.rhs);
          Enumeration listOfDvars = a.lhs.keys();
          while (listOfDvars.hasMoreElements()) {
              String variable = (String) listOfDvars.nextElement();
              lhs.put(variable, a.lhs.get(variable));
          }
      }

      /**
        * Keep RHS on the LHS. Specifies that all terms of this expression,
        * including the constant term, are to be on the LHS.
        * This is useful in certain cases to prevent build-up of negations.
        */
      public void keepLeft() {
          put_rhs_terms_on_right = false;
      }

      /**
        * Adds a term to the expression.
        *
        * @param term The LP term to be added.  If the term's decision varible is already
        *             included in another term of this expression, the coefficients of those
        *             terms are added.
        */
      public void add(LPTerm term)
      {
        if (term.getDvar()==null) {
            // add to rhs thing
            if (put_rhs_terms_on_right) term.negate();
            addToRhs(term.coef);

        } else {
            // add to the lhs hash table
            // first, though, check to see if this dvar is there already!
            if (lhs.containsKey(term.getDvar())) {
                // already have a term using this decision variable
                String currentvalue = (String) lhs.get(term.getDvar());
                if (term.coef.charAt(0) != '-') currentvalue += "+";
                lhs.put(term.getDvar(), currentvalue.concat(term.coef));
            } else {
                lhs.put(term.getDvar(), term.coef);
            }

            //debug System.out.println("Added lhs term: " + term.coef + "*" + term.getDvar());
        }
      }

      /**
        *  Merges another expression with this one by subtraction.
        *  Linearly subtracts the given expression from this.
        *
        *  @param  a The expression that will be subtracted from this one.
        */
      public void merge(LPExpression a) {
        // First, go through the lhs terms
        Enumeration listOfDvars = a.lhs.keys();
        LPTerm term;
        while (listOfDvars.hasMoreElements()) {
          String variable = (String) listOfDvars.nextElement();     // get next variable on the list
          String coefficient = (String) a.lhs.get(variable);   // obtain its coefficient
          term = new LPTerm(variable, coefficient);    // make a new LPTerm then add it to the result
          term.negate();
          this.add(term);
        }
        if (a.put_rhs_terms_on_right) {
          LPTerm rhsTerm = new LPTerm(null,a.rhs);
          rhsTerm.negate();
          addToRhs(rhsTerm.coef);                  // take care of the rhs
        }
        else this.addToRhs(a.rhs);
      }

      /**
        * Expression contains only a RHS.  Checks to see that there are no LHS terms present.
        *
        * @return False if there are terms on the LHS of this expression; True if there is only
        *         a RHS expression or that RHS expression is zero.
        */
      public boolean isConstant() {
          return lhs.isEmpty();
      }

      /**
        * Prints a String representation of this expression.  Used for debugging only.
        *
        * @return a new String in the form <CODE>"LHS = RHS"</code>
        */
      public String asString()
      {
        // print lhs=rhs out as a string, debugging only

        if (lhs.isEmpty()) {
            return new String(rhs);
        }
        StringBuffer sb = new StringBuffer();
        Enumeration en = lhs.keys();
        boolean firstElement = true;
        while (en.hasMoreElements()) {
            String variable = (String) en.nextElement();
            String coefficient = (String) lhs.get(variable);
            if (!firstElement)
              sb.append(" + ");
            firstElement = false;
            sb.append("(").append(coefficient).append(") ").append(variable);
        }
        sb.append(" = " + rhs);
        return sb.toString();
      }

      /**
        *  Writes a representation of this expression to a stream.  The format is intended to be
        *  read by another process, which will then generate an LP constraint from this information.
        *
        *  @param s An output stream to place this expression.
        */
      public void output(PrintWriter s) {
          // prints out the constraint
          s.println(lhs.size());
          Enumeration listOfDvars = lhs.keys();
          while (listOfDvars.hasMoreElements()) {
              String variable = (String) listOfDvars.nextElement();     // get next variable on the list
              String coefficient = (String) lhs.get(variable);          // obtain its coefficient
              // print format is "variable","coefficient"
              s.println("\"" + coefficient + "\",\"" + variable + "\"");
          }
          s.println(rhs);
      }

    private void addToRhs(String term) {
        if (rhs.equals("0")) {
            rhs = term;
        } else {
            if (!term.equals("0")) {
                if (term.charAt(0) != '-') rhs = rhs.concat("+");
                rhs = rhs.concat(term);
            }
        }
        //debug  System.out.println("Added rhs: " + term);
    }

}
