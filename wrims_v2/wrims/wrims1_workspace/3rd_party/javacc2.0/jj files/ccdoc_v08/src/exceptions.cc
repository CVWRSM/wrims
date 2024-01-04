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
static char ccdoc_rcsid[] = "$Id: exceptions.cc,v 1.2 2001/08/27 21:36:56 Administrator Exp $";

#include "exceptions.h"
#include <iostream>
#include <cstdio>

// ================================================================
// Default constructor.
// ================================================================
ccdoc::exceptions::base::base() {
}
// ================================================================
// Id based constructor.
// ================================================================
ccdoc::exceptions::base::base(const char* id,
			      const char* file,
			      int lineno,
			      const char* msg)
{
  char nbuf[32];
  sprintf(nbuf,"%d",lineno);
  ccdoc_assert(file);
  ccdoc_assert(msg);
  m_msg = "EXCEPTION:";
  m_msg += id;
  m_msg += ":";
  m_msg += file;
  m_msg += ":";
  m_msg += nbuf;
  m_msg += ": ";
  m_msg += msg;
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::exceptions::base::~base() {
}
// ================================================================
// Report
// ================================================================
void ccdoc::exceptions::base::report() const {
  cerr << endl << m_msg.c_str() << endl;
}

// ================================================================
// assert_true
// ================================================================
ccdoc::exceptions::assert_true::assert_true(const char* file,
					    int lineno,
					    const char* expr)
  : ccdoc::exceptions::base("assertion failed",file,lineno,expr)
{
}

// ================================================================
// invalid_database
// ================================================================
ccdoc::exceptions::invalid_database::invalid_database(const char* file,
						      int lineno,
						      const char* dbfile,
						      const char* msg)
  : ccdoc::exceptions::base("invalid database",file,lineno,dbfile)
{
  ccdoc_assert(msg);
  m_msg += " ";
  m_msg += msg;
}

// ================================================================
// duplicate_name
// ================================================================
ccdoc::exceptions::duplicate_name::duplicate_name(const char* file,
						  int lineno,
						  const char* name,
						  const char* msg)
  : ccdoc::exceptions::base("duplicate name",file,lineno,name)
{
  ccdoc_assert(msg);
  m_msg += " ";
  m_msg += msg;
}
// ================================================================
// duplicate_name
// ================================================================
ccdoc::exceptions::
unwriteable_output_file::unwriteable_output_file(const char* file,
						 int lineno,
						 const char* name)
  : ccdoc::exceptions::base("unwriteable output file",file,lineno,name)
{
}
