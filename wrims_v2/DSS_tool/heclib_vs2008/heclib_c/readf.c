#include <stdio.h>
#include <windows.h>
#include <errno.h>
 
void
readf_ (int *ihandl, void *buff, int *nbytes, int *istat, int *ntrans)
{
	DWORD dwBytesRead, dwBytesRequested;

	dwBytesRequested = *nbytes;

	ReadFile((HANDLE)*ihandl, 
		      buff,
			  dwBytesRequested,
			  &dwBytesRead,
			  NULL);
	
	*ntrans = dwBytesRead;	      
    *istat  = ((*ntrans >= 0) ? 0 : -1);
	/*if (*istat)
		*istat = errno; */
}
