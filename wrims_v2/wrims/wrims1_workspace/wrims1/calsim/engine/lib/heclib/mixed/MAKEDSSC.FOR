      SUBROUTINE makedsscatalog_   (CDSSFI, IFLTAB,
     +    CINSTR, NFOUND, IUNIT, length_CDSSFI, length_CINSTR)
C
C
      CHARACTER        CDSSFI            ![REFERENCE]
      INTEGER          IFLTAB            ![REFERENCE]
      CHARACTER        CINSTR            ![REFERENCE]
      INTEGER          NFOUND            ![REFERENCE]
      INTEGER          IUNIT             ![REFERENCE]
      INTEGER          length_CDSSFI     ![VALUE]
      INTEGER          length_CINSTR     ![VALUE]
C
C
      CALL MAKEDSSCATALOG (CDSSFI(1:length_CDSSFI), IFLTAB,
     +     CINSTR(1:length_CINSTR), NFOUND, IUNIT)
C
      RETURN
      END

      subroutine closescratchdsscatalog_  (unit)

      integer unit ![REFERENCE]

      call CLOSESCRATCHDSSCATALOG (unit)

      return
      end
