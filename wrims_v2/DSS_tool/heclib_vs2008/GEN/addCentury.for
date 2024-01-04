      SUBROUTINE ADDCENTURY (IYEAR)
C
C     This takes a two digit year and add the "correct" century
C     Bill Charley, HEC Oct 1997
C
C     A two digit year mean it is the year that falls within the
C     range of 90 years back (from now) through 10 year forward.
C
C     CHARACTER CSTR*24                                                 u
C
C     Is it 2 digit?
      IF (IYEAR.GT.100) RETURN
C
C     Get the current century
      CALL GETDAT(IYR,IMON,IDAY)                                        M
C     fdate is a Sun Fortran system routine
C     CALL FDATE(CSTR)                                                  u
C     IYR = INTGR(CSTR, 21, 4, IERR)                                    u
      ICENT = IYR - MOD (IYR, 100)
C
C     Add to year, then check if it is within range
      IYEAR = IYEAR + ICENT
      IF (IYEAR.GT.(IYR+10)) IYEAR = IYEAR - 100
      IF (IYEAR.LT.(IYR-90)) IYEAR = IYEAR + 100
C
      RETURN
      END
