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


! Stores XA solver RCC-format input in a FIFO cache
! D. McFadden (May 1998)

module rcc_cache
  USE xatypes
  USE Messenger
  
  TYPE(rcc), DIMENSION(:), ALLOCATABLE, PRIVATE   :: cache
  INTEGER, PRIVATE              :: current_size
  INTEGER, PRIVATE              :: top_item
  CHARACTER, PARAMETER, PRIVATE :: BadDataMarker = '*INVALID*'

  INTERFACE rcc_cache_add
     MODULE PROCEDURE  rcc_cache_add_char, rcc_cache_add_rcc
  END INTERFACE

  PRIVATE rcc_cache_add_char, rcc_cache_add_rcc

contains

  ! Create an empty cache
  subroutine rcc_cache_init(cache_size)
    INTEGER, INTENT(IN) :: cache_size
    ALLOCATE (cache(cache_size+1))
    cache(cache_size+1)%rowname = BadDataMarker   ! for safety and debugging use only
    call rcc_cache_clear
  end subroutine rcc_cache_init


  ! Destructor method
  subroutine rcc_cache_destroy
    DEALLOCATE(cache)
    call rcc_cache_clear
  END subroutine rcc_cache_destroy


  ! Reset and clear the cache
  subroutine rcc_cache_clear
    current_size=0
    top_item=1
  end subroutine rcc_cache_clear


  ! So, just how big is the cache?
  function rcc_cache_size()
    INTEGER :: rcc_cache_size
    rcc_cache_size = current_size
  end function rcc_cache_size


  ! Shift off top (first) member and return in the argument.  Returns false when end-of-cache reached.
  function rcc_cache_retrieve( cache_out) RESULT( thereAreMore)
    TYPE(rcc), INTENT(OUT)    :: cache_out
    LOGICAL                   :: thereAreMore
    thereAreMore = top_item <= current_size
    IF(.not. thereAreMore) return
    cache_out = cache( top_item)
    top_item = top_item+1
  end function rcc_cache_retrieve


  ! copy the entire cache into the given argument.  Assumed to be big enough to handle it all.
  ! This is a shortcut, in leiu of repeated calls to rcc_cache_retrieve()
  subroutine rcc_cache_getall( cache_out)
    TYPE(rcc), INTENT(OUT), DIMENSION(:)    :: cache_out
    cache_out( 1:current_size) = cache( 1:current_size)
  end subroutine rcc_cache_getall


  ! Add the given RCC item to the end of the list
  subroutine rcc_cache_add_char( rcc_string)
    CHARACTER(LEN=*), INTENT(IN) :: rcc_string
    CHARACTER(LEN=32) :: row
    CHARACTER(LEN=32) :: column
    REAL :: coefficient
    READ(rcc_string,100) row, column, coefficient
    call rcc_cache_add_rcc(row, column, coefficient)
100 FORMAT(a32,a32,f16.0)
  END subroutine rcc_cache_add_char


  ! Add the given RCC item to the end of the list
  subroutine rcc_cache_add_rcc(row, column, coefficent)
    CHARACTER(LEN=*), INTENT(IN) :: row, column
    REAL, INTENT(IN)             :: coefficent
    IF(current_size >= UBOUND(cache,1)-1) then
    	call WindowMessage(3, 'WRIMS Error', 'rcc_cache_add:  Need to increase RCC capacity')
    	CALL EXIT(1)
    END if
    current_size = current_size + 1
    cache(current_size)%rowname = row
    cache(current_size)%colname = column
    cache(current_size)%coef = coefficent
  end subroutine rcc_cache_add_rcc

end module rcc_cache
