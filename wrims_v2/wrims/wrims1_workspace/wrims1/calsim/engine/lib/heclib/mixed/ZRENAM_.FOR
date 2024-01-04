      SUBROUTINE ZRENAM_  (IFLTAB, CPATHO, NPATHO, CPATHN,
     * NPATHN, LFOUND, L_CPATHO, L_CPATHN)
C
      INTEGER   IFLTAB !![REFERENCE]
      CHARACTER CPATHO !![REFERENCE]
      INTEGER   NPATHO !![REFERENCE]
      CHARACTER CPATHN !![REFERENCE]
      INTEGER   NPATHN !![REFERENCE]
*     LOGICAL   LFOUND !![REFERENCE]  (Really a logical!)
      INTEGER   LFOUND !![REFERENCE]
      INTEGER L_CPATHO !![VALUE]
      INTEGER L_CPATHN !![VALUE]
C
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      LFOUND = 0
C
      CALL ZRENAM ( IFLTAB, CPATHO(1:L_CPATHO), NPATHO,
     * CPATHN(1:L_CPATHN), NPATHN, LFOUND )
C
       RETURN
       END
