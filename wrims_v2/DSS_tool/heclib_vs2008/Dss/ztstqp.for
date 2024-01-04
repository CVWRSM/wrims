      LOGICAL FUNCTION ZTSTQP (QUALWD)
C
C     Test the protection bit on a DSS quality flag to see if on.
C
      INTEGER QUALWD
      INTEGER IBIT
C
      CALL BITS (QUALWD, 32, IBIT, .TRUE.)
      IF (IBIT.EQ.0) THEN
         ZTSTQP = .FALSE.
      ELSE
         ZTSTQP = .TRUE.
      ENDIF
C
      RETURN
      END
 
