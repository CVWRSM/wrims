      SUBROUTINE zreadx_  (ifltab, pathname,
     +  internalHeader, maxNumberInternalHeader, numberInternalHeader,
     +  compressionHeader, maxNumberCompressionHeader,
     +  numberCompressionHeader,
     +  userHeader, maxNumberUserHeader, numberUserHeader,
     +  data, maxNumberData, numberData, iplan, found,
     +  lengthPathname)
C
C
      INTEGER          ifltab            !![REFERENCE]
      CHARACTER        pathname          !![REFERENCE]
      INTEGER          internalHeader    !![REFERENCE]
      INTEGER          maxNumberInternalHeader  !![REFERENCE]
      INTEGER          numberInternalHeader  !![REFERENCE]
      INTEGER          compressionHeader !![REFERENCE]
      INTEGER          maxNumberCompressionHeader  !![REFERENCE]
      INTEGER          numberCompressionHeader  !![REFERENCE]
      INTEGER          userHeader        !![REFERENCE]
      INTEGER          maxNumberUserHeader  !![REFERENCE]
      INTEGER          numberUserHeader  !![REFERENCE]
      INTEGER          data              !![REFERENCE]
      INTEGER          maxNumberData     !![REFERENCE]
      INTEGER          numberData        !![REFERENCE]
      INTEGER          iplan             !![REFERENCE]
      INTEGER          found             !![REFERENCE]
      INTEGER          lengthPathname    !![VALUE]
C
      CALL zreadx (ifltab, pathname(1:lengthPathname),
     +  internalHeader, maxNumberInternalHeader, numberInternalHeader,
     +  compressionHeader, maxNumberCompressionHeader,
     +  numberCompressionHeader,
     +  userHeader, maxNumberUserHeader, numberUserHeader,
     +  data, maxNumberData, numberData, iplan, found)
C
      RETURN
      END
