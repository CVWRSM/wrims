      SUBROUTINE DATIME (JYR,NDAY,J10SEC)                               MLu
C
C     PC VERSION
C
      INTEGER*2 IHR, IMIN, ISEC, I100SC, IYR, IMON, IDAY                M
      INTEGER*4 J10SEC, JDAY, JHR, JMIN, IYMDJL                         M
C
C     CHARACTER CT*11                                                   L
C     INTEGER*4 J10SEC, JHR, JMIN, JSEC, J100SC, JUL, IYMDJL            L
C
      CALL GETTIM(IHR,IMIN,ISEC,I100SC)                                 M
      CALL GETDAT(IYR,IMON,IDAY)                                        M
      JYR = IYR                                                         M
      JMON = IMON                                                       M
      NDAY = IDAY                                                       M
      JDAY = IYMDJL (JYR,JMON,NDAY)                                     M
      NDAY = JDAY - IYMDJL(JYR,1,1) + 1                                 M
      JHR = IHR                                                         M
      JMIN = IMIN                                                       M
      J100SC = I100SC                                                   M
      J10SEC = JHR*36000 + JMIN*600 + J100SC/10                         M
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
