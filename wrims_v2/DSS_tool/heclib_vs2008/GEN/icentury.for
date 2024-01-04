      integer function ICENTURY ()
C
C
C     Returns the year of the start of the current century
C     (e.g., 1900 or 2000)
C     Bill Charley, HEC
C
      SAVE ICENT
      DATA ICENT /0/
C
      IF (ICENT.EQ.0) THEN
         CALL GETDAT(IYR,IMON,IDAY)                                     M
C        CALL SYSTIM (JUL, ISEC)                                        u
C        I =  JLIYMD (JUL, IYR, IMON, IDAY)                             u
         ICENT = IYR - MOD (IYR, 100)
      ENDIF
C
      ICENTURY = ICENT
      RETURN
      END
