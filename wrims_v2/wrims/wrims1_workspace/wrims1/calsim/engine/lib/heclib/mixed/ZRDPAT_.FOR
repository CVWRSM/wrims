      SUBROUTINE zrdpat_  (icunit, ipos, inumb, tag,
     +   path, npath, lend, length_tag, length_path)
C
C
      INTEGER          icunit            !![REFERENCE]
      INTEGER          ipos              !![REFERENCE]
      INTEGER          inumb             !![REFERENCE]
      CHARACTER        tag               !![REFERENCE]
      CHARACTER        path              !![REFERENCE]
      INTEGER          npath             !![REFERENCE]
*     LOGICAL          lend              !![REFERENCE]  (Really a logical!)
      INTEGER          lend              !![REFERENCE]
      INTEGER          length_tag        !![VALUE]
      INTEGER          length_path       !![VALUE]
C
C
C     Initialize return logicals to zero, because of MS Fortran
C     interface problem (only 1 byte is set)
      lend = 0
C
      CALL ZRDPAT (icunit, ipos, inumb, tag(1:length_tag),
     +   path(1:length_path), npath, lend)
C
      RETURN
      END
