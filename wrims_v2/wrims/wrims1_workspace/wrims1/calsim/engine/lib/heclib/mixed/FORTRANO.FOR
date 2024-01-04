      INTEGER FUNCTION fortranopen_   (IUNIT, FILENAME, length)
C
C     Interface routine for C/C++ functions to open a file
C     under FORTRAN
C
      INTEGER IUNIT               ![REFERENCE]
      CHARACTER FILENAME          ![REFERENCE]
      INTEGER length              ![VALUE]
C
      INTEGER ISTAT
C
      OPEN (UNIT=IUNIT, FILE=FILENAME(1:length), IOSTAT=ISTAT)
      fortranopen_= ISTAT
C
      RETURN
      END
