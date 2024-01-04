#include <stdio.h>
#include <sys/locking.h>
#include <errno.h>
#include <io.h>
 
void __declspec(dllexport) __stdcall
lockdssc (int *ihandle, int *mode, long *position, long *nbytes, int *istat)
{
     long int newPosition;
/*   extern char *sys_errlist[];
     extern int errno;  */
 
     newPosition = _lseek (*ihandle, *position, SEEK_SET);
     if (newPosition < 0) {
        *istat = -1;
        return;
     }
 
/*   Unlock the record */
     if (*mode == 0) {
          *istat = _locking (*ihandle, _LK_UNLCK, *nbytes);
     }
/*   Lock the record.  If already locked, wait until available */
     else if (*mode == 1) {
          *istat = _locking (*ihandle, _LK_LOCK, *nbytes);
     }
/*   Lock the record.  If already locked, return with istat != 0 */
     else if (*mode == 2) {
          *istat = _locking (*ihandle, _LK_NBLCK, *nbytes);
     }
/*   Test to see if the record is already locked */
     else if (*mode == 3) {
          *istat = _locking (*ihandle, _LK_NBLCK, *nbytes);
                   _locking (*ihandle, _LK_UNLCK, *nbytes);
     }
 
/*   if (*istat == -1) printf ("\nError: Lock Failed:  %s\n",
     sys_errlist[errno]);  */
 
}
