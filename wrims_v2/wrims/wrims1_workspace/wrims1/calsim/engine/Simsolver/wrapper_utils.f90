!     Last change:  ED   14 Dec 2006   10:13 am
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


module wrapper_utils
integer			:: p=0
contains
    


  ! write dss path based on /Apart///InitDate/Epart/Fpart/
  function getpath (Apart,day, InitDate,Epart,Fpart) RESULT (path) !*****************
    CHARACTER(LEN=*),INTENT(IN) :: Apart,InitDate,Epart,Fpart
    CHARACTER(LEN=80)           :: path
    CHARACTER(LEN=9)            :: tempdate
    INTEGER                     :: i,ilen, day !********************

    tempdate=TRIM(InitDate)
    
    !**********remove the following lines*********************
    !IF ( ICHAR(tempdate(1:1)) > ICHAR('9')) THEN
    !   IF (chareq(tempdate(1:3),'FEB').or.chareq(tempdate(1:3),'feb')) THEN
    !      tempdate(1:2)='28'
    !   ELSE
    !      tempdate(1:2)='30'
    !   END IF
    !   tempdate(3:)=TRIM(InitDate)
    !END IF

    !***********append the actual start day to the date**************
    tempdate(3:) = tempdate
    WRITE (tempdate(1:2), fmt = '(i2)') day
    !****************************************************************
    path=''
    path(1:1)='/'
    ilen=LEN_TRIM(Apart)
    path(2:ilen+1)=TRIM(Apart)
    path(ilen+2:ilen+4)='///'
    i=ilen+5
    path(i:i+8)=TRIM(tempdate)
    i=i+9
    path(i:i)='/'
    i=i+1
    ilen=LEN_TRIM(Epart)
    path(i:i+ilen-1)=TRIM(Epart)
    i=i+ilen
    path(i:i)='/'
    i=i+1
    ilen=LEN_TRIM(Fpart)
    path(i:i+ilen-1)=TRIM(Fpart)
    path(i+ilen:i+ilen)='/'
  end function getpath

  function upper_case (old) RESULT (new)
    CHARACTER(LEN=80)       :: new
    CHARACTER(*),INTENT(IN):: old
    new=old
    do i=1,LEN_TRIM(old),1
       if (LGE(old(i:i),'a').and.LLE(old(i:i),'z')) then
          new(i:i) = ACHAR (IACHAR(old(i:i))-32)
       end if
    end do
  end function upper_case


  SUBROUTINE XA_ERRORHANDLER(modsts,solsts,date,icycle,Number_of_cycles,studyType,errorLog)
    include 'dwmy_type_definition.inc'
    INTEGER,INTENT(IN) :: modsts,solsts,icycle
    CHARACTER(LEN=200) :: errorLog
    CHARACTER(LEN=60)  :: msg
    CHARACTER(LEN=6)   :: studyType
    TYPE(dwmy), INTENT(INOUT)       :: date
300 FORMAT(a29,i2,'/', i2,'/',i4,' Cycle ',i5,' of ',i5)
    IF (modsts==3) THEN
       WRITE(msg,300)'UNBOUNDED SOLUTION  on date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (modsts==4) THEN
       WRITE(msg,300)'INFEASIBLE SOLUTION on date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (modsts==10) THEN
       WRITE(msg,300)'INTEGER INFEASIBLE on date:  ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (modsts==13) THEN
       WRITE(msg,300)'MORE MEMORY REQUIRED   date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (solsts==2) THEN
       WRITE(msg,300)'XA: ITERATION INTERUPT date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (solsts==3) THEN
       WRITE(msg,300)'XA: RESOURCE INTERUPT date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (solsts==4) THEN
       WRITE(msg,300)'XA: TERMINATED BY USER date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (solsts==8) THEN
       WRITE(msg,300)'XA: NODE TABLE OVERFLOW date:',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE IF (solsts==10) THEN
       WRITE(msg,300)'XA: SOLVER FAILURE on  date: ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    ELSE
       WRITE(msg,300)'ERROR OCCURRED. CHECK XA.LOG ',date%month,date%day,date%wateryear,icycle,Number_of_cycles
    END IF
    if(studyType == 'MULTI') then
       OPEN(99,FILE=errorLog)
       WRITE(99,'(A60)')msg
       CLOSE(99)
    else		! if SINGLE or nonexistent, do default Fortran msg'ing. - ED 12/14/2006
      call StopWithFinal(msg)    
    end if
  END SUBROUTINE XA_ERRORHANDLER


  SUBROUTINE ERRORHANDLER(msg)
    CHARACTER(LEN=*)  :: msg
    CALL StopWithError(msg)
  END SUBROUTINE ERRORHANDLER


  ! this subroutine is modified from datymd.for from heclib
  ! returns wateryear month number and wateryear given character string CALENDAR YEAR
  SUBROUTINE getMonthYear (date,imonth,iyear,ierror)
    IMPLICIT NONE
    CHARACTER(LEN=7),INTENT(IN)    :: date
    INTEGER,INTENT(OUT)            :: imonth,iyear,ierror
    INTEGER                        :: I,J,K
    CHARACTER(LEN=3)               :: CMON(12), given_month

    DATA CMON( 4) /'JAN'/
    DATA CMON( 5) /'FEB'/
    DATA CMON( 6) /'MAR'/
    DATA CMON( 7) /'APR'/
    DATA CMON( 8) /'MAY'/
    DATA CMON( 9) /'JUN'/
    DATA CMON(10) /'JUL'/
    DATA CMON(11) /'AUG'/
    DATA CMON(12) /'SEP'/
    DATA CMON( 1) /'OCT'/
    DATA CMON( 2) /'NOV'/
    DATA CMON( 3) /'DEC'/

    given_month=date(1:3)
    ! convert to uppercase
    do i=1,LEN(given_month)
       if (LGE(given_month(i:i),'a').and.LLE(given_month(i:i),'z')) then
          given_month(i:i) = ACHAR (IACHAR(given_month(i:i))-32)
       end if
    end do


    read(date(4:7),'(i4)', ERR=99) iyear
    DO J=1,12
       K = INDEX (given_month(1:3),CMON(J))
       IF (K.GT.0) THEN
          imonth = J
          GO TO 100
       ENDIF
    END DO
99  ierror=-1
    return
100 CONTINUE
    IF (imonth<4) THEN
       iyear = iyear+1
    END IF
    ierror=0
  END SUBROUTINE getMonthYear



  !   This function gets the problem as specified in the RCC derived type.  No multiobjective programming.
  FUNCTION RCC_SETUP(problem) RESULT(items)
    USE RCC_CACHE
    IMPLICIT NONE

    TYPE(rcc),DIMENSION(:),INTENT(OUT)  :: problem
    INTEGER                             :: items

    !  get all contraints and objective function
    call rcc_cache_getall( problem)
    items = rcc_cache_size()
  END FUNCTION RCC_SETUP

  !   This function sets up the problem for Sequential Multiobjective Programming.
  !   RCC is modified to include only those goals and objectives of the current
  !   priority level.  Higher priority OBJs are bounded to their solution values.
  FUNCTION MOP_SEQ_SETUP(PriorityLevel,PrevOBJvalue,problem) RESULT(problem_size)

    USE RCC_CACHE

    IMPLICIT NONE

    TYPE(rcc),DIMENSION(:),INTENT(INOUT)    :: problem
    REAL(8),DIMENSION(9),       INTENT(IN)  :: PrevOBJvalue
    INTEGER,                    INTENT(IN)  :: PriorityLevel
    INTEGER                                 :: i,j,PrNumber,problem_size
    CHARACTER(LEN=4)                        :: PrevOBJid

    ! get all contraints and objective function from cache
    problem_size = rcc_setup( problem)

    ! need to re-initialize so that previous OBJ bounding is cleared
    do i=1,SIZE(problem)
       problem(i)%rowname=''
       problem(i)%colname=''
       problem(i)%coef=0.0
    end do

    ! get all RCC data from cache and put into variable
    call rcc_cache_getall( problem)

    ! read thru Rcc and 'delete' constraints and objectives of lower priority
    ! include OBJ of current priority in problem and include any non-OBJ, non-numeric rows
    DO j=1,problem_size
       IF (ICHAR(problem(j)%rowname(1:1)) <= ICHAR('9').and. &
            charneq(problem(j)%rowname(2:10),'Objective')) THEN ! numeric rowname & not objective definition
!CB **** NEED TO CHANGE IN FUTURE FOR > 10 CYCLES ***********
          READ (problem(j)%rowname(1:1),FMT='(i1)') PrNumber
          IF (PrNumber /= 0 .and. PrNumber /= PriorityLevel) THEN
             !WRITE(12,*) 'deleting row -- different priority: ', problem(j)%rowname
             problem(j)%colname='DELETE'
          END IF
       ELSE IF (chareq(problem(j)%colname(1:3),'OBJ')) THEN ! OBJ column name
!CB **** NEED TO CHANGE IN FUTURE FOR > 10 CYCLES ***********       
          READ (problem(j)%colname(4:4),FMT='(i1)') PrNumber
          IF (chareq(problem(j)%rowname(1:3),'OBJ')) THEN 	! OBJ row name
             IF (PrNumber /= PriorityLevel .and. PrNumber /= 0) THEN
              	!WRITE(12,*) 'removing Obj from Objective Function: ', problem(j)%colname
              	problem(j)%coef=0.0
             END IF
          END IF
       END IF
    END DO

    ! bound higher priority OBJ variables (OBJ1,OBJ2,..) to their solution values
    IF (PriorityLevel > 1) THEN
       DO i=1,PriorityLevel-1
          problem_size = problem_size + 1
!CB **** NEED TO CHANGE IN FUTURE FOR > 10 CYCLES ***********          
          WRITE (PrevOBJid,FMT='("OBJ", i1)') i
          problem(problem_size)%rowname='MIN'
          problem(problem_size)%colname=PrevOBJid
          problem(problem_size)%coef=PrevOBJvalue(i)
       END DO
    END IF

  END FUNCTION MOP_SEQ_SETUP


  !   This function sets up the problem for weighted multiobjective programming.
  !   The problem is modified to weight the objective functions of different
  !   priorities as specified by the user
  FUNCTION MOP_WGT_SETUP(bigMlevel,bigM,problem) RESULT( problem_size)
    USE rcc_cache
    IMPLICIT NONE

    TYPE(rcc),DIMENSION(:),INTENT(INOUT):: problem
    INTEGER,DIMENSION(9),INTENT(IN)  :: bigMlevel
    REAL(8),DIMENSION(9),INTENT(IN)  :: bigM
    INTEGER                          :: i, problem_size
    CHARACTER(LEN=4)                 :: OBJid

    ! get all contraints and objective function from cache
    problem_size = rcc_setup( problem)

    !  add the weighting on each priority level
    DO i=1,9
       IF (bigMlevel(i)==0) exit
       WRITE (OBJid, FMT='("OBJ", i1)') bigMlevel(i)
       !CCB **** NEED TO CHANGE IN FUTURE FOR > 10 CYCLES ***********
       problem_size = problem_size + 1
       problem(problem_size)%rowname='*OBJ'
       problem(problem_size)%colname=OBJid
       problem(problem_size)%coef=bigM(i)
    END DO
  END FUNCTION MOP_WGT_SETUP

  !   Delete constraints not within scope of current cycle
  FUNCTION CYCLE_CON_SETUP(cyclenum,problem) RESULT(items)

    USE RCC_CACHE
    IMPLICIT NONE
    TYPE(rcc),DIMENSION(:),INTENT(INOUT)    :: problem
    TYPE(rcc)								:: xproblem
    INTEGER,                    INTENT(IN)  :: cyclenum
    INTEGER                                 :: items,i,i0,i9,r1,c1,icycle,j
    !CCB added next line for 10 or more cycles
    INTEGER                                 :: r2, c2, cycleNumber, cycleLeftDigit, cycleRightDigit    
!CB don't need anymore   CHARACTER(LEN=1)                        :: cyclechar
    LOGICAL                                 :: withinScope

    ! get all contraints and objective function from cache
    call rcc_cache_getall(problem)
    items=rcc_cache_size()

!CB don't need anymore    WRITE (cyclechar, FMT='(i1)') cyclenum
    i0 = ICHAR('0')
    i9 = ICHAR('9')
!CB don't need anymore    icycle = ICHAR(cyclechar)
     cycleNumber = cyclenum
    
    j=1
    do i=1,items    
       xproblem=problem(i)
       
       !CCB added block for debugging 10 or more cycles work
       icycle = -1
       withinScope = .false.

       !CCB replaced the block below with the block farther below to allow 10 cycles or more
!CB       r1 = ICHAR(xproblem%rowname(1:1))
!CB        c1 = ICHAR(xproblem%colname(4:4))

!CB        !CCB added for debugging 10 or more cycles work       
!CB        write (31, 102) r1, c1
       
!CB        IF (r1 == icycle .or. r1 ==i0) THEN
!CB        IF (icycle == cycleNumber .or. icycle == 0) THEN
!CB           problem(j) = xproblem
!CB           j = j + 1
          
!CB        ELSE IF (r1 < i0 .OR. r1 > i9) THEN
!CB        ELSE IF (secondChar < 0 .or. secondChar > 9) THEN
!CB           IF ( charneq(xproblem%colname(1:3),'OBJ') ) THEN
!CB              problem(j) = xproblem
!CB              j = j + 1
!CB           ELSE IF ( chareq(xproblem%colname(1:3),'OBJ') .AND. (c1==icycle .OR. c1==i0) ) THEN
!CB              problem(j) = xproblem
!CB              j = j + 1
!CB           END IF
!CB        END IF

      r1 = ICHAR(xproblem%rowname(1:1))
      r2 = ICHAR(xproblem%rowname(2:2)) !CCB added this line for 2nd digit
    	
      if (r1 >=i0 .and. r1<=i9) then
        cycleRightDigit = r2 - i0
        cycleLeftDigit = r1 - i0
        icycle = 10 * cycleLeftDigit + cycleRightDigit
        if (icycle == cyclenum .or. icycle == 0) withinScope = .true.
      else if (charneq(xproblem%colname(1:3),'OBJ')) then
        withinScope = .true.
      else
        c1 = ICHAR(xproblem%colname(4:4))
        c2 = ICHAR(xproblem%colname(5:5))
        cycleRightDigit = c2 - i0
        cycleLeftDigit = c1 - i0
        icycle = 10 * cycleLeftDigit + cycleRightDigit             
        if (icycle == cycleNumber .or. icycle == 0) withinScope = .true.
      end if
       
      if (withinScope) then
        problem(j) = xproblem
        j = j + 1
      end if
    END DO      
    items=j-1
  END FUNCTION CYCLE_CON_SETUP

  !  Function to retrieve the solution value of a variable given the variable name
  FUNCTION getSolutionValue(variableName) RESULT(value)
    USE XA_INTERFACE
    USE XATYPES

    CHARACTER(LEN=*),INTENT(IN) :: variableName
    REAL(8)                     :: value
    INTEGER(2)                  :: frc
    INTEGER(4)                  :: return_code
    CHARACTER(LEN=80)           :: message

    CALL XFACTC(frc,return_code,variableName,value,colsize)
    IF(frc/=0) THEN
       WRITE(message,FMT='("XFACTC had problem getting solution for: ",a16)') variableName
       call StopWithError(message)
    END IF

  END FUNCTION getSolutionValue


  !  Writes out the solution, to both DSS and a log file. CacheType is either dvar or dvarCycle cache
  subroutine save_solution( solution, lp_unit, day, month, year, output_option, cacheType, debug) !***********
    USE wrangler, ONLY: dss_save_dvar
    USE xatypes, ONLY: answer
    implicit none
    TYPE(answer),DIMENSION(:) :: solution, output
    ALLOCATABLE               :: output
    INTEGER                   :: lp_unit, day, month, year, k, nbr_wresl_dvars, output_option !*********
    INTENT(IN)                :: day, month, year, lp_unit, solution, output_option !***************
    INTEGER,INTENT(IN)        :: cacheType !1=dvarTable 2=dvarCycleTable
    LOGICAL                   :: debug

    if (output_option/=0) WRITE( lp_unit,110) month, day, year !************************
    nbr_wresl_dvars=0
    ALLOCATE(output(SIZE(solution)))

    ! Separate Wresl decision variables from internal decision variables
    DO k=1,SIZE(solution)
       IF (chareq(solution(k)%id,'OBJ').and.k/=1) THEN
          EXIT
       ELSE IF (chareq(solution(k)%id(1:3),'OBJ').OR.chareq(solution(k)%id(1:5),'SLACK').OR.&
            chareq(solution(k)%id(1:5),'SURPL').OR.chareq(solution(k)%id(1:1),'').OR.&
            (ICHAR(solution(k)%id(1:1)) <= ICHAR('9')) ) THEN
          ! write OBJ and SLACK/SURPLUS variables to separate file
          IF(output_option/=0) WRITE (lp_unit,100) solution(k)%id,solution(k)%value
       ELSE         ! get decision variables for dss - ignore OBJ and SLACK/SURPLUS
          nbr_wresl_dvars = nbr_wresl_dvars+1
          output(nbr_wresl_dvars) = solution( k)
       END IF
    END DO
!    call FLUSH( lp_unit)
100 FORMAT(a16,f16.5,2x,a6)
110 FORMAT('>> WY date ',I2,'/', I2, '/', I4) !************************

    ! save decision variables in dss files
    CALL dss_save_dvar(output(1:nbr_wresl_dvars)%id, output(1:nbr_wresl_dvars)%value, cacheType)
    DEALLOCATE(output)
  end subroutine save_solution


  subroutine note_date(wyorcy, day, month,year) !***********************
    USE xasolver, ONLY: solver_message
    IMPLICIT NONE
    CHARACTER(LEN=50)           :: message
    INTEGER,INTENT(IN)          :: day, month,year,wyorcy !**********************
    CHARACTER(LEN=8),DIMENSION(2):: yr_types = (/'water   ','calendar'/)

100 format ('>> Solving at date ',i2,'/',i2,', of ',a,' year ',i4) !***********************
    WRITE(message,100) month, day, TRIM(yr_types(wyorcy)), year !************************
    call solver_message( message)
  end subroutine note_date


  ! This will have to be modified for intervals besides monthly
  !***************THIS HAS BEEN MODIFIED FOR DAILY TIME STEPS**************
  subroutine increment_date( date, interval)
        include 'dwmy_type_definition.inc'
        TYPE(dwmy), INTENT(INOUT)    :: date
        CHARACTER(LEN=*), INTENT(IN) :: interval
    
        if (interval(1:4) == '1MON') then
           date%month = date%month + 1
           if (date%month>12) then
              date%month = 1
              date%wateryear = date%wateryear + 1
           end if
           date%day = days_in_month( date)
           
        !***********THIS PORTION WAS ADDED**************
        else if (interval(1:4) == '1DAY') then
        	date%day = date%day + 1
        	if (date%day > days_in_month(date)) then
        		date%day = 1
        		date%month = date%month + 1
        		if (date%month > 12) then
        			date%month = 1
        			date%wateryear = date%wateryear + 1
        		end if
        	end if
        !****************************************************
        
        else if (interval(1:5) == '7DAYS') then
           ! undefined as of this version
           call ERRORHANDLER("Weekly time step not implemented yet")
        else
           call ERRORHANDLER("Unknown Time Step: " // interval)
        end if
  end subroutine increment_date



  subroutine set_solver_options( opt_flag)
    USE xasolver
    implicit none
    INTEGER,DIMENSION(40) :: opt_flag
    CHARACTER(LEN=256)    :: clp_options

    clp_options = 'MATLIST'
    IF     ( opt_flag(1) == 1) THEN
       clp_options(LEN_TRIM(clp_options)+2:) = 'var'
    ELSE IF( opt_flag(1) == 2) THEN
       clp_options(LEN_TRIM(clp_options)+2:) = 'con'
    ELSE IF( opt_flag(1) == 3) THEN
       clp_options(LEN_TRIM(clp_options)+2:) = 'both'
    ELSE
       clp_options(LEN_TRIM(clp_options)+2:) = 'none'
    END IF

    IF( opt_flag(3) == 1) clp_options(LEN_TRIM(clp_options)+2:) = 'set relaxed yes'
    IF( opt_flag(4) == 0) clp_options(LEN_TRIM(clp_options)+2:) = 'set winfriendly no'

    call solver_options( clp_options)
  end subroutine set_solver_options


  integer function days_in_month(date)
    include 'dwmy_type_definition.inc'
    type(dwmy), intent(in) :: date
    !  o  n  d  j  f  m  a  m  j  j  a  s
    INTEGER, dimension(12) :: daysin = (/31,30,31,31,28,31,30,31,30,31,31,30/)
    days_in_month = daysin( date%month)
    ! uncomment lines below if you want leap years counting
    if( mod( date%wateryear, 100) /= 0 .or. mod( date%wateryear, 400) == 0) then
    	if ( date%month == 5 .and. mod(date%wateryear,4) == 0)  days_in_month = 29
   	end if
  end function days_in_month


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


  ! this subroutine is system-independent.  Modify the Mesenger module when
  ! porting to another OS.
  SUBROUTINE StopWithError(msg)
    USE Messenger
    IMPLICIT NONE
    CHARACTER (LEN=*) :: msg
    call WindowMessage(3, 'WRIMS Error', msg)
    CALL EXIT(1)
  END SUBROUTINE StopWithError


  ! this subroutine is system-independent.  Modify the Mesenger module when
  ! porting to another OS.
  SUBROUTINE StopWithFinal(msg)
    USE Messenger
    IMPLICIT NONE
    CHARACTER (LEN=*) :: msg
    CALL WindowMessage(4,'WRIMS Completed', msg)
  END SUBROUTINE StopWithFinal
  
  !**************INSERT NEW SUBROUTINE TO INITIALIZE THE FLUSH INTERVAL************************
  
  SUBROUTINE set_flush_interval(Epart, flush_interval, cache_back, cache_forward)
    	CHARACTER(LEN=*), INTENT(IN)	:: Epart
    	INTEGER, INTENT(INOUT)		::flush_interval, cache_back, cache_forward
    	
    	IF (chareq(Epart, '1DAY')) THEN
    		flush_interval = 365
    		cache_back = 366   !changed from 180 to 366 by Z.Q. Richard Chen
    		cache_forward = flush_interval + 35
    	ELSE IF (chareq(Epart, '1MON')) THEN
    		flush_interval = 120	 !CB TODO: THIS SHOULD BE SMALLER IF THE STUDY IS LESS THAN TEN YEARS IN LENGTH
    		cache_back = 24
    		cache_forward = flush_interval + 24 	
    	ELSE
    		CALL ERRORHANDLER("Unknown Time Step: " // Epart)
    	END IF
  END SUBROUTINE set_flush_interval
  
  !***********************************************************************************************

END module wrapper_utils
