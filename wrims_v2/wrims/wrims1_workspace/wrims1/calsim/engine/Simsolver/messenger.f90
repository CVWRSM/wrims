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


! This module is platform-dependent.

module Messenger
contains

  SUBROUTINE WindowMessage(level,title,msg)
    USE Winteracter
    IMPLICIT NONE
    CHARACTER (LEN=*), INTENT(IN) :: msg, title
    INTEGER, INTENT(IN) :: level
    CALL WInitialise(' ')
    CALL WMessageBox(OKOnly,level,CommonOK,msg, title)
  END SUBROUTINE WindowMessage


  subroutine dialogInit
    USE winteracter
    TYPE(win_style) :: mywin
    call winitialise(' ')
    mywin%flags =  SysMenuOn + MinButton + FixedSizeWin
    mywin%x = -1
    mywin%y = 50
    mywin%width = 400
    mywin%height = 100
    mywin%title = 'WRIMS Execution'
    call WindowOpen(mywin)
  END subroutine dialogInit


  subroutine dialogUpdate( posn, s)
    USE winteracter
    CHARACTER(LEN=*) :: s
    CHARACTER(LEN=100) :: s_out
    INTEGER :: posn
    s_out = s   ! add trailing spaces
    s_out(100:100) = '.'
    call windowOutString(1000,posn, s_out)
  end subroutine dialogUpdate


  function dialogQuitRequest()
    USE winteracter
    implicit none
    TYPE(win_message) :: message
    LOGICAL :: dialogQuitRequest
    INTEGER :: itype

    call WMessagePeek( itype, message)
    if (itype == 9) THEN
       dialogQuitRequest = .true.
    else
       dialogQuitRequest = .false.
    END if
  END function dialogQuitRequest

  subroutine dialogRemove
    USE Winteracter
    call windowClose()
  end subroutine dialogRemove


end module Messenger
