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


!     Interface (API) to HEC-DSS and RDBMS functionality for WRIMS
!     D. McFadden (March, 1998)

module wrangler
  interface

     !----------------------- Called by DSS_INIT (via Wresl) -----------------------------

     ! Subroutine to inform us of the size of the cache tables
     ! THIS MUST BE CALLED FIRST!
     subroutine dss_init_tables(dvarSize, parmSize)
       INTEGER, INTENT(IN) :: dvarSize, parmSize
     end subroutine dss_init_tables

     ! Subroutine to initialize storage for a decision variable
     ! THIS IS CALLED ONCE FOR EVERY DECISION VARIABLE THAT IS DEFINED
     subroutine dss_dvar_init(pos, var_name, var_kind, var_units, desc, header)
       CHARACTER(LEN=*), INTENT(IN) :: var_name, var_kind, var_units, desc, header
       INTEGER, INTENT(IN) :: pos
       OPTIONAL :: desc, header
     end subroutine dss_dvar_init

     ! Subroutine to initialize storage for a time-series parameter variable
     ! THIS IS CALLED ONCE FOR EVERY TIMESERIES DEFINITION THAT IS DEFINED
     subroutine dss_ts_init(pos, var_name, var_kind, var_units, desc)
       CHARACTER(LEN=*), INTENT(IN) :: var_name, var_kind, var_units, desc
       INTEGER, INTENT(IN) :: pos
       OPTIONAL :: desc
     end subroutine dss_ts_init


     !---------------------- Subs used by Wrapper -----------------------------------------
     ! set the f part for the init file
     subroutine dss_set_init_fpart(fpart)
       character(len=*), intent(in) :: fpart
     end subroutine dss_set_init_fpart


     ! Initialize this module, and open files
     ! dvar_f is the DSS filename to use to store/retrieve decision variables
     ! parm_f is the DSS filename to use to retrieve parameter (timeseries) data
     ! bpath the the base pathname (the a,d,e, and f parts) to use.  Other parts of the path
     !   name must be null.  For example:  /CALSIM-V122///01OCT1997/1MONTH/STUDY/
     ! The simulation will start at the date specified as the D-part of this pathname, and
     ! the model time step will be what you said in the E-part.
     subroutine dss_open(initvar_f,dvar_f, parm_f, bpath, debugging_wanted)
       CHARACTER(LEN=*),INTENT(IN)    :: initvar_f,dvar_f, parm_f
       CHARACTER(LEN=*),INTENT(IN)    :: bpath   ! the a,e, and f parts of the DSS pathname
       LOGICAL, INTENT(IN)            :: debugging_wanted
     END subroutine dss_open


     subroutine dss_clear_dvars( start_date, nper)
       INTEGER, INTENT(IN) :: nper
       CHARACTER(LEN=20) :: start_date
     END subroutine dss_clear_dvars


     ! Write out all buffered data to disk, then close files (all done).
     subroutine dss_close
     end subroutine dss_close

     ! Informs DSS as to how many periods of
     !   lookahead to read in to cache (for the ts parameter data), including the current one,
     !   and how many periods to read in to cache for previous values of dvars,
     !   excluding the current one.
     ! Subroutine will write out any new period values of decision varibles that have been sent
     !   since the previous call to this subroutine or since dss_open was called.
     ! Example:  call dss_read_flush(1, 12)                !  after a year.  Uses internal date.
     subroutine dss_read_flush(nper_back, nper_fore, keep_log)
       INTEGER, INTENT(in) :: nper_back,nper_fore
       LOGICAL, INTENT(IN) :: keep_log
     end subroutine dss_read_flush

     ! Initializes the CYCLE cache. Allocates storage and reads in initial data.
     ! Calls to this subroutine are usuall cycle_read_flush(0,number_of_cycles,logical)
     subroutine cycle_read_flush(nper_back, nper_fore, keep_log)
       INTEGER, INTENT(in) :: nper_back,nper_fore
       LOGICAL, INTENT(IN) :: keep_log
     end subroutine cycle_read_flush

     ! Tells DSS to increment the simulation date.  Uses the E-part of the pathname
     ! specified in the DSS_OPEN call to figure out how much to increment.
     subroutine dss_inc_date
     end subroutine dss_inc_date


     ! Tells dvarCycleTable to increment by one cycle.  Uses date as increment but
     ! cache access only looks back in relative terms.
     subroutine dss_inc_cycle
     end subroutine dss_inc_cycle

     ! Writes out data for one decision variable, for the current date.
     ! Doesn't actually write to disk; that is done by dss_read_flush.
     ! This just takes the LP solution and adds it to the cache.
     subroutine dss_save_dvar(dvar_id, dvar_values, cacheType)
       REAL(8),             INTENT(IN)  :: dvar_values(:)
       CHARACTER(LEN=*), INTENT(IN)  :: dvar_id(:)
       INTEGER, INTENT(IN)             :: cacheType
     end subroutine dss_save_dvar

  end interface

  DLL_IMPORT DSS_INIT_TABLES, DSS_DVAR_INIT, DSS_TS_INIT
  DLL_IMPORT DSS_OPEN, DSS_CLEAR_DVARS, DSS_CLOSE, dss_set_init_fpart
  DLL_IMPORT DSS_READ_FLUSH, CYCLE_READ_FLUSH
  DLL_IMPORT DSS_INC_DATE, DSS_INC_CYCLE
  DLL_IMPORT DSS_SAVE_DVAR

END module wrangler
