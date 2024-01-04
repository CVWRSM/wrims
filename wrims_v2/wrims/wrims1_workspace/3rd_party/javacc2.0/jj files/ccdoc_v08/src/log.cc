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
#include "log.h"
#include <fstream>

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: log.cc,v 1.2 2001/08/21 18:47:30 Administrator Exp $";

// ================================================================
// Global log object.
// ================================================================
ccdoc::log ccdoc::s_log;

// ================================================================
// Constructor.
// ================================================================
ccdoc::log::log()
  : m_output_flag(true),
    m_warnings_flag(true),
    m_warnings(0),
    m_errors(0)
{
  m_os.push_back(&cout);
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::log::~log() {
}
// ================================================================
// Insert stream.
// ================================================================
void ccdoc::log::insert(ostream* log) {
  if(log)
    m_os.push_back(log);
}
// ================================================================
// Insert log file.
// ================================================================
void ccdoc::log::insert(string& log) {
  ofstream* os = new ofstream(log.c_str());
  if(! (*os) ) {
    s_log << "ERROR: Log file '" << log << "' is invalid.\n";
  }
  m_os.push_back( os );
}
// ================================================================
// Remove ostream.
// ================================================================
void ccdoc::log::remove(ostream* os) {
  vector<ostream*>::iterator itr = m_os.begin();
  for( ; itr!=m_os.end(); ++itr) {
    if( *itr == os ) {
      // Only erase the 1st one.
      m_os.erase(itr);
      return;
    }
  }
}
// ================================================================
// operator << const char*
// ================================================================
ccdoc::log& ccdoc::log::flush() {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	os->flush();
      }
    }
  }
  return *this;
}
// ================================================================
// operator << const char*
// ================================================================
ccdoc::log& ccdoc::log::operator << (const char* str) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os)
	(*os) << str;
    }
  }
  return *this;
}
// ================================================================
// operator << int
// ================================================================
ccdoc::log& ccdoc::log::operator << (int val) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	(*os) << val;
      }
    }
  }
  return *this;
}
// ================================================================
// operator << unsigned int
// ================================================================
ccdoc::log& ccdoc::log::operator << (unsigned int val) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	(*os) << val;
      }
    }
  }
  return *this;
}
// ================================================================
// operator << long
// ================================================================
ccdoc::log& ccdoc::log::operator << (long val) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	(*os) << val;
      }
    }
  }
  return *this;
}
// ================================================================
// operator << unsigned long
// ================================================================
ccdoc::log& ccdoc::log::operator << (unsigned long val) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	(*os) << val;
      }
    }
  }
  return *this;
}
// ================================================================
// operator << char
// ================================================================
ccdoc::log& ccdoc::log::operator << (char val) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	(*os) << static_cast<char>(val);
      }
    }
  }
  return *this;
}
// ================================================================
// operator << unsigned char
// ================================================================
ccdoc::log& ccdoc::log::operator << (unsigned char val) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os) {
	(*os) << static_cast<unsigned char>(val);
      }
    }
  }
  return *this;
}
// ================================================================
// operator << const string&
// ================================================================
ccdoc::log& ccdoc::log::operator << (const string& str) {
  *this << str.c_str();
  return *this;
}
// ================================================================
// operator << const vector<string>&
// ================================================================
ccdoc::log& ccdoc::log::operator << (const vector<string>& vec) {
  vector<string>::const_iterator itr = vec.begin();
  for(;itr!=vec.end();++itr) {
    *this << (*itr).c_str();
  }
  return *this;
}
// ================================================================
// operator <<
// ================================================================
ccdoc::log& ccdoc::log::operator << (ostream& (fct)(ostream&) ) {
  if(m_output_flag) {
    vector<ostream*>::iterator i = m_os.begin();
    for( ; i!=m_os.end(); ++i) {
      ostream* os = *i;
      if(os)
	(*os) << fct( *os );
    }
  }
  return *this;
}
// ================================================================
// Warning
// ================================================================
ccdoc::log& ccdoc::log::warning() {
  if(m_warnings_flag) {
    *this << "WARNING: ";
    m_warnings++;
    return *this;
  }
  return disable();
}
// ================================================================
// Error
// ================================================================
ccdoc::log& ccdoc::log::error() {
  *this << "ERROR: ";
  m_errors++;
  return *this;
}
