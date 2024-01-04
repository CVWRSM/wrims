      SUBROUTINE ZPATH_  (CA, CB, CC, CD, CE, CF, CPATH, NPATH,
     *                   L_CA, L_CB, L_CC, L_CD, L_CE, L_CF, L_CPATH)
C
      CHARACTER CA     !![REFERENCE]
      CHARACTER CB     !![REFERENCE]
      CHARACTER CC     !![REFERENCE]
      CHARACTER CD     !![REFERENCE]
      CHARACTER CE     !![REFERENCE]
      CHARACTER CF     !![REFERENCE]
      CHARACTER CPATH  !![REFERENCE]
      INTEGER   NPATH  !![REFERENCE]
      INTEGER L_CA     !![VALUE]
      INTEGER L_CB     !![VALUE]
      INTEGER L_CC     !![VALUE]
      INTEGER L_CD     !![VALUE]
      INTEGER L_CE     !![VALUE]
      INTEGER L_CF     !![VALUE]
      INTEGER L_CPATH  !![VALUE]
C
C
      CALL ZPATH (CA(1:L_CA), CB(1:L_CB), CC(1:L_CC), CD(1:L_CD),
     *             CE(1:L_CE), CF(1:L_CF), CPATH(1:L_CPATH), NPATH)
C
      RETURN
      END
