      SUBROUTINE holchr_  (ihol, ipos, inumb,
     +   cstring, ibeg, length_cstring)
C
C
      CHARACTER        cstring           !![REFERENCE]
      INTEGER          ipos              !![REFERENCE]
      INTEGER          inumb             !![REFERENCE]
      INTEGER          ihol              !![REFERENCE]
      INTEGER          ibeg              !![REFERENCE]
      INTEGER          length_cstring    !![VALUE]
C
C
      CALL HOLCHR (ihol, ipos, inumb, cstring(1:length_cstring), ibeg)
C
      RETURN
      END
