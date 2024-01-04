      integer function ierrno_()
      use IFPORT,only: IERRNO
      ierrno_ = IERRNO()
      return 
      end
    