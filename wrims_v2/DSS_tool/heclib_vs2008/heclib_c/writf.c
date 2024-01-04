#include <stdio.h>
#include <windows.h>
 
void
writf_ (int *ihandl, void *buff, int *nbytes, int *istat, int *ntrans)
{

	DWORD dwBytesToWrite, dwBytesWritten;

	dwBytesToWrite = *nbytes;

	WriteFile((HANDLE)*ihandl, 
		      buff,
			  dwBytesToWrite,
			  &dwBytesWritten,
			  NULL);
	
	*ntrans = dwBytesWritten;	      
    *istat  = ((*ntrans >= 0) ? 0 : -1);
   
}
