      SUBROUTINE ZWMEM (IUNIT, IREC, IARRAY, NWORDS, ISTAT)
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
 20   FORMAT (T3,'=====DSS===Debug: Write to memory;  array',I4,
     * '  Record',I8)
C
C
      ISTAT = 0
C     MS-DOS and Unix Assembly I-O
      IOFSET = (IREC-1) * 512
      IEND = IOFSET + (NWORDS *4)
      IWDPOS = IOFSET / 4
C
      IF (IEND.GT.MEMSIZES(IUNIT)) THEN
         IADDSIZE = MEMSIZES(IUNIT) / 10
         IF (IADDSIZE.GT.5000000) THEN
             IADDSIZE = 5000000
         ELSE IF (IADDSIZE.LT.100000) THEN
             IADDSIZE = 100000
         ENDIF
         MEMSIZES(IUNIT) = MEMSIZES(IUNIT) + IADDSIZE
         CALL reallocatemem (MEMARRAY(IUNIT), MEMSIZES(IUNIT))
         IF (ISTAT.NE.0) THEN
             RETURN
         ENDIF
      ENDIF
C
C
      CALL writemem(MEMARRAY(IUNIT), IWDPOS, IARRAY, NWORDS)
C
C
C
 800  CONTINUE
      RETURN
      END
