      INTEGER*4 FUNCTION iymdjl_  (IYEAR, IMONTH, IDAY)
C
C     Interface routine to call HECLIB routine IDAYWK
C
      INTEGER IYEAR  !![REFERENCE]
      INTEGER IMONTH !![REFERENCE]
      INTEGER IDAY   !![REFERENCE]
C
      iymdjl_ = IYMDJL (IYEAR, IMONTH, IDAY)
C
      RETURN
      END
