C
      SUBROUTINE closf (ihandle, status)
      INTERFACE
         SUBROUTINE closfc (ihandle, status)
         INTEGER ihandle   ![REFERENCE]
      INTEGER status    ![REFERENCE]
      end subroutine closfc
      end interface
      dll_import closfc
C
      INTEGER ihandle, status
C
      CALL closfc (ihandle, status)
C
      Return
      End
C
      SUBROUTINE filesize (ihandle, isize)
      INTERFACE
      subroutine filesizec (ihandle,isize)
      INTEGER ihandle
      INTEGER isize
      end subroutine filesizec
      END INTERFACE
      dll_import filesizec
C
C
        INTEGER ihandle, isize
C
        CALL filesizec (ihandle, isize)
C
        Return
        End
C
      SUBROUTINE flushf (ihandle, status)
      INTERFACE
         SUBROUTINE flushfc (ihandle, status)
         INTEGER ihandle   ![REFERENCE]
         INTEGER status    ![REFERENCE]
      end subroutine flushfc
      end interface
      dll_import flushfc
C
C
      INTEGER ihandle, status
C
      CALL flushfc (ihandle, status)
C
      Return
      End
C
      SUBROUTINE lockdss (ihandle, mode, position, nbytes, status)
      INTERFACE
         SUBROUTINE lockdssc (ihandle, mode, position,
     *        nbytes, status)
	 INTEGER ihandle   ![REFERENCE]
	 INTEGER mode      ![REFERENCE]
	 INTEGER position  ![REFERENCE]
	 INTEGER nbytes    ![REFERENCE]
	 INTEGER status    ![REFERENCE]
      end subroutine lockdssc
      end interface
      dll_import lockdssc
C
C
      INTEGER ihandle, mode, position, nbytes, status
C
      CALL lockdssc (ihandle, mode, position, nbytes, status)
C
      Return
      End
C
      SUBROUTINE openf (cname, access, handle, status)
      INTERFACE
         SUBROUTINE openfc (cname, access, handle, status)
         CHARACTER cname    ![REFERENCE]
	 INTEGER   access   ![REFERENCE]
	 INTEGER   handle   ![REFERENCE]
	 INTEGER   status   ![REFERENCE]
      end subroutine openfc
      end interface
      dll_import openfc
C
C
      CHARACTER(len=*) cname
      character(len=132) cnameWithNull
      INTEGER access, handle, status
C
      cnameWithNull=cname
      i=min(len(cname),len_trim(cname)+1,132)
      cnameWithNull(i:i) = char(0)
      CALL openfc (cnameWithNull, access, handle, status)
C
      Return
      End
C
      SUBROUTINE readf (ihandle, buff, nbytes, status, ntrans)
      INTERFACE
         SUBROUTINE readfc (ihandle, buff, nbytes, 
     *        status, ntrans)
	 INTEGER ihandle   ![REFERENCE]
	 INTEGER buff      ![REFERENCE]
	 INTEGER nbytes    ![REFERENCE]
	 INTEGER status    ![REFERENCE]
	 INTEGER ntrans    ![REFERENCE]
      end subroutine readfc
      end interface
      dll_import readfc
C
C
      INTEGER ihandle, buff, nbytes, status, ntrans
C
      CALL readfc (ihandle, buff, nbytes, status, ntrans)
C
      Return
      End
C
      SUBROUTINE seekf (ihandle, origin, offset, ipos, status)
      INTERFACE 
      SUBROUTINE seekfc (ihandle, origin, offset,
     *        ipos, status)
	 INTEGER ihandle   ![REFERENCE]
	 INTEGER origin    ![REFERENCE]
	 INTEGER offset    ![REFERENCE]
	 INTEGER ipos      ![REFERENCE]
	 INTEGER status    ![REFERENCE]
      END subroutine seekfc
      end interface
      dll_import seekfc
C
C
      INTEGER ihandle, origin, offset, ipos, status
C
      CALL seekfc (ihandle, origin, offset, ipos, status)
C
      Return
      End
C
      SUBROUTINE writf (ihandle, buff, nbytes, status, ntrans)
      INTERFACE 
         SUBROUTINE writfc (ihandle, buff, nbytes,
     *                                    status, ntrans)
	 INTEGER ihandle   ![REFERENCE]
	 INTEGER buff      ![REFERENCE]
	 INTEGER nbytes    ![REFERENCE]
	 INTEGER status    ![REFERENCE]
	 INTEGER ntrans    ![REFERENCE]
      end subroutine writfc
      END interface
      dll_import writfc
C
C
      INTEGER ihandle, buff, nbytes, status, ntrans
C
      CALL writfc (ihandle, buff, nbytes, status, ntrans)
C
      Return
      End
