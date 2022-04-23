      INTEGER FUNCTION IHM2M (CTIME)
C
C     CHANGE FROM CODED MILITARY TIME TO MINUTES SINCE MIDNIGHT
C
      CHARACTER CTIME*(*)
      CHARACTER CTEMP*4
C
C
      CTEMP = ' '
      CALL CHRLNB (CTIME, N)
      IF (N.EQ.0) GO TO 900
      IPOS = 4
      DO 10 I=N, 1, -1
         K = ICHAR(CTIME(I:I))
         IF ((K.GE.48).AND.(K.LE.57)) THEN
            IF (IPOS.GE.1) CTEMP(IPOS:IPOS) = CTIME(I:I)
            IPOS = IPOS - 1
         ENDIF
         IF (K.GT.59) GO TO 900
 10   CONTINUE
C
C
      READ ( CTEMP, 20, ERR=900) IHR, IMIN
 20   FORMAT (2I2)
C
      IHM2M = (IHR*60) + IMIN
      RETURN
C
C     ERROR
  900 IHM2M = -1
      RETURN
      END
