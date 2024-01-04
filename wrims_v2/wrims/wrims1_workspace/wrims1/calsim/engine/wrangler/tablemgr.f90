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

! Wresl interface entry point (API)
! See the functions in module tablemgr_query for the real stuff
!
! 04/03/98 :  finalized
! 06/04/98 :  bug fixed.  added gc_kiwi assignment for queryLookup block

real function tableComplex( tablename, answer, gc, gv, um, wc, wv)
  USE tablemgr_query
  implicit none
  dll_export tableComplex
  CHARACTER(LEN=*), INTENT(IN)      :: tablename, answer
  CHARACTER(LEN=*), INTENT(IN)      :: gc, wc(:)
  CHARACTER(LEN=KIWI)               :: tablename_kiwi, answer_kiwi
  REAL, INTENT(IN)                  :: gv, wv(:)
  INTEGER, INTENT(IN)               :: um

  tablename_kiwi = tablename
  answer_kiwi = answer
  tableComplex = queryComplex(tablename_kiwi, answer_kiwi, gc, gv, um, wc, wv)
end function tableComplex


real function tableLookup( tablename, answer, gc, gv, um)
  USE tablemgr_query
  implicit none
  dll_export tableLookup
  CHARACTER(LEN=*), INTENT(IN)  :: tablename, answer
  CHARACTER(LEN=*), INTENT(IN)  :: gc
  CHARACTER(LEN=KIWI)           :: tablename_kiwi, answer_kiwi
  REAL, INTENT(IN)              :: gv
  INTEGER, intent(in)           :: um

  tablename_kiwi = tablename
  answer_kiwi = answer
  tableLookup = queryLookup(tablename_kiwi, answer_kiwi, gc, gv, um)
end function tableLookup


real function tableBasic( tablename, answer, wc, wv)
  USE tablemgr_query
  implicit none
  dll_export tableBasic
  CHARACTER(LEN=*), INTENT(IN)           :: tablename, answer
  CHARACTER(LEN=*), INTENT(IN)           :: wc(:)
  CHARACTER(LEN=KIWI)                    :: tablename_kiwi, answer_kiwi
  REAL, INTENT(IN)                       :: wv(:)

  answer_kiwi = answer
  tablename_kiwi = tablename
  tableBasic = queryBasic(tablename_kiwi, answer_kiwi, wc, wv)
end function tableBasic

subroutine setTableDirectory(directory, commondir)
  USE tablemgr_io
  implicit none
  dll_export setTableDirectory
  CHARACTER(LEN=*), INTENT(IN)           :: directory, commondir
  
  tableDatabasePath=TRIM(directory) // "\lookup"
  tableCommonPath  =TRIM(commondir) !// "\lookup"
end subroutine setTableDirectory


