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


!     Written by:  Armin Munevar, California Department of Water Resources

!     These are the global variables available in all subroutines.
MODULE GLOBAL
IMPLICIT NONE

INCLUDE 'dwmy_type_definition.inc'

! Global integer variables for use in DO loop and SUM indexing
INTEGER :: i,j,k

!  Global variable to contain current date.
type(dwmy) :: date

!  Conversion between acre-feet and second-foot-days
real, parameter, private :: AF2SFD = 1.983471

contains

! Volume-Volume conversions
!==========================
    real function af_taf()      ! multiplier to convert from AF to TAF
        af_taf = 0.001
    end function

    real function taf_af()      ! multiplier to convert from TAF to AF
		taf_af = 1000.
    end function
! --------------
    real function cf_taf()     ! multiplier to convert from CF to TAF
		cf_taf = 2.29568411E-8
    end function

    real function taf_cf()     ! multiplier to convert from TAF to CF
        taf_cf = 43560000.
    end function
! --------------
    real function ckm_taf()     ! multiplier to convert from CKM(cubic kilometers) to TAF
        ckm_taf = 810.713
    end function

    real function taf_ckm()     ! multiplier to convert from TAF to CKM
		taf_ckm = 1./810.713
    end function
! --------------
    real function cdm_taf()     ! multiplier to convert from CDM(cubic dekameters) to TAF
        cdm_taf = 8.10713E-4
    end function

    real function taf_cdm()     ! multiplier to convert from TAF to CDM
		taf_cdm = 1./8.10713E-4
    end function
! --------------
    real function cm_taf()     ! multiplier to convert from CM(cubic meters) to TAF
        cm_taf = 8.10713E-7
    end function

    real function taf_cm()     ! multiplier to convert from TAF to CM
		taf_cm = 1./8.10713E-7
    end function
! --------------

! Volume-Flow conversions
!============================
    real function taf_cfs(offset)       ! multiplier to convert from TAF/deltaT_days to CFS
        INTEGER, OPTIONAL, INTENT(IN) :: offset
        INTEGER            :: month, wy
        INTEGER, dimension(12) :: daysin = (/31,30,31,31,28,31,30,31,30,31,31,30/)
        
      if (date%timestep == '1MON') then !*************************
      	month = date%month
      	wy = date%wateryear
        if (present(offset)) then
        	if (date%month + offset >= 1) then
        		wy = date%wateryear + int((date%month + offset - 1)/12)
        		month = 1 + mod((date%month + offset - 1),12)
        	else
        		wy = date%wateryear - 1 + int((date%month + offset)/12)
        		month = 12 + mod((date%month + offset),12)
        	end if
        end if
        taf_cfs = 43560000. / 86400. / real(daysin(month))
        ! uncomment lines below if you want leap years counting
        if( mod( wy, 100) /= 0 .or. mod( wy, 400) == 0) then
         	if ( month == 5 .and. mod(wy,4) == 0)  then
        		taf_cfs = 43560000. / 86400. / 29.
         	end if
        end if
        
      else !timestep must be 1DAY *************************
      
      	taf_cfs = 43560000.0/86400.0
      	
      end if !*********************************************
      
    end function

    real function cfs_taf(offset)       ! multiplier to convert from CFS to TAF/period
        INTEGER, OPTIONAL, INTENT(IN) :: offset
        if (present(offset)) then
			cfs_taf = 1./taf_cfs(offset)
        else
			cfs_taf = 1./taf_cfs()
        end if
    end function

!---------------
    real function af_cfs()       ! multiplier to convert from AF/period to CFS
        af_cfs = 43560. / 86400. / real(date%deltaT_days)
    end function

    real function cfs_af()       ! multiplier to convert from CFS to AF/period
		cfs_af = 1./af_cfs()
    end function
!---------------
	real function cf_cfs()       ! multiplier to convert from CF/period to CFS
        cf_cfs = 1. / 86400. / real(date%deltaT_days)
    end function

    real function cfs_cf()       ! multiplier to convert from CFS to CF/period
		cfs_cf = 1./cf_cfs()
    end function
!---------------
    real function ckm_cfs()       ! multiplier to convert from CKM/period to CFS
        ckm_cfs = 3.531465828E10 / 86400. / real(date%deltaT_days)
    end function

    real function cfs_ckm()       ! multiplier to convert from CFS to CKM/period
		cfs_ckm = 1./ckm_cfs()
    end function
!---------------
    real function cdm_cfs()       ! multiplier to convert from CDM/period to CFS
        cdm_cfs = 3.531465828E4 / 86400. / real(date%deltaT_days)
    end function

    real function cfs_cdm()       ! multiplier to convert from CFS to CDM/period
		cfs_cdm = 1./cdm_cfs()
    end function
!---------------
! FLOW CONVERSION (SI - ENGLISH units)
!-------------------------------------
	real function cms_cfs()       ! multiplier to convert from CM/S to CFS
        cms_cfs = 3.531465828E1
    end function

    real function cfs_cms()       ! multiplier to convert from CFS to CM/S
		cfs_cms = 1./cms_cfs()
    end function
!---------------

! Intrinsic Wresl functions
!==========================
   real function pow(m,e)
        real, intent(in) :: m,e
        pow = m**e
   end function

! Previous month integer values
	integer function prevOCT()
		if(date%month > 1) then
  		prevOCT = 1-date%month
		else
   		prevOCT = -11-date%month
  	end if
	end function

	integer function prevNOV()
		if(date%month > 2) then
  		prevNOV = 2-date%month
    else
    	prevNOV = -10-date%month
    end if
	end function

    integer function prevDEC()
		if(date%month > 3) then
        	prevDEC = 3-date%month
        else
        	prevDEC = -9-date%month
        end if
    end function

    integer function prevJAN()
		if(date%month > 4) then
        	prevJAN = 4-date%month
        else
        	prevJAN = -8-date%month
        end if
    end function

    integer function prevFEB()
		if(date%month > 5) then
        	prevFEB = 5-date%month
        else
        	prevFEB = -7-date%month
        end if
    end function

    integer function prevMAR()
		if(date%month > 6) then
        	prevMAR = 6-date%month
        else
        	prevMAR = -6-date%month
        end if
    end function

    integer function prevAPR()
		if(date%month > 7) then
        	prevAPR = 7-date%month
        else
        	prevAPR = -5-date%month
        end if
    end function

    integer function prevMAY()
		if(date%month > 8) then
        	prevMAY = 8-date%month
        else
        	prevMAY = -4-date%month
        end if
    end function

    integer function prevJUN()
		if(date%month > 9) then
        	prevJUN = 9-date%month
        else
        	prevJUN = -3-date%month
        end if
    end function

    integer function prevJUL()
		if(date%month > 10) then
        	prevJUL = 10-date%month
        else
        	prevJUL = -2-date%month
        end if
    end function

    integer function prevAUG()
		if(date%month > 11) then
        	prevAUG = 11-date%month
        else
        	prevAUG = -1-date%month
        end if
    end function

    integer function prevSEP()
		if(date%month > 12) then
        	prevSEP = 0
        else
        	prevSEP = -date%month
        end if
    end function
    
    !******************************************************
    !Added 2/22/01
    !******************************************************
    
    integer function daysInMonth(d)
        type(dwmy), intent(in) :: d
        !  o  n  d  j  f  m  a  m  j  j  a  s
        INTEGER, dimension(12) :: daysin = (/31,30,31,31,28,31,30,31,30,31,31,30/)
        daysInMonth = daysin( d%month)
        ! uncomment lines below if you want leap years counting
        if( mod( d%wateryear, 100) /= 0 .or. mod( d%wateryear, 400) == 0) then
        	if ( d%month == 5 .and. mod(d%wateryear,4) == 0)  daysInMonth = 29
       	end if
    end function daysInMonth
  
    !**********************************************************
    !**********************************************************

END MODULE GLOBAL
