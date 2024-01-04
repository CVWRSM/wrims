      INTEGER FUNCTION ihm2m_  (CMINUTE, L_CMINUTE)
C
C     Interface routine to call HECLIB routine IHM2M
C
      CHARACTER CMINUTE !![REFERENCE]
      INTEGER L_CMINUTE !![VALUE]
C
      ihm2m_ = IHM2M (CMINUTE(1:L_CMINUTE))
C
      RETURN
      END
