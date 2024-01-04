      subroutine flush_(iunit)
      use IFCORE
      integer iunit
      logical l
      l = COMMITQQ(IUNIT)
      return
      end
    