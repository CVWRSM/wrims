      SUBROUTINE getime_  (cin, ibline, ilen, juls, istime,
     +   jule, ietime, istat, length_cin)
C
C
      CHARACTER        cin               !![REFERENCE]
      INTEGER          ibline            !![REFERENCE]
      INTEGER          ilen              !![REFERENCE]
      INTEGER          juls              !![REFERENCE]
      INTEGER          istime            !![REFERENCE]
      INTEGER          jule              !![REFERENCE]
      INTEGER          ietime            !![REFERENCE]
      INTEGER          istat             !![REFERENCE]
      INTEGER          length_cin        !![VALUE]
C
      CALL GETIME (cin(1:length_cin), ibline, ilen, juls, istime,
     +   jule, ietime, istat)
C
      RETURN
      END
