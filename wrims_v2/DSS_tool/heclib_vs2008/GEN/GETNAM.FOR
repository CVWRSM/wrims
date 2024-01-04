      SUBROUTINE GETNAM (ILFN,CNAME,IFLAG)
C
C       THIS ROUTINE COPIED FROM QL=HECC ON 21 MAY 85 FOR USE
C       WITH MODIFICATIONS TO PREAD FUNCTION FILE ASSIGNMENTS
C       BY DON HANSEN.  ( HECC IS A TEMPORARY REPOSITORY OF
C       CHARACTER MANIPULATIONS ROUTINES BEING WRITTEN AT THIS
C       TIME.
C                         DENNIS HUFF 22MAY85
C
C     INTEGER NAME1(6)                                                  
      CHARACTER CNAME*(*)
C     CHARACTER CNAME1*18                                               
C     EQUIVALENCE (NAME1,CNAME1)                                        
C
C
C     NOTE: INQUIRE DOES NOT WORK WELL ON HARRIS
      CNAME = ' '
      INQUIRE (UNIT=ILFN,NAME=CNAME,IOSTAT=IFLAG)                       
C
C     IF ((ILFN.EQ.5).OR.(ILFN.EQ.6)) THEN                              
C     IF ((CNAME(1:2).EQ.'  ').AND.(IFLAG.EQ.0)) CNAME = '/dev/tty'     
C     IF (CNAME(1:3).EQ.'std') CNAME = '/dev/tty'                       
C     ENDIF                                                             
C
C     CALL LFNAME (ILFN,NAME1,IFLAG)                                    
C
C     IF (IFLAG.EQ.-1)THEN                                              
      RETURN
C     ELSE IF(IFLAG.EQ.1) THEN                                          
C     IFLAG=NAME1(1)                                                    
C     RETURN                                                            
C     ENDIF                                                             
C
C     CNAME(1:8) = CNAME1(10:17)                                        
C     CALL CHRLNB (CNAME(1:8),IPOS)                                     
C     IPOS = IPOS + 1                                                   
C     CNAME(IPOS:IPOS) = '*'                                            
C     IPOS = IPOS + 1                                                   
C     JPOS = IPOS + 7                                                   
C     CNAME(IPOS:JPOS) = CNAME1(1:8)                                    
C     IF (JPOS.LT.17) THEN                                              
C     JPOS = JPOS + 1                                                   
C     CNAME(JPOS:17) = ' '                                              
C     ENDIF                                                             
C
C     RETURN                                                            
      END
