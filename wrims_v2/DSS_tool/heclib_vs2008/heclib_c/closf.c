#include <stdio.h>
#include <windows.h>
 
void
closf_ (int *ihandle, int *istat)
{
    *istat = (CloseHandle((HANDLE)*ihandle) != -1 ? 0 : -1);
}
