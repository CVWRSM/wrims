      SUBROUTINE ZDBLOOK (IFLTAB, IADD, ILEN)
C
C     Prints coded information in a DSS file to MUNIT
C     IADD is the beginning address in the file
C     ILEN is the number of words to print.
C
C     Written by Bill Charley at HEC
C
      INTEGER IFLTAB(*), IADD, ILEN
C            
      INCLUDE 'zdssmz.h'
C
      INCLUDE 'zdssts.h'
C 
C
C    Clear all buffers to ensure we get what is on disk
      CALL ZBDUMP (IFLTAB, 1)
C
      CALL ZGTREC (IFLTAB, ILBUFF, ILEN, IADD, .FALSE.)
      CALL ZDEBUG (MUNIT, ILBUFF, IADD, ILEN)
C
      RETURN
C
      END
