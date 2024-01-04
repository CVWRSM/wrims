      SUBROUTINE CTIME (CARRAY)
C
C
C     INTEGER IARRAY(3)                                                 H
      CHARACTER CARRAY*(*)
      CHARACTER CT*11                                                   L
      INTEGER*2 IHR,IMIN,ISEC,I100SC                                    M
      CHARACTER CZONE*5
      INTEGER IVALS(8)
C
C
      I = LEN(CARRAY)
      IF (I.LT.8) THEN
      CALL CHRFIL (CARRAY,'*')
      RETURN
      ENDIF
C
C     Harris time routine
C     DO 10 I = 1,3                                                     H
C     IARRAY(I) = 3H                                                    H
C10   CONTINUE                                                          H
C
C     CALL TIME(IARRAY)                                                 H
C     CALL HOLCHR(IARRAY,2,8,CARRAY,1)                                  H
C     IF (CARRAY(1:1).EQ.' ') CARRAY(1:1) = '0'                         H
C
C     MS FORTRAN TIME ROUTINE
c     CALL GETTIM (IHR,IMIN,ISEC,I100SC)                                M
C     WRITE (CARRAY,20) IHR,IMIN,ISEC                                   M
C20   FORMAT (I2.2,':',I2.2,':',I2.2)                                   M
C     Fortran 90
      CALL DATE_AND_TIME(CARRAY, CT, CZONE, IVALS)
      IHR=IVALS(5)
      IMIN=IVALS(6)
      ISEC=IVALS(7)
      WRITE (CARRAY,20) IHR,IMIN,ISEC                                   M
 20   FORMAT (I2.2,':',I2.2,':',I2.2)                                   M
C     Lahey time routine
C     CALL TIME (CT)                                                    L
C     CT(9:) = ' '                                                      L
C     CARRAY = CT                                                       L
C
C     Unix time
C     CALL SYSTIM (JUL, ISECS)                                          u
C     IHR = ISECS / 3600                                                u
C     IMIN = (ISECS - (IHR * 3600)) / 60                                u
C     ISEC = ISECS - (IHR * 3600) - (IMIN * 60)                         u
C     WRITE (CARRAY,20) IHR,IMIN,ISEC                                   u
C20   FORMAT (I2.2,':',I2.2,':',I2.2)                                   u
C
      RETURN
      END
