!     Last change:  AJD    26 Sep 2001    1.34 pm
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



!     Last modified by: Andy Draper, California Department of Water Resources

!     LF90 demo RCC interface program for XA solver
!     by D. McFadden

module xasolver
    !INTEGER(4), PRIVATE :: return_code
    INTEGER, PRIVATE    :: errunit, solution_size
    LOGICAL, PRIVATE    :: debug
    CHARACTER(LEN=200)  :: outputDirectory
    CHARACTER(LEN=500)  :: tempOption
    include "xacommon.txt"

    contains

    ! This function must be called once, to initialize XA
    subroutine solver_init(debug_flag, error_unit_number)
        USE xa_interface
        implicit none
        INTENT(IN) :: debug_flag, error_unit_number
        LOGICAL :: debug_flag
        INTEGER :: error_unit_number, version
        INTEGER(2) :: frc
        CHARACTER(LEN=150) :: msg

    77  FORMAT(a)

        debug = debug_flag
        errunit = error_unit_number
        solution_size = 0
	
        ! allocate memory
        XAOSLSIZE=92
!CB        CALL xfinit(frc, return_code, 12000, 2000, -1_2)  !CB Ryan Wilbur's 35 cycle ops model crashed due to insufficient memory
!CB        CALL xfinit(frc, return_code, 36000, 2000, -1_2)  !CB tripled allowed memory
        CALL xfinit(frc, return_code, 100000, 2000, -1_2)  !CB increased allowed memory further for CalSim 3

        !ENTERED AT JIM BYER'S REQUEST*****************************
        if (frc == 1) then
        	if (return_code == 996 .or. return_code == 997) then
        		msg = "XA license requires activation"
        	else if (return_code == 998) then
        		msg = "XA dongle not plugged in or xa dll does not match the build lib file"
        	else
        		msg = "XA initialization error RC ="
        		write(msg(len_trim(msg) + 1:), fmt = "(i4)") return_code
        		msg(len_trim(msg) + 1:) = "."
        	end if
        	call fatal_error (msg)
        end if
        !*******************************************************

        ! set up XA's options
        CALL solver_options('MAXIMIZE yes MUTE yes FORCE no LISTINPUT no BASIS never SET VISIBLE no')
        tempOption='OUTPUT'
        tempOption(LEN_TRIM(tempOption)+2:)=TRIM(outputDirectory)
        tempOption(LEN_TRIM(tempOption)+1:)='\xa.log'
        call solver_options(tempOption)

        IF(debug)  CALL solver_options('SET XAOK yes SET DEBUG yes WARNING yes ')

        !*****DE added for solver version control
      	XAModelSize  = 272       ! Size (bytes) of XAMODEL structure
      	CALL XFMODEL(FRC , return_code , XAModelSize)
      	IF(FRC .NE. 0) then
		msg = "XFMODEL ERROR - Couldn't access XA solver version."
		call fatal_error(msg)
	end if

	msg = "Solver XA"
	version = int(XAMversion/100)
	write(msg(len_trim(msg) + 1:), fmt = "(i2)") version
	msg(len_trim(msg) + 1:) = "."
	version = mod(XAMversion,100)
	write(msg(len_trim(msg) + 1:), fmt = "(i2)") version
	call solver_message(msg)
	if (XAMversion < 1397) then
!		write (*,*) 'XAMversion = ', XAMversion
		msg(len_trim(msg) + 1:) = " is out of date.  Download XA13.97 at"
		msg(len_trim(msg) + 1:) = " http://www.sunsetsoft.com/outgoing/CalsimDownload.html."
		call fatal_error(msg)
	end if
    END subroutine solver_init


    subroutine solver_options( options)
        USE xa_interface
        implicit none
        CHARACTER(LEN=*), INTENT(IN) :: options
        INTEGER(2) :: frc
        call solver_message('CLP options: '//options)
        CALL xfclp( frc, return_code, TRIM(options)//'$')
        IF(frc/=0) CALL fatal_error('Solver_options call had problem'//CHAR(13)//options)
    end subroutine


    ! This subroutine sets up the problem (poses it) to XA
    subroutine solver_pose(problem)
        USE XATYPES
        USE xa_interface
        implicit none
        TYPE(rcc), DIMENSION(:), INTENT(IN)     :: problem
        INTEGER(2) :: frc
        CHARACTER(LEN=150) :: msg
        
        ! set up XA and load the problem
        CALL xfrcci(frc, return_code, &
                    problem%rowname, problem%colname, problem%coef, &
                    rowsize, colsize, SIZE(problem), &
                    0, 0, 0, 1_2)
!CB        IF(frc/=0) CALL fatal_error('XFRCCI was bogus')
        
        
	!CB ENTERED FOR MORE DETAILED MESSAGE*****************************
	if (frc /= 0) then
		IF (return_code == 301) THEN
			msg = "Not enough memory for XA.  Increase 3rd argument in XFCCI call"
			call fatal_error (msg)
		END IF
		msg = "Solver Problem Construction Error, with return_code = "
		write(msg(len_trim(msg) + 1:), fmt = "(i5)") return_code
		msg(len_trim(msg) + 1:) = "."
		call fatal_error (msg)
        end if
	!*******************************************************        
        
        solution_size = 0
    end subroutine


    ! This function solves the LP
    function solver(solution)
        USE XATYPES
        USE xa_interface
        implicit none
        INTEGER :: solver(2)
        TYPE(answer), DIMENSION(:), INTENT(OUT) :: solution
        INTEGER                           :: ind,begin_time,end_time
        INTEGER(2)                        :: frc, model_status, solution_status

        ! solve the problem
        CALL xfsolv(frc, return_code, model_status, solution_status)
        
        IF(frc/=0) CALL fatal_error('XFSOLV had error')
        solver  = (/model_status, solution_status/)
        
!        write(*,*) 'return_code after xfsolv = ', return_code    !CB debugging
!        write(*,*) 'model_status after xfsolv = ', model_status    !CB debugging
!        write(*,*) 'solution_status after xfsolv = ', solution_status    !CB debugging

        ! get the value of the objective function
        solution(1)%id='OBJ'
        CALL XFACTC(frc,return_code,solution(1)%id//'                             ',solution(1)%value,rowsize)
!CB        IF(frc/=0)
!CB        call fatal_error('XFACTC had problem getting OBJ')
	!CB altered for better error message
	IF(frc/=0) THEN
		!CB MISSING DONGLE IS POSSIBLE, MAYBE LIKELY
		write(*,*) 'rowsize from XFACTC = ', rowsize       !CB debugging
		write(*,*) 'return code from XFACTC = ', return_code    !CB debugging
		write(*,*) 'solution(1)%value from XFACTC = ', solution(1)%value    !CB debugging
		write(*,*) 'frc from XFACTC = ', frc    !CB debugging
        	call fatal_error('XFACTC had problem retrieving objective function value - is the dongle in?')
        END IF

        ! get the values for all columns
        do ind=2, SIZE(solution)
            CALL XFACT(frc, return_code, ind-2, solution(ind)%value)
            if (frc/=0)  EXIT  ! error or end of list
            CALL XFNAME(frc, return_code, ind-2, solution(ind)%id)
            if (frc/=0)  EXIT  ! error or end of list
        end do

        ! check for situation other than a normal end-of-list termination
        IF (return_code .gt. 10)  CALL fatal_error('XFACT or XFNAME had undesirable rc')

        ! sentinel for end of solution
        IF(ind<=SIZE(solution))  solution(ind)%id = ''
        solution_size = ind
    END function solver


    ! retrieves the actual size of the solution
    function solver_get_solution_size()
        INTEGER :: solver_get_solution_size
        solver_get_solution_size = solution_size
        END function


    ! Prints a message in the XA log file
    subroutine solver_message(a)
        USE xa_interface
        CHARACTER(LEN=*), intent(IN) :: a
        CHARACTER(LEN=256) message
        INTEGER(2) :: frc
        INTEGER :: length
        
        length = LEN_TRIM(a) 
        100 FORMAT('MESSAGE `',a,'$')
        IF (length > 0) then
        	WRITE(message,100) TRIM(a)
        END IF
        call xfclp(frc, return_code, message)
        frc=frc    ! disregard the return code
        end subroutine


    ! Prints a detailed solution report to the XA log file
    subroutine solver_report
        use xa_interface
    call xfrprt(frc, return_code)
    end subroutine


    ! This subroutine must be called once, at the end of the program, to close files and free memory
    subroutine solver_close
        USE xa_interface
        INTEGER(2) :: frc
        CALL XFDONE(frc, return_code)
        IF(frc/=0) call fatal_error('XFDONE call!')
        return
        END subroutine


    ! Used for fatal errors that require immediate termination
    subroutine fatal_error(msg)
        USE xa_interface
        PRIVATE :: fatal_error
        CHARACTER(LEN=*), INTENT(IN) :: msg
        INTEGER(2) :: frc
        call XFMSG(frc,-1_4)
        CLOSE(errunit)
        call stopWithError(msg)
        CALL XFDONE(frc, return_code)  ! attempt to stop XA
        frc=frc                        ! stops LF90 warning about unused variable
        call exit(1)
        10 format ('XA: Gotta stop: ',a,i6)
        end subroutine


    SUBROUTINE StopWithError(msg)
        USE Messenger
        IMPLICIT NONE
        CHARACTER (LEN=*) :: msg
        call WindowMessage(3, 'WRIMS XA Error', msg)
        END SUBROUTINE


    END module xasolver
