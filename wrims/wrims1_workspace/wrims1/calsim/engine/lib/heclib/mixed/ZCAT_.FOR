      SUBROUTINE ZCAT_  (IFLTAB, ICUNIT, ICDUNT, INUNIT,
     *   CINSTR, LABREV, LDOSRT, LCDCAT, NORECS, L_CINSTR)
C
      INTEGER   IFLTAB  !![REFERENCE]
      INTEGER   ICUNIT  !![REFERENCE]
      INTEGER   ICDUNT  !![REFERENCE]
      INTEGER   INUNIT  !![REFERENCE]
      CHARACTER CINSTR  !![REFERENCE]
      LOGICAL   LABREV  !![REFERENCE]
      LOGICAL   LDOSRT  !![REFERENCE]
*     LOGICAL   LCDCAT  !![REFERENCE]  (Really a logical!)
      INTEGER   LCDCAT  !![REFERENCE]
      INTEGER   NORECS  !![REFERENCE]
      INTEGER L_CINSTR  !![VALUE]
 
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      LCDCAT = 0
C
      CALL ZCAT (IFLTAB, ICUNIT, ICDUNT, INUNIT,
     * CINSTR(1:L_CINSTR), LABREV, LDOSRT, LCDCAT, NORECS)
C
      RETURN
      END
