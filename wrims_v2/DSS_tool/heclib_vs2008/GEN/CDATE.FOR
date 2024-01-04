      SUBROUTINE CDATE (CARRAY)
C
C     RETURNS THE CURRENT SYSTEM DATE IN A 9 CHARACTER
C     MILITARY STYLE FORMAT.  E.G. 07 APR 82
C
      CHARACTER CARRAY*(*)
C     INTEGER IARRAY(3)                                                 H
C
C     INTEGER*4 JUL                                                     L
      INTEGER*2 IYR, IMON, IDAY                                         M
      INTEGER JYR, JMON, JDAY                                           M
C
      ILEN = LEN(CARRAY)
      IF (ILEN.LT.9) THEN
      CALL CHRFIL (CARRAY, '*')
      RETURN
      ENDIF
C
C     HARRIS DATE ROUTINE
C     CALL DATE(IARRAY)                                                 H
C     CALL HOLCHR (IARRAY,1,9,CARRAY,1)                                 H
C     IF (CARRAY(1:1).EQ.' ') CARRAY(1:1) = '0'                         H
C
C     MS FORTRAN DATE ROUTINE
      CALL GETDAT (IYR, IMON, IDAY)                                     M
      JYR = IYR                                                         M
      JMON = IMON                                                       M
      JDAY = IDAY                                                       M
      CALL YMDDAT (JYR, JMON, JDAY, 117, CARRAY, NDATE, IERR)           M
C
C     Lahey Fortran
C     CALL DATE (CARRAY)                                                L
C     CALL DATJUL (CARRAY, JUL, IERR)                                   L
C     CALL JULDAT (JUL, 117, CARRAY, NDATE)                             L
C
C     Unix
C     CALL SYSTIM (JUL, ISEC)                                           u
C     CARRAY = ' '                                                      u
C     CALL JULDAT (JUL, 117, CARRAY, NDATE)                             u
C
      IF (IERR.NE.0) CALL CHRFIL (CARRAY, '*')                          ML
C
      RETURN
      END
