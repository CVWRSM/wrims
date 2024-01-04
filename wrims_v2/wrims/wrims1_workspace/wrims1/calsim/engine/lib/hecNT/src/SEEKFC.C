#include <io.h>
#include <stdio.h>

void __declspec(dllexport)  __stdcall
seekfc (int *ihandl, int *iorigin, long *iofset, int *ipos, int *istat)
{
    *ipos = _lseek(*ihandl, *iofset, *iorigin);
    *istat = ((*ipos == -1) ? -1 : 0);
}
