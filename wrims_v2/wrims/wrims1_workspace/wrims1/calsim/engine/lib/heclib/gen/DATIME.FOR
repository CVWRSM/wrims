      SUBROUTINE DATIME (JYR,NDAY,J10SEC)                               MLu
C
C     PC VERSION
C
C     INTEGER*2 IHR, IMIN, ISEC, I100SC, IYR, IMON, IDAY                M
      INTEGER IHR, IMIN, ISEC, J10SEC
C     INTEGER*4 J10SEC, JDAY, JHR, JMIN, IYMDJL                         M
C
      CHARACTER CT*11                                                   L
C     INTEGER*4 J10SEC, JHR, JMIN, JSEC, J100SC, JUL, IYMDJL            L
      CHARACTER  CZONE*5, CARRAY*10
      INTEGER IVALS(8)
C
C     FORTRAN 90
      CALL DATE_AND_TIME(CARRAY, CT, CZONE, IVALS)
      JYR=IVALS(1)
      JDAY=IVALS(3)
      IHR=IVALS(5)
      IMIN=IVALS(6)
      ISEC=IVALS(7)
      J10SEC=IHR*36000 + IMIN*600 + ISEC*10 + IVALS(8)/100
C     CALL GETTIM(IHR,IMIN,ISEC,I100SC)                                 M
C     CALL GETDAT(IYR,IMON,IDAY)                                        M
C     JYR = IYR                                                         M
C     JMON = IMON                                                       M
C     NDAY = IDAY                                                       M
C     JDAY = IYMDJL (JYR,JMON,NDAY)                                     M
      NDAY = JDAY - IYMDJL(JYR,1,1) + 1                                 M
C     JHR = IHR                                                         M
C     JMIN = IMIN                                                       M
C     J100SC = I100SC                                                   M
C     J10SEC = JHR*36000 + JMIN*600 + J100SC/10                         M
C
C     Lahey Fortran version
C     CALL TIME (CT)                                                    L
C     READ (CT, '(4(I2,1X))') JHR, JMIN, JSEC, J100SC                   L
C     J10SEC = (JHR * 36000) + (JMIN * 600) + (JSEC * 10) + (J100SC/10) L
C     CT = ' '                                                          L
C     CALL DATE (CT)                                                    L
C     CALL DATYMD (CT, JYR, IMON, IDAY, IERR)                           L
C     JUL = IYMDJL (JYR, IMON, IDAY)                                    L
C     NDAY = JUL - IYMDJL (JYR, 1, 1) + 1                               L
C
C     CALL SYSTIM (JUL, ISEC)                                           u
C     J10SEC = ISEC * 10                                                u
C     I =  JLIYMD (JUL, JYR, IMON, IDAY)                                u
C     NDAY = JUL - IYMDJL (JYR, 1, 1) + 1                               u
C
      RETURN                                                            MLu
      END                                                               MLu
