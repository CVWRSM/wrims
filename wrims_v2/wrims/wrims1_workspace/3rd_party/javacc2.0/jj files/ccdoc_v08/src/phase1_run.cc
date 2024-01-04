//@copyright_begin
// ================================================================
// Copyright Notice
// Copyright (C) 1998-2001 by Joe Linoff (www.joelinoff.com/ccdoc)
// 
// This software is distributed in the hope that it will be useful, but
// without WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// 
// Permission is granted to anyone to make or distribute altered copies
// of this software provided that the copyright notice and this
// permission notice are preserved, that the distributor grants the
// recipent permission for further distribution as permitted by this
// notice and that the origin of the software is represented correctly.
// 
// Comments and suggestions are always welcome.
// Please report bugs to http://www.joelinoff.com/ccdoc
// ================================================================
//@copyright_end
#include "phase1.h"
#include "phase1_file.h"

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase1_run.cc,v 1.1.1.1 2001/08/20 20:59:12 Administrator Exp $";

// ================================================================
// Run
// ================================================================
bool ccdoc::phase1::run(switches& sw,
			database& db)
{
  if(sw.verbose()) {
    s_log << "phase1: begins\n";
  }
  bool debug = false;
  if( ::getenv("CCDOC_PHASE1_DEBUG") ) {
    debug = true;
  }

  // Walk through all of the files specified on
  // the command line.
  switches::files_type files;
  sw.files(files);
  switches::files_type::iterator itr = files.begin();
  for(;itr!=files.end();++itr) {
    phase1::file f(sw,db,*itr);
    f.set_debug(debug);
    f.compile();
  }

  if(sw.verbose()) {
    s_log << "phase1: ends\n";
  }

  return s_log.errors() == 0;
}
