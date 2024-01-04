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
static char ccdoc_rcsid[] = "$Id: database.cc,v 1.19 2001/11/27 19:02:39 Administrator Exp $";

#include "database.h"
#include <iomanip>
#include <fstream>
#include <cstdio>

// ================================================================
// Constructor.
// ================================================================
ccdoc::database::database(switches& sw)
  : m_sw(sw),
    m_debug(false),
    m_comment_id(0),
    m_root(0),
    m_write(true)
{
  m_root = new statement::base;
  m_root->set_type( statement::base::STMT_PACKAGE );
  m_root->set_id( m_sw.root() );
  m_root->set_file( m_sw.db() );

  string db = m_sw.db();
  if( db.size() == 0 ) {
    throw ccdoc::exceptions::invalid_database
      (__FILE__,
       __LINE__,
       "<NULL>",
       "Illegal file name.");
  }
  // Read the database, if it exists.
  read();
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::database::~database() {
  write();
}
// ================================================================
// get_package
// ================================================================
void ccdoc::database::parse_path(string path,vector<string>& ids)
{
  // Handle the special case of the top package.
  // The user can change this on the command line
  // by specifying -pkg <new_name>.
  if( path == "[ROOT]" || path == m_sw.default_root() )
    return;

  // Package names can be separated by periods or
  // double colons. Here are some examples:
  //   A.B.C
  //   A::B::C
  string name;
  string::iterator i = path.begin();
  for(;i!=path.end();++i) {
    if( *i == ':' ) {
      ++i;
      if( i==path.end() ) {
	// The user specified something like "FOO::BAR:".
	// This is an invalid name, don't construct it.
	s_log.warning()
	  << "Illegal package name separator ':', expected '::' in path '"
	  << path 
	  << "', ignored."
	  << "\n"
	  << s_log.enable();
	name += ':';
      }
      else if( *i != ':' ) {
	// The user specified something like "FOO:BAR".
	// This is an invalid name, don't construct it.
	s_log.warning()
	  << "Illegal package name separator ':', expected '::' in path '"
	  << path 
	  << "', ignored."
	  << "\n"
	  << s_log.enable();
	name += ':';
	name += *i;
      }
      else {
	if(name.size()) {
	  // This name was separated by a '::'.
	  // Add the name.
	  // Make sure that we filter out the top level package name.
	  if( ids.size() )
	    ids.push_back(name);
	  else if( name != "[ROOT]" && name != m_sw.default_root() )
	    ids.push_back(name);
	}
	else if( ids.size() ) {
	  // The ids.size() check it to make sure that we
	  // don't report a problem for '::A'.
	  // The user specified something like "FOO::::BAR".
	  // This is an invalid name, don't construct it.
	  s_log.warning()
	    << "Illegal NULL package name '"
	    << path 
	    << "', ignored."
	    << "\n"
	    << s_log.enable();
	}
	name = "";
      }
      continue;
    }
    else if( *i == '.' ) {
      if(name.size()) {
	// This name was separated by a '.'.
	// Add the name.
	// Make sure that we filter out the top level package name.
	if( ids.size() )
	  ids.push_back(name);
	else if( name != "[ROOT]" && name != m_sw.default_root() )
	  ids.push_back(name);
      }
      else {
	// The user specified something like "FOO..BAR".
	// This is an invalid name, don't construct it.
	s_log.warning()
	  << "Illegal NULL package name '"
	  << path 
	  << "', ignored."
	  << "\n"
	  << s_log.enable();
      }
      name = "";
      continue;
    }
    name += *i;
  }

  // Tack on the final name in the package list.
  // Make sure the we do not tack on the top package
  // name.
  if( name.size() ) {
    if( ids.size() )
      ids.push_back(name);
    else if( name != "[ROOT]" && name != m_sw.default_root() )
      ids.push_back(name);
  }
}
// ================================================================
// get_statement
// ================================================================
ccdoc::statement::base*
ccdoc::database::get_statement(string path)
{
  statement::base* stmt = m_root;
  vector<string> ids;
  parse_path(path,ids);
  if( ids.size() == 0 ) {
    return stmt;
  }
  vector<string>::iterator itr = ids.begin();
  for(;itr!=ids.end();++itr) {
    string& name = *itr;
    stmt = stmt->get_child_by_id(name);
    if(!stmt)
      break;
  }
  return stmt;
}
// ================================================================
// clear path map
// ================================================================
void ccdoc::database::clear_path_map()
{
  m_path_map.clear();
}
// ================================================================
// load path map
// ================================================================
void ccdoc::database::load_path_map()
{
  if( !m_path_map.size() ) // Issue 0041
    load_path_map(m_root);
}
// ================================================================
// load path map
// ================================================================
void ccdoc::database::load_path_map(ccdoc::statement::base* stmt)
{
  if( stmt ) {
    if( stmt->get_type() != statement::base::STMT_COMMENT_PKGDOC &&
	stmt->get_type() != statement::base::STMT_COMMENT_PKGDOC_URL &&
	stmt->get_type() != statement::base::STMT_COMMENT_PREFIX &&
	stmt->get_type() != statement::base::STMT_COMMENT_SUFFIX ) {
      // Issue 0041
      // Ignore comments, they don't have meaningful names.
      const char* pid = stmt->get_id();
      if( *pid ) {
	string id;
	stmt->get_hier_id_no_pkgs(id);
	if( id.size() ) {
	  path_map_itr_type itr = m_path_map.find(id);
	  if( itr == m_path_map.end() ) {
	    pair<string,statement::base::stmts_t > p;
	    p.first = id;
	    p.second.push_back(stmt);
	    m_path_map.insert(p);
	  }
	  else {
	    // Issue 0046:
	    (*itr).second.push_back(stmt);
	  }
	}
      }
    }
    statement::base::stmts_t& children = stmt->get_children();
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      load_path_map(*itr);
    }
  }
}
// ================================================================
// erase from path map
// ================================================================
void ccdoc::database::erase_from_path_map(ccdoc::statement::base* stmt)
{
  if( stmt ) {
    if( stmt->get_type() != statement::base::STMT_COMMENT_PKGDOC &&
	stmt->get_type() != statement::base::STMT_COMMENT_PKGDOC_URL &&
	stmt->get_type() != statement::base::STMT_COMMENT_PREFIX &&
	stmt->get_type() != statement::base::STMT_COMMENT_SUFFIX ) {
      // Issue 0041
      // Ignore comments, they don't have meaningful names.
      const char* pid = stmt->get_id();
      if( *pid ) {
	string id;
	stmt->get_hier_id_no_pkgs(id);
	if( id.size() ) {
	  path_map_itr_type itr = m_path_map.find(id);
	  if( itr != m_path_map.end() ) {
            // Issue 0117
            //   Contributed by Chris Martin 2001/11/25
            statement::base::stmts_t& vec = (*itr).second;
            statement::base::stmts_itr_t vitr = vec.begin();
            for(;vitr != vec.end(); ++vitr) {
              if( (*vitr) == stmt ) {
                vec.erase(vitr);
                break;
              }
            }
            if( vec.size() == 0 )
              m_path_map.erase(itr);
	  }
	}
      }
    }
    statement::base::stmts_t& children = stmt->get_children();
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      erase_from_path_map(*itr);
    }
  }
}
// ================================================================
// get_statement_no_pkgs
// ================================================================
void ccdoc::database::get_stmt_no_pkgs(string path,
				       statement::base::stmts_t& stmts)
{
  // Always do a direct lookup for speed.
  path_map_itr_type itr = m_path_map.find(path);
  if( itr != m_path_map.end() ) {
    stmts = (*itr).second;
    return;
  }

  // The path was not found, see whether there are embedded #'s.
  // This allows users to use the javadoc syntax more freely.
  size_t hash_marks = path.find("#");
  if( hash_marks != string::npos ) {
    // Convert the hash marks to '::' to conform to ccdoc syntax
    // and try again.
    string newpath;
    string::iterator itr = path.begin();
    for(;itr!=path.end();++itr) {
      if( *itr == '#' )
        newpath += "::";
      else
        newpath += *itr;
    }
    get_stmt_no_pkgs( newpath, stmts );
    return;
  }
  
  // Issue 0080
  //  The path was not found, try stripping the
  //  trailing w/s.
  // bzoe 10/18/01 -- try stripping trailing spaces.
  size_t last = path.find_last_not_of(" \t\r\n");
  // no trailing spaces.
  if (string::npos == last || last+1 == path.size())
    return;

  path = path.substr(0,last+1);
  itr = m_path_map.find(path);
  if( itr != m_path_map.end() ) {
    stmts = (*itr).second;
  }
}
// ================================================================
// get_create_package
// ================================================================
ccdoc::statement::base*
ccdoc::database::get_create_package(string path)
{
  statement::base* pkg = m_root;
  vector<string> ids;
  parse_path(path,ids);
  if( ids.size() == 0 ) {
    return pkg;
  }
  vector<string>::iterator itr = ids.begin();
  for(;itr!=ids.end();++itr) {
    string& name = *itr;
    statement::base* child_pkg = pkg->get_child_by_id(name);
    if(!child_pkg) {
      child_pkg = new statement::base;
      child_pkg->set_id(name);
      child_pkg->set_type(statement::base::STMT_PACKAGE);
      child_pkg->set_parent(pkg);
      child_pkg->set_file( m_sw.db() );
    }
    pkg = child_pkg;
  }
  return pkg;
}
// ================================================================
// Remove file statements.
// ================================================================
void ccdoc::database::remove_file_statements(const char* file)
{
  if(file) {
    remove_file_statements(file,m_root);
  }
}
// ================================================================
// Remove file statements.
// ================================================================
void ccdoc::database::remove_file_statements(const char* file,
					     statement::base* stmt)
{
  if(stmt) {
    if( stmt->get_type() == statement::base::STMT_PACKAGE ||
	stmt->get_type() == statement::base::STMT_NAMESPACE_BEGIN ||
	stmt->get_type() == statement::base::STMT_NAMESPACE_END ) {
      // Make a copy of the the children so that a lower level
      // deletion does not make the iterator invalid.
      statement::base::stmts_t vec = stmt->get_children();
      statement::base::stmts_itr_t itr = vec.begin();
      for(;itr!=vec.end();++itr) {
	remove_file_statements(file,*itr);
      }
    }
    else if(!::strcmp(file,stmt->get_file())) {
      // This is not a package or a namespace and the 
      // files names match, delete it.
      delete stmt;
    }
  }
}
// ================================================================
// read
// ================================================================
void ccdoc::database::read()
{
  if( m_sw.verbose() ) {
    s_log << "db: read begins\n";
  }

  // Write out the database.
  string db = m_sw.db();
  ifstream is(db.c_str());
  if(!is) {
    // The database does not exist.
    if( m_sw.verbose() ) {
      s_log << "db: does not exist\n";
      s_log << "db: read ends\n";
    }
    return;
  }

  string token;

  // ================================================
  // Read the header.
  // ================================================
  is >> token;
  if( token != "ccdoc" ) {
    read_error(1,"ccdoc",token);
    return;
  }
  is >> token;
  if( token != "v0.8" ) {
    read_error(1,"v0.8",token);
    return;
  }
  
  // ================================================
  // Read the records.
  // ================================================
  bool ok = true;
  is >> token;
  if( token == "verbose" ) {
    ok = read_verbose(is);
  }
  else if( token == "terse" ) {
    ok = read_terse(is);
  }
  else {
    read_error(1,"verbose or terse",token);
    return;
  }
  if( !ok ) {
    if( m_root->get_children().size() ) {
      // The read failed, delete all of the children.
      // Leave the root intact.
      statement::base::stmts_t vec = m_root->get_children();
      statement::base::stmts_itr_t itr = vec.begin();
      for(;itr!=vec.end();++itr) {
	statement::base* child = *itr;
	delete child;
      }
    }
  }

  // ================================================
  // Attach the comments:
  // Loop through the statements, grab the comment records
  // and attach them.
  // ================================================
  if( ok ) {
    statement::base::stmts_t stmts;
    load(stmts);
    if( stmts.size() ) {
      statement::base::stmts_itr_t itr = stmts.begin();
      for(;itr!=stmts.end();++itr) {
	statement::base* rec = *itr;
	if( rec->get_type() == statement::base::STMT_COMMENT_PREFIX ) {
	  // Attach this comment to the next statement.
	  statement::base* comm = rec;
	  ++itr;
	  if( itr == stmts.end() )
	    break; // Issue 0020
	  statement::base* stmt = *itr;
	  stmt->set_comment( comm );
	  comm->set_comment( stmt );
	  --itr; // Issue 0036
	}
	else if( rec->get_type() == statement::base::STMT_COMMENT_SUFFIX ) {
	  // Attach this comment to the previous.
	  statement::base* comm = rec;
	  if( itr != stmts.begin() ) {
	    --itr;
	    statement::base* stmt = *itr;
	    stmt->set_comment( comm );
	    comm->set_comment( stmt );
	    ++itr;
	  }
	}
	else if( rec->get_type() == statement::base::STMT_COMMENT_PKGDOC_URL ||
		 rec->get_type() == statement::base::STMT_COMMENT_PKGDOC ) {
	  // Get the pkgdoc id.
	  statement::base* comm = rec;
	  statement::comment comm_stmt(comm);
	  
	  string pkgdoc_id;
	  const vector<string>& pkgdoc_tokens = comm_stmt.get_pkgdoc();
	  vector<string>::const_iterator itr1 = pkgdoc_tokens.begin();
	  if( itr1 != pkgdoc_tokens.end() ) {
	    // Issue 0025
	    // Issue 0031
	    // Check for the special case of the @url or the @tid field.
	    for(;itr1!=pkgdoc_tokens.end();++itr1) {
	      if( *itr1 == "@url" || *itr1 == "@tid" ) {
		++itr1;
		if( itr1==pkgdoc_tokens.end() ) {
		  break;
		}
		continue;
	      }
	      if( pkgdoc_id.size() )
		pkgdoc_id += "::";
	      pkgdoc_id += *itr1;
	    }
	  }
	  
	  statement::base* stmt = get_create_package( pkgdoc_id );
	  stmt->set_comment( comm );
	  comm->set_comment( stmt );
	}
      }
    }
  }

  if( m_sw.verbose() ) {
    s_log << "db: read ends\n";
  }
}
// ================================================================
// Read verbose format
// ================================================================
bool ccdoc::database::read_verbose(istream& is)
{
  // ================================================
  // Read the string table.
  // ================================================
  unsigned lineno = 1;
  vector<string> strings;
  if( !read_string_table(is,lineno,strings) )
    return false;
  
  // ================================================
  // Read the statement header.
  // S <hex_num>
  // ================================================
  string token;
  unsigned sz = 0;
  is >> token;
  is >> hex >> sz;
  lineno++;
  if( token != "S" ) {
    read_error(lineno,"S",token);
    return false;
  }
  
  // ================================================
  // Read the statements.
  // ================================================
  statement::base::stmts_t stmts;
  stmts.push_back(m_root); 
  statement::base::ACCESS stmt_access = statement::base::STMT_PUBLIC;
  string stmt_file;
  for(;sz>0;--sz) {
    lineno++;
    is >> token;

    // ================================================
    // Read a statement.
    // ================================================
    if( token == "s" ) {
      // ================================================
      // Get the id.
      // ================================================
      lineno++;
      string stmt_id;
      {
	const char* str = read_verbose_string(is,lineno);
	if(!str)
	  return false;
	stmt_id = str;
      }
      
      // ================================================
      // Get the statement tag.
      // ================================================
      lineno++;
      unsigned stmt_tag = 0;
      is >> hex >> stmt_tag;
      if( stmt_tag > 0 && stmt_tag != stmts.size() ) {
	  s_log.warning()
	    << "Syntax error.\n"
	    << "\tUnexpected statement tag " << stmt_tag << " found at line "
	    << lineno
	    << "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
	    << "\tExpected tag " << stmts.size() << ".\n"
	    << "\tThe db contents will be ignored.\n"
	    << s_log.enable();
	  return false;
      }
      
      // ================================================
      // Get the parent tag.
      // ================================================
      lineno++;
      unsigned parent_tag = 0;
      is >> hex >> parent_tag;
      
      // ================================================
      // Get the type.
      // ================================================
      lineno++;
      is >> token;
      statement::base::TYPE stmt_type;
      stmt_type = statement::base::get_terse_type(token);
      
      // ================================================
      // Get the extern.
      // ================================================
      lineno++;
      string stmt_extern;
      {
	const char* str = read_verbose_string(is,lineno);
	if(!str)
	  return false;
	stmt_extern = str;
      }
      
      // ================================================
      // Statement lineno.
      // ================================================
      lineno++;
      unsigned stmt_lineno = 0;
      is >> hex >> stmt_lineno;

      // ================================================
      // Get the number of tokens.
      // ================================================
      lineno++;
      unsigned num_tokens = 0;
      is >> hex >> num_tokens;

      // ================================================
      // Get the tokens.
      // ================================================
      vector<string> stmt_tokens;
      for(;num_tokens>0;--num_tokens) {
	lineno++;
	const char* str = read_verbose_string(is,lineno);
	if(!str)
	  return false;
	stmt_tokens.push_back(str);
      }

      // ================================================
      // Create the statement.
      // Ignore the first one, it is always the root.
      // ================================================
      if( stmt_tag ) {
	statement::base* parent = 0;
	if( parent_tag >= stmts.size() ) {
	  s_log.warning()
	    << "Syntax error.\n"
	    << "\tUnexpected parent tag " << parent_tag << " found at line "
	    << lineno
	    << "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
	    << "\tExpected a tag less than " << stmts.size() << ".\n"
	    << "\tThe db contents will be ignored.\n"
	    << s_log.enable();
	  return false;
	}
	parent = stmts[parent_tag];
	statement::base* stmt = new statement::base;
	stmt->set_id(stmt_id.c_str());
	stmt->set_tag(stmt_tag);
	stmt->set_type(stmt_type);
	stmt->set_access(stmt_access);
	stmt->set_extern(stmt_extern.c_str());
	stmt->set_lineno(stmt_lineno);
	stmt->set_file(stmt_file.c_str());
	stmt->set_tokens(stmt_tokens);
	stmt->set_parent( parent );
	stmts.push_back(stmt);
      }
    }
    // ================================================
    // Read a modal file statement.
    // ================================================
    else if( token == "f" ) {
      const char* str = read_verbose_string(is,lineno);
      if(!str)
	return false;
      stmt_file = str;
      ++sz; // This doesn't count as a statement.
    }
    // ================================================
    // Read a modal access statement.
    // ================================================
    else if( token == "a" ) {
      is >> token;
      stmt_access = statement::base::get_terse_access(token);
      ++sz; // This doesn't count as a statement.
    }
    // ================================================
    // Unrecognized token, report an error.
    // ================================================
    else {
      read_error(lineno,"s or f",token);
    }
  }

  // ================================================
  // Make sure we are at the end.
  // ================================================
  is >> token;
  lineno++;
  if( token != "e" ) {
    read_error(lineno,"e",token);
    return false;
  }

  return true;
}
// ================================================================
// Read terse format
// ================================================================
bool ccdoc::database::read_terse(istream& is)
{
  // ================================================
  // Read the string table.
  // ================================================
  unsigned lineno = 1;
  vector<string> strings;
  if( !read_string_table(is,lineno,strings) )
    return false;
  
  // ================================================
  // Read the statement header.
  // S <hex_num>
  // ================================================
  string token;
  unsigned sz = 0;
  is >> token;
  is >> hex >> sz;
  lineno++;
  if( token != "S" ) {
    read_error(lineno,"S",token);
    return false;
  }

  // ================================================
  // Read the statements.
  // ================================================
  statement::base::stmts_t stmts;
  stmts.push_back(m_root);
  statement::base::ACCESS stmt_access = statement::base::STMT_PUBLIC;
  string stmt_file;
  for(;sz>0;--sz) {
    lineno++;
    is >> token;

    // ================================================
    // Read a statement.
    // ================================================
    if( token == "s" ) {
      // ================================================
      // Get the id.
      // ================================================
      lineno++;
      string stmt_id;
      {
	const char* str = read_terse_string(is,lineno,strings);
	if(!str)
	  return false;
	stmt_id = str;
      }
      
      // ================================================
      // Get the statement tag.
      // ================================================
      lineno++;
      unsigned stmt_tag = 0;
      is >> hex >> stmt_tag;
      if( stmt_tag > 0 && stmt_tag != stmts.size() ) {
	  s_log.warning()
	    << "Syntax error.\n"
	    << "\tUnexpected statement tag " << stmt_tag << " found at line "
	    << lineno
	    << "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
	    << "\tExpected tag " << stmts.size() << ".\n"
	    << "\tThe db contents will be ignored.\n"
	    << s_log.enable();
	  return false;
      }
      
      // ================================================
      // Get the parent tag.
      // ================================================
      lineno++;
      unsigned parent_tag = 0;
      is >> hex >> parent_tag;
      
      // ================================================
      // Get the type.
      // ================================================
      lineno++;
      statement::base::TYPE stmt_type = statement::base::STMT_IGNORE;
      {
	const char* str = read_terse_string(is,lineno,strings);
	if(!str)
	  return false;
	stmt_type = statement::base::get_terse_type(str);
      }
      
      // ================================================
      // Get the extern.
      // ================================================
      lineno++;
      string stmt_extern;
      {
	const char* str = read_terse_string(is,lineno,strings);
	if(!str)
	  return false;
	stmt_extern = str;
      }
      
      // ================================================
      // Statement lineno.
      // ================================================
      lineno++;
      unsigned stmt_lineno = 0;
      is >> hex >> stmt_lineno;

      // ================================================
      // Get the number of tokens.
      // ================================================
      lineno++;
      unsigned num_tokens = 0;
      is >> hex >> num_tokens;

      // ================================================
      // Get the tokens.
      // ================================================
      vector<string> stmt_tokens;
      for(;num_tokens>0;--num_tokens) {
	lineno++;
	const char* str = read_terse_string(is,lineno,strings);
	if(!str)
	  return false;
	stmt_tokens.push_back(str);
      }

      // ================================================
      // Create the statement.
      // Ignore the first one, it is always the root.
      // ================================================
      if( stmt_tag ) {
	statement::base* parent = 0;
	if( parent_tag >= stmts.size() ) {
	  s_log.warning()
	    << "Syntax error.\n"
	    << "\tUnexpected parent tag " << parent_tag << " found at line "
	    << lineno
	    << "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
	    << "\tExpected a tag less than " << stmts.size() << ".\n"
	    << "\tThe db contents will be ignored.\n"
	    << s_log.enable();
	  return false;
	}
	parent = stmts[parent_tag];
	statement::base* stmt = new statement::base;
	stmt->set_id(stmt_id.c_str());
	stmt->set_tag(stmt_tag);
	stmt->set_type(stmt_type);
	stmt->set_access(stmt_access);
	stmt->set_extern(stmt_extern.c_str());
	stmt->set_lineno(stmt_lineno);
	stmt->set_file(stmt_file.c_str());
	stmt->set_tokens(stmt_tokens);
	stmt->set_parent( parent );
	stmts.push_back(stmt);
      }
    }
    // ================================================
    // Read a modal file statement.
    // ================================================
    else if( token == "f" ) {
      const char* str = read_terse_string(is,lineno,strings);
      if(!str)
	return false;
      stmt_file = str;
      ++sz; // This doesn't count as statements.
    }
    // ================================================
    // Read a modal access statement.
    // ================================================
    else if( token == "a" ) {
      const char* str = read_terse_string(is,lineno,strings);
      if(!str)
	return false;
      stmt_access = statement::base::get_terse_access(str);
      ++sz; // This doesn't count as statements.
    }
    // ================================================
    // Unrecognized token, report an error.
    // ================================================
    else {
      read_error(lineno,"s or f",token);
    }
  }

  // ================================================
  // Make sure we are at the end.
  // ================================================
  is >> token;
  lineno++;
  if( token != "e" ) {
    read_error(lineno,"e",token);
    return false;
  }

  return true;
}
// ================================================================
// Read the string table. This is the same in both verbose and
// terse mode.
// ================================================================
bool ccdoc::database::read_string_table(istream& is,
					unsigned& lineno,
					vector<string>& strings)
{
  // ================================================
  // Read the string table header.
  // $ <hex_num>
  // ================================================
  lineno = 2;
  string token;
  is >> token;
  if( token != "$" ) {
    read_error(lineno,"$",token);
    return false;
  }
  
  // ================================================
  // Read each string entry.
  // <hex_num> <hex_num> <string>
  //  ^         ^         ^
  //  |         |         +----- The string.
  //  |         +--------------- The string length.
  //  +------------------------- The index.
  // ================================================
  unsigned sz = 0;
  is >> hex >> sz;
  if( sz ) {
    strings.reserve(sz);
  }
  
  for(;sz>0;--sz) {
    lineno++;
    unsigned index = 0;
    is >> hex >> index;

    // Now read the full string.
    // This must be done in raw mode to guarantee that
    // all w/s bits are read correctly.
    const char* str = read_verbose_string(is,lineno);
    if(!str)
      return false;
#if 0
    CCDOC_DEBUG_MADE_IT << "DEBUG:"
			<< " lineno=" << lineno
			<< " index=" << index
			<< " line='" << str << "'\n";
#endif    
    // Verify that the string was added to the
    // right place.
    if( strings.size() != index ) {
      s_log.warning()
	<< "Syntax error.\n"
	<< "\tUnexpected string index " << index << " found at line "
	<< lineno
	<< "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
	<< "\tExpected index " << strings.size() << ".\n"
	<< "\tThe db contents will be ignored.\n"
	<< s_log.enable();
      return false;
    }

    // Add the string to the strings list.
    strings.push_back(str);
  }
  return true;
}
// ================================================================
// Read verbose string.
// ================================================================
const char* ccdoc::database::read_verbose_string(istream& is,
						 unsigned lineno)
{
  static char token[65536];
  // The string format is: <len> <string>.
  token[0] = 0;
  unsigned len = 0;
  is >> hex >> len;
  if( len ) {
    char ch = 0;
    is.get(ch); // get the space
    if( ch != ' ' ) {
      char s[2];
      s[0] = ch;
      s[1] = 0;
      read_error(lineno," ",s);
      return 0;
    }
    char* p = token;
    for(;len>0;--len) {
      is.get(ch);
      *p++ = ch;
    }
    *p = 0;
  }
  return token;
}
// ================================================================
// Read terse string.
// ================================================================
const char* ccdoc::database::read_terse_string(istream& is,
					       unsigned lineno,
					       vector<string>& strings)
{
  // The string format is: <id>
  // This id is then looked up on the string table.
  // Make sure that there is a leading space.
  unsigned idx = 0;
  is >> hex >> idx;
  if( idx < strings.size() ) {
    return strings[idx].c_str();
  }

  s_log.warning()
    << "Syntax error.\n"
    << "\tUnexpected string index " << idx << " found at line "
    << lineno
    << "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
    << "\tExpected an index less than " << strings.size() << ".\n"
    << "\tThe db contents will be ignored.\n"
    << s_log.enable();

  return 0;
}
// ================================================================
// Read error
// ================================================================
void ccdoc::database::read_error(unsigned lineno,
				 const char* expected,
				 const string& found)
{
  read_error(lineno,expected,found.c_str());
}
// ================================================================
// Read error
// ================================================================
void ccdoc::database::read_error(unsigned lineno,
				 const char* expected,
				 const char* found)
{
  if( expected && found ) {
    s_log.warning()
      << "Syntax error.\n"
      << "\tUnexpected token '" << found << "' found at line "
      << lineno
      << "\n\tin ccdoc db '" << m_sw.db() << "'.\n"
      << "\tExpected token '" << expected << "'.\n"
      << "\tThe db contents will be ignored.\n"
      << s_log.enable();
  }
}
// ================================================================
// write
// ================================================================
void ccdoc::database::write(statement::base::stmts_t& vec,
			    statement::base* stmt)
{
  if( stmt ) {
    stmt->set_tag( vec.size() );
    vec.push_back(stmt);
    statement::base::stmts_t& children = stmt->get_children();
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      write(vec,*itr);
    }
  }
}
// ================================================================
// write
// ================================================================
void ccdoc::database::write()
{
  if( !m_write ) {
    return;
  }
  if( m_sw.verbose() ) {
    s_log << "db: write begins\n";
  }
  
  // Write out the database.
  string db = m_sw.db();
  ofstream os(db.c_str());
  if(!os) {
    // Cannot create the database, throw an exception.
    throw ccdoc::exceptions::invalid_database
      (__FILE__,
       __LINE__,
       db.c_str(),
       "Can't create the database.");
    return;
  }
  
  statement::base::stmts_t stmts;
  write(stmts,m_root);
  
  if( m_sw.verbose() ) {
    s_log << "db: writing " << stmts.size() << " statements\n";
  }
  
  // ================================================
  // The header is the same in verbose and terse modes.
  // ================================================
  os << "ccdoc v0.8 ";
  if( m_sw.verbose_format() )
    os << "verbose";
  else 
    os << "terse";
  os << "\n";
  
  // ================================================
  // String table. The syntax is the same in verbose
  // and terse modes but there are no entries in
  // verbose mode.
  // ================================================
  strmgr& mgr = statement::base::get_strmgr();
  os << "$ ";
  if( m_sw.verbose_format() ) {
    os << "0\n";
  }
  else {
    strmgr::str_coll_itr itr = mgr.begin();
    mgr.gen_maps();
    os << hex << mgr.size() << "\n";
    for(;itr!=mgr.end();++itr) {
      os << hex << (*itr).second << " ";
      os << hex << (*itr).first.size() << " ";
      os << (*itr).first << "\n";
    }
  }

  // ================================================
  // Statements section header.
  // ================================================
  os << "S " << hex << stmts.size() << "\n";

  // ================================================
  // Verbose format
  // ================================================
  if( m_sw.verbose_format() ) {
    string fn = "";
    statement::base::ACCESS stmt_access = statement::base::STMT_PUBLIC;
    statement::base::stmts_itr_t itr = stmts.begin();
    for(int i=0;itr!=stmts.end();++itr,++i) {
      statement::base* stmt = *itr;

      // Write out the modal file specifier.
      string new_fn = stmt->get_file();
      if( new_fn != fn ) {
	fn = new_fn;
	os << "f " << hex << fn.size() << " "
	   << fn << "\n";
      }

      // Write out the modal access specifier.
      if( stmt->get_access() != stmt_access ) {
	os << "a "
	   << stmt->get_terse_access_name()
	   << "\n";
	stmt_access = stmt->get_access();
      }

      // Write out the start of the statement: 's'.
      os << "s\n";

      // Write out the stmt id.
      os << hex << strlen(stmt->get_id()) << " "
	 << stmt->get_id() << "\n";

      // Write out the tag.
      os << hex << stmt->get_tag() << "\n"; // tag

      // Write out the parent tag.
      if( stmt->get_parent() ) {
	os << hex << stmt->get_parent()->get_tag() << "\n";
      }
      else {
	os << "0\n";
      }

      // Write out the type.
      os << stmt->get_terse_type_name() << "\n";

      // Write out the extern specifier.
      os << hex << strlen(stmt->get_extern()) << " "
	 << stmt->get_extern() << "\n";

      // Write out the lineno.
      os << hex << stmt->get_lineno() << "\n";
      
      // Note that children do not have to be stored,
      // they are implied by the the parent.
      // Note that the first entry is always the root.
      
      const statement::base::cstrs_t& tokens = stmt->get_tokens();
      statement::base::cstrs_citr_t titr = tokens.begin();
      os << hex << tokens.size() << "\n";
      for(;titr!=tokens.end();++titr) {
	const char* token = *titr;
	os << hex << strlen(token) << " " << token << "\n";
      }
    }
  }
  // ================================================
  // Terse format
  // ================================================
  else {
    string fn = "";
    statement::base::ACCESS stmt_access = statement::base::STMT_PUBLIC;
    statement::base::stmts_itr_t itr = stmts.begin();
    for(int i=0;itr!=stmts.end();++itr,++i) {
      statement::base* stmt = *itr;

      // Write out the modal file specifier.
      string new_fn = stmt->get_file();
      if( new_fn != fn ) {
	// The file is modal.
	fn = new_fn;
	os << "f\n";
	os << hex << mgr.get_idx(fn) << "\n";
      }

      // Write out the modal access specifier.
      if( stmt->get_access() != stmt_access ) {
	os << "a "
	   << hex << mgr.get_idx(stmt->get_terse_access_name())
	   << "\n";
	stmt_access = stmt->get_access(); 
     }

      // Write out the start of the statement: 's'.
      os << "s" << "\n";
      os << hex << mgr.get_idx(stmt->get_id()) << "\n";
      os << hex << stmt->get_tag() << "\n"; // tag
      if( stmt->get_parent() ) {
	os << hex << stmt->get_parent()->get_tag() << "\n";
      }
      else {
	os << "0\n";
      }
      os << hex << mgr.get_idx(stmt->get_terse_type_name()) << "\n";
      os << hex << mgr.get_idx(stmt->get_extern()) << "\n";
      os << hex << stmt->get_lineno() << "\n";

      const statement::base::cstrs_t& tokens = stmt->get_tokens();
      statement::base::cstrs_citr_t titr = tokens.begin();
      os << hex << tokens.size() << "\n";
      for(;titr!=tokens.end();++titr) {
	const char* token = *titr;
	os << hex << mgr.get_idx(token) << "\n";
      }
    }
  }
  
  os << "e\n";
  if( m_sw.verbose() ) {
    s_log << "db: write ends\n";
  }

}
// ================================================================
// Get the next comment id.
// ================================================================
void ccdoc::database::get_next_comment_id(string& id)
{
  static char buf[64];
  sprintf(buf,"$comment-%d",m_comment_id);
  m_comment_id++;
  id = buf;
}
// ================================================================
// Load all statements.
// ================================================================
void ccdoc::database::load(statement::base::stmts_t& vec)
{
  load(vec,m_root);
}
// ================================================================
// Load all statements.
// ================================================================
void ccdoc::database::load(statement::base::stmts_t& vec,
			   statement::base* stmt)
{
  if(stmt) {
    vec.push_back(stmt);
    statement::base::stmts_t& children = stmt->get_children();
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      load(vec,*itr);
    }
  }
}
// ================================================================
// Load all statements of a specific type.
// ================================================================
void ccdoc::database::load(statement::base::stmts_t& vec,
			   statement::base::TYPE t)
{
  load(vec,t,m_root);
}
// ================================================================
// Load all statements of a specific type.
// ================================================================
void ccdoc::database::load(statement::base::stmts_t& vec,
			   statement::base::TYPE t,
			   statement::base* stmt)
{
  if(stmt) {
    if( stmt->get_type() == t )
      vec.push_back(stmt);
    statement::base::stmts_t& children = stmt->get_children();
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      load(vec,t,*itr);
    }
  }
}
// ================================================================
// Load all statements of a specific type.
// ================================================================
void ccdoc::database::load_top(statement::base::stmts_t& vec,
			       statement::base::TYPE t)
{
  load_top(vec,t,m_root);
}
// ================================================================
// Load all statements of a specific type.
// ================================================================
void ccdoc::database::load_top(statement::base::stmts_t& vec,
			       statement::base::TYPE t,
			       statement::base* stmt)
{
  if(stmt) {
    // Don't process statements unless their parent
    // is a namespace or a package.
    if( stmt->get_parent() ) {
      statement::base* parent = stmt->get_parent();
      if ( parent->get_type() != statement::base::STMT_PACKAGE &&
	   parent->get_type() != statement::base::STMT_NAMESPACE_BEGIN )
	return;
    }
    if( stmt->get_type() == t )
      vec.push_back(stmt);
    statement::base::stmts_t& children = stmt->get_children();
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      load_top(vec,t,*itr);
    }
  }
}
// ================================================================
// Debug dump.
// ================================================================
void ccdoc::database::debug_dump(const char* prefix) const
{
  debug_dump(prefix,m_root);
}
// ================================================================
// Debug dump.
// ================================================================
void ccdoc::database::debug_dump(const char* prefix,
				 statement::base* stmt) const
{
  if(stmt) {
    stmt->debug_dump(prefix);
    statement::base::stmts_t& vec = stmt->get_children();
    statement::base::stmts_itr_t itr = vec.begin();
    for(;itr!=vec.end();++itr) {
      debug_dump(prefix,*itr);
    }
  }
}
