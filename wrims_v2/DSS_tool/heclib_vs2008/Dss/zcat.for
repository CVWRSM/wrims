      SUBROUTINE ZCAT (IFLTAB, ICUNIT, ICDUNT, INUNIT, CINSTR,
     *  LABREV, LDOSRT, LCDCAT, NORECS)
C
C     Z - CATALOG,  Generates a listing of record pathnames in a
C     DSS File.  This subroutine will sort the pathnames on Harris
C     Computers (takes significantly longer), or Selectively chose
C     pathnames based on their pathname parts.
C
C     Written by Bill Charley at HEC, 1982.
C
C
      CHARACTER CINSTR*(*)
      INTEGER IMXPRT(6)
      INTEGER IFLTAB(*)
      LOGICAL LABREV, LDOSRT, LSELCA, LCDCAT
C
      INTEGER JORDER(6)
      CHARACTER CSCRAT*392                                              Md
      CHARACTER CINST*392, CSCRAT2*392
      CHARACTER CSORTIN*392, CSORTOUT*392, CSORTMP*392
      CHARACTER CNAME*392, CDNAME*392
      LOGICAL LERR
C
      INCLUDE 'zdsskz.h'
C
      INCLUDE 'zdssca.h'
C
      INCLUDE 'zdsscc.h'
C
      INCLUDE 'zdsscm.h'
C
      INCLUDE 'zdssmz.h'
C
C
C     IFIELD are the fields to sort with.
C     (e.g., IFIELD 1 and 2 are the starting and ending
C     positions of the first part to sort.
C
C
C
      CINST = CINSTR
      CALL CHRLNB (CINST, NINST)  
      CALL UPCASE (CINST)
C
      IF (MLEVEL.GE.11) THEN
      N = NINST
      IF (N.EQ.0) N = 1
      WRITE (MUNIT,20) ICUNIT, ICDUNT, INUNIT, LABREV, LDOSRT, LCDCAT,
     * NINST, CINST(1:N)
 20   FORMAT (T6,'-----DSS---Debug:  Enter ZCAT'/,
     * T11,'ICUNIT:',I4,',  ICDUNT:',I4,',  INUNIT:',I4,/,
     * T11,'LABREV:',L4,',  LDOSRT:',L4,',  LCDCAT:',L4,/,
     * T11,'NINST: ',I4,',  Instructions: ',A)
      ENDIF
C
C
C
C     Check that IFLTAB is valid (e.g., the DSS file is open)
      IF (IFLTAB(1).NE.6) CALL ZERROR (IFLTAB, 5, 'ZCAT',
     * 0, IFLTAB, ' ', 0, ' ',0)
C
C
C     Find out how many records are in the file
      NUMREC = IFLTAB(KNRECS)
C     If no records in the file, error out.
      IF (NUMREC.EQ.0) THEN
      NORECS = 0
      GO TO 900
      ENDIF
   
C
C
      CNAME = ' '
      CDNAME = ' '
      CSINDEX = ' '
      NSINDEX = 0
      LEXTND = .TRUE.
      LSORT = .TRUE.
      IF (LABREV) LEXTND = .FALSE.
      IF (.NOT.LDOSRT) LSORT = .FALSE.
      NOPTHS = 0
      JCUNIT = ICUNIT
      JNUNIT = INUNIT
      IF (INUNIT.NE.0) THEN
      REWIND INUNIT
      LEXTND = .FALSE.
      LSORT = .FALSE.
      ENDIF
      LSELCA = .FALSE.
           
C
C     **** NON-SORT Installed Sites:  No Sort Flag ****
C     LSORT = .FALSE.
C     *************************************************
C
C
C     Determine the length of the maximum pathname and the sort file
      MTOTAL = 0
      DO 40 I=1,6
C     Get the maximum length of each part.  If less than 4, set to 4.
      CALL GETHOL (IFLTAB(KMXPRT), I, IMXPRT(I))
      IF (IMXPRT(I).LT.4) IMXPRT(I) = 4
C     Time series records must have a minimum of 6 for the D and E parts
      IF (((I.EQ.4).OR.(I.EQ.5)).AND.(IMXPRT(I).LT.6)) IMXPRT(I) = 6
      MTOTAL = MTOTAL + IMXPRT(I)
 40   CONTINUE
C
C
      ISUNIT(1) = 66
      ISUNIT(2) = 67
      ISUNIT(3) = 68
C
C
C     Make an exclusive assignment, so that
C     no one else can access the catalog while we are writing
C     to it, and so that we will know that no one is
C     currently using it.
      IF (INUNIT.EQ.0) THEN                                             Md
      CALL GETNAM ( ICUNIT, CNAME, IERR)                                Md
      IF (IERR.EQ.-1) GO TO 920                                         Md
      IF (IERR.EQ.0) THEN                                               Md
      CLOSE (UNIT=ICUNIT)                                               Md
      CALL CHRLNB (CNAME, ILAST)                                        Md
      OPEN ( UNIT=ICUNIT, FILE=CNAME(1:ILAST), IOSTAT=JERR,             Md
     * SHARE='DENYWR')                                                  Md
      IF (JERR.NE.0) GO TO 930                                          Md
      ENDIF                                                             Md
      ENDIF                                                             Md
C
C     Determine if a condensed version of the catalog is to be produced
      IF (LSORT.AND.(ICDUNT.GT.0)) THEN
      LCDCAT = .TRUE.
      ELSE
      LCDCAT = .FALSE.
      ENDIF
C
C
C     Initialize Variables
      DO 60 I=1,6
      JORDER(I) = 0
 60   CONTINUE
C     Default Order of pathname parts for sorting
      IORDER(1) = 1
      IORDER(2) = 2
      IORDER(3) = 3
      IORDER(4) = 6
      IORDER(5) = 5
      IORDER(6) = 4
C
C
C     Decipher any Instructions passed in:
C        Is a sort order specified (e.g., O=CB)
C        Is a Selective Catalog Deisired (e.g., C=FLOW)
C
      IF (LSORT) THEN
C
      IF (NINST.GT.0) THEN
C
C     Check for a sort order specified
      IPOS = INDEX (CINST, 'O=')
      IF (IPOS.NE.0) THEN
C     Yes, a sort order was given.  Deactivate the condensed version
      LCDCAT = .FALSE.
C     Blank O= for catalog title
      CINST(IPOS:IPOS+1) = '   '
      IPOS = IPOS + 2
      IF (IPOS+2.LE.NINST) THEN
C     Look for 'OFF' as a sort parameter
      IF (CINST(IPOS:IPOS+2).EQ.'OFF') THEN
      CINST(IPOS:IPOS+2) = '   '
      LSORT = .FALSE.
      GO TO 300
      ENDIF
      ENDIF
C
C     Get the sort order
      DO 100 I=1,6
      M = INDEX ( 'ABCDEF', CINST(IPOS:IPOS))
      IF (M.EQ.0) GO TO 120
      JPOS = I
      JORDER(I) = M
      CINST(IPOS:IPOS) = ' '
      IPOS = IPOS + 1
      IF (IPOS.GT.NINST) GO TO 120
 100  CONTINUE
C
C     Fill in any remainding order not specified
C     First remove from IORDER any parts given by zeroing them out.
 120  CONTINUE
      IF (JPOS.GT.0) THEN
      DO 140 I=1,JPOS
      DO 140 J=1,6
      IF (JORDER(I).EQ.IORDER(J)) IORDER(J) = 0
 140  CONTINUE
      ENDIF
C
C     Now fill in any remaining parts
      JPOS = JPOS + 1
      IF (JPOS.LE.6) THEN
      DO 180 I=JPOS,6
      DO 160 J=1,6
C     Has this part been specified yet
      IF (IORDER(J).GT.0) THEN
C     No - Use it then zero it out.
      JORDER(I) = IORDER(J)
      IORDER(J) = 0
      GO TO 180
      ENDIF
 160  CONTINUE
 180  CONTINUE
      ENDIF
C
C     COPY BACK COMPLETED ORDER TO IORDER
      DO 190 I=1,6
      IORDER(I) = JORDER(I)
 190  CONTINUE
C
      ENDIF
      ENDIF
C
C
C
C     Open the sort input file.  This file will contain the pathname
C     parts (in sort order), followed by an integer record number.
C     That record number corresponds to a catalog line in DSSSORT.TMP
      CALL TEMPNAME (CSORTIN, ISUNIT(1))
      OPEN (UNIT=ISUNIT(1), FILE=CSORTIN, IOSTAT=IERR)
      IF (IERR.NE.0) THEN
      CALL CHRLNB(CSORTIN,N)
      WRITE (MUNIT, 200) IERR, ISUNIT(1), CSORTIN(1:N)
 200  FORMAT(' *** ERROR - ZCAT:  Unable to Access Needed Scratch',
     * ' Files for Sort ***',/,' The Catalog is NOT Sorted;  Error:',I5,
     * /,' Unit:',I5,'  File: ',A,/)
      LSORT = .FALSE.
      ENDIF
C
C     This file will contain the catalog lines.  Once the file has been
C     sorted, the catalog lines will be read from this file to create
C     the final catalog.
      CALL TEMPNAME (CSORTMP, ISUNIT(3))
      OPEN (UNIT=ISUNIT(3), FILE=CSORTMP, ACCESS='DIRECT',
     * RECL=400, IOSTAT=IERR)
      IF (IERR.NE.0) THEN
      CALL CHRLNB(CSORTMP, N)
      WRITE (MUNIT, 200) IERR, ISUNIT(3), CSORTMP(1:N)
      LSORT = .FALSE.
      ENDIF
C
      CALL TEMPNAME (CSORTOUT, ISUNIT(2))
C
C     Get the beginning and ending locations of the maximum parts
C     This is so we can construct a minimum size sort input file
C     containing the A part followed by the B part, etc.
      IBPMAX(1) = 1
      J = IORDER(1)
      IEPMAX(1) =  IMXPRT(J)
      DO 260 I=2,6
      IBPMAX(I) = IEPMAX(I-1) + 1
      J = IORDER(I)
      IEPMAX(I) = IBPMAX(I) + IMXPRT(J) - 1
 260  CONTINUE
C
      ENDIF
C
C
C     Look for Selective Catalog Parameters
 300  CONTINUE
      IF (NINST.GT.0) THEN
      CALL ZSETCA (CINST, LSELCA)
      IF (ILWFLG.EQ.-1) GO TO 960
      ENDIF
C
C
      IF (.NOT.LSORT) LCDCAT = .FALSE.
C
      IF (LCDCAT) THEN
C     Initialize MAXPRT
      DO 320 I=1,6
      MAXPRT(I) = 6
 320  CONTINUE
      MAXPRT(7) = 3
      CALL GETNAM ( ICDUNT, CDNAME, IERR)                               Md
      IF (IERR.EQ.-1) GO TO 920                                         Md
      IF (IERR.EQ.0) THEN                                               Md
      CLOSE (UNIT=ICDUNT)                                               Md
      CALL CHRLNB (CDNAME, ILAST)                                       Md
      OPEN ( UNIT=ICDUNT, FILE=CDNAME(1:ILAST), IOSTAT=JERR,            Md
     * SHARE='DENYWR')                                                  Md
      IF (JERR.NE.0) GO TO 930                                          Md
      ENDIF                                                             Md
      ENDIF
C
C
C     Write the title to the catalog
      CALL ZCATIT (IFLTAB, ICUNIT, LSELCA, .FALSE., CINST, LERR)
      IF (LERR) GO TO 910
C
      IF (INUNIT.EQ.0) THEN
C     Now obtain a list of the pathnames from the DSS File
      CALL ZCATFI (IFLTAB, LEXTND, LSELCA, LCDCAT, .FALSE., LERR)
      ELSE
C     Obtain a list of pathnames from the current catalog file
      CALL ZORDPN (LSELCA, LERR)
      ENDIF
      IF (LERR) GO TO 800
C
      NORECS = NOPTHS
C
C     Sorting Software interface
C     This sort uses the Harris Computer Utility sort software
      IF (LSORT) THEN
C
      IF (MLEVEL.GT.2) WRITE (MUNIT, *)'Sorting...'
C
      ENDFILE ISUNIT(1)
      CLOSE (UNIT=ISUNIT(1))
C
      CALL CHRLNB (CSORTIN, N)
      CALL CHRLNB (CSORTOUT, J)
      CSORTIN(N+1:N+1) = CHAR(0)
      CSORTOUT(J+1:J+1) = CHAR(0)
      JERR = 0
C
C     CSCRAT2 = 'sort <' // CSORTIN(1:N) // ' >' // CSORTOUT(1:J) //    u
C    * CHAR(0)                                                          u
C     CALL system (CSCRAT2)                                             u
C
      CALL sortfilesfort (CSORTIN, CSORTOUT, JERR)                      M
C     CALL sortfilesinterface (CSORTIN, CSORTOUT, JERR)                 d
C
      IF (JERR.EQ.0) THEN
         CALL filesizen (CSORTIN, ISIZE, IERR)
         CALL filesizen (CSORTOUT, JSIZE, JERR)
         IF (ISIZE.GT.JSIZE) JERR = -1
      ENDIF
C
C     Check to see if the sort occurred (was there an error?)
      IF (JERR.EQ.0) THEN
         OPEN (UNIT=ISUNIT(2), FILE=CSORTOUT, STATUS='OLD',
     *   IOSTAT=IERR)
      ELSE
         IERR = -2
      ENDIF
C
 340  CONTINUE
      IF (IERR.NE.0) THEN
      IF (MLEVEL.GE.1) WRITE (MUNIT, 400) IERR
 400  FORMAT (' ZCAT:  Unable to Sort Catalog,  Error:',I6,/)
      LCDCAT = .FALSE.
      OPEN (UNIT=ISUNIT(2), FILE=CSORTIN, STATUS='OLD',
     * IOSTAT=IERR)
      IF (IERR.NE.0) GO TO 910
C     Rewrite the title to the catalog
      REWIND (ICUNIT)
      LSORT = .FALSE.
      CALL ZCATIT (IFLTAB, ICUNIT, LSELCA, .FALSE., CINST, LERR)
      LSORT = .TRUE.
      IF (LERR) GO TO 910
      ELSE
      OPEN (UNIT=ISUNIT(1), FILE=CSORTIN, IOSTAT=IERR)
      ENDIF
C
C
      IF (LCDCAT) CALL ZCATIT (IFLTAB, ICDUNT,
     * LSELCA, .TRUE., CINST, LERR)
C
      CALL ZCAOUT (IFLTAB, ICUNIT, ICDUNT, LCDCAT, NORECS)
C
C
C     DONE!
      GO TO 740
      ENDIF
C
C     Reached the end of file without getting all the pathnames
      IF ((LSORT).AND.(MLEVEL.GE.1)) WRITE (MUNIT, 720)
 720  FORMAT (' WARNING:  Incomplete Catalog (Insufficient Disk'
     * ' Space to Complete Sorted Catalog)')
C
 740  CONTINUE
      IF (LDOSRT) CLOSE (UNIT=ISUNIT(1), STATUS='DELETE')
      IF (LDOSRT) CLOSE (UNIT=ISUNIT(2), STATUS='DELETE')
      IF (LDOSRT) CLOSE (UNIT=ISUNIT(3), STATUS='DELETE')
C
C
C     Re-attach the Catalog File with a Normal Assignment
 800  CONTINUE
      IF (INUNIT.EQ.0) THEN                                             Md
      CLOSE (UNIT=ICUNIT)                                               Md
      CALL CHRLNB (CNAME, ILAST)                                        Md
      OPEN ( UNIT=ICUNIT, FILE=CNAME(1:ILAST), IOSTAT=IERR,             Md
     * SHARE='DENYNONE')                                                Md
      IF (IERR.NE.0) GO TO 940                                          Md
      ENDIF                                                             Md
C
      IF ((CDNAME(1:2).NE.'  ').AND.(ICDUNT.GT.0)) THEN                 Md
      CLOSE (UNIT=ICDUNT)                                               Md
      CALL CHRLNB (CDNAME, ILAST)                                       Md
      OPEN ( UNIT=ICDUNT, FILE=CDNAME(1:ILAST), IOSTAT=IERR,            Md
     * SHARE='DENYNONE')                                                Md
      IF (IERR.NE.0) GO TO 950                                          Md
      ENDIF                                                             Md
C
 820  CONTINUE
      LMAP = .FALSE.
      IF (MLEVEL.GE.11) WRITE (MUNIT,840)
 840  FORMAT (T6,'-----DSS---Debug:  Exit ZCAT')
      RETURN
C
C
C     Error Conditions
C     No Records in the DSS File
 900  CONTINUE
      WRITE (MUNIT,901)
 901  FORMAT (/' **** ERROR - ZCAT:  No Records in DSS ',
     * 'File (Catalog not Created) ****',/)
      GO TO 820
C
C     Error During Write
 910  CONTINUE
      WRITE (MUNIT,911)
 911  FORMAT(/' **** ERROR - ZCAT:  Error During Write to',
     * ' the Catalog File ***',/,' Unable to Complete Catalog',/)
      GO TO 740
C
C     Catalog File Not Attached
 920  CONTINUE                                                          Md
      WRITE (MUNIT,921)                                                 Md
 921  FORMAT (/' **** ERROR - ZCAT:  Catalog File Not ',                Md
     * 'Attached (New Catalog Not Created)',/)                          Md
      GO TO 800                                                         Md
C
C     Error during Exclusive Assign
 930  CONTINUE                                                          Md
      WRITE (MUNIT,931)                                                 Md
 931  FORMAT (/' **** ERROR - ZCAT:  Catalog file Currently in use;',   Md
     * /,' Cannot Create New Catalog at this Time.',/)                  Md
      GO TO 800                                                         Md
C
C     Could not reopen the catalog file
 940  CONTINUE                                                          Md
      CALL CHRLNB(CNAME, N)                                             Md
      WRITE (MUNIT,941) CNAME(1:N)                                      Md
 941  FORMAT (/' **** ERROR - ZCAT:  Cannot Re-open Catalog File',/     Md
     * ' Catalog File: ',A,/)                                           Md
      GO TO 820                                                         Md
C
C     Could not reopen the catalog file
 950  CONTINUE                                                          Md
      CALL CHRLNB(CDNAME, N)                                            Md
      WRITE (MUNIT,951) CDNAME(1:N)                                     Md
 951  FORMAT (/' ****CAUTION - ZCAT:  Cannot Re-open Condensed Catalog',Md
     * /,' Condensed Catalog File: ',A,/)                               Md
      GO TO 820                                                         Md
C
C     Error in selective parameter
 960  CONTINUE
      WRITE (MUNIT,961)
 961  FORMAT (' **** ERROR - ZCAT:  Invalid Selective Catalog',
     * ' Parameter ***')
      GO TO 800
C
      END
