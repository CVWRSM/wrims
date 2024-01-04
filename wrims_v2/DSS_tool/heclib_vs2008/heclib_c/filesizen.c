#include <sys/types.h>
#include <sys/stat.h>
 
void
filesizen_ (char *name, int *length, int *status, int n)
{
    struct _stat buffer;
 
    *status = _stat (name, &buffer);
 
    if (*status == 0) {
        *length = buffer.st_size;
    }
    else {
        *length = -1;
    }
}


