#include <windows.h>
 
void
filesize_ (int *ihandle, int *isize)
{
	DWORD dwFileSize;
	dwFileSize = GetFileSize((HANDLE)*ihandle, NULL);
	*isize = (int)dwFileSize;
}
