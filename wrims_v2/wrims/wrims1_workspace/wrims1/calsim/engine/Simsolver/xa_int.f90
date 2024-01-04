!     Last change:  AM    3 Dec 1999    4:56 pm
!     Copyright (C) 1998, 2000 State of California, Department of Water
!     Resources.

!     This program is licensed to you under the terms of the GNU General
!     Public License, version 2, as published by the Free Software
!     Foundation.

!     You should have received a copy of the GNU General Public License
!     along with this program; if not, contact Dr. Sushil Arora, below,
!     or the Free Software Foundation, 675 Mass Ave, Cambridge, MA
!     02139, USA.

!     THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA
!     DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY
!     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
!     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
!     PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA
!     DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR
!     ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
!     CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
!     OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR
!     BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
!     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
!     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
!     USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
!     DAMAGE.

!     For more information, contact:

!     Dr. Sushil Arora
!     California Dept. of Water Resources
!     Division of Planning, Delta Modeling Section
!     1416 Ninth Street
!     Sacramento, CA  95814
!     916-653-7921
!     sushil@water.ca.gov



!   Last modified by:  Armin Munevar, California Department of Water Resources

!     LF90 Interface to the XA DLL,  by D.McFaddden

!     compile with options -win -ml msvc

module xa_interface
  implicit none

  interface

     subroutine xfactc (frc,rc,nameid,activity,rownamesize)
       DLL_IMPORT XFACTC
       INTEGER(2), INTENT(OUT)       :: frc
       INTEGER(4), INTENT(OUT)       :: rc
       CHARACTER(LEN=*), INTENT(IN)  :: nameid
       DOUBLE PRECISION, INTENT(OUT) :: activity
       INTEGER(2), INTENT(IN)        :: rownamesize
     end subroutine xfactc

     subroutine xfinit(frc,rc,use_how,reserve,handle)
       DLL_IMPORT XFINIT
       INTEGER(2), INTENT(OUT)      :: frc
       INTEGER(4), INTENT(OUT)      :: rc
       INTEGER(4), INTENT(IN)       :: use_how, reserve
       INTEGER(2), INTENT(IN)       :: handle
     end subroutine xfinit

	subroutine xfmodel(frc,rc,xamversion)
		DLL_IMPORT XFMODEL
		INTEGER(2), INTENT(OUT)       :: frc
       		INTEGER(4), INTENT(OUT)       :: rc
		INTEGER			      :: xamversion
	end subroutine xfmodel 			

     subroutine xfclp(frc,rc,data_items)
       DLL_IMPORT XFCLP
       INTEGER(2), INTENT(OUT)          :: frc
       INTEGER(4), INTENT(OUT)          :: rc
       CHARACTER(LEN=*), INTENT(IN)     :: data_items
     end subroutine xfclp

     subroutine xfrcci(frc,rc, &
          rowname,colname,coef, &
          rownamesize,colnamesize,noitems, &
          maxrow,maxcol,maxnonzero,uselast)
       DLL_IMPORT XFRCCI
       INTEGER(2), INTENT(OUT)                            :: frc
       INTEGER(4), INTENT(OUT)                            :: rc
       CHARACTER(LEN=*), INTENT(IN),DIMENSION(*)          :: rowname,colname
       REAL(8), INTENT(IN), DIMENSION(*)                  :: coef
       INTEGER(2), INTENT(IN)                             :: rownamesize,colnamesize,uselast
       INTEGER(4), INTENT(IN)                             :: noitems,maxrow,maxcol,maxnonzero
     end subroutine xfrcci

     subroutine xfrccm(frc,rc, rowname,colname,coef,noitems,uselast)
       DLL_IMPORT XFRCCM
       INTEGER(2), INTENT(OUT)                            :: frc
       INTEGER(4), INTENT(OUT)                            :: rc
       CHARACTER(LEN=*), INTENT(IN),DIMENSION(*)          :: rowname,colname
       REAL(8), INTENT(IN), DIMENSION(*)                  :: coef
       INTEGER(4), INTENT(IN)                             :: noitems
       INTEGER(2), INTENT(IN)                             :: uselast
     end subroutine xfrccm

     subroutine xfsolv(frc,rc,modsts,solsts)
       DLL_IMPORT XFSOLV
       INTEGER(2), INTENT(OUT)                     :: frc
       INTEGER(4), INTENT(OUT)                     :: rc
       INTEGER(2), INTENT(OUT)                     :: modsts, solsts
     end subroutine xfsolv

     subroutine xfact(frc,rc,c,activity)
       DLL_IMPORT XFACT
       INTEGER(2), INTENT(OUT)         :: frc
       INTEGER(4), INTENT(OUT)         :: rc
       INTEGER(4), INTENT(IN)          :: c
       DOUBLE PRECISION, INTENT(OUT)   :: activity
     end subroutine xfact

     subroutine xfname(frc,rc,c,nameid)
       DLL_IMPORT XFNAME
       INTEGER(2), INTENT(OUT)         :: frc
       INTEGER(4), INTENT(OUT)         :: rc
       INTEGER(4), INTENT(IN)          :: c
       CHARACTER(LEN=*), INTENT(OUT)   :: nameid
     end subroutine xfname

     subroutine xfdone(frc, rc)
       DLL_IMPORT XFDONE
       INTEGER(2), INTENT(OUT)     :: frc
       INTEGER(4), INTENT(OUT)     :: rc
     end subroutine xfdone

     subroutine xfmsg(frc, rc)
       DLL_IMPORT XFMSG
       INTEGER(2), INTENT(OUT)     :: frc       
       INTEGER(4), INTENT(IN)      :: rc
     end subroutine xfmsg

     subroutine xfrprt(frc, rc)
       DLL_IMPORT XFRPRT
       INTEGER(2), INTENT(OUT)     :: frc
       INTEGER(4), INTENT(INOUT)      :: rc
     end subroutine xfrprt

  end interface

end module xa_interface
