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


!   Written by:  Armin Munevar, California Department of Water Resources

! These are the routines that are available to subroutine code


!   This subroutine adds the weights and decision variables to the RCC matrix. Priority
!   levels are written as the first character in the row name ==> 1OBJECTIVE.
SUBROUTINE ADD_OBJECTIVE (Weight,ObjVar_Name,Priority,nwt)
  USE RCC_CACHE, ONLY: rcc_cache_add

  IMPLICIT NONE
  dll_export add_objective

  REAL,             INTENT(IN) :: Weight
  CHARACTER(LEN=*), INTENT(IN) :: ObjVar_Name
  CHARACTER(LEN=2), INTENT(IN) :: Priority
  INTEGER,          INTENT(IN) ::  nwt
  CHARACTER(LEN=50)            :: objective_row, objective_column

200 FORMAT ('OBJ',A2)
210 format (A2,'Objective')
  write (objective_column,200) Priority
  write (objective_row,210) Priority
  IF (ObjVar_Name(1:3) =='OBJ') THEN
     call rcc_cache_add( 'OBJ', objective_column, 1.)
     call rcc_cache_add( objective_row, objective_column, -1.)
     call rcc_cache_add( objective_row, 'FIX', 0.)
     call rcc_cache_add( 'MIN', objective_column, -1.0E+38)
     call rcc_cache_add( 'MAX', objective_column, 1.0E+38)
  ELSE
     call rcc_cache_add( objective_row, ObjVar_Name, Weight)
  END IF
END SUBROUTINE ADD_OBJECTIVE


SUBROUTINE ADD_CONSTRAINT (Control_Tag,C,Dvar_Name,&
     Surpl_Label,Slack_Label,Pen_Surpl,Pen_Slack,&
     RHS,goal_count,ndvar)
  USE RCC_CACHE, ONLY: rcc_cache_add

  IMPLICIT NONE
  dll_export add_constraint

  REAL,             DIMENSION(*), INTENT(IN) :: C
  REAL,                           INTENT(IN) :: Pen_Surpl, Pen_Slack, RHS
  CHARACTER(LEN=*), DIMENSION(*), INTENT(IN) :: Dvar_Name
  CHARACTER(LEN=*),               INTENT(IN) :: Control_Tag
  CHARACTER(LEN=*),               INTENT(IN) :: Surpl_label, Slack_label
  INTEGER,                        INTENT(IN) :: goal_count,ndvar
  CHARACTER(LEN=32)                          :: Surpl_Name, Slack_Name, prioritized_objective
!CB need???  CHARACTER(LEN=32)                          :: Surpl_Name, Slack_Name, prioritized_objective    !CB need to change LEN to 32?????????????????????????????????????
  INTEGER                                    :: i
  LOGICAL           			:: Slack_constrained,Surpl_constrained
  CHARACTER(LEN=2)			:: cyclenum

100 FORMAT('SURPL',i5.5)
110 FORMAT('SLACK',i5.5)
120 FORMAT(A2,'Objective')

  ! first we write the lhs decision variables and their coefficients
  DO i=1,ndvar
     call rcc_cache_add( Control_Tag, Dvar_Name(i), C(i))
  END DO

  ! Figure out how it is constrained, if at all
  if (Slack_Label(1:7)=='penalty') then
     Slack_constrained = .false.
  else
     Slack_constrained = .true.
  end if

  if (Surpl_Label(1:7)=='penalty') then
     Surpl_constrained = .false.
  else
     Surpl_constrained = .true.
  end if



  ! now write the actual constraint for the goal (ie. what is RHS): Fix, Max, Min
  IF      (Pen_Slack==0 .and..not. Slack_constrained .and. Surpl_constrained) THEN
     call rcc_cache_add( Control_Tag, 'Max', RHS)
  ELSE IF (Pen_Surpl==0 .and..not. Surpl_constrained .AND. Slack_constrained) THEN
     call rcc_cache_add( Control_Tag, 'Min', RHS)
  ELSE
     call rcc_cache_add( Control_Tag, 'Fix', RHS)
     ! get the priority number from the control tag, and prepare names for slack or surplus variables should they be needed
     READ( Control_Tag(1:2),FMT='(a2)') cyclenum
     WRITE( prioritized_objective, 120) cyclenum
     WRITE(Surpl_Name,100) goal_count
     WRITE(Slack_Name,110) goal_count
     IF (.not. Surpl_constrained) THEN
        call rcc_cache_add( Control_Tag, Surpl_Name, -1.)
        call rcc_cache_add( prioritized_objective, Surpl_Name, -1.*Pen_Surpl)
     END IF
     IF (.not. Slack_constrained) THEN
        call rcc_cache_add( Control_Tag, Slack_Name,  1.)
        call rcc_cache_add( prioritized_objective, Slack_Name, -1.*Pen_Slack)
     END IF
  END IF
END SUBROUTINE ADD_CONSTRAINT


SUBROUTINE MAKE_INTEGER (Bound_Var_Name, Priority)
  USE RCC_CACHE, ONLY: rcc_cache_add
  IMPLICIT NONE
  dll_export make_integer
  CHARACTER(LEN=*), INTENT(IN) :: Bound_Var_Name
  INTEGER, INTENT(IN)          :: Priority
  CALL rcc_cache_add( 'PRIORITY', Bound_Var_Name, REAL( Priority))
END SUBROUTINE MAKE_INTEGER


SUBROUTINE ADD_BOUND (Bound_Var_Name,Up_Bound,Lo_Bound)
  USE RCC_CACHE, ONLY: rcc_cache_add

  IMPLICIT NONE
  dll_export add_bound

  REAL,             INTENT(IN) :: Lo_Bound, Up_Bound
  CHARACTER(LEN=*), INTENT(IN) :: Bound_Var_Name

  ! write the MIN and MAX bounds of the variable in the RowName
  CALL rcc_cache_add( 'MIN', Bound_Var_Name, Lo_Bound)
  CALL rcc_cache_add( 'MAX', Bound_Var_Name, Up_Bound)
END SUBROUTINE ADD_BOUND


! This subroutine would ordinarily not be required.  All it does is perform a formatted
! write command, which is straighforward enough that it should not require a separate
! subroutine.
!
! Output will be going to the statevars.out file, which  was opened within this DLL.
!
! This output is generated by the state_report subroutine, generated by the Wresl Parser,
! which is not in this DLL.  Since it is not in this DLL, state_report does not have
! access to the same I/O units as the DLL.
!
! Therefore, the only way to write to this file from outside the DLL is by way of a
! subroutine located within the DLL.
SUBROUTINE WRITELOG( c1, c2, r)
  dll_export writelog
  CHARACTER(LEN=32), INTENT(IN) :: c1,c2
  REAL, INTENT(IN) :: r
  !   Format of each line is two 32-character strings followed by a floating-point number.
100 FORMAT(2a32,f16.2)
110 FORMAT(a32,32x,f16.2)
  IF(c2(1:1) == CHAR(0)) then
     WRITE(15,110) c1,r
  else
     WRITE(15,100) c1,c2,r
  END if
END SUBROUTINE WRITELOG
!
subroutine readlog(filename,var_names, var_values)
  implicit none
  dll_export readlog
  character(len=32), intent(in) :: filename
  character(len=32), dimension(:), intent(inout) :: var_names
  real, dimension(:), intent(inout) :: var_values
  !local variables
  character(len=40) :: dummy
  real, dimension(1094) :: value
  integer :: i,month, year, cycle
100 format(a12,i2,a1,i4)
101 format(a8,i2)
102 format(a32,a40,f10.4)
  open(unit=16,file=filename,status='old',err=220)
  read(16,100,END=210) dummy,month,dummy,year
  read(16,101,END=210) dummy,cycle
  i=1
  do while(.true.) 
     read(16,102,END=210) var_names(i), dummy, var_values(i)
     !write(*,*) i, var_names(i), var_values(i)
     i=i+1
  end do
210 close(unit=16)
220 continue
end subroutine readlog
! 
subroutine getvarvalue(var,names,values,val)
  implicit none
  dll_export getvarvalue
  character(len=32), dimension(:), intent(in) :: names
  real, dimension(:), intent(in) :: values
  character(len=32), intent(in) :: var
  real, intent(out) :: val
  character(len=32) :: tmpvar1, tmpvar2
  integer :: i, lenarray
  lenarray = size(names)
  tmpvar1 = trim(var)
  !write(*,*) 'Searching for ' , tmpvar1
  do i=1,lenarray
     tmpvar2 = trim(names(i))
     if ( lle(tmpvar1,tmpvar2) .and. lge(tmpvar1,tmpvar2) ) then
        val = values(i)
        !write(*,*) 'Found ', tmpvar2 , ' @ ', i, ' = ', values(i)
        return
     end if
  end do
  val = -99999
end subroutine getvarvalue
