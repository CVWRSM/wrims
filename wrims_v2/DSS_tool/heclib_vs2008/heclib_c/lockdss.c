#include <stdio.h>
#include <windows.h>
#include <io.h>
 
extern void
lockdss_ (int *ihandle, int *mode, long *position, long *nbytes, int *istat)
{
	int status;
 
/*   Unlock the record */
     if (*mode == 0) {
          status = UnlockFile((HANDLE)*ihandle, *position, 0, *nbytes, 0);
     }
/*   Lock the record.  If already locked, wait until available */
     else if (*mode == 1) {
		short i;
		int err;
		for(i = 0; i < 50; i++) {
			if(status = LockFile((HANDLE)*ihandle, *position, 0, *nbytes, 0)) {				
				break;
			}
			Sleep(100);
		}
		// timeout
		err = GetLastError();
		printf("\nUnable to Lock file, status = %d\n", err);		

     }
/*   Lock the record.  If already locked, return with istat != 0 */
     else if (*mode == 2) {		 		 
		 status = LockFile((HANDLE)*ihandle, *position, 0, *nbytes, 0);		 
		 /* err = GetLastError();
		 printf("\n\nlock status = %d\nLock error = %d\n", stat, err); */
         
		 
	 }
/*   Test to see if the record is already locked */
     else if (*mode == 3) {
          status = LockFile((HANDLE)*ihandle, *position, 0, *nbytes, 0);
		   if((status)) UnlockFile((HANDLE)*ihandle, *position, 0, *nbytes, 0);

     }
	 *istat = status ? 0 : -1;
/*printf("\nEnter lockdssc, handle = %d, mode = %d, istat = %d, status = %d\n", *ihandle, *mode, *istat, status); */
/*   if (*istat == -1) printf ("\nError: Lock Failed:  %s\n",
     sys_errlist[errno]);  */
 
}
