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
#include "statement.h"
#include "log.h"
#include <algorithm>
#include <cstdio>

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: statement.cc,v 1.12 2001/10/25 22:37:39 Administrator Exp $";

// ================================================================
// String manager.
// ================================================================
static ccdoc::strmgr s_strmgr;
ccdoc::strmgr& ccdoc::statement::base::get_strmgr()
{
  // Make sure that all of the terse strings in are
  // the string table. This guarantees that the db
  // write will work correctly in terse mode.
  s_strmgr.get("pub");
  s_strmgr.get("pro");
  s_strmgr.get("pri");

  s_strmgr.get("unk");
  s_strmgr.get("ign");
  s_strmgr.get("att");
  s_strmgr.get("atf");
  s_strmgr.get("enu");
  s_strmgr.get("ext");
  s_strmgr.get("frc");
  s_strmgr.get("frf");
  s_strmgr.get("fct");
  s_strmgr.get("con");
  s_strmgr.get("des");
  s_strmgr.get("opr");
  s_strmgr.get("cls");
  s_strmgr.get("clx");
  s_strmgr.get("cod");
  s_strmgr.get("cop");
  s_strmgr.get("cos");
  s_strmgr.get("m00");
  s_strmgr.get("m01");
  s_strmgr.get("m0n");
  s_strmgr.get("mnn");
  s_strmgr.get("mif");
  s_strmgr.get("miv");
  s_strmgr.get("met");
  s_strmgr.get("nsp");
  s_strmgr.get("nsx");
  s_strmgr.get("pkg");
  s_strmgr.get("str");
  s_strmgr.get("stx");
  s_strmgr.get("tyf");
  s_strmgr.get("tyv");
  s_strmgr.get("uni");
  s_strmgr.get("unx");
  s_strmgr.get("var");
  s_strmgr.get("vaf");

  return s_strmgr;
}
// ================================================================
// Constructor
// ================================================================
ccdoc::statement::base::base()
  : m_access(STMT_PUBLIC),
    m_comment(0),
    m_file(0),
    m_id(0),
    m_extern(0),
    m_lineno(0),
    m_tag(0),
    m_parent(0),
    m_sorted(true),
    m_type(STMT_IGNORE)
{
  set_id(0);
  set_file(0);
  set_extern(0);
}
// ================================================================
// Copy Constructor
// ================================================================
ccdoc::statement::base::base(const base& x)
  : m_access(x.m_access),
    m_children(x.m_children),
    m_comment(x.m_comment),
    m_file(x.m_file),
    m_id(x.m_id),
    m_extern(x.m_extern),
    m_lineno(x.m_lineno),
    m_tag(x.m_tag),
    m_parent(x.m_parent),
    m_sorted(x.m_sorted),
    m_tokens(x.m_tokens),
    m_type(x.m_type)
{
}
// ================================================================
// Copy operator
// ================================================================
ccdoc::statement::base&
ccdoc::statement::base::operator = (const base& x)
{
  m_access   = x.m_access;
  m_children = x.m_children;
  m_comment  = x.m_comment;
  m_file     = x.m_file;
  m_id       = x.m_id;
  m_extern   = x.m_extern;
  m_lineno   = x.m_lineno;
  m_tag      = x.m_tag;
  m_parent   = x.m_parent;
  m_sorted   = x.m_sorted;
  m_tokens   = x.m_tokens;
  m_type     = x.m_type;

  return *this;
}
// ================================================================
// Destructor
// ================================================================
ccdoc::statement::base::~base()
{
  if( m_parent ) {
    m_parent->remove_child(this);
    m_parent = 0;
  }
  if( m_comment ) {
    m_comment->set_comment(0);
    m_comment = 0;
  }
  if( m_children.size() ) {
    // Make a copy of the children vector
    // to avoid messing up the iterator.
    stmts_t vec = m_children;
    m_children.clear();
    stmts_itr_t itr = vec.begin();
    for(;itr!=vec.end();++itr) {
      base* child = *itr;
      delete child;
    }
  }
}
// ================================================================
// Clear
// ================================================================
void ccdoc::statement::base::clear()
{
  m_access = STMT_PUBLIC;
  m_comment = 0;
  m_children.clear();
  set_file(0);
  set_id(0);
  set_extern(0);
  m_lineno = 0;
  m_tag = 0;
  m_parent = 0;
  m_tokens.clear();
  m_type = STMT_IGNORE;
  set_id(0);
}
// ================================================================
// Set string.
// ================================================================
const char* ccdoc::statement::base::set_string(const char* value)
{
  if(value) {
    string key = value;
    const string& p = s_strmgr.get(key);
    return p.c_str();
  }
  return set_string("");
}
// ================================================================
// Set file.
// ================================================================
void ccdoc::statement::base::set_file(const char* file)
{
  m_file = set_string(file);
}
// ================================================================
// Set the external linkage type.
// ================================================================
void ccdoc::statement::base::set_extern(const char* x)
{
  m_extern = set_string(x);
}
// ================================================================
// Set id.
// ================================================================
void ccdoc::statement::base::set_id(const char* id)
{
  m_id = set_string(id);
  if( m_parent )
    m_parent->m_sorted = false;
}
// ================================================================
// Set id.
// ================================================================
void ccdoc::statement::base::set_id(const string& id)
{
  set_id(id.c_str());
}
// ================================================================
// add token
// ================================================================
void ccdoc::statement::base::add_token(const char* str)
{
  if(str) {
    const string& p = s_strmgr.get(str);
    m_tokens.push_back( p.c_str() );
  }
}
// ================================================================
// Set tokens.
// ================================================================
void ccdoc::statement::base::set_tokens(strs_t& vec)
{
  m_tokens.clear();
  strs_itr_t itr = vec.begin();
  for(;itr!=vec.end();++itr) {
    string& str = *itr;
    const string& p = s_strmgr.get(str);
    m_tokens.push_back( p.c_str() );
  }
}
// ================================================================
// Get parents.
// ================================================================
void ccdoc::statement::base::get_parents(base::stmts_t& parents) const
{
  if(parents.size())
    parents.clear();
  base* stmt = get_parent();
  while(stmt) {
    parents.push_back(stmt);
    stmt = stmt->get_parent();
  }
  reverse(parents.begin(),parents.end());
}
// ================================================================
// Get parents, don't include packages.
// ================================================================
void ccdoc::statement::base::get_parents_no_pkgs(stmts_t& parents) const
{
  if(parents.size())
    parents.clear();
  if( get_type() != STMT_PACKAGE ) {
    base* stmt = get_parent();
    while(stmt) {
      if( stmt->get_type() == STMT_PACKAGE )
	break;
      parents.push_back(stmt);
      stmt = stmt->get_parent();
    }
    reverse(parents.begin(),parents.end());
  }
}
// ================================================================
// Set parent.
// ================================================================
void ccdoc::statement::base::set_parent(base* parent)
{
  if(m_parent) {
    m_parent->remove_child(this);
  }
  m_parent = parent;
  if(m_parent) {
    m_parent->add_child(this);
  }
}
// ================================================================
// Insert before.
// ================================================================
void ccdoc::statement::base::insert_before(base* stmt)
{
  // Insert this after stmt in it's child list.
  if( stmt ) {
    if( m_parent ) {
      m_parent->remove_child(this);
    }
    if( stmt->m_parent ) {
      stmts_t& vec = stmt->m_parent->m_children;
      stmts_itr_t itr = vec.begin();
      for(;itr!=m_children.end();++itr) {
	if( stmt == *itr ) {
	  m_sorted = false;
	  vec.insert(itr,this);
	  m_parent = stmt->m_parent;
	  return;
	}
      }
    }
  }
}
// ================================================================
// Insert after.
// ================================================================
void ccdoc::statement::base::insert_after(base* stmt)
{
  // Insert this after stmt in it's child list.
  if( stmt ) {
    if( m_parent ) {
      m_parent->remove_child(this);
    }
    if( stmt->m_parent ) {
      stmts_t& vec = stmt->m_parent->m_children;
      stmts_itr_t itr = vec.begin();
      for(;itr!=m_children.end();++itr) {
	if( stmt == *itr ) {
	  ++itr;
	  m_sorted = false;
	  if( itr != m_children.end() ) {
	    vec.insert(itr,this);
	  }
	  else {
	    vec.push_back(this);
	  }
	  m_parent = stmt->m_parent;
	  return;
	}
      }
    }
  }
}
// ================================================================
// Add a child.
// ================================================================
void ccdoc::statement::base::add_child(base* child)
{
  if(child && find_child(child) == false) {
    if( m_sorted &&
	m_children.size() &&
	strcmp(m_children.back()->get_id(),child->get_id())>0 )
      m_sorted = false;
    m_children.push_back(child);
    child->m_parent = this;
  }
}
// ================================================================
// Remove a child.
// ================================================================
void ccdoc::statement::base::remove_child(base* child)
{
  if(child) {
    stmts_itr_t itr = m_children.begin();
    for(;itr!=m_children.end();++itr) {
      if( *itr == child ) {
	child->m_parent = 0;
	m_children.erase(itr);
	return;
      }
    }
  }
}
// ================================================================
// Get all children, load them into a vector.
// ================================================================
void ccdoc::statement::base::get_all_children(stmts_t& children) const
{
  stmts_citr_t itr = m_children.begin();
  for(;itr!=m_children.end();++itr) {
    statement::base* stmt = *itr;
    children.push_back(stmt);
    stmt->get_all_children(children);
  }
}
// ================================================================
// Get a child by a type and an id name.
// ================================================================
ccdoc::statement::base*
ccdoc::statement::base::get_child_by_id_type(const char* id,TYPE t) const
{
  if(id) {
    stmts_citr_t itr = m_children.begin();
    for(;itr!=m_children.end();++itr) {
      statement::base* stmt = *itr;
      if( stmt->get_type() == t && !::strcmp(stmt->get_id(),id) )
	return stmt;
    }
  }
  return 0;
}
// ================================================================
// Get a child by a type and an id name.
// ================================================================
ccdoc::statement::base*
ccdoc::statement::base::get_child_by_id_type(const string& id,TYPE t) const
{
  return get_child_by_id_type(id.c_str(),t);
}
// ================================================================
// Get a child by an id name.
// ================================================================
ccdoc::statement::base*
ccdoc::statement::base::get_child_by_id(const char* id) const
{
  if(id) {
    stmts_citr_t itr = m_children.begin();
    for(;itr!=m_children.end();++itr) {
      statement::base* stmt = *itr;
      if( !::strcmp(stmt->get_id(),id) )
	return stmt;
    }
  }
  return 0;
}
// ================================================================
// Get a child by an id name.
// ================================================================
ccdoc::statement::base*
ccdoc::statement::base::get_child_by_id(const string& id) const
{
  return get_child_by_id(id.c_str());
}
// ================================================================
// Find a child.
// ================================================================
bool ccdoc::statement::base::find_child(base* child) const
{
  if(child) {
    stmts_citr_t itr = m_children.begin();
    for(;itr!=m_children.end();++itr) {
      if( *itr == child )
	return true;
    }
  }
  return false;
}
// ================================================================
// Get children by an id name.
// ================================================================
bool ccdoc::statement::base::get_children_by_id(stmts_t& vec,
						const char* id)
{
  bool st = false;
  if(id) {
    stmts_itr_t itr = search(id);
    for(;itr!=m_children.end();++itr) {
      statement::base* stmt = *itr;
      if( !::strcmp(stmt->get_id(),id) ) {
	st = true;
	vec.push_back(stmt);
      }
      else {
	break;
      }
    }    
  }
  return st;
}
// ================================================================
// Get children by an id name.
// ================================================================
bool ccdoc::statement::base::get_children_by_id(stmts_t& vec,
						const string& id)
{
  return get_children_by_id(vec,id.c_str());
}
// ================================================================
// Get hier id.
// ================================================================
void ccdoc::statement::base::get_hier_id(string& id) const
{
  id = "";
  if(get_parent()) {
    strs_t vec;
    const base* x = this;
    while( x ) {
      vec.push_back(x->get_id());
      x = x->get_parent();
    }
    
    strs_ritr_t itr = vec.rbegin();
    for(;itr!=vec.rend();++itr) {
      id += "::";
      id += *itr;
    }
  }
}
// ================================================================
// Get hier id, don't include packages.
// ================================================================
void ccdoc::statement::base::get_hier_id_no_pkgs(string& id) const
{
  id = "";
  if(get_parent()) {
    strs_t vec;
    const base* x = this;
    while( x ) {
      if( x->get_type() == STMT_PACKAGE )
	break;
      vec.push_back(x->get_id());
      x = x->get_parent();
    }
    
    if( vec.size() ) {
      strs_ritr_t itr = vec.rbegin();
      if( itr != vec.rend() )
	id += *itr;
      for(++itr;itr!=vec.rend();++itr) {
	id += "::";
	id += *itr;
      }
    }
  }
}
// ================================================================
// Get the access name.
// ================================================================
const char* ccdoc::statement::base::get_access_name(ACCESS a)
{
  switch(a) {
  case STMT_PUBLIC:    return "public";
  case STMT_PROTECTED: return "protected";
  case STMT_PRIVATE:   return "private";
  default: break;
  }
  return "unknown";
}
// ================================================================
// Get the terse access name.
// ================================================================
const char* ccdoc::statement::base::get_terse_access_name(ACCESS a)
{
  switch(a) {
  case STMT_PUBLIC:    return "pub";
  case STMT_PROTECTED: return "pro";
  case STMT_PRIVATE:   return "pri";
  default: break;
  }
  return "unknown";
}
// ================================================================
// Get the terse access name.
// ================================================================
ccdoc::statement::base::ACCESS
ccdoc::statement::base::get_terse_access(const string& token)
{
  if( token == "pro" ) return STMT_PROTECTED;
  if( token == "pri" ) return STMT_PRIVATE;
  return STMT_PUBLIC;
}
// ================================================================
// Get the type name.
// ================================================================
const char* ccdoc::statement::base::get_type_name1(TYPE t)
{
  switch(t) {
  case STMT_ATTRIBUTE:            return "attribute";
  case STMT_ATTRIBUTE_FUNCTION:   return "attribute";
  case STMT_ENUM:                 return "enum";
  case STMT_FRIEND_FUNCTION:      return "friend";
  case STMT_FUNCTION:             return "function";
  case STMT_FUNCTION_OPERATOR:    return "operator";
  case STMT_CLASS_BEGIN:          return "class";
  case STMT_MACRODEF_0_0:         return "macro";
  case STMT_MACRODEF_0_1:         return "macro";
  case STMT_MACRODEF_0_N:         return "macro";
  case STMT_MACRODEF_N_N:         return "macro";
  case STMT_MACROINST_FUNCTION:   return "macroinst";
  case STMT_MACROINST_VARIABLE:   return "macroinst";
  case STMT_METHOD:               return "method";
  case STMT_METHOD_CONSTRUCTOR:   return "constructor";
  case STMT_METHOD_DESTRUCTOR:    return "destructor";
  case STMT_METHOD_OPERATOR:      return "operator";
  case STMT_NAMESPACE_BEGIN:      return "namespace";
  case STMT_PACKAGE:              return "package";
  case STMT_STRUCT_BEGIN:         return "struct";
  case STMT_TYPEDEF_FUNCTION:     return "typedef";
  case STMT_TYPEDEF_VARIABLE:     return "typedef";
  case STMT_UNION_BEGIN:          return "union";
  case STMT_VARIABLE:             return "variable";
  case STMT_VARIABLE_FUNCTION:    return "variable";
  default: break;
  }
  return get_type_name(t);
}
// ================================================================
// Get the type name.
// ================================================================
const char* ccdoc::statement::base::get_type_name(TYPE t)
{
  switch(t) {
  case STMT_IGNORE:               return "IGNORE";
  case STMT_ATTRIBUTE:            return "ATTRIBUTE";
  case STMT_ATTRIBUTE_FUNCTION:   return "ATTRIBUTE_FUNCTION";
  case STMT_ENUM:                 return "ENUM";
  case STMT_EXTERN:               return "EXTERN";
  case STMT_FRIEND_CLASS:         return "FRIEND_CLASS";
  case STMT_FRIEND_FUNCTION:      return "FRIEND_FUNCTION";
  case STMT_FUNCTION:             return "FUNCTION";
  case STMT_FUNCTION_OPERATOR:    return "FUNCTION_OPERATOR";
  case STMT_CLASS_BEGIN:          return "CLASS_BEGIN";
  case STMT_CLASS_END:            return "CLASS_END";
  case STMT_COMMENT_PKGDOC:       return "COMMENT_PKGDOC";
  case STMT_COMMENT_PKGDOC_URL:   return "COMMENT_PKGDOC_URL";
  case STMT_COMMENT_PREFIX:       return "COMMENT_PREFIX";
  case STMT_COMMENT_SUFFIX:       return "COMMENT_SUFFIX";
  case STMT_MACRODEF_0_0:         return "MACRODEF_0_0";
  case STMT_MACRODEF_0_1:         return "MACRODEF_0_1";
  case STMT_MACRODEF_0_N:         return "MACRODEF_0_N";
  case STMT_MACRODEF_N_N:         return "MACRODEF_N_N";
  case STMT_MACROINST_FUNCTION:   return "MACROINST_FUNCTION";
  case STMT_MACROINST_VARIABLE:   return "MACROINST_VARIABLE";
  case STMT_METHOD:               return "METHOD";
  case STMT_METHOD_CONSTRUCTOR:   return "METHOD_CONSTRUCTOR";
  case STMT_METHOD_DESTRUCTOR:    return "METHOD_DESTRUCTOR";
  case STMT_METHOD_OPERATOR:      return "METHOD_OPERATOR";
  case STMT_NAMESPACE_BEGIN:      return "NAMESPACE_BEGIN";
  case STMT_NAMESPACE_END:        return "NAMESPACE_END";
  case STMT_PACKAGE:              return "PACKAGE";
  case STMT_STRUCT_BEGIN:         return "STRUCT_BEGIN";
  case STMT_STRUCT_END:           return "STRUCT_END";
  case STMT_TYPEDEF_FUNCTION:     return "TYPEDEF_FUNCTION";
  case STMT_TYPEDEF_VARIABLE:     return "TYPEDEF_VARIABLE";
  case STMT_UNION_BEGIN:          return "UNION_BEGIN";
  case STMT_UNION_END:            return "UNION_END";
  case STMT_VARIABLE:             return "VARIABLE";
  case STMT_VARIABLE_FUNCTION:    return "VARIABLE_FUNCTION";
  default: break;
  }
  return "UNKNOWN";
}
// ================================================================
// Get the terse type name.
// ================================================================
const char* ccdoc::statement::base::get_terse_type_name(TYPE t)
{
  switch(t) {
  case STMT_IGNORE:               return "ign";
  case STMT_ATTRIBUTE:            return "att";
  case STMT_ATTRIBUTE_FUNCTION:   return "atf";
  case STMT_ENUM:                 return "enu";
  case STMT_EXTERN:               return "ext";
  case STMT_FRIEND_CLASS:         return "frc";
  case STMT_FRIEND_FUNCTION:      return "frf";
  case STMT_FUNCTION:             return "fct";
  case STMT_FUNCTION_OPERATOR:    return "opr";
  case STMT_CLASS_BEGIN:          return "cls";
  case STMT_CLASS_END:            return "clx";
  case STMT_COMMENT_PKGDOC:       return "cod";
  case STMT_COMMENT_PKGDOC_URL:   return "cou";
  case STMT_COMMENT_PREFIX:       return "cop";
  case STMT_COMMENT_SUFFIX:       return "cos";
  case STMT_MACRODEF_0_0:         return "m00";
  case STMT_MACRODEF_0_1:         return "m01";
  case STMT_MACRODEF_0_N:         return "m0n";
  case STMT_MACRODEF_N_N:         return "mnn";
  case STMT_MACROINST_FUNCTION:   return "mif";
  case STMT_MACROINST_VARIABLE:   return "miv";
  case STMT_METHOD:               return "met";
  case STMT_METHOD_CONSTRUCTOR:   return "con";
  case STMT_METHOD_DESTRUCTOR:    return "des";
  case STMT_METHOD_OPERATOR:      return "mop";
  case STMT_NAMESPACE_BEGIN:      return "nsp";
  case STMT_NAMESPACE_END:        return "nsx";
  case STMT_PACKAGE:              return "pkg";
  case STMT_STRUCT_BEGIN:         return "str";
  case STMT_STRUCT_END:           return "stx";
  case STMT_TYPEDEF_FUNCTION:     return "tyf";
  case STMT_TYPEDEF_VARIABLE:     return "tyv";
  case STMT_UNION_BEGIN:          return "uni";
  case STMT_UNION_END:            return "unx";
  case STMT_VARIABLE:             return "var";
  case STMT_VARIABLE_FUNCTION:    return "vaf";
  default: break;
  }
  return "unk";
  s_strmgr.get("pro");
}
// ================================================================
// Get the terse type name.
// ================================================================
ccdoc::statement::base::TYPE
ccdoc::statement::base::get_terse_type(const string& token)
{
  if(token == "ign" ) return STMT_IGNORE              ;
  if(token == "att" ) return STMT_ATTRIBUTE           ;
  if(token == "atf" ) return STMT_ATTRIBUTE_FUNCTION  ;
  if(token == "enu" ) return STMT_ENUM                ;
  if(token == "ext" ) return STMT_EXTERN              ;
  if(token == "frc" ) return STMT_FRIEND_CLASS        ;
  if(token == "frf" ) return STMT_FRIEND_FUNCTION     ;
  if(token == "fct" ) return STMT_FUNCTION            ;
  if(token == "opr" ) return STMT_FUNCTION_OPERATOR   ;
  if(token == "cls" ) return STMT_CLASS_BEGIN         ;
  if(token == "clx" ) return STMT_CLASS_END           ;
  if(token == "cod" ) return STMT_COMMENT_PKGDOC      ;
  if(token == "cou" ) return STMT_COMMENT_PKGDOC_URL  ;
  if(token == "cop" ) return STMT_COMMENT_PREFIX      ;
  if(token == "cos" ) return STMT_COMMENT_SUFFIX      ;
  if(token == "m00" ) return STMT_MACRODEF_0_0        ;
  if(token == "m01" ) return STMT_MACRODEF_0_1        ;
  if(token == "m0n" ) return STMT_MACRODEF_0_N        ;
  if(token == "mnn" ) return STMT_MACRODEF_N_N        ;
  if(token == "mif" ) return STMT_MACROINST_FUNCTION  ;
  if(token == "miv" ) return STMT_MACROINST_VARIABLE  ;
  if(token == "met" ) return STMT_METHOD              ;
  if(token == "con" ) return STMT_METHOD_CONSTRUCTOR  ;
  if(token == "des" ) return STMT_METHOD_DESTRUCTOR   ;
  if(token == "mop" ) return STMT_METHOD_OPERATOR     ;
  if(token == "nsp" ) return STMT_NAMESPACE_BEGIN     ;
  if(token == "nsx" ) return STMT_NAMESPACE_END       ;
  if(token == "pkg" ) return STMT_PACKAGE             ;
  if(token == "str" ) return STMT_STRUCT_BEGIN        ;
  if(token == "stx" ) return STMT_STRUCT_END          ;
  if(token == "tyf" ) return STMT_TYPEDEF_FUNCTION    ;
  if(token == "tyv" ) return STMT_TYPEDEF_VARIABLE    ;
  if(token == "uni" ) return STMT_UNION_BEGIN         ;
  if(token == "unx" ) return STMT_UNION_END           ;
  if(token == "var" ) return STMT_VARIABLE            ;
  if(token == "vaf" ) return STMT_VARIABLE_FUNCTION   ;
  return STMT_IGNORE;
}
// ================================================================
// Get depth.
// ================================================================
unsigned ccdoc::statement::base::get_depth() const
{
  // The top always returns zero.
  unsigned depth = 0;
  base* stmt = get_parent();
  while(stmt) {
    depth++;
    stmt = stmt->get_parent();
  }
  return depth;
}
// ================================================================
// Get depth.
// ================================================================
unsigned ccdoc::statement::base::get_depth_no_pkgs() const
{
  // The top always returns zero.
  unsigned depth = 0;
  base* stmt = get_parent();
  while(stmt && stmt->get_type() != STMT_PACKAGE ) {
    depth++;
    stmt = stmt->get_parent();
  }
  return depth;
}
// ================================================================
// Get the matching begin statement.
// ================================================================
ccdoc::statement::base*
ccdoc::statement::base::get_matching_begin() const
{
  switch( get_type() ) {
  case STMT_CLASS_END:
    return get_matching_begin(STMT_CLASS_BEGIN);
    break;
  case STMT_STRUCT_END:
    return get_matching_begin(STMT_STRUCT_BEGIN);
    break;
  case STMT_UNION_END:
    return get_matching_begin(STMT_UNION_BEGIN);
    break;
  default:
    break;
  }
  return 0;
}
// ================================================================
// Get the matching begin statement.
// ================================================================
ccdoc::statement::base*
ccdoc::statement::base::get_matching_begin(TYPE t) const
{
  unsigned depth = get_depth();
  base* parent = get_parent();
  if( parent ) {
    stmts_t& vec = parent->get_children();
    stmts_itr_t itr = vec.begin();
    for(;itr!=vec.end();++itr) {
      if( *itr == this ) {
	// Found the end statement.
	// Now back up to the enclosing begin.
	for(;itr!=vec.begin();--itr) {
	  base* begin = *itr;
	  if( begin->get_type() == t && begin->get_depth() == depth )
	    return begin;
	}
	break;
      }
    }
  }
  return 0;
}
// ================================================================
// Local sort compare.
// ================================================================
static bool compare_stmts(const ccdoc::statement::base* a,
			  const ccdoc::statement::base* b)
{
  return strcmp(a->get_id(),b->get_id()) < 0;
}
// ================================================================
// Sort
// ================================================================
void ccdoc::statement::base::sort_children()
{
  if( !m_sorted ) {
    sort(m_children.begin(),m_children.end(),compare_stmts);
    m_sorted = true;
  }
}
// ================================================================
// Binary search
// ================================================================
ccdoc::statement::base::stmts_itr_t
ccdoc::statement::base::search(const char* name)
{
  if( name ) {
    if( m_children.size() ) {
      if( !m_sorted )
	sort_children();

      // Check the first record.
      base* stmt = m_children[0];
      if( !strcmp(stmt->get_id(),name) ) {
	return m_children.begin();
      }

      // Check all of the other records.
      unsigned top = m_children.size()-1;
      unsigned bot = 0;
      unsigned cnt = 0;
      while( top>bot ) {
	unsigned delta = top-bot;
	unsigned mid = bot + ((delta+1)/2);
	stmt = m_children[mid];
	int cmp = strcmp(stmt->get_id(),name);
	if( cmp == 0 ) {
	  stmts_itr_t itr = m_children.begin();
	  if( mid ) {
	    itr += mid;
	    // Backup to the 1st one (duplicates are allowed).
	    stmts_itr_t itr1 = itr;
	    for(;;) {
	      --itr1;
	      stmt = *itr1;
	      if( strcmp(stmt->get_id(),name) ) {
		++itr1;
		itr = itr1;
		break;
	      }
	      if( itr1 == m_children.begin() ) {
		// The names match but we are at the beginning.
		itr = itr1;
		break;
	      }
	    }
	  }
	  return itr;
	}
	if( cmp > 0 ) {
	  top = mid;
	}
	else {
	  bot = mid;
	}
	if(++cnt>32)
	  break;
      }
    }
  }
  return m_children.end();
}
// ================================================================
// Binary search
// ================================================================
ccdoc::statement::base::stmts_itr_t
ccdoc::statement::base::search(const string& name)
{
  return search(name.c_str());
}
// ================================================================
// Debug dump
// ================================================================
void ccdoc::statement::base::debug_dump(const char* prefix)
{
  string p;
  if(prefix)
    p = prefix;

  string hid;
  get_hier_id(hid);

  s_log << p << "stmt: begin: ================================================\n";
  s_log << p << "stmt: id:     '" << get_id() << "'\n";
  s_log << p << "stmt: hid:    '" << hid << "'\n";
  s_log << p << "stmt: type:   " << get_type_name() << "\n";
  s_log << p << "stmt: access: " << get_access_name() << "\n";
  s_log << p << "stmt: depth:  " << get_depth() << "\n";

  s_log << p << "stmt: file:   '" << get_file() << "'\n";
  s_log << p << "stmt: lineno: " << get_lineno() << "\n";
  s_log << p << "stmt: extern: '" << get_extern() << "'\n";

  if( m_comment ) {
    m_comment->get_hier_id(hid);
    s_log << p << "stmt: ctype:  " << m_comment->get_type_name() << "'\n";
    s_log << p << "stmt: cid:    '" << hid << "'\n";
  }
  else {
    s_log << p << "stmt: ctype:\n";
    s_log << p << "stmt: cid:    ''\n";
  }

  // Children
  s_log << p << "stmt: nch:    " << m_children.size() << "\n";
  if( m_children.size() ) {
    stmts_itr_t itr = m_children.begin();
    for(int i=0;itr!=m_children.end();++itr,i) {
      s_log << p << "stmt:   child[" << i << "]: "
	    << (*itr)->get_type_name()
	    << ": '"
	;
      (*itr)->get_hier_id(hid);
      s_log << hid << "'\n";
    }
  }
  
  switch( m_type ) {
  case STMT_COMMENT_PKGDOC:
  case STMT_COMMENT_PKGDOC_URL:
  case STMT_COMMENT_PREFIX:
  case STMT_COMMENT_SUFFIX:
    {
      s_log << p << "stmt: ccdoc_begin\n";
      cstrs_itr_t itr = m_tokens.begin();
      for(;itr!=m_tokens.end();++itr) {
	s_log << p << "stmt:   token: '" << (*itr);
	s_log << "'\n";
      }
      s_log << p << "stmt: ccdoc_end\n";
      break;
    }
  case STMT_CLASS_END:
  case STMT_NAMESPACE_END:
  case STMT_STRUCT_END:
  case STMT_UNION_END:
    // Ignore hier-id and tokens for end statements.
    break;
  default:
    {
      s_log << p << "stmt: tokens:";
      cstrs_itr_t itr = m_tokens.begin();
      for(;itr!=m_tokens.end();++itr) {
	s_log << " " << (*itr);
      }
      s_log << "\n";
      break;
    }
  }
  s_log << p << "stmt: end\n";
}
// ================================================================
//
// Comment class.
//
// ================================================================
// ================================================================
// Constructor
// ================================================================
ccdoc::statement::comment::comment()
  : m_suffix(false),
    m_stmt(0)
{
}
// ================================================================
// Constructor
// ================================================================
ccdoc::statement::comment::comment(base* stmt)
  : m_suffix(false),
    m_stmt(stmt)
{
  if(stmt) {
    set(stmt->get_tokens());
  }
}
// ================================================================
// Destructor
// ================================================================
ccdoc::statement::comment::~comment()
{
}
// ================================================================
// Set
// ================================================================
void ccdoc::statement::comment::set(const base::cstrs_t& tokens)
{
  base::cstrs_citr_t i = tokens.begin();
  base::cstrs_citr_t e = tokens.end();

  string token;
  if(!set(token,i,e,"@{"   )) return;

  if(!set(token,i,e,"@file")) return;
  if(!set(token,i,e,"2")) return;
  if(!set(token,i,e)) return;
  add_file(token);
  if(!set(token,i,e)) return;
  add_lineno(token);

  if(!set_scalar(token,i,e,"@type")) return;
  m_suffix = token == "@suffix";

  if(!set       (m_short_desc,i,e,"@short_desc")) return;
  if(!set       (m_long_desc ,i,e,"@long_desc")) return;
  if(!set       (m_params    ,i,e,"@params","@param")) return;
  if(!set       (m_returns   ,i,e,"@returns")) return;
  if(!set       (m_exceptions,i,e,"@exceptions","@exception")) return;
  if(!set       (m_deprecated,i,e,"@deprecated")) return;
  if(!set       (m_authors   ,i,e,"@authors")) return;
  if(!set_scalar(m_version   ,i,e,"@version")) return;
  if(!set       (m_sees      ,i,e,"@sees","@see")) return;
  if(!set_scalar(m_since     ,i,e,"@since")) return;
  if(!set_scalar(m_source    ,i,e,"@source")) return;
  if(!set       (m_pkg       ,i,e,"@pkg")) return;
  if(!set       (m_pkgdoc    ,i,e,"@pkgdoc")) return;
  if(!set       (token       ,i,e,"@}")) return;
}
// ================================================================
// Set scalar token
// ================================================================
bool ccdoc::statement::comment::set_scalar(string& var,
					   base::cstrs_citr_t& i,
					   base::cstrs_citr_t& e,
					   const char* match)
{
  string token;
  if(!set(token,i,e,match)) return false;
  if(!set(token,i,e)) return false;
  if( token == "1" ) {
    if(!set(var,i,e)) return false;
  }
  else if( token != "0" ) {
    // Issue 0053
    s_log.warning() << "Internal comment parsing error";
    if(m_stmt) {
      s_log << " at line " << m_stmt->get_lineno()
	    << " in " << m_stmt->get_file();
    }
    s_log << ".\n"
	  << "\tExpected 0 or 1 for the number of " << match << " arguments.\n"
	  << "\tThis comment will be ignored.\n"
	  << s_log.enable();
    clear();
    return false;
  }
  return true;
}
// ================================================================
// Set token
// ================================================================
bool ccdoc::statement::comment::set(string& token,
				    base::cstrs_citr_t& i,
				    base::cstrs_citr_t& e,
				    const char* match)
{
  if(i == e) {
    s_log.warning() << "Internal comment parsing error, empty list";
    if(m_stmt) {
      // Issue 0053
      s_log << " at line " << m_stmt->get_lineno()
	    << " in " << m_stmt->get_file();
    }
    s_log << ".\n"
	  << "\tUnexpected EOF for '" << token <<"'.\n"
	  << "\tThis comment will be ignored.\n"
	  << s_log.enable();
    clear();
    return false;
  }
  if( match && *match && ::strcmp(*i,match) ) {
    // Match failed, report a warning.
    s_log.warning() << "Internal comment parsing error, match failed";
    if(m_stmt) {
      // Issue 0053
      s_log << " at line " << m_stmt->get_lineno()
	    << " in " << m_stmt->get_file();
    }
    s_log << ".\n"
	  << "\tExpected token '" << match << "' but found '" << *i << "'.\n"
	  << "\tThis comment will be ignored.\n"
	  << s_log.enable();
    clear();
    return false;
  }
  token = *i;
  ++i;
  return true;
}
// ================================================================
// Set token
// ================================================================
bool ccdoc::statement::comment::set(base::strs_t& vec,
				    base::cstrs_citr_t& i,
				    base::cstrs_citr_t& e,
				    const char* match)
{
  string token;
  if(!set(token,i,e,match)) return false;
  if(!set(token,i,e)) return false;
  unsigned k = atoi(token.c_str());
  for(unsigned j=0;j<k;++j) {
    if(!set(token,i,e)) return false;
    vec.push_back(token);
  }
  return true;
}
// ================================================================
// Set token
// ================================================================
bool ccdoc::statement::comment::set(base::strss_t& vecvec,
				    base::cstrs_citr_t& i,
				    base::cstrs_citr_t& e,
				    const char* match,
				    const char* match1)
{
  string token;
  if(!set(token,i,e,match)) return false;
  if(!set(token,i,e)) return false;
  unsigned k = atoi(token.c_str());
  for(unsigned j=0;j<k;++j) {
    base::strs_t vec;
    if(!set(vec,i,e,match1)) return false;
    vecvec.push_back(vec);
  }
  return true;
}
// ================================================================
// Get.
// ================================================================
void ccdoc::statement::comment::get(base::strs_t& tokens)
{

  tokens.push_back("@{");
  tokens.push_back("@file");
  tokens.push_back("2");
  tokens.push_back(m_file);
  tokens.push_back(m_lineno);
  tokens.push_back("@type");
  tokens.push_back("1");
  if( m_suffix ) {
    tokens.push_back("@suffix");
  }
  else {
    tokens.push_back("@prefix");
  }
  get( tokens, m_short_desc, "@short_desc" );
  get( tokens, m_long_desc, "@long_desc" );
  get( tokens, m_params, "@param" );
  get( tokens, m_returns, "@returns" );
  get( tokens, m_exceptions, "@exception" );
  get( tokens, m_deprecated, "@deprecated" );
  get( tokens, m_authors, "@authors" );
  if( m_version.size() ) {
    tokens.push_back("@version");
    tokens.push_back( "1" );
    tokens.push_back( m_version );
  }
  else {
    tokens.push_back("@version");
    tokens.push_back("0");
  }
  get( tokens, m_sees, "@see" );

  tokens.push_back("@since");
  if( m_since.size() ) {
    tokens.push_back("1");
    tokens.push_back(m_since);
  }
  else {
    tokens.push_back("0");
  }

  tokens.push_back("@source");
  if( m_source.size() ) {
    tokens.push_back("1");
    tokens.push_back(m_source);
  }
  else {
    tokens.push_back("0");
  }
  get( tokens, m_pkg, "@pkg" );
  get( tokens, m_pkgdoc, "@pkgdoc" );
  tokens.push_back("@}");
}
// ================================================================
// Get.
// ================================================================
void ccdoc::statement::comment::get(base::strs_t& tokens,
				    const base::strs_t& vec,
				    const char* type)
{
  if(vec.size()) {
    char nbuf[16];
    sprintf(nbuf,"%d",vec.size());

    tokens.push_back(type);
    tokens.push_back(nbuf);
    
    base::strs_citr_t itr = vec.begin();
    for(;itr!=vec.end();++itr) {
      tokens.push_back(*itr);
    }
  }
  else {
    tokens.push_back(type);
    tokens.push_back("0");
  }
}
// ================================================================
// Insert tokens
// ================================================================
void ccdoc::statement::comment::get(base::strs_t& tokens,
				    base::strss_t& vecvec,
				    const char* type)
{
  string x = type;
  x += "s";
  if( vecvec.size() ) {
    tokens.push_back(x);
    char nbuf[16];
    sprintf(nbuf,"%d",vecvec.size());
    tokens.push_back(nbuf);
    base::strss_itr_t itr = vecvec.begin();
    for(;itr!=vecvec.end();++itr) {
      base::strs_t& vec = *itr;

      sprintf(nbuf,"%d",vec.size());

      tokens.push_back(type);
      tokens.push_back(nbuf);

      base::strs_citr_t itr1 = vec.begin();
      for(;itr1!=vec.end();++itr1) {
	tokens.push_back(*itr1);
      }
    }
  }
  else {
    tokens.push_back(x);
    tokens.push_back("0");
  }
}
// ================================================================
// Empty?
// ================================================================
bool ccdoc::statement::comment::empty() const
{
  if(m_authors.size()) return false;
  if(m_exceptions.size()) return false;
  if(m_long_desc.size()) return false;
  if(m_params.size()) return false;
  if(m_pkg.size()) return false;
  if(m_pkgdoc.size()) return false;
  if(m_returns.size()) return false;
  if(m_sees.size()) return false;
  if(m_short_desc.size()) return false;
  if(m_since.size()) return false;
  if(m_source.size()) return false;
  if(m_version.size()) return false;
  return true;
}
// ================================================================
// Clear
// ================================================================
void ccdoc::statement::comment::clear()
{
  m_authors.clear();
  m_exceptions.clear();
  m_long_desc.clear();
  m_params.clear();
  m_pkg.clear();
  m_pkgdoc.clear();
  m_returns.clear();
  m_sees.clear();
  m_short_desc.clear();
  m_since  = "";
  m_source  = "";
  m_version = "";
}
// ================================================================
// Add since
// ================================================================
void ccdoc::statement::comment::add_since(const string& name)
{
  m_since=name;
}
// ================================================================
// Add source
// ================================================================
void ccdoc::statement::comment::add_source(const string& name)
{
  m_source=name;
}
// ================================================================
// Add version
// ================================================================
void ccdoc::statement::comment::add_version(const string& name)
{
  m_version=name;
}
// ================================================================
// Add author
// ================================================================
void ccdoc::statement::comment::add_author(const string& name)
{
  m_authors.push_back(name);
}
// ================================================================
// Add deprecated
// ================================================================
void ccdoc::statement::comment::add_deprecated(const string& desc)
{
  m_deprecated.push_back(desc);
}
// ================================================================
// Add exception
// ================================================================
void ccdoc::statement::comment::add_new_exception(const string& name,
						  const string& desc)
{
  base::strs_t vec;
  vec.push_back(name);
  vec.push_back(desc);
  m_exceptions.push_back(vec);
}
// ================================================================
// Add exception
// ================================================================
void ccdoc::statement::comment::add_new_exception(const string& name)
{
  base::strs_t vec;
  vec.push_back(name);
  m_exceptions.push_back(vec);
}
// ================================================================
// Add exception
// ================================================================
void ccdoc::statement::comment::add_exception_desc(const string& desc)
{
  if( m_exceptions.size() ) {
    base::strs_t& vec = m_exceptions.back();
    vec.push_back(desc);
  }
}
// ================================================================
// Add file
// ================================================================
void ccdoc::statement::comment::add_file(const string& name)
{
  m_file = name;
}
// ================================================================
// Add lineno
// ================================================================
void ccdoc::statement::comment::add_lineno(const string& name)
{
  m_lineno = name;
}
// ================================================================
// Add long desc
// ================================================================
void ccdoc::statement::comment::add_long_desc(const string& desc)
{
  m_long_desc.push_back(desc);
}
// ================================================================
// Add params desc
// ================================================================
void ccdoc::statement::comment::add_new_param(const string& name)
{
  base::strs_t vec;
  vec.push_back(name);
  m_params.push_back(vec);
}
// ================================================================
// Add params desc
// ================================================================
void ccdoc::statement::comment::add_new_param(const string& name,
					      const string& desc)
{
  base::strs_t vec;
  vec.push_back(name);
  vec.push_back(desc);
  m_params.push_back(vec);
}
// ================================================================
// Add params desc
// ================================================================
void ccdoc::statement::comment::add_param_desc(const string& desc)
{
  if( m_params.size() ) {
    base::strs_t& vec = m_params.back();
    vec.push_back(desc);
  }
}
// ================================================================
// Add pkg
// ================================================================
void ccdoc::statement::comment::add_pkg(const string& desc)
{
  m_pkg.push_back(desc);
}
// ================================================================
// Add pkgdoc
// ================================================================
void ccdoc::statement::comment::add_pkgdoc(const string& desc)
{
  m_pkgdoc.push_back(desc);
}
// ================================================================
// Add pkgdoc_tid
// ================================================================
void ccdoc::statement::comment::add_pkgdoc_tid(const string& desc)
{
  // The package doc tag is always inserted at the
  // beginning of the pkgdoc vector as @tid <name>.
  base::strs_t vec;
  vec.push_back("@tid");
  vec.push_back(desc);
  base::strs_itr_t itr = m_pkgdoc.begin();
  for(;itr!=m_pkgdoc.end();++itr) {
    vec.push_back(*itr);
  }
  m_pkgdoc.clear();
  m_pkgdoc = vec;
}
// ================================================================
// Add returns
// ================================================================
void ccdoc::statement::comment::add_returns(const string& desc)
{
  m_returns.push_back(desc);
}
// ================================================================
// Add sees desc
// ================================================================
void ccdoc::statement::comment::add_new_see(const string& name)
{
  base::strs_t vec;
  vec.push_back(name);
  m_sees.push_back(vec);
}
// ================================================================
// Add sees desc
// ================================================================
void ccdoc::statement::comment::add_new_see(const string& name,
					      const string& desc)
{
  base::strs_t vec;
  vec.push_back(name);
  vec.push_back(desc);
  m_sees.push_back(vec);
}
// ================================================================
// Add sees desc
// ================================================================
void ccdoc::statement::comment::add_see_desc(const string& desc)
{
  if( m_sees.size() ) {
    base::strs_t& vec = m_sees.back();
    vec.push_back(desc);
  }
}
// ================================================================
// Add short desc
// ================================================================
void ccdoc::statement::comment::add_short_desc(const string& desc)
{
  m_short_desc.push_back(desc);
}
// ================================================================
// Get the pkgdoc url.
// ================================================================
const string& ccdoc::statement::comment::get_pkgdoc_url() const
{
  base::strs_citr_t itr = m_pkgdoc.begin();
  for(;itr!=m_pkgdoc.end();++itr) {
    if( *itr == "@url" ) {
      ++itr;
      if( itr!=m_pkgdoc.end())
	return *itr;
      break;
    }
  }
  static string null;
  return null;
}
// ================================================================
// Get the pkgdoc tid.
// ================================================================
const string& ccdoc::statement::comment::get_pkgdoc_tid() const
{
  base::strs_citr_t itr = m_pkgdoc.begin();
  for(;itr!=m_pkgdoc.end();++itr) {
    if( *itr == "@tid" ) {
      ++itr;
      if( itr!=m_pkgdoc.end())
	return *itr;
      break;
    }
  }
  static string null;
  return null;
}
