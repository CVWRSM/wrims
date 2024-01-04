#include <io.h>
 
void __declspec(dllexport) __stdcall 
writfc (int *ihandl, void *buff, int *nbytes, int *istat, int *ntrans)
{
    *ntrans = _write (*ihandl, buff, *nbytes);
    *istat  = ((*ntrans >= 0) ? 0 : -1);
}
