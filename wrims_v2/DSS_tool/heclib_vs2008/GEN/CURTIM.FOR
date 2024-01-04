      SUBROUTINE CURTIM ( JUL, MIN)
C
C     PROVIDES CURRENT HEC JULIAN DATE AND
C     TIME IN MINS PAST MIDNIGHT WHEN CALLED
      USE IFPORT, ONLY:GETTIM,GETDAT
C
C     CHARACTER CDATE*8, CTIME*8, CHR*4                                 L
      INTEGER*4 JUL, IYMDJL                                             M
C     INTEGER*4 JUL                                                     L
      INTEGER IHR,IMIN,ISEC,I100SC                                  
      INTEGER IYR,IMON,IDAY                                          
C
C     CALL DATIME ( IYR, IDAY, ITSEC)                                   H
C     JAN1 = IYMDJL ( IYR, 1, 1)                                        H
C     JUL = JAN1 + IDAY - 1                                             H
C     MIN = ITSEC/600                                                   H
C
C     CALL WHEN ( CDATE, CTIME)                                         L
C     CHR(1:2) = CTIME(1:2)                                             L
C     CHR(3:4) = CTIME(4:5)                                             L
C     CALL DATJUL (CDATE, JUL, I)                                       L
C     MIN = IHM2M (CHR)                                                 L
C
      CALL GETTIM(IHR,IMIN,ISEC,I100SC)                                 M
      JHR = IHR                                                         M
      JMIN = IMIN                                                       M
      MIN = JHR*60 + JMIN                                               M
      CALL GETDAT(IYR,IMON,IDAY)                                        M
      JYR = IYR                                                         M
      JMON = IMON                                                       M
      JDAY = IDAY                                                       M
      JUL = IYMDJL(JYR,JMON,JDAY)                                       M
C
C     CALL SYSTIM (JUL, ISEC)                                           u
C     MIN = ISEC / 60                                                   u
C
      RETURN
      END
