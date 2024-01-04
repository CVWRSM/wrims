      SUBROUTINE zwritx_  (ifltab, pathname, nPathname,
     +  internalHeader, numberInternalHeader,
     +  compressionHeader, numberCompressionHeader,
     +  userHeader, numberUserHeader,
     +  data, numberData, itype, iplan, status, found,
     +  lengthPathname)
C
C
      INTEGER          ifltab            !![REFERENCE]
      CHARACTER        pathname          !![REFERENCE]
      INTEGER          nPathname         !![REFERENCE]
      INTEGER          internalHeader    !![REFERENCE]
      INTEGER          numberInternalHeader  !![REFERENCE]
      INTEGER          compressionHeader !![REFERENCE]
      INTEGER          numberCompressionHeader  !![REFERENCE]
      INTEGER          userHeader        !![REFERENCE]
      INTEGER          numberUserHeader  !![REFERENCE]
      INTEGER          data              !![REFERENCE]
      INTEGER          numberData        !![REFERENCE]
      INTEGER          itype             !![REFERENCE]
      INTEGER          iplan             !![REFERENCE]
      INTEGER          status            !![REFERENCE]
      INTEGER          found             !![REFERENCE]
      INTEGER          lengthPathname    !![VALUE]
C
      CALL zwritx (ifltab, pathname(1:lengthPathname),  nPathname,
     +  internalHeader, numberInternalHeader,
     +  compressionHeader, numberCompressionHeader,
     +  userHeader, numberUserHeader,
     +  data, numberData, itype, iplan, status, found)
C
      RETURN
      END
