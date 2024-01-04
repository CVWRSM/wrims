      subroutine gettim_(ihr, imin, isec, i100th)
      use IFPORT,only:GETTIM
      integer ihr, imin, isec, i100th      
      CALL GETTIM(ihr, imin, isec, i100th)
      return
      end
    