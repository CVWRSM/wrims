      SUBROUTINE ZDTYPE_  (IFLTAB, CPATH, NSIZE, LEXIST,
     *                              CDTYPE, IDTYPE, L_CPATH, L_CDTYPE)
C
      INTEGER   IFLTAB !![REFERENCE]
      CHARACTER CPATH  !![REFERENCE]
      INTEGER   NSIZE  !![REFERENCE]
*     LOGICAL   LEXIST !![REFERENCE] (Really a logical!)
      INTEGER   LEXIST !![REFERENCE]
      CHARACTER CDTYPE !![REFERENCE]
      INTEGER   IDTYPE !![REFERENCE]
      INTEGER L_CPATH  !![VALUE]
      INTEGER L_CDTYPE !![VALUE]
C
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      LEXIST = 0
C
      CALL ZDTYPE ( IFLTAB, CPATH(1:L_CPATH), NSIZE, LEXIST,
     *  CDTYPE(1:L_CDTYPE), IDTYPE)
C
       RETURN
       END
