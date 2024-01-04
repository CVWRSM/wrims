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

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: main.cc,v 1.4 2001/09/08 15:47:59 Administrator Exp $";

// ================================================================
// Includes
// ================================================================
#include "switches.h"
#include "database.h"
#include "statement.h"
#include "phase1.h"
#include "phase2.h"
#include "phase3.h"

// ================================================================
// Report status.
// ================================================================
static int status(int st,bool v=false) {
  if( v ) {
    if( ccdoc::s_log.warnings_enabled() )
      ccdoc::s_log << ccdoc::s_log.warnings() << " warnings  ";
    ccdoc::s_log << ccdoc::s_log.errors() << " errors\n";
  }
  ccdoc::s_log.flush();
  return st;
}
// ================================================================
// MAIN
// ================================================================
int main(int argc,char** argv) {
  try {
    // Load the switches.
    ccdoc::switches sw(argc,argv);
    if( ! sw.ok() )
      return status(1,sw.verbose());

    // Load the database information.
    // If the database does not exist,
    // an empty database is created.
    // If the -db switch argument was
    // not specified, an exception is
    // thrown.
    ccdoc::database db(sw);

    bool run_phase2 = sw.index();
    bool write_flag = false;

    // Is phase 1 enabled? This occurs when the -pkg <name> switch
    // is specified or when source files are specified.
    if( sw.pkg().size() || sw.num_files() ) {
      write_flag = true;
      if( sw.html().size() ) {
	run_phase2 = true;
      }
      if( ! ccdoc::phase1::run(sw,db) )
	return status(1,sw.verbose());
    }

    // Is phase 2 enabled? This occurs when the -index switch
    // is specified or when phase3 was specified with some new
    // source files.
    if( run_phase2 ) {
      write_flag = true;
      if( ! ccdoc::phase2::run(sw,db) )
	status(1,sw.verbose());
    }

    // Is phase 3 enabled? This occurs when the -htm or -html switch
    // is specified.
    if( sw.html().size() ) {
      if( !ccdoc::phase3::run(sw,db) )
	return status(1,sw.verbose());
      if( !write_flag )
	db.disable_write();
    }

    return status(0);
  }
  catch (const ccdoc::exceptions::base& e) {
    ccdoc::s_log << "Caught exception\n";
    e.report();
  }

  return status(1);
}

