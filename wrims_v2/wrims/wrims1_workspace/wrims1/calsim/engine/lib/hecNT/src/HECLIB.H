#ifndef HECLIB_H
#define HECLIB_H
 
 
/*  Microsoft C++ uses stdcall for FORTRAN interface  */
 
#ifdef _MSC_VER
#define VOID void __stdcall
#define INT  int  __stdcall
#else
#define VOID void
#define INT  int
#endif
 
 
extern "C" {
 
VOID attach_ (const int*, char*, char*, char*, char*, int*,
              int, int, int, int);
 
VOID chrlnb_ (const char*, int*, int);
 
VOID curtim_ (int*, int* );
 
VOID datjul_ (const char*, int*, int*, int);
 
INT  fortranclose (const int *unit);
 
INT  fortranopen (const int *unit, const char *fileName, int lenFileName);
 
VOID getime_ (const char *line, const int *beg, const int *len,
              int *juls, int *istime, int *jule, int *ietime, int *status,
              int line_length);
 
INT  idaywk_ (const int *julian);
 
INT  ihm2m_ (const char*, int);
 
INT  inctim_ (const int *interval, const int *dayflag, const int *number,
             const int *julstart, const int *starttime,
             int *julend, int *endtime);
 
INT  isUndefinedDbl (double);
 
INT  iymdjl_ (const int *year, const int *month, const int *day);
 
INT  jliymd_ (const int *julian, int *year, int *month, int *day);
 
VOID juldat_ (const int*, const int*, char*, int*, int);
 
VOID m2ihm_ (const int*, char*, int);
 
INT  nopers_ (const int *interval, const int *flag, const int *juls,
              const int *istime, const int *jule, const int *ietime);
 
VOID systim_ (int*, int* );
 
VOID when_ (char*, char*, int, int);
 
VOID ymddat_ ( const int*, const int*, const int*,
               const  int*, char*, int*, int*, int );
 
 
 
/*  DSS  routines  */
 
VOID zbegdt_ (const int*, const int*, int*, int*, int*, int*, int*);
 
VOID zcheck_ (int *, const char *, const int *, int *, int *, int *, int);
 
VOID zclose_ (int *);
 
VOID zdtype_ (int *, const char *, int *, int *, char *, int *,
              int, int);
 
VOID zfname_ ( const char*, char*, int*, int*, int, int );
 
VOID zgintl_ (int*, char*, int*, int*, int);
 
VOID zgpnp_ (const char*, char*, char*, char*, char*, char*, char*, int*,
                int, int, int, int, int, int, int);
 
VOID zincbk_ (const int*, int*, int*, int*, int*);
 
VOID zinqir_ (int *, char *, char *, int *, int, int);
 
VOID zirbeg_ (const int*, const int*, const char*, int*, int*, int*,
              int*, int*, int*, int);
 
VOID zofset_ (int*, int*, const int*, const int*, int*);
 
VOID zopen_ (int *, const char *, int *, int);
 
VOID zopnca_ (const char *dssName, const int *icunit, const int *lgenca,
              int *lopnca, int *lcatlg, const int *icdunt, const int *lgencd,
              int *lopncd, int *lcatcd, int *nrecs, int lenDssName);
 
VOID zpath_ (const char*, const char*, const char*, const char*,
             const char*, const char*, char*, int*,
                int, int, int, int, int, int, int);
 
VOID zrdpat_ (const int *icunit, int *ipos, int *inumb, char *tag,
              char *path, int *npath, int *lend, int lenTag, int lenPath);
 
VOID zread_ (int*, const char*, const int*, int*, int*,
             int*, int*, const int*, int*, int);
 
VOID zreadx_ (int *ifltab, const char *pathname,
             int *internalHeader, const int *maxNumberInternalHeader,
             int *numberInternalHeader,
             int *compressionHeader, const int *maxNumberCompressionHeader,
             int *numberCompressionHeader,
             int *userHeader, const int *maxNumberUserHeader,
             int *numberUserHeader,
             int *data, const int *maxNumberData, int *numberData,
             const int *iplan, int *found,
             int lengthPathname);
 
 
VOID zrits_ (int* ifltab, const char* cpath, const int* juls,
             const int* istime, const int* jule, const int* ietime,
             int* itimes, float* values, const int* kvals, int *nvals,
             const int* ibdate, char* cunits, char* ctype, int* istat,
             int lenCpath, int lenCunits, int lenCtype);
 
VOID zrpd_ (int*, const char*, int*, int*, int*,
            char*, char*, char*, char*, float*, const int*, int*,
            char*, const int*, int*, int*, int*, int*, int*,
            int, int, int, int, int, int);
 
VOID zrrts_ (int*, const char*, const char*, const char*, const int*, float*,
                char*, char*, int*, int*, int, int, int, int, int);
 
VOID zset_ (const char*, const char*, const int*, int, int);
 
VOID zspd_ (int*, const char*, const int*, const int*, const int*,
            const char*, const char*, const char*, const char*,
            const float*, const char*, const int*, const int*,
            const int*,  const int*, int*,
            int, int, int, int, int, int);
 
VOID zsits_ (int*, const char*, const int*, const float*, const int*,
             const int*, const char*, const char*, const int*, int*,
             int, int, int);
 
VOID zsrts_ (int*, const char*, const char*, const char*, const int*,
             const float*, const char*, const char*, const int*, int*,
             int, int, int, int, int);
 
VOID zstfh_ ( const char*, const char*, const int*,
              int*, const int*, int*, int*,
              int, int );
 
VOID zupath_ (const char* pathname, int*, int*, int*, int* istat,
              int pathnameLength);
 
VOID zustfh_ ( char*, char*, int*, int*, const int*, int*, int*,
               int, int );
 
VOID zwrite_ (int*, const char*, const int*, const int*, const int*,
              const int*, const int*, const int*, int*, int);
 
VOID zwritx_ (int *ifltab , const char *pathname, const int *nPathname,
             const int *internalHeader, const int *numberInternalHeader,
             const int *compressionHeader, const int *numberCompressionHeader,
             const int *userHeader, const int *numberUserHeader,
             const int *data, const int *numberData,
             const int *itype, const int *iplan, int *status, int *found,
             int lengthPathname);
 
VOID opendssoutput_ (const char *fileName, int *status, int *fileNameLength,
                     int length);
 
VOID closedssoutput_ ();
 
};
 
#endif	/* HECLIB_H */
