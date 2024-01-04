
module misc

  use string_utility

  implicit none

  public :: intervals_per_day





  contains
  
  function intervals_per_day(dss_Epart) result(intervals)
    use string_utility
    implicit none
    character(*), intent(in) :: dss_Epart
    integer :: intervals
      
    if ( StrLowCase(trim(dss_Epart)) .eq. "1hour" ) then
      intervals = 24
    elseif ( StrLowCase(trim(dss_Epart)) .eq. "1day" ) then
      intervals = 1
      
    end if
  end function

end module

