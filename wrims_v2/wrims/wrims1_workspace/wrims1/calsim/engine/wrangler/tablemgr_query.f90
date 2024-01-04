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

module tablemgr_query
  USE tablemgr_utils, ONLY: kiwi

  public
  PRIVATE selectT, fetchTable

contains


  function queryBasic(tablename, resultID, whereID, whereValues)
    USE tablemgr_utils
    implicit none
    REAL :: queryBasic
    CHARACTER(LEN=kiwi),INTENT(IN) :: tablename, whereID(:), resultID
    REAL,DIMENSION(:),INTENT(IN)   :: whereValues
    REAL,DIMENSION(MAX_ENTRIES,MAX_COLS):: a
    INTEGER, DIMENSION(2)          :: dims
    LOGICAL, DIMENSION(MAX_ENTRIES) :: mask
    TYPE(tableType)                :: tab
    INTEGER :: i
    CHARACTER(LEN=kiwi),DIMENSION(MAX_COLS+1) :: wantCol
    REAL,DIMENSION(MAX_COLS+1) :: wantVal
    INTEGER                                       :: ncols

101 FORMAT('Table ',a,': ',a,'Non-singleton selection and no GIVEN clause!')
102 FORMAT('Table ',a,': ',a,'No rows meet WHERE criteria!',10(a,'WHERE ',a,' = ',f7.1))
103 FORMAT('Table ',a,': ',a,'Nonconforming query columns:',i3)

    ncols=UBOUND(whereID,1)
    IF(ncols /= UBOUND(whereValues,1)) then
       WRITE(msg,103) TRIM(tablename), CHAR(13), ncols
       call stopWithError
    END if
    wantCol(1)  = resultID                            ! forces selectT to include result col
    wantCol(2:) = whereID
    wantVal(1)  = -902.                               ! forces screenOutRows to ignore result col
    wantVal(2:) = whereValues

    ! Get the table and remove extraneous cols and rows
    dims = fetchTable(tab, tablename, wantCol(:ncols+1))       ! Load table and obtain its dimensions
    call selectT(a(:dims(1),:dims(2)), tab, wantCol(:ncols+1))                    ! obtain relevent columns
    call screenOutRows(mask(:dims(1)), a(:dims(1),:dims(2)), wantVal(:ncols+1))             ! Determine relevent rows

    ! Should only be one row left, and it contains our answer
    IF(COUNT(mask(:dims(1))) /= 1) then
       if (COUNT(mask(:dims(1)))>1)  WRITE(msg,101) TRIM(tablename), CHAR(13)
       if (COUNT(mask(:dims(1)))==0) 	WRITE(msg,102) TRIM(tablename), CHAR(13), &
        (char(13), TRIM(whereID(i)), whereValues(i), i=1,ncols)
       call stopWithError
    END if
    do i=1,UBOUND(mask(:dims(1)),1)                            ! Hunt for the sole .true. answer
       IF(mask(i)) then
          queryBasic = a(i,1)
          exit
       end if
    END do
  END function queryBasic


  function queryComplex(tablename, resultID, givenID, givenVal, lint, whereID, whereValues)
    USE tablemgr_utils
    implicit none
    REAL :: queryComplex
    CHARACTER(LEN=kiwi),INTENT(IN) :: tablename, whereID(:), resultID, givenID
    REAL,DIMENSION(:),INTENT(IN)   :: whereValues
    REAL, INTENT(IN)               :: givenVal
    INTEGER, INTENT(IN)            :: lint
    REAL,DIMENSION(MAX_ENTRIES,MAX_COLS) :: a
    INTEGER, DIMENSION(2)          :: dims
    LOGICAL, DIMENSION(MAX_ENTRIES) :: mask
    TYPE(tableType)                :: tab
    TYPE(pd_type), DIMENSION(MAX_ENTRIES) :: pd
    INTEGER :: i,row
    CHARACTER(LEN=kiwi),DIMENSION(MAX_COLS+2) :: wantCol
    REAL,DIMENSION(MAX_COLS+2)                :: wantVal
    INTEGER                                       :: ncols

102 FORMAT('Table ',a,': ',a,'No rows meet WHERE criteria!',10(a,'WHERE ',a,' = ',f7.1))
103 FORMAT('Table ',a,':',a,i3,' columns?!')

    ncols=UBOUND(whereID,1)
    IF(ncols /= UBOUND(whereValues,1)) then
       WRITE(msg,103) TRIM(tablename), CHAR(13), ncols
       call stopWithError
    END if
    wantCol(1)  = resultID                            ! forces selectT to include result col
    wantCol(2)  = givenID                             !  ... and the given col
    wantCol(3:) = whereID
    wantVal(1)  = -902.                               ! forces screenOutRows to ignore result col
    wantVal(2)  = -902.                               ! forces screenOutRows to ignore given col
    wantVal(3:) = whereValues

    ! Get the table and remove extraneous cols and rows
    dims = fetchTable(tab, tablename, wantCol(:ncols+2))       ! Load table and obtain its dimensions
    call selectT(a(:dims(1),:dims(2)), tab, wantCol(:ncols+2))                    ! obtain relevent columns
    call screenOutRows(mask(:dims(1)), a(:dims(1),:dims(2)), wantVal(:ncols+2))             ! Determine relevent rows

    ! Should be a set of rows left, which contains our answers
    if (COUNT(mask(:dims(1)))==0) then
	WRITE(msg,102) TRIM(tablename), CHAR(13), &
        (char(13), TRIM(whereID(i)), whereValues(i), i=1,ncols)
       call stopWithError
    END if

    ! Create a PD_TYPE containing all relevent rows
    row=1
    do i=1,UBOUND(mask(:dims(1)),1)          ! Hunt for the sole .true. answer
       IF(mask(i)) then
          pd(row)%ind = a(i,2)     ! given is in column 2
          pd(row)%dep = a(i,1)     ! result is in column 1
          row=row+1
       end if
    END do
    queryComplex = pd_retrieve(pd(:COUNT(mask(:dims(1)))), givenVal, lint)
    return
  END function queryComplex


  function queryLookup(tablename, resultID, givenID, givenVal, lint)
    USE tablemgr_utils
    implicit none
    REAL :: queryLookup
    CHARACTER(LEN=kiwi),INTENT(IN) :: tablename, resultID, givenID
    REAL, INTENT(IN)               :: givenVal
    INTEGER, INTENT(IN)            :: lint
    REAL,DIMENSION(MAX_ENTRIES,MAX_COLS):: a
    INTEGER, DIMENSION(2)          :: dims
    TYPE(tableType)                :: tab
    TYPE(pd_type), DIMENSION(MAX_ENTRIES) :: pd
    CHARACTER(LEN=kiwi),DIMENSION(2) :: wantCol

    wantCol(1)  = resultID                            ! forces selectT to include result col
    wantCol(2)  = givenID                             !  ... and the given col

    ! Get the table and remove extraneous cols and rows
    dims = fetchTable(tab, tablename, wantCol)       ! Load table and obtain its dimensions
    call selectT(a(:dims(1),:dims(2)), tab, wantCol)                    ! obtain relevent columns

    ! Create a PD_TYPE containing all relevent rows
    pd%ind = a(:dims(1),2)     ! given is in column 2
    pd%dep = a(:dims(1),1)     ! result is in column 1
    queryLookup = pd_retrieve(pd(:dims(1)), givenVal, lint)
    return
  END function queryLookup


  ! ----------------- Internal Use Functions Follow ----------------------

  ! Returns the dimensions required for the slice of the requested table,
  ! that you will retrieve later, by calling selectT.  May read in the table also.
  function fetchTable(theTable, tablename, wantedColumnName)
    USE tablemgr_io
    implicit none
    INTEGER, DIMENSION(2)          :: fetchTable
    TYPE(tableType), INTENT(OUT)   :: theTable
    CHARACTER(LEN=kiwi),INTENT(IN) :: wantedColumnName(:), tablename
    INTEGER :: numberWanted
101 FORMAT('Table ',a,':',a,'You want ',i3,' columns, have',i3,'!')

    theTable = getTable(tablename)             ! Fetch the table, perhaps reading it in.

    numberWanted = UBOUND(wantedColumnName,1)
    if (numberWanted<2 .or. UBOUND(theTable%goods,2) < numberWanted) then
       WRITE(msg,101) TRIM(tablename), CHAR(13), numberWanted, UBOUND(theTable%goods,2)
       call stopWithError
    END if
    fetchTable(1) = UBOUND(theTable%goods,1)   ! required rows
    fetchTable(2) = numberWanted               ! required columns
  end function fetchTable


  ! Returns a matrix (Matrix A) that contains all of the
  ! specified columns from a table.  No bounds checking is done (that should have
  ! been done by the getNeededSize() call)
  subroutine selectT(a, theTable, wantedColumnName)
    USE tablemgr_utils
    implicit none
    TYPE(tableType), INTENT(IN)       :: theTable
    REAL, DIMENSION(:,:), INTENT(OUT) :: a
    CHARACTER(LEN=kiwi),INTENT(IN)    :: wantedColumnName(:)
    INTEGER :: i,pos,numberWanted

102 FORMAT('Query on table ',a,': ',a,'No column "',a,'"')

    numberWanted = UBOUND(wantedColumnName,1)
    do i=1, numberWanted
       pos = positionOf(wantedColumnName(i), theTable%metadata%colname)
       IF (pos==0) then
          WRITE(msg,102) TRIM(theTable%metadata%tabname), CHAR(13), TRIM(wantedColumnName(i))
          call stopWithError
       end if
       a(:,i) = theTable%goods(:,pos)
    end do
  end subroutine selectT

end module tablemgr_query
