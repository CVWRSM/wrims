      SUBROUTINE ZIRCPY (JARRAY, JTIME, DVALUES, SVALUES, JQUAL, IPOS,
     *                   LDOUBLE, LSQUAL, LRQUAL, LSTORE, LDSWAP)
C
      INTEGER JARRAY(*)
      INTEGER JTIME, IPOS
      DOUBLEPRECISION DVALUES(*)
      REAL SVALUES(*)
      INTEGER JQUAL(*)
      LOGICAL LDOUBLE
      LOGICAL LSQUAL
      LOGICAL LSTORE
      LOGICAL LRQUAL
      LOGICAL LDSWAP
C
      DOUBLEPRECISION DEQUIV
      REAL            SEQUIV
      INTEGER         IEQUIV(2)
      EQUIVALENCE (IEQUIV, DEQUIV), (IEQUIV, SEQUIV)
C
      LOGICAL ZTSTQP
      INCLUDE 'zdsslz.h'
C
C
C     Get the index for the quality word
      IF (LDOUBLE) THEN
         N = 4
      ELSE
         N = 3
      ENDIF
C
C
      IF (LSTORE) THEN
C        Do we need to check the data protection bit?
         IF (LRQUAL.AND.LQPBIT) THEN
            IF (ZTSTQP(JARRAY(N)).AND.(.NOT.ZTSTQP(JQUAL(IPOS))))
     *         RETURN
         ENDIF
C        Copy time value
         JARRAY(1) = JTIME
C
C        Copy data value
         IF (LDOUBLE) THEN
            DEQUIV = DVALUES(IPOS)
            IF (LDSWAP) CALL ZDSWAP(DEQUIV, 2)
            JARRAY(2) = IEQUIV(1)
            JARRAY(3) = IEQUIV(2)
         ELSE
            SEQUIV = SVALUES(IPOS)
            JARRAY(2) = IEQUIV(1)
         ENDIF
C
C        Copy quality value, if needed
         IF (LSQUAL) THEN
             JARRAY(N) = JQUAL(IPOS)
         ELSE
             IF (LRQUAL) THEN
                 JARRAY(N) = 0
             ENDIF
         ENDIF
C
      ELSE
C
C        Copy time value
         JTIME = JARRAY(1)
C
C        Copy data value
         IF (LDOUBLE) THEN
            IEQUIV(1) = JARRAY(2)
            IEQUIV(2) = JARRAY(3)
            IF (LDSWAP) CALL ZDSWAP(DEQUIV, 2)
            DVALUES(IPOS) = DEQUIV
         ELSE
            IEQUIV(1)       = JARRAY(2)
           SVALUES(IPOS) = SEQUIV
         ENDIF
C
C        Copy quality value, if needed
         IF (LSQUAL) JQUAL(IPOS) = JARRAY(N)
      ENDIF
C
      RETURN
      END
      LOGICAL FUNCTION LCOMPAR (SVALUES, DVALUES, IPOS, LDOUBLE, VAL)
C
      REAL SVALUES(*)
      DOUBLEPRECISION DVALUES(*)
      LOGICAL LDOUBLE
      REAL VAL
      INTEGER IPOS
C
      IF (LDOUBLE) THEN
          IF (DVALUES(IPOS).EQ.DBLE(VAL)) THEN
             LCOMPAR = .TRUE.
          ELSE
              LCOMPAR = .FALSE.
          ENDIF
      ELSE
          IF (SVALUES(IPOS).EQ.VAL) THEN
             LCOMPAR = .TRUE.
          ELSE
              LCOMPAR = .FALSE.
          ENDIF
      ENDIF
C
      RETURN
      END
