c This file contains interfaces that will help compile the
c hec-dss library so that the HEC routines are accessible
c with the Intel compiler default naming conventions
c 
c This file is not part of the HEC Intel distribution. It
c contains routines added by the Delta Modeling Section,
c Department of Water Resources, California
c Copyright 2007 California Department of Water Resources
        
        
        SUBROUTINE openf (cname, access, handle, status)
C

        INTERFACE
          SUBROUTINE openfcc(cname,access,handle,status,l_cname)
          !DEC$ATTRIBUTES STDCALL,ALIAS:'_openf_@20' :: openfcc
          CHARACTER(LEN=l_cname) cname [REFERENCE]
          INTEGER l_cname [VALUE]
	    INTEGER access [REFERENCE]
	    INTEGER handle [REFERENCE]
	    INTEGER status [REFERENCE]
        END SUBROUTINE
        END INTERFACE
        CHARACTER*(*) cname
	  INTEGER access, handle, status
C
        CALL openfcc(cname, access, handle, status, LEN(cname))
C
        Return
        End        
        
      SUBROUTINE writf (ihandle, buff, nbytes, status, ntrans)
      INTERFACE
      SUBROUTINE writfcc(ihandle, buff, nbytes, status, ntrans)
      !DEC$ATTRIBUTES STDCALL, ALIAS: '_writf_@20' :: writfcc
      INTEGER ihandle [REFERENCE]
      INTEGER buff [REFERENCE]
      INTEGER nbytes [REFERENCE]
      INTEGER status [REFERENCE]
      INTEGER ntrans [REFERENCE]
      END SUBROUTINE
      END INTERFACE
C
      INTEGER ihandle, buff, nbytes, status, ntrans
C
      CALL writfcc (ihandle, buff, nbytes, status, ntrans)
C
      Return
      End        
      
      
      SUBROUTINE closf (ihandle, status)
      INTERFACE
      SUBROUTINE closfcc(ihandle,status)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_closf_@8' :: closfcc
      INTEGER ihandle [REFERENCE]
      INTEGER status [REFERENCE]
      END SUBROUTINE
      END INTERFACE
C
      INTEGER ihandle, status
C
      CALL closfcc (ihandle, status)
C
      Return
      End
      
      
      SUBROUTINE readf (ihandle, buff, nbytes, status, ntrans)
C     
      INTERFACE
      SUBROUTINE readfcc(ihandle, buff, nbytes, status, ntrans)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_readf_@20' :: readfcc
      INTEGER ihandle [REFERENCE]
      INTEGER buff [REFERENCE]
      INTEGER nbytes [REFERENCE]
      INTEGER status [REFERENCE]
      INTEGER ntrans [REFERENCE]
      END SUBROUTINE
      END INTERFACE     
      INTEGER ihandle, buff, nbytes, status, ntrans
C
      CALL readfcc (ihandle, buff, nbytes, status, ntrans)
C
      Return
      End
      
      SUBROUTINE flushf(ihandle, status)
      INTERFACE
      SUBROUTINE flushfcc(ihandle,status)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_flushf_@8' ::flushfcc
      INTEGER ihandle [REFERENCE]
      INTEGER status [REFERENCE]
      END SUBROUTINE
      END INTERFACE  
      
C
      INTEGER ihandle, status
C
      CALL flushfcc (ihandle, status)
C
      Return
      End   
      
      SUBROUTINE filesize(ihandle, isize)
      INTERFACE
      SUBROUTINE filesizecc(ihandle,status)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_filesize_@8' :: filesizecc
      INTEGER ihandle [REFERENCE]
      INTEGER status [REFERENCE]
      END SUBROUTINE
      END INTERFACE        

C
      INTEGER ihandle, isize
C
      CALL filesizecc (ihandle, isize)
C
      Return
      End
      
      SUBROUTINE lockdss(ihandle, mode, position, nbytes, status)
      INTERFACE 
      SUBROUTINE lockdsscc (ihandle, mode, position, nbytes, status)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_lockdss_@20' :: lockdsscc
      INTEGER ihandle [REFERENCE]
      INTEGER mode    [REFERENCE]
      INTEGER position [REFERENCE]
      INTEGER nbytes   [REFERENCE]
      INTEGER status   [REFERENCE]                        
      END SUBROUTINE
      END INTERFACE        
      
C
      INTEGER ihandle, mode, position, nbytes, status
C
      CALL lockdsscc (ihandle, mode, position, nbytes, status)

C
      Return
      End
      
      
      SUBROUTINE seekf(ihandle, origin, offset, ipos, status)

      INTERFACE 
      SUBROUTINE seekfcc (ihandle, origin, offset, ipos, status)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_seekf_@20' :: seekfcc
      INTEGER ihandle [REFERENCE]
      INTEGER origin    [REFERENCE]
      INTEGER offset [REFERENCE]
      INTEGER ipos   [REFERENCE]
      INTEGER status   [REFERENCE]                        
      END SUBROUTINE
      END INTERFACE       
      
C
      INTEGER ihandle, origin, offset, ipos, status
C
      CALL seekfcc (ihandle, origin, offset, ipos, status)
C
      Return
      End
      
      


      SUBROUTINE writemem(membuff, wordpos, intbuff, nwords)
      INTERFACE
      SUBROUTINE writmemcc (membuff, wordpos, intbuff, nwords)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_writemem_@16' :: writmemcc
      INTEGER membuff [REFERENCE]
      INTEGER wordpos   [REFERENCE]
      INTEGER intbuff [REFERENCE]
      INTEGER nwords   [REFERENCE]                       
      END SUBROUTINE
      END INTERFACE         
C
      INTEGER membuff, wordpos, intbuff, nwords
C
      CALL writmemcc (membuff, wordpos, intbuff, nwords)
C
      Return
      End
      
      

      
      SUBROUTINE readmem(membuff, wordpos, intbuff, nwords)
      INTERFACE
      SUBROUTINE readmemcc (membuff, wordpos, intbuff, nwords)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_readmem_@16' :: readmemcc
      INTEGER membuff [REFERENCE]
      INTEGER wordpos   [REFERENCE]
      INTEGER intbuff [REFERENCE]
      INTEGER nwords   [REFERENCE]                       
      END SUBROUTINE
      END INTERFACE         
C
      INTEGER membuff, wordpos, intbuff, nwords
C
      CALL readmemcc (membuff, wordpos, intbuff, nwords)
C
      Return
      End
      
      SUBROUTINE reallocatemem(buff, nbytes)
      INTERFACE
      SUBROUTINE ramcc (buff, nbytes)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_reallocatemem_@8' ::ramcc
      INTEGER buff [REFERENCE]
      INTEGER nbytes [REFERENCE]
      END SUBROUTINE
      END INTERFACE     
C
      INTEGER buff, nbytes
C
      CALL ramcc(buff, nbytes)

C
      Return
      End


      SUBROUTINE filesizen(name, length, status)
      INTERFACE
      SUBROUTINE filesizencc (name,length, status,l_name)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_filesizen_@16' :: filesizencc
      CHARACTER(LEN=l_name) name [REFERENCE]
      INTEGER l_name [VALUE]
	INTEGER length [REFERENCE]
	INTEGER status [REFERENCE]
      END SUBROUTINE
      END INTERFACE       
      
C
      CHARACTER name*(*)
      INTEGER  length
      INTEGER  status
C
      CALL filesizencc (name,length, status,LEN(name))
C
      RETURN
      END      
      
      
      SUBROUTINE isonlocaldrive(cfilename, islocal)
      INTERFACE
      SUBROUTINE ioldcc (cfilename, islocal, l_name)
       !DEC$ATTRIBUTES STDCALL,ALIAS:'_isonlocaldrive_@12':: ioldcc
      CHARACTER(LEN=l_name) cfilename [REFERENCE]
      INTEGER l_name [VALUE]
	INTEGER islocal [REFERENCE]
      END SUBROUTINE
      END INTERFACE   
C
      CHARACTER cfilename
	INTEGER islocal
C
      CALL ioldcc (cfilename, islocal,LEN(cfilename))
C
      Return
      End
      
      
      SUBROUTINE sortfilesinterface(fileIn, fileOut, status)
      INTERFACE
       SUBROUTINE sfifcc(fileIn,fileOut,status,l_filein,
     *                                 l_fileout)
      !DEC$ATTRIBUTES STDCALL,ALIAS:'_sortfilesinterface_@20'::sfifcc
        CHARACTER(LEN=l_filein) fileIn [REFERENCE]
        INTEGER l_filein [VALUE]
        CHARACTER(LEN=l_fileout) fileOut [REFERENCE]
        INTEGER l_fileout [VALUE]
	INTEGER status [REFERENCE]
       END SUBROUTINE
      END INTERFACE  
      CHARACTER fileIn*(*)
      CHARACTER fileOut*(*)
      INTEGER status
      CALL sfifcc(fileIn,fileOut,status,
     *           LEN(fileIn),LEN(fileOut))
      RETURN
      END      
