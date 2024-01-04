#include <stdio.h>
#include <windows.h>

 
/* open a file using "c" i/o, typically from fortran
   set iaccess = 10 (decimal) for most apps.
   istat is returned -1 if error, otherwise 0 */

void 
openf_ (char *cname, int *iaccess, int *ihandl, int *istat, int len_cname)
{
	HANDLE hFile;

	if (*iaccess == 10) {
		//  open in read / write mode
		hFile = CreateFile (cname, 
							GENERIC_READ | GENERIC_WRITE,
							FILE_SHARE_READ | FILE_SHARE_WRITE,
							NULL,
							OPEN_ALWAYS,
							FILE_ATTRIBUTE_NORMAL,
							NULL);
	}
	else {
		//  Open in read only mode
		hFile = CreateFile (cname, 
							GENERIC_READ,
							FILE_SHARE_READ | FILE_SHARE_WRITE,
							NULL,
							OPEN_ALWAYS,
							FILE_ATTRIBUTE_NORMAL,
							NULL);
	}
      	  
	*ihandl = (int)hFile;
	*istat  = ((hFile != INVALID_HANDLE_VALUE) ? 0 : 1);
}
