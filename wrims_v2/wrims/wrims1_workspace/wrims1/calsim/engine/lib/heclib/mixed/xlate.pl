while (<>) {
    #	s/(\[REFERENCE\])/!$1/;
    #	s/(\[VALUE\])/!$1/;
    #	s/(\[STDCALL\])//;
    #s/!DEC\$/! DEC \$/;
    s/\t/    /g;
    print;
}
