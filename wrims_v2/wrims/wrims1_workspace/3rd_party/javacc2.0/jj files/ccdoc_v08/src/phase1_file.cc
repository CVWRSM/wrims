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
#include "phase1_file.h"
#include "phase1_parser.h"
#include "log.h"

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase1_file.cc,v 1.1.1.1 2001/08/20 20:59:12 Administrator Exp $";

// ================================================================
// Constructor.
// ================================================================
ccdoc::phase1::file::file(switches& sw,
			  database& db,
			  const string& name)
  : m_sw(sw),
    m_db(db),
    m_name(name)
{
}
// ================================================================
// Constructor.
// ================================================================
ccdoc::phase1::file::~file()
{
}
// ================================================================
// Load.
// ================================================================
void ccdoc::phase1::file::compile()
{
  parser p(m_sw,m_db,m_name);
  p.set_debug(m_debug);
  p.parse();
}
