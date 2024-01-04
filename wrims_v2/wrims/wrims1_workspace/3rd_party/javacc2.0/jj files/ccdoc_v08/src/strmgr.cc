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
static char ccdoc_rcsid[] = "$Id: strmgr.cc,v 1.2 2001/08/21 20:42:26 Administrator Exp $";

#include "strmgr.h"

// ================================================================
// Constructor.
// ================================================================
ccdoc::strmgr::strmgr() {
  m_mapped = false;
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::strmgr::~strmgr() {
}
// ================================================================
// Retrieve
// ================================================================
const string& ccdoc::strmgr::get(const string& key) {
  str_coll_itr i = m_strmap.find(key);
  if( i == m_strmap.end() ) {
    // Not found, add it.
    pair<string,unsigned> p(key,0);
    m_strmap.insert( p );
    i = m_strmap.find(key);
    m_mapped = false;
  }
  return (*i).first;
}
// ================================================================
// Get index.
// ================================================================
unsigned ccdoc::strmgr::get_idx(const string key) {
  gen_maps();
  const string& x = get(key);
  str_coll_itr i = m_strmap.find(key);
  return (*i).second;
}
// ================================================================
// Get by index.
// ================================================================
const string& ccdoc::strmgr::get_by_idx(unsigned key) {
  gen_maps();
  idx_coll_itr i = m_idxmap.find(key);
  ccdoc_assert( i != m_idxmap.end() );
  return *((*i).second);
}
// ================================================================
// Generate the xref maps.
// ================================================================
void ccdoc::strmgr::gen_maps() {
  if(!m_mapped) {
    m_idxmap.clear();

    // Assign the index values.
    str_coll tmp;
    str_coll_itr i = m_strmap.begin();
    str_coll_itr e = m_strmap.end();
    for(unsigned idx=0;i!=e;++i,++idx) {
      pair<const string,unsigned>& p = *i;
      p.second = idx;
    }
    
    // Map the string data.
    i = m_strmap.begin();
    e = m_strmap.end();
    for(;i!=e;++i) {
      m_idxmap.insert( make_pair( (*i).second, &((*i).first) ) );
    }
    m_mapped = true;
  }
}


