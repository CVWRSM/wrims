#include <io.h>
#include <errno.h>
 
void
system_(char *func, int len)
{
     system(func);
}
