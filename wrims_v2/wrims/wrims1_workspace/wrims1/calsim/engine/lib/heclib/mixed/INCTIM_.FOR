      FUNCTION inctim_  (interval, flag, number, juls, istime,
     +   jule, ietime)
C
C
      INTEGER          interval          !![REFERENCE]
      INTEGER          flag              !![REFERENCE]
      INTEGER          number            !![REFERENCE]
      INTEGER          juls              !![REFERENCE]
      INTEGER          istime            !![REFERENCE]
      INTEGER          jule              !![REFERENCE]
      INTEGER          ietime            !![REFERENCE]
C
      inctim_ = INCTIM (interval, flag, number, juls, istime, jule,
     + ietime)
C
      RETURN
      END
