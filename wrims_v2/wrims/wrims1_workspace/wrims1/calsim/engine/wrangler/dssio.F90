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

module dssio
  USE dss_cache

  ! variables to hold caches and simulation state
  TYPE(VariableCache), SAVE  :: dvarTable, parmTable, dvarCycleTable
  logical, save:: first_call = .true.
  integer, save:: per_offset = 0
  logical, save:: init_sameas_dvar = .false.
  character(len=200), save :: init_fpart
  ! variables to hold the HEC DSS file buffers and pointers
  INTEGER, DIMENSION(600), SAVE :: dvar_ifltab, parm_ifltab, initvar_ifltab

  ! variable to hold the decision variable headers
  CHARACTER(LEN=header_len), DIMENSION(:), ALLOCATABLE, SAVE :: dvarHeaderTable

	contains

	! Utility function to compare two string lexigraphically
	function chareq (string,compstring) RESULT (trueorfalse)
		CHARACTER(LEN=*),INTENT(IN) :: string,compstring
		logical                     :: trueorfalse
		trueorfalse=LLE(string,compstring).and.LGE(string,compstring)
	end function chareq

end module dssio
