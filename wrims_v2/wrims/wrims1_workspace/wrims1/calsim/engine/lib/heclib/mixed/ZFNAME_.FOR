      SUBROUTINE ZFNAME_  (CNAMIN, CNAME, NNAME, LEXIST,
     *                              L_CNAMIN, L_CNAME)
C
      CHARACTER CNAMIN !![REFERENCE]
      CHARACTER CNAME  !![REFERENCE]
      INTEGER   NNAME  !![REFERENCE]
*     LOGICAL   LEXIST !![REFERENCE] (Really a logical!)
      INTEGER   LEXIST !![REFERENCE]
      INTEGER L_CNAMIN !![VALUE]
      INTEGER L_CNAME  !![VALUE]
C
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      LEXIST = 0
C
      CALL ZFNAME (CNAMIN(1:L_CNAMIN), CNAME(1:L_CNAME), NNAME, LEXIST)
C
       RETURN
       END
