!     Last change:  R    25 May 2001    3:52 pm
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


module code_utils
  interface
     SUBROUTINE ADD_OBJECTIVE (Weight,ObjVar_Name,Priority,nwt)
       IMPLICIT NONE
       REAL,             INTENT(IN) :: Weight
       CHARACTER(LEN=*), INTENT(IN) :: ObjVar_Name
       CHARACTER(LEN=2), INTENT(IN) :: Priority
       INTEGER,          INTENT(IN) :: nwt
     END subroutine ADD_OBJECTIVE

     SUBROUTINE ADD_CONSTRAINT (Control_Tag,C,Dvar_Name,&
          Surpl_Label,Slack_Label,Pen_Surpl,Pen_Slack,&
          RHS,goal_count,ndvar)
       IMPLICIT NONE
       REAL,             DIMENSION(*), INTENT(IN) :: C
       REAL,                           INTENT(IN) :: Pen_Surpl, Pen_Slack, RHS
       CHARACTER(LEN=*), DIMENSION(*), INTENT(IN) :: Dvar_Name
       CHARACTER(LEN=*),               INTENT(IN) :: Control_Tag
       CHARACTER(LEN=*),               INTENT(IN) :: Surpl_label, Slack_label
       INTEGER,                        INTENT(IN) :: goal_count,ndvar

     END subroutine ADD_CONSTRAINT

     SUBROUTINE ADD_BOUND (Bound_Var_Name,Up_Bound,Lo_Bound)
       IMPLICIT NONE
       REAL,             INTENT(IN) :: Lo_Bound, Up_Bound
       CHARACTER(LEN=*), INTENT(IN) :: Bound_Var_Name
     END subroutine ADD_BOUND

     SUBROUTINE MAKE_INTEGER (Bound_Var_Name,Priority)
       IMPLICIT NONE
       CHARACTER(LEN=*), INTENT(IN) :: Bound_Var_Name
       INTEGER,          INTENT(IN) :: Priority
     END subroutine MAKE_INTEGER

     subroutine wrapper (date, code, dss_utils, reportsv, directory)
       INCLUDE 'dwmy_type_definition.inc'
       type (dwmy) :: date
       CHARACTER(LEN=*),INTENT(IN) :: directory
       external code, dss_utils, reportsv
     end subroutine wrapper

     ! Function to retrieve input data from a DSS time-series database (in Wresl)
     function dssin( pos, deltaT)
       REAL                         :: dssin
       INTEGER, INTENT(IN)          :: deltaT, pos
     end function dssin

     ! Function to retrieve previous values of decision variables (in Wresl)
     function dssdvar( pos, deltaT)
       REAL                         :: dssdvar
       INTEGER, INTENT(IN)          :: deltaT, pos
     end function dssdvar

     ! Function to retrieve previous CYCLE values of decision variables (in Wresl)
     function dsscycle( pos, deltaCycle)
       REAL                         :: dsscycle
       INTEGER, INTENT(IN)          :: deltaCycle, pos
     end function dsscycle

     real function tableBasic( tablename, answer, wc, wv)
       implicit none
       CHARACTER(LEN=*), INTENT(IN) :: tablename, answer, wc(:)
       real, INTENT(IN)             :: wv(:)
     end function tableBasic

     real function tableComplex( tablename, answer, gc, gv, um, wc, wv)
       implicit none
       CHARACTER(LEN=*), INTENT(IN)  :: tablename, answer, gc, wc(:)
       REAL, INTENT(IN)              :: gv, wv(:)
       INTEGER, INTENT(IN)           :: um
     end function tableComplex

     real function tableLookup( tablename, answer, gc, gv, um)
       implicit none
       CHARACTER(LEN=*), INTENT(IN)  :: tablename, answer, gc
       REAL, INTENT(IN)              :: gv
       INTEGER, INTENT(IN)           :: um
     end function tableLookup

     subroutine setTableDirectory( directory, commondir)
       CHARACTER(LEN=*), INTENT(IN)  :: directory, commondir
     end subroutine setTableDirectory

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

     ! Writes to the state variable log file
     SUBROUTINE WRITELOG( c1, c2, r)
       CHARACTER(LEN=32), INTENT(IN) :: c1,c2
       REAL, INTENT(IN) :: r
     END SUBROUTINE WRITELOG

     ! reads from  the state variable log file
     SUBROUTINE readlog(filename, var_names, var_values)
       CHARACTER(LEN=32), INTENT(IN) :: filename
       CHARACTER(LEN=32), dimension(:) , INTENT(INOUT) :: var_names
       real, dimension(:), intent(inout) :: var_values
     END SUBROUTINE readlog
     ! loops to get the values from the variable names 
     subroutine getvarvalue(var,names,values,val)
       character(len=32), intent(in) :: var
       character(len=32), dimension(:), intent(in) :: names
       real, dimension(:), intent(in) :: values
       real, intent(out) :: val
     end subroutine getvarvalue
  END interface

  ! From lp_utils module
  dll_import add_constraint, add_objective, add_bound, make_integer
  dll_import wrapper
  dll_import writelog,readlog,getvarvalue

  ! From wrangler module
  DLL_IMPORT DSSDVAR, DSSIN, DSSCYCLE
  DLL_IMPORT tableBasic, tableLookup, tableComplex,setTableDirectory
  DLL_IMPORT DSS_INIT_TABLES, DSS_DVAR_INIT, DSS_TS_INIT
  
  
end module code_utils
