      FUNCTION nopers_  (interval, flag, juls, istime,
     +   jule, ietime)
C
C
      INTEGER          interval          !![REFERENCE]
      INTEGER          flag              !![REFERENCE]
      INTEGER          juls              !![REFERENCE]
      INTEGER          istime            !![REFERENCE]
      INTEGER          jule              !![REFERENCE]
      INTEGER          ietime            !![REFERENCE]
C
      nopers_ = NOPERS (interval, flag, juls, istime, jule, ietime)
C
      RETURN
      END
