      subroutine getdat_(iyr, imon, iday)
      use IFPORT, only:GETDAT
      integer iyr, imon, iday      
      CALL GETDAT(iyr, imon, iday)
      return
      end
    