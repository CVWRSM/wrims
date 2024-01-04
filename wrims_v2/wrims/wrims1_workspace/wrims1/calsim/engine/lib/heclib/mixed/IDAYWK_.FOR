      INTEGER FUNCTION idaywk_  (JULIAN)
C
C     Interface routine to call HECLIB routine IDAYWK
C
      INTEGER*4 JULIAN !![REFERENCE]
C
      idaywk_ = IDAYWK (JULIAN)
C
      RETURN
      END
