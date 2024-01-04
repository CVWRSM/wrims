      FUNCTION m2ihm_  (min, ctime, ctime_len)
C
      INTEGER          min               !![REFERENCE]
      CHARACTER        ctime             !![REFERENCE]
      INTEGER          ctime_len         !![VALUE]
C
      m2ihm_ = M2IHM (min, ctime(1:ctime_len))
C
      RETURN
      END
