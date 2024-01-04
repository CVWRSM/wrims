
module parameters

  implicit none

  integer, public, parameter :: HOURS_PER_DAY  = 24
  integer, public, parameter :: MONTHS_PER_YEAR  = 12
  integer, public, parameter :: READS_PER_LOOP_FOR_HOURLY  = 24  ! dividable by 96 (24*4) 
  integer, public, parameter :: READS_PER_LOOP_FOR_MONTHLY = 24





end module 
