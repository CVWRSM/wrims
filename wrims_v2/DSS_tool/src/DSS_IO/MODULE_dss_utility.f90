
module dss_utility

  implicit none

  integer, private, save, dimension(600) :: ifltab_in_dss           ! DSS table for each input file
  
  integer, parameter :: MSG_LENGTH = 1500
  integer, parameter :: MSG_STACK_SIZE = 300000

  integer, private, save :: successful_assert_count = 0
  integer, private, save :: failed_assert_count = 0
  character (len = MSG_LENGTH), private, dimension (MSG_STACK_SIZE), save :: messageArray
  character (len = MSG_LENGTH), private, save :: msg = '[unit name not set from set_name]: '
  character (len = MSG_LENGTH), private, save :: unit_name  = '_not_set_'
  integer, private, save :: messageIndex = 1

  integer, private, save :: successful_case_count = 0
  integer, private, save :: failed_case_count = 0
  integer, private, save :: testCaseIndex = 1
  logical, private, save :: last_passed = .false.

  interface dss_open
     module procedure dss_open_ 
  end interface

  interface dss_read
     !module procedure dss_read_hourly_specify_endtime_ 
     module procedure dss_read_specify_nvals_ 

  end interface

!  interface dss_write
!     !module procedure dss_read_hourly_specify_endtime_ 
!     module procedure dss_write_specify_nvals_ 
!
!  end interface

  private :: &
            dss_read_specify_nvals_ 
           ! dss_write_specify_nvals_

     

contains

  include 'dss_open_.f90'
  include 'dss_read_specify_nvals_.f90'
  include 'dss_write_specify_nvals_.f90'


end module 
