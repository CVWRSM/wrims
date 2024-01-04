#include <windows.h>
 
void
flushf_ (int *ihandle, int *istat)
{
	boolean b;
    b = FlushFileBuffers ((HANDLE)*ihandle);
	if (b) {
		*istat = 0;
	}
	else {
		*istat = -1;
	}
}
