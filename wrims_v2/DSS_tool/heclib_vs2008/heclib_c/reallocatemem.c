#include <stdio.h>
#include <malloc.h>
#include <stdlib.h>

void
reallocatemem_ (int **buff, int *nbytes)
{
	void *b;
	b = (int *)realloc (*buff, *nbytes);
	*buff = b;
fprintf (stderr, "\nEnter reallocatememc, address = %Xh, nbytes = %d\n\n", b, *nbytes); 
}
