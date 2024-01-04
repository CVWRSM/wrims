!     Last change:  R    29 May 2001   11:08 am
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
!   Altered by:  Dan Easton, California Department of Water Resources


! This program produces Fortran90 code from parsed WRESL input files.
! Parsed input tokens, or text strings, are manipulated and written
! out to a text file in Fortran90 format which contains all statements
! necessary to formulate the constraints and objective function in a RCC matrix.
!
  PROGRAM MKCODE

  IMPLICIT NONE

  INTEGER :: maxdef, maxdvar, maxwt, maxcond, pathlen,i,ilen
  PARAMETER(maxdef=40000,maxdvar=400,maxwt=4000,maxcond=200) !CB augmented (NEEDED WHEN MKCODE.EXE ERROR OCCURS IN LARGE CYCLES)
!CB  PARAMETER(maxdef=30000,maxdvar=200,maxwt=2000,maxcond=100) !CB augmented
!  PARAMETER(maxLineLength=130)  !CB need as global to this program
!CB  PARAMETER(maxdef=5000, maxgoal=10000, maxdvar=100, maxwt=1000, maxcond=50)  !CB if it will not compile, may need to up one or more of these
  CHARACTER(LEN=255):: wresl_path,define_file,goal_file,weight_file,code_file,types_file,cl
  CHARACTER(LEN=2):: cyclenum

! get command line WRESL_PATH to read and write files
  call getcl(cl)
  cl=TRIM(cl)
  DO i=1,2
  	ilen=INDEX(cl,' ')
  	IF (i==1) wresl_path=cl(1:ilen-1)
  	IF (i==2) cyclenum=cl(1:ilen-1)
  	cl=cl(ilen+1:)
  END DO

  wresl_path=TRIM(wresl_path)
  pathlen=LEN_TRIM(wresl_path)

  define_file(1:pathlen)=wresl_path
  define_file(pathlen+1:pathlen+8)='\defines'
  define_file(pathlen+9:pathlen+10)=TRIM(cyclenum)
  define_file(pathlen+11:pathlen+14)='.txt'

  goal_file(1:pathlen)=wresl_path
  goal_file(pathlen+1:pathlen+6)='\goals'
  goal_file(pathlen+7:pathlen+8)=TRIM(cyclenum)
  goal_file(pathlen+9:pathlen+12)='.txt'

  weight_file(1:pathlen)=wresl_path
  weight_file(pathlen+1:pathlen+8)='\weights'
  weight_file(pathlen+9:pathlen+10)=TRIM(cyclenum)
  weight_file(pathlen+11:pathlen+14)='.txt'

  code_file(1:pathlen)=wresl_path
  code_file(pathlen+1:pathlen+5)='\code'
  code_file(pathlen+6:pathlen+7)=TRIM(cyclenum)
  code_file(pathlen+8:pathlen+11)='.f90'

  types_file(1:pathlen)=wresl_path
  types_file(pathlen+1:pathlen+6)='\types'
  types_file(pathlen+7:pathlen+8)=TRIM(cyclenum)
  types_file(pathlen+9:pathlen+12)='.f90'
  
! open input and output files

  OPEN  (UNIT=5,  FILE=define_file,  STATUS='old')
IF (charneq(cyclenum,'00'))  OPEN  (UNIT=7,  FILE=goal_file,    STATUS='old')
IF (charneq(cyclenum,'00'))  OPEN  (UNIT=8,  FILE=weight_file,  STATUS='old')

  OPEN  (UNIT=15, FILE=code_file, STATUS='unknown')
  OPEN  (UNIT=16, FILE=types_file,STATUS='unknown')  

  WRITE(15,*) '!    Copyright (C) 1998 State of California, Department of Water        '
  WRITE(15,*) '!    Resources.                                                         '
  WRITE(15,*) '                                                                        '
  WRITE(15,*) '!    This program is licensed to you under the terms of the GNU General '
  WRITE(15,*) '!    Public License, version 2, as published by the Free Software       '
  WRITE(15,*) '!    Foundation.                                                        '
  WRITE(15,*) '                                                                        '
  WRITE(15,*) '!    You should have received a copy of the GNU General Public License  '
  WRITE(15,*) '!    along with this program; if not, contact Dr. Francis Chung, below, '
  WRITE(15,*) '!    or the Free Software Foundation, 675 Mass Ave, Cambridge, MA       '
  WRITE(15,*) '!    02139, USA.                                                        '
  WRITE(15,*) '                                                                        '
  WRITE(15,*) '!    THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA     '
  WRITE(15,*) '!    DEPARTMENT OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY     '
  WRITE(15,*) '!    EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE  '
  WRITE(15,*) '!    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR '
  WRITE(15,*) '!    PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE CALIFORNIA          '
  WRITE(15,*) '!    DEPARTMENT OF WATER RESOURCES OR ITS CONTRIBUTORS BE LIABLE FOR    '
  WRITE(15,*) '!    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR           '
  WRITE(15,*) '!    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT  '
  WRITE(15,*) '!    OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS; OR  '
  WRITE(15,*) '!    BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF         '
  WRITE(15,*) '!    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT          '
  WRITE(15,*) '!    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE  '
  WRITE(15,*) '!    USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   '
  WRITE(15,*) '!    DAMAGE.                                                            '
  WRITE(15,*) '                                                                        '
  WRITE(15,*) '!    For more information, contact:                                     '
  WRITE(15,*) '                                                                        '
  WRITE(15,*) '!    Dr. Francis Chung                                                  '
  WRITE(15,*) '!    California Dept. of Water Resources                                '
  WRITE(15,*) '!    Division of Planning, Delta Modeling Section                       '
  WRITE(15,*) '!    1416 Ninth Street                                                  '
  WRITE(15,*) '!    Sacramento, CA  95814                                              '
  WRITE(15,*) '!    916-653-5601                                                       '
  WRITE(15,*) '!    chung@water.ca.gov                                                 '
  WRITE(15,*) '                                                                        '

  IF (charneq(cyclenum,'00')) THEN
  	WRITE (15,*) 'SUBROUTINE CODE',cyclenum,'(p, goal_count)'
  ELSE 
  	WRITE (15,*) 'SUBROUTINE CODE',cyclenum,'(p)'
  END IF

  WRITE (15,*) ' '
  WRITE (15,*) 'USE TYPES00'
  IF (charneq(cyclenum,'00'))  WRITE (15,*) 'USE TYPES',cyclenum
  WRITE (15,*) 'USE GLOBAL'
  WRITE (15,*) 'USE CODE_UTILS'
  WRITE (15,*) 'IMPLICIT NONE'
  WRITE (15,*)
  WRITE (15,*)  'INTEGER, intent(in) :: p'
  IF (charneq(cyclenum,'00')) WRITE (15,*)  'INTEGER, intent(inout) :: goal_count'
  ! This initializes the condition tags in the event that the corresponding
  ! state variable is not redefined during this iteration
  WRITE (15,*) 'Def_Condition_Tag = "(unchanged)"'

  WRITE (16,*) 'MODULE TYPES',cyclenum
  WRITE (16,*) ' '
  WRITE (16,*) 'IMPLICIT NONE '
  WRITE (16,*) ' '
  	if (chareq(cyclenum,'00')) then

  		WRITE (16,*) 'INTEGER :: maxdvar'
  		WRITE (16,*) ' '
  		WRITE (16,120) maxdvar
  		WRITE (16,*) ' '

		120 FORMAT ('PARAMETER(maxdvar=',i3,')')

		! Type statements written for variables used in the weights section of source code
  		WRITE (16,*) '! Type Statements for WEIGHTS Variables '
  		WRITE (16,*) ' '
  		WRITE (16,*) 'REAL              :: Weight '
  		WRITE (16,*) 'CHARACTER(LEN=32) :: ObjVar_Name '
		!  WRITE (16,*) 'CHARACTER(LEN=2)  :: Priority '
		!  WRITE (16,*) 'INTEGER           :: nwt '
  		WRITE (16,*) ' '

		! Type statements written for variables used in define section of source code
  		WRITE (16,*) '! Type Statements for DEFINE Variables '
  		WRITE (16,*) ' '
  		WRITE (16,*) 'CHARACTER(LEN=32)  :: Def_Condition_Tag(', maxdef, ')'
  		WRITE (16,*) ' '

		! Type statements written for variables used in goal section of source code
  		WRITE (16,*) '! Type Statements for GOAL Variables '
  		WRITE (16,*) ' '
  		WRITE (16,*) 'REAL , DIMENSION(maxdvar)              :: C '
  		WRITE (16,*) 'CHARACTER(LEN=32),DIMENSION(maxdvar)  :: Dvar_Name '
  		WRITE (16,*) 'CHARACTER(LEN=32)                     :: Control_Tag '
  		WRITE (16,*) 'CHARACTER(LEN=9)                      :: Surpl_Label, Slack_Label '
  		WRITE (16,*) 'REAL                                :: Pen_Surpl, Pen_Slack, RHS '
		!  WRITE (16,*) 'INTEGER                               :: goal_count, ndvar '
  		!WRITE (16,*) 'CHARACTER(LEN=16)                     :: Bound_Var_Name '
		!WRITE (16,*) 'REAL                                   :: Up_Bound, Lo_Bound '
  		WRITE (16,*) ' '
  	end if

! call subroutine to read and write define data
  CALL DEFINES(maxcond)
  
! subroutine to read and write weights data
IF (charneq(cyclenum,'00'))  CALL WEIGHTS(maxwt,cyclenum)

! call subroutine to read and write goal data
IF (charneq(cyclenum,'00'))  CALL GOALS(maxcond, cyclenum)

  WRITE (16,*) 'END MODULE'
  WRITE (15,*) 'END SUBROUTINE'

!------------------------------------------------------------------------------
  CONTAINS

! ****************** READ WEIGHTS DATA FROM UNIT 8 AND WRITE TO UNIT 15 ********

  SUBROUTINE WEIGHTS(maxwt,cyclenum)

  IMPLICIT NONE

  INTEGER                             :: maxwt
  CHARACTER(LEN=32), DIMENSION(maxwt) :: Weight
  CHARACTER(LEN=32), DIMENSION(maxwt) :: ObjVar_Name  
  CHARACTER(LEN=2), DIMENSION(maxwt)  :: Priority
  INTEGER                             :: i,k,nwts
  CHARACTER(LEN=2), INTENT(IN):: cyclenum
! initialize all variables used in weights
  DO i=1,maxwt ! max number of weighted variables
    ObjVar_Name(i) = 'none'
    Weight(i) = '0'
    Priority(i) = '00'
  END DO
  
  WRITE (15,100) '0'
  WRITE (15,110) 'OBJ'
  call writeAddObjective( 15, cyclenum, 0)

! read weights and variable names and write weight section of source code
i=1
  DO while (.true.)
    READ  (8,*,END=777)    Weight(i), ObjVar_Name(i),Priority(i)
    i=i+1
  END DO
777 CONTINUE

  nwts = i - 1
  k=0
  DO i=1,nwts
    IF (chareq(Priority(i),'00') .and. k==0) THEN ! special for OBJ0
  		WRITE (15,100) '0'
		WRITE (15,110) 'OBJ'
  		call writeAddObjective( 15, Priority(i), 0)
        k=1
    END IF
    WRITE (15,100)   TRIM(Weight(i))
    WRITE (15,110)   TRIM(ObjVar_Name(i))
    call writeAddObjective( 15, Priority(i), i)
  END DO

100 FORMAT ('Weight = ',a)
110 FORMAT ('ObjVar_Name = ',"'",a,"'")

  CLOSE (UNIT=8)

  END SUBROUTINE WEIGHTS
! ******************************************************************************


! ****************** READ DEFINE DATA FROM UNIT 5 AND WRITE TO UNITS 15,16 *****

  SUBROUTINE DEFINES(maxcond)

  IMPLICIT NONE
  INTEGER                                      :: maxcond,j, Def_Var_Number
  CHARACTER(LEN=32)                            :: Def_Var_Name, Temp   !CB moved temp here  
  CHARACTER(LEN=32)                            :: Def_Condition_Tag
  CHARACTER(LEN=500)                           :: Def_Condition
  CHARACTER(LEN=50)                            :: DoRange,ArraySize,Counter
!CB  CHARACTER(LEN=2000)                          :: Def_Value,Temp
  !CB next line is 30*132+100 where 132 is max line length (tested) and 30 is max # of lies of length 132 (tested)
  CHARACTER(LEN=4060)                         :: Def_Value     !CB Al needed it much larger than 2000; max of 39 contination lines!
  INTEGER, DIMENSION(maxcond)                  :: Defcond_Num
  LOGICAL                                      :: skipAnyOthers, needToEndIf,isSum,isArray

100 FORMAT (a32,i5)
110 FORMAT (i5,12x,a)
120 FORMAT (a)

define: DO while (.true.)
          read (5,100,END=777) Def_Var_Name, Def_Var_Number
          isArray = .false.
          IF (chareq(Def_Var_Name(1:1),'$')) THEN        ! An Array Statement declaration/dimensionalization
             Def_Var_Name = Def_Var_Name(2:)
             isArray = .true.
             read (5,120) ArraySize
             read (5,120) DoRange
             read (5,120) Counter
             WRITE (16,*) 'REAL,DIMENSION(',TRIM(ArraySize),') :: ', TRIM(Def_Var_Name), ' !', Def_Var_Number
             write (15,*) 'do ',DoRange
          ELSE
             ! normal declaration for a real variable
	     WRITE (16,*) 'REAL  :: ', TRIM(Def_Var_Name), ' !', Def_Var_Number
          END IF
          
          needToEndIf = .false.
          skipAnyOthers = .false.
          j = 1
defcond:  DO while (.true.) ! loop through the number of conditions in define statement
            read (5,110)  Defcond_Num(j), Def_Condition_Tag

            IF (Defcond_Num(j)==99999) EXIT defcond
            IF (Defcond_Num(j)==-1) THEN      ! An External definition
           		write(16,*) 'EXTERNAL ', TRIM(Def_Var_Name)
           		EXIT defcond
            END if
            
            read (5,120) Def_Condition

            read (5,120) Def_Value
            isSum = .false.
            IF (chareq(Def_Value(1:1),'$')) THEN        ! A Summation Statement
                isSum = .true.
                DoRange = Def_Value(2:)
                read (5,120) Def_Value
                Def_Value(34:)=TRIM(Def_Value(1:))
                Def_Value(1:32)=TRIM(Def_Var_Name)
                Def_Value(33:33)='+'
            END IF
            IF (isArray) THEN        ! An Array Statement
                !write (15,*) 'do ',DoRange
            END IF

            ! handles the case where they have cases beyond a CONDITION ALWAYS
            if (skipAnyOthers) CYCLE defcond

            IF (j==1) THEN   ! if it is the first condition
              IF (Def_Condition == 'always') THEN
                 skipAnyOthers = .true.
                 if (isSum) write (15,*) TRIM(Def_Var_Name),'=0.0'
                 if (isSum) write (15,*) 'do ',DoRange
              else
                 needToEndIf = .true.
                 CALL writetrimmedIF( 15, 'IF (', Def_Condition, ') THEN')
                 call writeDefConTag( 15, Def_Var_Number, Def_Condition_Tag)
                 if (isSum) write (15,*) TRIM(Def_Var_Name),'=0.0'
                 if (isSum) write (15,*) 'do ',DoRange
              END IF
            ELSE        ! if second or later condition
              IF (Def_Condition == 'always') THEN
                  skipAnyOthers = .true.
                  write( 15, *) 'ELSE'
              else
                  CALL writetrimmedIF( 15, 'ELSE IF (', Def_Condition,') THEN')
              END if
              call writeDefConTag( 15, Def_Var_Number, Def_Condition_Tag)
              if (isSum) write (15,*) TRIM(Def_Var_Name),'=0.0'
              if (isSum) write (15,*) 'do ',DoRange
            END IF

            if (isArray) then
              !array declaration
              Temp = TRIM(Def_Var_Name) // "(" // TRIM(Counter) // ")"
              call writeTrimmed( 15, Temp, Def_Value)
            else
              !normal equation
              call writeTrimmed( 15, Def_Var_Name, Def_Value)
            end if

            IF (isSum) WRITE(15,*) 'end do'         ! A Summation/Array Statement
            
            j = j + 1
                        
          END DO defcond

          IF( needToEndIf)  WRITE (15,*) 'END IF '
          IF (isArray) WRITE(15,*) 'end do'
          call subBreaker(15,100,cyclenum)
        END DO define

777  CONTINUE

130 FORMAT (2x,a,' = ',a)
135 FORMAT (a,' = ',a,a1)
137 FORMAT (a1,a,a1)

140 FORMAT (a4,a,a6)
145 FORMAT (a9,a,a6)

      WRITE (15,*) '!******************************************** '
      WRITE (15,*) ' '

      CLOSE (UNIT=5)

      END SUBROUTINE DEFINES
! **********************************************************************


! ********* READ IN GOAL DATA FROM UNIT 7 AND WRITE TO UNIT 15 **********

  SUBROUTINE GOALS(maxcond,cyclenum)

  IMPLICIT NONE

  INTEGER                                       :: ndvar,maxcond
  CHARACTER(LEN=2)			:: cyclenum
  CHARACTER(LEN=32), DIMENSION(maxcond) :: Pen_Surpl, Pen_Slack
! CHARACTER(LEN=16)                             :: Goal_Set
  CHARACTER(LEN=29)                             :: Goal_Tag
  CHARACTER(LEN=32), DIMENSION(maxdvar) :: Dvar_Name
  CHARACTER(LEN=500), DIMENSION(maxdvar) :: C
  CHARACTER(LEN=500), DIMENSION(maxcond) :: Condition
  CHARACTER(LEN=2000), DIMENSION(maxcond) :: RHS
  CHARACTER(LEN=32), DIMENSION(maxcond) :: Condition_Tag
  CHARACTER(LEN=9),  DIMENSION(maxcond) :: Surpl_Label, Slack_Label
  INTEGER, DIMENSION(maxcond)           :: Cond_Num
  CHARACTER(LEN=32)                             :: Bound_Var_Name
  CHARACTER(LEN=64)                             :: Up_Bound, Lo_Bound
  CHARACTER(LEN=5)                              :: coefchar
  INTEGER                                       :: j,k

  coefchar(1:2)='C('
  coefchar(5:5)=')'

! read (7,*)  Goal_Set

goal: DO while (.true.)  ! beginning of goal loop
         READ (7,100,END=777)  Goal_Tag
         
100 FORMAT (a29)

! the i index in arrays below kept for later addition of goal sets -- otherwise not needed
j = 0
glcond: DO while (.true.) ! beginning of condition loop
	j = j + 1
         READ (7,110) Cond_Num(j),Condition_Tag(j)
         IF (Cond_Num(j)==99999) THEN
         	IF (Condition(j-1)/='always'.or.Cond_Num(j-1)/=1) THEN ! if at end of conditional statement
           		WRITE (15,*) 'END IF '
         	END IF
            	WRITE (15,*) 'goal_count = goal_count + 1'
            	EXIT
         END IF

         ! Special cases:  negative condition numbers.
         !     These are defining a decision variable
         !
         !     -1    :  is real, has nonstandard bounds
         !     -2    :  is real, has standard bounds
         !     -3    :  is integer, has nonstandard bounds.  Condition_Tag has priority level.
         !     -4    :  is integer, has standard bounds.  Condition_Tag has priority level.
         IF (Cond_Num(j)<=-1) THEN     ! This might require a Bound statement
             READ (7,120) Bound_Var_Name
             IF(Cond_num(j) < -2) write (15,333) Trim(Bound_Var_Name), TRIM(Condition_Tag(j))

             IF (Cond_Num(j) == -2 .OR. Cond_Num(j) == -4) THEN ! standard bounds
             ELSE
                 READ (7,120) Up_Bound
                 READ (7,120) Lo_Bound
                 WRITE (15,332) trim( Bound_Var_Name), trim( Up_Bound), trim( Lo_Bound)
             END IF

             EXIT

         ELSE
         
          READ (7,120) Condition(j)
          READ (7,130) ndvar ! number of decision variables in this goal
          IF (j==1) THEN  ! if this is the first condition
            IF (Condition(j)=='always') THEN ! if 1st condition is 'always'
              WRITE (15,315) TRIM(Goal_Tag),j
              DO k=1,ndvar ! loop through number of decision variables
                read (7,*)     C(k), Dvar_Name(k)
              	write (coefchar(3:4),FMT='(i2)') k
                call writeTrimmed(15,coefchar,C(k))
                WRITE (15,155) k, TRIM(Dvar_Name(k))
              END DO
              READ (7,120)  RHS(j)
              READ (7,160)  Surpl_Label(j), Pen_Surpl(j)
              READ (7,160)  Slack_Label(j), Pen_Slack(j)
              WRITE (15,170) 'Surpl_Label = ',"'",Surpl_Label(j),"'"
              WRITE (15,170) 'Slack_Label = ',"'",Slack_Label(j),"'"
              WRITE (15,180) 'Pen_Surpl = ', Pen_Surpl(j)
              WRITE (15,180) 'Pen_Slack = ', Pen_Slack(j)
              call writeTrimmed(15,'RHS',RHS(j))
              call writeAddConstraint( 15, ndvar)
            ELSE  ! if first condition is not 'always'
              CALL writeTrimmedIF( 15, 'IF (', Condition(j), ') THEN')
              WRITE (15,320) TRIM(Goal_Tag),j
              DO k=1,ndvar ! loop through number of decision variables
                read (7,*)     C(k), Dvar_Name(k)
              	write (coefchar(3:4),FMT='(i2)') k
                call writeTrimmed(15,coefchar,C(k))
                WRITE (15,150) k, TRIM(Dvar_Name(k))
              END DO
              READ (7,120)  RHS(j)
              READ (7,160)  Surpl_Label(j), Pen_Surpl(j)
              READ (7,160)  Slack_Label(j), Pen_Slack(j)
              WRITE (15,175) 'Surpl_Label = ',"'",Surpl_Label(j),"'"
              WRITE (15,175) 'Slack_Label = ',"'",Slack_Label(j),"'"
              WRITE (15,185) 'Pen_Surpl = ', Pen_Surpl(j)
              WRITE (15,185) 'Pen_Slack = ', Pen_Slack(j)
              call writeTrimmed(15,'RHS',RHS(j))
              call writeAddConstraint(15, ndvar)
            END IF
          ELSE  ! if this is second or greater condition
            IF (Condition(j)=='always') THEN  ! if condition is 'always'
              WRITE (15,*) 'ELSE '
              IF (j<10) THEN  ! this 'IF' is only for formatting output
                WRITE (15,320) TRIM(Goal_Tag),j
              ELSE
                WRITE (15,330) TRIM(Goal_Tag),j
              END IF
              DO k=1,ndvar ! loop through number of decision variables
                read (7,*)     C(k), Dvar_Name(k)
              	write (coefchar(3:4),FMT='(i2)') k
                call writeTrimmed(15,coefchar,C(k))
                WRITE (15,150) k, TRIM(Dvar_Name(k))
              END DO
              READ (7,120)  RHS(j)
              READ (7,160)  Surpl_Label(j), Pen_Surpl(j)
              READ (7,160)  Slack_Label(j), Pen_Slack(j)
              WRITE (15,175) 'Surpl_Label = ',"'",Surpl_Label(j),"'"
              WRITE (15,175) 'Slack_Label = ',"'",Slack_Label(j),"'"
              WRITE (15,185) 'Pen_Surpl = ', Pen_Surpl(j)
              WRITE (15,185) 'Pen_Slack = ', Pen_Slack(j)
              call writeTrimmed(15,'RHS',RHS(j))
              call writeAddConstraint(15, ndvar)
            ELSE  ! if condition is not 'always'
              CALL writeTrimmedIF( 15, 'ELSE IF (', Condition(j), ') THEN')
              IF (j<10) THEN  ! this 'IF' is only for formatting output
                WRITE (15,320) TRIM(Goal_Tag),j
              ELSE
                WRITE (15,330) TRIM(Goal_Tag),j
              END IF
              DO k=1,ndvar ! loop through number of decision variables
                read (7,*)     C(k), Dvar_Name(k)
              	write (coefchar(3:4),FMT='(i2)') k
                call writeTrimmed(15,coefchar,C(k))
                WRITE (15,150) k, TRIM(Dvar_Name(k))
              END DO
              READ (7,120)  RHS(j)
              READ (7,160)  Surpl_Label(j), Pen_Surpl(j)
              READ (7,160)  Slack_Label(j), Pen_Slack(j)
              WRITE (15,175) 'Surpl_Label = ',"'",Surpl_Label(j),"'"
              WRITE (15,175) 'Slack_Label = ',"'",Slack_Label(j),"'"
              WRITE (15,185) 'Pen_Surpl = ', Pen_Surpl(j)
              WRITE (15,185) 'Pen_Slack = ', Pen_Slack(j)
              call writeTrimmed(15,'RHS',RHS(j))
              call writeAddConstraint(15, ndvar)
            END IF
          END IF
         END IF
         
         END DO glcond  ! end of goal-conditions loop

         call subBreaker(15,50,cyclenum)

      END DO goal  ! end of goal loop

777 CONTINUE

    CLOSE (UNIT=7)

110 FORMAT (i5,12X,a)
!CB not used 115 FORMAT (a16)
117 FORMAT (e16.5)
119 FORMAT (a,e16.5)
120 FORMAT (a)
130 FORMAT (i2)
140 FORMAT (2x,'C(',i2,') = ',a)
145 FORMAT ('C(',i2,') = ',a)
150 FORMAT (2x,'Dvar_Name(',i2,') = ',"'",a,"'")
155 FORMAT ('Dvar_Name(',i2,') = ',"'",a,"'")
160 FORMAT (a9,a)
170 FORMAT (a,a,a9,a)
180 FORMAT (a,a)
175 FORMAT (2x,a,a,a9,a)
185 FORMAT (2x,a,a)
200 FORMAT (a4,a,a6)
205 FORMAT (a9,a,a6)
300 FORMAT ('C(',i2,') = ',a)
310 FORMAT ('Dvar_Name(',i2,') = ',"'",a,"'")
315 FORMAT ('Control_Tag = ',"'",a,'/',i1,"'")
320 FORMAT (2x,'Control_Tag = ',"'",a,'/',i1,"'")
330 FORMAT (2x,'Control_Tag = ',"'",a,'/',i2,"'")
 332 FORMAT ('CALL ADD_BOUND ("',a,'",real(',a,'),real(',a,'))')
!332 FORMAT ('CALL ADD_BOUND ("',a,'",dfloati(',a,'),dfloati(',a,'))')
333 FORMAT ('CALL MAKE_INTEGER ("',a,'",'a,')')
400 FORMAT('RHS= ',a,a1)
405 FORMAT(a1,a,a1)
410 FORMAT(a1,a)
420 FORMAT('RHS= ',a)

    END SUBROUTINE GOALS
! **********************************************************************

    function chareq (string,compstring) RESULT (trueorfalse)
      CHARACTER(LEN=*),INTENT(IN) :: string,compstring
      logical                     :: trueorfalse
      trueorfalse=LLE(string,compstring).and.LGE(string,compstring)
    end function chareq

    function charneq (string,compstring) RESULT (trueorfalse)
        CHARACTER(LEN=*),INTENT(IN) :: string,compstring
        logical                     :: trueorfalse
        trueorfalse=LLT(string,compstring).or.LGT(string,compstring)
    end function charneq

END PROGRAM MKCODE


! breaks up the CODE subroutine into manageable parts
SUBROUTINE subBreaker(iunit, when2break,cyclenum)
  INTEGER, SAVE      :: linecounter=0, subNumberCounter = 1
  INTEGER, INTENT(IN):: iunit, when2break
  CHARACTER(LEN=2),INTENT(IN)  :: cyclenum

  linecounter = linecounter + 1
  IF( MOD( linecounter, when2break) == 0) then
    if (chareq(cyclenum,'00')) then
      WRITE( iunit,601) cyclenum, subNumberCounter, cyclenum, subNumberCounter,cyclenum
      subNumberCounter = subNumberCounter + 1
    else
      WRITE( iunit,600) cyclenum, subNumberCounter, cyclenum, subNumberCounter,cyclenum
      subNumberCounter = subNumberCounter + 1
    END if
  END IF

  600 format ('call code',a2,i3.3,'(p, goal_count)'/'end subroutine'//'subroutine code',a2,i3.3,'(p, goal_count)'/ &
              'use types00'/'use types',a2/'use global'/'use code_utils'/'implicit none'//'INTEGER, intent(in) :: p'/&
              'INTEGER, intent(inout) :: goal_count')
  601 format ('call code',a2,i3.3,'(p)'/'end subroutine'//'subroutine code',a2,i3.3,'(p)'/ &
              'use types',a2/'use global'/'use code_utils'/'implicit none'//'INTEGER, intent(in) :: p')
contains

      function chareq (string,compstring) RESULT (trueorfalse)
        CHARACTER(LEN=*),INTENT(IN) :: string,compstring
        logical                     :: trueorfalse
        trueorfalse=LLE(string,compstring).and.LGE(string,compstring)
      end function chareq
  END SUBROUTINE


! Writes out an assignment expression that might require breaking up the lines
! to conform to F90's 140 column limit
! CB changed max charecters/line from 100 to 105 due to 140 - 32 - 1 -2 = 105 chars max
!   and only 39 continuation lines max allowed by Lahey
! Work around to LF90 compiler bug is implemented.  Compiler errors occur if the
! last characters of a line are '_&'.  Remove this IF-ELSE block when compiler bug
! is fixed
! Armin Munevar 10/7/98
! Clay Booher - this would be better writen as calling a recursive routine instead of the 20+ IFs 
subroutine writetrimmed(iunit, var_name, var_expr)
  CHARACTER(LEN=*)   :: var_name,var_expr
!  INTEGER :: maxLineLength = 130  !CB
  maxLineLength = 100
  

  ilen=LEN_TRIM(var_expr)
  WRITE(iunit, *) '!  ilen=', ilen
  if (ilen<=maxLineLength) then
     WRITE(iunit,450)  TRIM(var_name), var_expr(1:ilen)
  else
      call checkunderscore(var_expr)
!      WRITE (iunit,500) TRIM(var_name), var_expr(1:100)
!      IF (ilen>100) WRITE(iunit,510) var_expr(101:200)
!      IF (ilen>200) WRITE(iunit,510) var_expr(201:300)
!      IF (ilen>300) WRITE(iunit,510) var_expr(301:400)
!      IF (ilen>400) WRITE(iunit,510) var_expr(401:500)
!      IF (ilen>500) WRITE(iunit,510) var_expr(501:600)
!      IF (ilen>600) WRITE(iunit,510) var_expr(601:700)
!      IF (ilen>700) WRITE(iunit,510) var_expr(701:800)
!      IF (ilen>800) WRITE(iunit,510) var_expr(801:900)
!      IF (ilen>900) WRITE(iunit,510) var_expr(901:1000)
!      IF (ilen>1000) WRITE(iunit,510) var_expr(1001:1100)        
!      IF (ilen>1100) WRITE(iunit,510) var_expr(1101:1200)
!      IF (ilen>1200) WRITE(iunit,510) var_expr(1201:1300)
!      IF (ilen>1300) WRITE(iunit,510) var_expr(1301:1400)
!      IF (ilen>1400) WRITE(iunit,510) var_expr(1401:1500)
!      IF (ilen>1500) WRITE(iunit,510) var_expr(1501:1600)
!      IF (ilen>1600) WRITE(iunit,510) var_expr(1601:1700)
!      IF (ilen>1700) WRITE(iunit,510) var_expr(1701:1800)
!      IF (ilen>1800) WRITE(iunit,510) var_expr(1801:1900)
!      IF (ilen>1900) WRITE(iunit,510) var_expr(1901:ilen)
!      WRITE(iunit,510)
      !CB changed the above mess to a DO loop
      DO i=0,30,1
        IF (i==0) THEN
          WRITE (iunit,500) TRIM(var_name), var_expr(1:100)
        ELSE
          IF (ilen > i*maxLineLength ) WRITE(iunit,510) var_expr( i*maxLineLength + 1: (i+1)*maxLineLength)
!        WRITE(iunit, *) '!  (i-1)*maxLineLength + 101 =', (i-1)*maxLineLength + 101
!        WRITE(iunit, *) '!  i*maxLineLength+100 =', i*maxLineLength+100
        END IF
	  END DO
	  WRITE(iunit,510)
  end if

  450 FORMAT(a,'=',a)
  500 FORMAT(a,'=',a,'&')
  510 FORMAT('&', a, '&')
END subroutine writetrimmed
  
!subroutine 
  
subroutine checkunderscore(expr)
  CHARACTER(LEN=*) :: expr
  INTEGER :: i
  
  maxLineLength = 100  
  
  ilen=LEN_TRIM(expr)
  if (ilen>maxLineLength) then
    do i=maxLineLength,ilen,maxLineLength
      
      ! prevent (1) "_" split
      !         (2) ".and." splits into ".a&" and "&nd."
      !         (3) ".or." splits into ".o&" and "&r."
      if (chareq(expr(i:i),'_') ) then           
         expr(i+1:)=expr(i:)
         expr(i:i)='&'
         ilen=ilen+1

      else if (chareq(expr(i-1:i+3),'.and.') )  then           
         expr(i+1:)=expr(i-1:)
         expr(i-1:i)='  '
         !expr(i:i)='&'
         ilen=ilen+2
         
      else if (chareq(expr(i-1:i+2),'.or.') )  then           
         expr(i+1:)=expr(i-1:)
         expr(i-1:i)='  '
         !expr(i:i)='&'
         ilen=ilen+2   
         
      end if

      
    end do
  end if

  contains
      function chareq (string,compstring) RESULT (trueorfalse)
        CHARACTER(LEN=*),INTENT(IN) :: string,compstring
        logical                     :: trueorfalse
        trueorfalse=LLE(string,compstring).and.LGE(string,compstring)
      end function chareq

end subroutine checkunderscore
     
    

! Writes out an expression in an IF statement that might require breaking up the lines
! to conform to F90's 140 column limit
subroutine writetrimmedIf(iunit, if_expr, var_expr, post_expr)
  CHARACTER(LEN=*) :: if_expr, post_expr, var_expr

  ilen=LEN_TRIM(var_expr)
  if (ilen<=100) then
     WRITE(iunit,450) if_expr, var_expr(1:ilen), post_expr
  else
      call checkunderscore(var_expr)
      WRITE(iunit,510) TRIM(if_expr)
! .AND. LEN_TRIM(var_expr(101:200)) > 0
      WRITE (iunit,520) var_expr(1:100)
      IF (ilen>100) WRITE(iunit,520) var_expr(101:200)        !CB TODO need do loop instead of mega-if
      IF (ilen>200) WRITE(iunit,520) var_expr(201:300)
      IF (ilen>300) WRITE(iunit,520) var_expr(301:400)
      IF (ilen>400) WRITE(iunit,520) var_expr(401:500)
      IF (ilen>500) WRITE(iunit,520) var_expr(501:600)
      IF (ilen>600) WRITE(iunit,520) var_expr(601:700)
      IF (ilen>700) WRITE(iunit,520) var_expr(701:800)
      IF (ilen>800) WRITE(iunit,520) var_expr(801:900)
      IF (ilen>900) WRITE(iunit,520) var_expr(901:1000)
      IF (ilen>1000) WRITE(iunit,520) var_expr(1001:1100)        
      IF (ilen>1100) WRITE(iunit,520) var_expr(1101:1200)
      IF (ilen>1200) WRITE(iunit,520) var_expr(1201:1300)
      IF (ilen>1300) WRITE(iunit,520) var_expr(1301:1400)
      IF (ilen>1400) WRITE(iunit,520) var_expr(1401:1500)
      IF (ilen>1500) WRITE(iunit,520) var_expr(1501:1600)
      IF (ilen>1600) WRITE(iunit,520) var_expr(1601:1700)
      IF (ilen>1700) WRITE(iunit,520) var_expr(1701:1800)
      IF (ilen>1800) WRITE(iunit,520) var_expr(1801:1900)
      IF (ilen>1900) WRITE(iunit,520) var_expr(1901:2000)
      IF (ilen>2000) WRITE(iunit,520) var_expr(2001:2100)
      IF (ilen>2100) WRITE(iunit,520) var_expr(2101:2200)
      IF (ilen>2200) WRITE(iunit,520) var_expr(2201:2300)
      IF (ilen>2300) WRITE(iunit,520) var_expr(2301:2400)
      IF (ilen>2400) WRITE(iunit,520) var_expr(2401:ilen)
      WRITE( iunit,460) TRIM(post_expr)

  end if

  450 FORMAT(3a)
  460 FORMAT(a)
  510 FORMAT(a,'&')
  520 FORMAT('&',a,'&')
  END subroutine writetrimmedIF


  subroutine writeAddObjective( iunit, Priority, nwt)
  INTEGER :: nwt, iunit
  CHARACTER (LEN=2) :: Priority
  WRITE(iunit, 100) Priority, nwt
  100 FORMAT('CALL ADD_OBJECTIVE (Weight,ObjVar_Name, "',a2,'",',i4,')')
  end subroutine


  subroutine writeAddConstraint(iunit, ndvar)
  INTEGER :: iunit, ndvar
  WRITE( iunit,100) ndvar
  100  FORMAT('CALL ADD_CONSTRAINT (Control_Tag,C,Dvar_Name,', &
                  'Surpl_Label,Slack_Label,Pen_Surpl,Pen_Slack,', &
                  'RHS, goal_count,',I3,')')
  end subroutine

  subroutine writeDefConTag( iu, var_num, tag)
  implicit none
  CHARACTER(LEN=*), INTENT(IN) :: tag
  INTEGER, INTENT(IN) :: iu, var_num
150 FORMAT ('Def_Condition_Tag(',i5,')="',a,'"')
  WRITE( iu,150) var_num, TRIM( tag)
  end subroutine

