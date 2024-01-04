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


!     Low-level routines that provide table lookup functionality
module tablemgr_utils
  implicit none
  private

  public pd_type, pd_retrieve             ! paired data items
  public  uc, deNullify, stopWithError, positionOf, screenOutRows    ! general-use utilities
  PUBLIC tableType, metaDataType

  ! Limit definitions

  INTEGER, PUBLIC, parameter :: MAX_COLS = 10                       ! max number of independent vars
  INTEGER, PUBLIC, parameter :: MAX_TABLES = 1000                   ! max number of tables allowed per model
  INTEGER, PUBLIC, parameter :: MAX_ENTRIES = 5000                  ! max total entries per table
  INTEGER, PUBLIC, parameter :: KIWI = 32                           ! size of most character strings

  ! Error Message thing
  CHARACTER(LEN=200), PUBLIC :: msg

  ! A paired-data element
  TYPE PD_TYPE
     REAL :: ind, dep
  END TYPE PD_TYPE

  ! Metadata for each table
  TYPE metaDataType
     CHARACTER(LEN=KIWI)                       :: tabname       ! the table name
     CHARACTER(LEN=KIWI), DIMENSION(MAX_COLS)  :: colname       ! names of each column
  END TYPE metaDataType

  ! Here is one entry (table) in the table database
  TYPE tableType
     TYPE(metaDataType)            :: metadata
     REAL, DIMENSION(:,:), POINTER :: goods
  END TYPE tableType

contains

  ! Screens out nonconforming rows by way of a logical mask.
  ! If corresponding element is .FALSE., that is an undesirable row
  ! If vl(:) is -902, that column is ignored and passed regardless
  subroutine screenOutRows(mask, a, vl)
    LOGICAL, INTENT(OUT)  :: mask(:)
    REAL,INTENT(IN)       :: a(:,:), vl(:)
    INTEGER               :: i
100 FORMAT('ScreenOutRows: array size mismatch.',a,'columns: ',2I2, ', rows: ',2I4)
    if (UBOUND(a,2)/=UBOUND(vl,1) .or. UBOUND(mask,1)/=UBOUND(a,1)) then
       WRITE(msg,100) CHAR(13), UBOUND(a,2), UBOUND(vl,1), UBOUND(mask,1), UBOUND(a,1)
       call stopWithError
    END if
    mask = .true.
    do i=1,UBOUND(vl,1)
       if (vl(i) > -900.) where (ABS(a(:,i)-vl(i)) > SPACING(.1)) mask = .false.
    END do
  end subroutine screenOutRows


  ! Returns a real value from a paired-data table
  ! The table is modified in this function.
  function pd_retrieve(pd, arg,lint)
    REAL                                    :: pd_retrieve, arg
    TYPE(PD_TYPE),DIMENSION(:),INTENT(INOUT):: pd
    INTEGER :: lint

!    call bubblesort(pd)
    call quicksort(UBOUND(pd,1),pd)
    pd_retrieve = slint(pd, arg, lint)
  end function pd_retrieve


  ! Returns a value from a paired-data table, optionally with interpolation/extrapolation.
  ! Based on 1987 routine by M. Chen, K. Linnett of Allied-Signal Aerospace
  ! Lint      Returns
  !   1         Min of the two
  !   2         Linear interpolate
  !   3         Mean value
  !   4         Max of the two
  !   5 *       Linear-log
  !   6 *       Log-linear
  !   7 *       Log-Log
  ! *not implemented
  function slint(pd,arg,lint)
    REAL                                    :: slint, arg
    TYPE(PD_TYPE),DIMENSION(:),INTENT(IN)   :: pd
    REAL, DIMENSION(2)                      :: a,b
    INTEGER                                 :: i, np,lint

    np=UBOUND(pd,1)
    IF (np==1) then
       slint=pd(1)%dep
    ELSE
       do i=2, np-1
          if ( arg < pd(i)%ind ) exit
       end do
       ! note:  i is now 2 if np==2; i is now either 2 or 3 if np==3, etc.
       IF(lint==1) THEN               ! Lower side value
          slint=pd(i-1)%dep
       ELSEIF(lint==2) THEN           ! Linear Interpolate
          a = pd(i-1:i)%ind
          b = pd(i-1:i)%dep
          slint = b(1) + (arg-a(1))*(b(2)-b(1))/(a(2)-a(1))
       ELSEIF(lint==3) THEN           ! Mean
          b = pd(i-1:i)%dep
          slint = (b(1)+b(2))/2.
       ELSE                           ! Upper side value
          slint=pd(i)%dep
       ENDIF
    end if
    return
  end function slint

  ! Sorts pd_type array in ascending order, based on the ind field.
  ! Uses the Quicksort algorithm of "Numerical Recipes".
	! n is the size or array; arr is replaced on output by its sorted rearrangement.
	! Parameters: M is the size of subarrays sorted by straight insertion and NSTACK is the required
	! auxiliary storage.
  ! A. Munevar 4/6/00
	SUBROUTINE quicksort(n,arr)
    INTEGER,INTENT(IN)::n
		INTEGER M,NSTACK
		TYPE(PD_TYPE), DIMENSION(:), INTENT(INOUT) :: arr
		TYPE(PD_TYPE) :: a,temp
		PARAMETER (M=7,NSTACK=50)
		INTEGER i,ir,j,jstack,k,l,istack(NSTACK)

100 FORMAT('Quick Sort: Stack limit exceeded: ',i2)

		jstack=0
		l=1
		ir=n
1 	if (ir-l < M) then ! Insertion sort when subarray small enough.
			do j=l+1,ir
				a=arr(j)
				do i=j-1,l,-1
					if (arr(i)%ind <= a%ind) goto 2
					arr(i+1)=arr(i)
				enddo
				i=l-1
2				arr(i+1)=a
			enddo

			if (jstack==0) return
			ir=istack(jstack) !Pop stack and begin a new round of partitioning.
			l=istack(jstack-1)
			jstack=jstack-2
		else
			k=(l+ir)/2 !Choose median of left,center, and right elements as partitioning element a. Also rearrange so that a(l) a(l+1)  a(ir).
			temp=arr(k)
			arr(k)=arr(l+1)
			arr(l+1)=temp
			if(arr(l)%ind > arr(ir)%ind)then
				temp=arr(l)
				arr(l)=arr(ir)
				arr(ir)=temp
			endif
			if(arr(l+1)%ind > arr(ir)%ind)then
				temp=arr(l+1)
				arr(l+1)=arr(ir)
				arr(ir)=temp
			endif
			if(arr(l)%ind > arr(l+1)%ind)then
				temp=arr(l)
				arr(l)=arr(l+1)
				arr(l+1)=temp
			endif
			i=l+1 !Initialize pointers for partitioning.
			j=ir
			a=arr(l+1) !Partitioning element.
3			continue !Beginning of innermost loop.
			i=i+1
			if (arr(i)%ind < a%ind) goto 3
4			continue
			j=j-1
			if (arr(j)%ind > a%ind) goto 4
			if (j < i) goto 5 !Pointers crossed. Exit with partitioning complete.
			temp=arr(i) !Exchange elements.
			arr(i)=arr(j)
			arr(j)=temp
			goto 3 !End of innermost loop.
5			arr(l+1)=arr(j) !Insert partitioning element.
			arr(j)=a
			jstack=jstack+2
			!Push pointers to larger subarray on stack, process smaller subarray immediately.
			if(jstack > NSTACK) then
       	WRITE(msg,100) jstack
      	call stopWithError()
      end if
			if(ir-i+1 >= j-l)then
				istack(jstack)=ir
				istack(jstack-1)=i
				ir=j-1
			else
				istack(jstack)=j-1
				istack(jstack-1)=l
				l=i
			endif
		endif
		goto 1
	END subroutine



  ! Ascending-sorts a pd_type array in place, based on the ind field
  ! 3/16/98 D. McFadden. THIS CODE HAS AN ERROR WITH SORTING
  subroutine bubblesort(array)
    TYPE(PD_TYPE), DIMENSION(:), INTENT(INOUT) :: array
    INTEGER :: n,i,p(1)

    n=UBOUND(array,1)
    IF(n<2) return
    do i=1,n-1
       p=minloc(array%ind,1)
       call swap(i,p(1)+i-1)
    end do
    return

  contains

    subroutine swap(m,n)
      INTEGER, INTENT(IN) :: m,n
      TYPE(PD_TYPE) :: temp
      temp=array(m)
      array(m)=array(n)
      array(n)=temp
    end subroutine swap

  end subroutine bubblesort


  ! A utility function to return the position of a character string in a character string array
  ! The last string in the array is '' .  Returns 0 if not found.
  function positionOf(wantedOne, list) Result(pos)
    implicit none
    CHARACTER(LEN=KIWI),INTENT(IN) :: wantedOne, list(:)
    CHARACTER(LEN=KIWI) :: desired
    INTEGER :: pos,i
    desired=uc(wantedOne)
    do i=1, UBOUND(list,1)
       if (list(i)==' ' ) exit
       if (list(i)==desired) then
          pos = i
          return
       END if
    end do
    pos=0
    return
  end function positionOf




  ! converts nulls to spaces in character strings
  subroutine deNullify(c)
    CHARACTER(LEN=*)::c
    INTEGER::i
    do i=1,LEN(c)
       IF(IACHAR(c(i:i))==0) c(i:i) = ' '
    end do
    return
  end subroutine deNullify


  ! converts string to upper case, remove tabs
  function uc(old) RESULT(new)
    CHARACTER(LEN=KIWI)             :: new
    CHARACTER(LEN=KIWI), INTENT(IN) :: old
    INTEGER :: i
    new=old
    do i=1,LEN_TRIM(old),1
       if (LGE(old(i:i),'a') .AND. LLE(old(i:i), 'z') ) new(i:I)=ACHAR(IACHAR(old(i:i))-32)
       if (old(i:i) == CHAR(9)) new(i:i) = ' '
    END do
  end function uc


  ! stops with error message printed
  ! This is Lahey and Wisk specific
  subroutine stopWithError
    USE Winteracter
    implicit none
    call WInitialise(' ')
    call WMessageBox(0,3,1,msg,'WRIMS Wrangler')
    call EXIT(1)    ! error level may be ignored in Windows
  end subroutine stopWithError


end module tablemgr_utils
