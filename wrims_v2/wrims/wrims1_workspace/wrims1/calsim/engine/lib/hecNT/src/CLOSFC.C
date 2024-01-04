#include <io.h>
#include <errno.h>
 
void __declspec(dllexport) __stdcall
closfc (int *ihandle, int *istat)
{
    *istat = (_close(*ihandle) != -1 ? 0 : -1);
}
