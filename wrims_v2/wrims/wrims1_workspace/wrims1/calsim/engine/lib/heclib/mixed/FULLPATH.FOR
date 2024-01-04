      SUBROUTINE fullpathname_  (filename, fullpath,
     +   numberFullpath, length_filename, length_fullpath)
C
C
      CHARACTER        filename          ![REFERENCE]
      CHARACTER        fullpath          ![REFERENCE]
      INTEGER          numberFullpath    ![REFERENCE]
      INTEGER          length_filename   ![VALUE]
      INTEGER          length_fullpath   ![VALUE]
C
      CALL FULLPATHNAME (filename(1:length_filename),
     +   fullpath(1:length_fullpath), numberFullpath)
C
      RETURN
      END
