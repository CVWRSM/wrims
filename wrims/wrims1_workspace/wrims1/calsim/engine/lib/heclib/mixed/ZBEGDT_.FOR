      SUBROUTINE zbegdt_  (JUL, INTL, IYR, IMON, IDAY,
     *                             IBLOCK, IVERNO)
C
      INTEGER   JUL    !![REFERENCE]
      INTEGER   INTL   !![REFERENCE]
      INTEGER   IYR    !![REFERENCE]
      INTEGER   IMON   !![REFERENCE]
      INTEGER   IDAY   !![REFERENCE]
      INTEGER   IBLOCK !![REFERENCE]
      INTEGER   IVERNO !![REFERENCE]
C
      CALL ZBEGDT (JUL, INTL, IYR, IMON, IDAY, IBLOCK, IVERNO)
C
      RETURN
      END
