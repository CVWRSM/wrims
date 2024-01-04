      SUBROUTINE SYSTIM ( JUL, ISECONDS)
C
C     PROVIDES CURRENT HEC JULIAN DATE AND
C     TIME IN SECONDSS PAST MIDNIGHT WHEN CALLED
C
      INTEGER*4 JUL, IYMDJL                                             M
C     INTEGER*4 JUL                                                     L
      INTEGER*2 IHR,IMIN,ISEC,I100SC                                    M
      INTEGER*2 IYR,IMON,IDAY                                           M
C
C
      CALL GETTIM(IHR,IMIN,ISEC,I100SC)                                 M
      JHR = IHR                                                         M
      JMIN = IMIN                                                       M
      ISECONDS = JHR*3600 + JMIN*60 + ISEC                             
      CALL GETDAT(IYR,IMON,IDAY)                                        M
      JYR = IYR                                                         M
      JMON = IMON                                                       M
      JDAY = IDAY                                                       M
      JUL = IYMDJL(JYR,JMON,JDAY)                                       M
C
      RETURN
      END
