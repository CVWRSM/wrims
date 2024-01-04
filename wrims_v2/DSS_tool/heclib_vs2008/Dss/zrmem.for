      SUBROUTINE ZRMEM (IUNIT, IREC, IARRAY, NWORDS, ISTAT)
C
C     Write physical record to disk
C
C     Written by Bill Charley at HEC, 1984.
C
C     USE DFPORT                                                        d
C
      INTEGER IARRAY(NWORDS)
C
      INCLUDE 'zdssnz.h'
C
      INCLUDE 'zdssmz.h'
C
      COMMON /DSSMEM/ MEMARRAY(10), MEMSIZES(10)
C
C
      IF (MLEVEL.GE.10) WRITE (MUNIT, 20) IUNIT, IREC
 20   FORMAT (T3,'=====DSS===Debug: Read from memory;  array',I4,
     * '  Record',I8)
C
C
      ISTAT = 0
C     MS-DOS and Unix Assembly I-O
      IOFSET = (IREC-1) * 512      
      IWDPOS = IOFSET / 4
C
      IF (IOFSET.GT.MEMSIZES(IUNIT)) THEN
         IF (MLEVEL.GE.0) WRITE (MUNIT, 40) IREC, IOFSET, 
     *                    MEMSIZES(IUNIT)
 40      FORMAT(T3,'**** Invalid memory location, record:',I12,/,
     *          T3,'     Offset location:',I12,',  Memory size:',I12)
         ISTAT = -1
      ENDIF
C
      CALL readmem(MEMARRAY(IUNIT), IWDPOS, IARRAY, NWORDS)
C
C
C
 800  CONTINUE
      RETURN
      END
