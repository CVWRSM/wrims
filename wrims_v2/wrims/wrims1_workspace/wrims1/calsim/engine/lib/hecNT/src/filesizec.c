#include <io.h>
 
void __declspec(dllexport) __stdcall
filesizec (int *ihandle, int *isize)
{
 
    *isize = _filelength (*ihandle);
 
}
