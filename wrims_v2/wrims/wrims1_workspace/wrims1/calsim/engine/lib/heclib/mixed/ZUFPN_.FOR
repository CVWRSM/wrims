      SUBROUTINE ZUFPN_  (CA, NA, CB, NB, CC, NC, CD, ND,
     +                             CE, NE, CF, NF, CPATH, NPATH, ISTAT,
     +                     L_CA, L_CB, L_CC, L_CD, L_CE, L_CF, L_CPATH)
C
      CHARACTER CA     !![REFERENCE]
      INTEGER   NA     !![REFERENCE]
      CHARACTER CB     !![REFERENCE]
      INTEGER   NB     !![REFERENCE]
      CHARACTER CC     !![REFERENCE]
      INTEGER   NC     !![REFERENCE]
      CHARACTER CD     !![REFERENCE]
      INTEGER   ND     !![REFERENCE]
      CHARACTER CE     !![REFERENCE]
      INTEGER   NE     !![REFERENCE]
      CHARACTER CF     !![REFERENCE]
      INTEGER   NF     !![REFERENCE]
      CHARACTER CPATH  !![REFERENCE]
      INTEGER   NPATH  !![REFERENCE]
      INTEGER   ISTAT  !![REFERENCE]
      INTEGER L_CA     !![VALUE]
      INTEGER L_CB     !![VALUE]
      INTEGER L_CC     !![VALUE]
      INTEGER L_CD     !![VALUE]
      INTEGER L_CE     !![VALUE]
      INTEGER L_CF     !![VALUE]
      INTEGER L_CPATH  !![VALUE]
C
C
      CALL ZUFPN (CA(1:L_CA), NA, CB(1:L_CB), NB, CC(1:L_CC), NC,
     +            CD(1:L_CD), ND, CE(1:L_CE), NE, CF(1:L_CF), NF,
     +            CPATH(1:L_CPATH), NPATH, ISTAT)
C
      RETURN
      END
