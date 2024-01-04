      SUBROUTINE ZOPNCA_  (CDSSFI, ICUNIT, LGENCA, LOPNCA,
     * LCATCA, ICDUNT, LGENCD, LOPNCD, LCATCD, NRECS,
     * L_CDSSFI)
C
      CHARACTER CDSSFI !![REFERENCE]
      INTEGER   ICUNIT !![REFERENCE]
      LOGICAL   LGENCA !![REFERENCE]
*     LOGICAL   LOPNCA !![REFERENCE] (Really a logical!)
      INTEGER   LOPNCA !![REFERENCE]
*     LOGICAL   LCATCA !![REFERENCE] (Really a logical!)
      INTEGER   LCATCA !![REFERENCE]
      INTEGER   ICDUNT !![REFERENCE]
      LOGICAL   LGENCD !![REFERENCE]
*     LOGICAL   LOPNCD !![REFERENCE] (Really a logical!)
      INTEGER   LOPNCD !![REFERENCE]
*     LOGICAL   LCATCD !![REFERENCE] (Really a logical!)
      INTEGER   LCATCD !![REFERENCE]
      INTEGER   NRECS  !![REFERENCE]
      INTEGER L_CDSSFI !![VALUE]
C
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      LOPNCA = 0
      LCATCA = 0
      LOPNCD = 0
      LCATCD = 0
C
      CALL ZOPNCA (CDSSFI(1:L_CDSSFI), ICUNIT, LGENCA, LOPNCA,
     * LCATCA, ICDUNT, LGENCD, LOPNCD, LCATCD, NRECS)
C
      RETURN
      END
