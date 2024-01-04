      SUBROUTINE CTIME (CARRAY)
C
C
      CHARACTER CARRAY*(*)
C     CHARACTER CT*11                                                   L
      INTEGER*2 IHR,IMIN,ISEC,I100SC                                    M
C
C
      I = LEN(CARRAY)
      IF (I.LT.8) THEN
      CALL CHRFIL (CARRAY,'*')
      RETURN
      ENDIF
C
C
C     MS FORTRAN TIME ROUTINE
      CALL GETTIM (IHR,IMIN,ISEC,I100SC)                                M
      WRITE (CARRAY,20) IHR,IMIN,ISEC                                   M
 20   FORMAT (I2.2,':',I2.2,':',I2.2)                                   M
C
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
