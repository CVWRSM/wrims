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
import java.text.NumberFormat;

/**
 *  Creates a Fortran subroutine that generates a listing of all
 *  of the Wresl state variables.  This subroutine is intended for
 *  runtime debugging of a model.  It is compiled and linked during model creation.
 *  The list of state variables is sorted by methods in this class and therefore no
 *  runtime sorting is necessary.
 * @author Armin Munevar
 * @version $Id: ReportWriter.java,v 1.1.2.6 2001/07/12 02:00:09 amunevar Exp $
 */
public class ReportWriter {
  java.io.PrintWriter out;
  private int max_cycles = 0;
  /**
   *  Creates a new <code>ReportWriter</code> instance.  It will write to the given stream.
   *
   *  @param pw The desired stream, generally a new Fortran source code file.
   */
  public ReportWriter( java.io.PrintWriter pw) {
    out = pw;
  }

  public void close(){ out.close(); }

  /**
   *  Generates the report.
   *
   *  @param list A list of the variables to include in the report
   */
  public void mkReport( UniqueList list, int cycleno) {
    max_cycles=Math.max(max_cycles,cycleno);

    QSort(list,0,list.size()-1);
    NumberFormat nf = NumberFormat.getInstance();
    //
    nf.setMinimumIntegerDigits(2);
    String seqStr = nf.format(cycleno);
    int nlines = list.size();
    int ncount = nlines/50;
    for( int k=0; k < ncount; k++){
      dump(list,k,50,seqStr,nf,cycleno);
    }
    if ( ncount*50 < nlines ) {
      dump(list,ncount,50,seqStr,nf,cycleno);
    }
    out.println("subroutine reportsv"+seqStr);
    out.println("use global");
    out.println("use types"+seqStr);
    out.println("use code_utils");
    out.println("implicit none");
    out.println("character(len=32)::heading,blank=''");
    out.println("100 format('>> WY month ', i2,'/',i4)");
    out.println("101 format('CYCLE # ', i2)");
    out.println("write(heading,100) date%month, date%wateryear");
    out.println("call writelog(heading,blank,0.)");
    out.println("write(heading,101) "+cycleno);
    out.println("call writelog(heading,blank,0.)");
    //
    for( int k=0; k < ncount; k++){
      out.println("call reportsv"+seqStr+nf.format(k)+"()");
    }
    if ( ncount*50 < nlines ) {
      out.println("call reportsv"+seqStr+nf.format(ncount)+"()");
    }
    //
    out.println("end subroutine");
    out.flush();
    if ( cycleno == 0 ){
      out.println("subroutine reportsv(cycleno)");
      out.println("use global");
      out.println("implicit none");
			out.println("integer :: cycleno");
      for(int i=0; i <= max_cycles; i++){
				out.println("if (cycleno=="+i+") call reportsv"+nf.format(i)+"()");
      }
      out.println("end subroutine");
      out.close();
    }
	}

  private void dump(UniqueList list, int k, int n, String seqStr, NumberFormat nf,int cycleno){
		out.println("subroutine reportsv"+seqStr+nf.format(k));
    out.println("use global");
    out.println("use types"+seqStr);
    if (cycleno != 0) out.println("use types00");
    out.println("use code_utils");
    out.println("implicit none");
    out.println("120 format(2a32,f16.2)");   //CB - I don't think we need this line
    int maxn = Math.min(k*n+n,list.size());
      // runs from k*n -> Math.min(k*n+n,maxn)
    for( int i=k*n; i<maxn; i++) {
			String varName = (String) list.elementAt(i);
      out.print( "call writelog( '");
			out.print( varName.concat("                               ").substring( 0, 32) );
			out.print( "',Def_Condition_Tag(" + list.getIdCode( varName) + "),");
      out.println( varName + ")" );
		}
    out.println("end subroutine");
    out.flush();
  }

  /**
   *
   */
  private int Partition( UniqueList raw, int p, int r){
    int i=p,j=r;
    boolean done = false;
    String x=(String)raw.elementAt( p);
    while (!done)  {
      while (( ((String)raw.elementAt( j)).compareTo( x) >= 0) && (j > p))      j--;
      while (( ((String)raw.elementAt( i)).compareTo( x) < 0) && (i < r))      i++;
      if (i < j)    { raw.swap( i,j); }
      else done = true;
    }
    return j;
  }

  /**
   *
   */
  private void QSort( UniqueList raw, int p, int r){
    int q;
    if (p < r)  {
      q = Partition( raw,p,r);
      QSort( raw,p,q);
      QSort( raw,q+1,r);
    }
  }

}
