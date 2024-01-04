      SUBROUTINE ZCHECK_  (IFLTAB, CPATH, NPATH, NHEAD,
     *                              NDATA, LFOUND, L_CPATH)
C
      INTEGER   IFLTAB !![REFERENCE]
      CHARACTER CPATH  !![REFERENCE]
      INTEGER   NPATH  !![REFERENCE]
      INTEGER   NHEAD  !![REFERENCE]
      INTEGER   NDATA  !![REFERENCE]
*     LOGICAL   LFOUND !![REFERENCE]  (Really a logical!)
      INTEGER   LFOUND !![REFERENCE]
      INTEGER L_CPATH  !![VALUE]
C
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      LFOUND = 0
C
      CALL ZCHECK ( IFLTAB, CPATH(1:L_CPATH), NPATH, NHEAD, NDATA,
     * LFOUND)
C
      RETURN
      END
