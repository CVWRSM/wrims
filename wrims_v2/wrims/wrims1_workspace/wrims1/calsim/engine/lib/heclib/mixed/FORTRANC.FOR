      INTEGER FUNCTION fortranclose_  (IUNIT)
C
C     Interface routine for C/C++ functions to close a file
C     open under FORTRAN
C
      INTEGER IUNIT     ![REFERENCE]
C
      INTEGER ISTAT
C
      CLOSE (UNIT=IUNIT, IOSTAT=ISTAT)
      fortranclose_= ISTAT
C
      RETURN
      END
