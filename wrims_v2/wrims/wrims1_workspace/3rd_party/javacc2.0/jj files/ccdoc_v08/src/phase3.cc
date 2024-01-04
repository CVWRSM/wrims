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
#include "phase3.h"
#include "phase3_html.h"

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase3.cc,v 1.1.1.1 2001/08/20 20:59:12 Administrator Exp $";

// ================================================================
// Run
// ================================================================
bool ccdoc::phase3::run(switches& sw,database& db)
{
  if(sw.verbose()) {
    s_log << "phase3: begins\n";
  }
  bool debug = false;
  if( ::getenv("CCDOC_PHASE3_DEBUG") ) {
    debug = true;
    s_log << "CCDOC_PHASE3_DEBUG: "
	  << "================================================\n";
    s_log << "CCDOC_PHASE3_DEBUG: file: " << sw.db() << "\n";
    db.debug_dump("CCDOC_PHASE3_DEBUG: ");
  }
  html h(sw,db);
  h.set_debug(debug);
  bool status = h.run();
  if(sw.verbose()) {
    s_log << "phase3: ends\n";
  }

  return status;
}
