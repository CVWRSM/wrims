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
#include "phase1_parser.h"
#include "log.h"
#include <cstdio>

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase1_parser.cc,v 1.22 2001/11/28 22:03:30 Administrator Exp $";

// ================================================================
// Constructor.
// ================================================================
ccdoc::phase1::parser::parser(switches& sw,
			      database& db,
			      const string& name)
  : m_sw(sw),
    m_db(db),
    m_parsed(false),
    m_debug(false),
    m_scanner(sw)
{
  m_action.clear();
  m_access_specifier.clear();
  m_statements.clear();
  m_defines.clear();
  m_sw.defines(m_defines);
  m_parents.clear();
  m_action.push_back(ACCEPT);
  m_access_specifier.push_back(statement::base::STMT_PUBLIC);
  m_scanner.open(name);
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::phase1::parser::~parser()
{
  m_scanner.close();
}
// ================================================================
// Parse
// ================================================================
bool ccdoc::phase1::parser::parse()
{
  ccdoc_assert( m_action.size() == 1 );
  ccdoc_assert( m_parsed == false );
  m_parsed = true; // guard against re-entrancy

  // Parse the file.
  if(m_sw.verbose()) {
    s_log << "phase1: parsing '" << m_scanner.get_file() << "' ...\n";
  }
  if(m_debug) {
    s_log << "CCDOC_PHASE1_DEBUG: "
	  << "================================================\n";
    s_log << "CCDOC_PHASE1_DEBUG: file: " << m_scanner.get_file() << "\n";
  }

  // Remove previous statements associated with this
  // file.
  m_db.remove_file_statements(m_scanner.get_file());

  // The user specified a package on the command line
  // using the -pkg switch. This overrides an internal
  // @pkg directive.
  statement::base* pkg = m_db.get_create_package(m_sw.pkg());
  add_statement(pkg);
  m_parents.push_back(pkg);

  while( get_next_statement() )
    ;

  return true;
}
// ================================================================
// Create a statement object.
// ================================================================
ccdoc::statement::base*
ccdoc::phase1::parser::make_statement( const string& id,
				       ccdoc::statement::base::TYPE t )
{
  // ================================================
  // Check for duplicates for namespaces and
  // packages. These can be merged.
  // ================================================
  statement::base* stmt = 0;
  if( t == statement::base::STMT_PACKAGE ||
      t == statement::base::STMT_NAMESPACE_BEGIN ||
      t == statement::base::STMT_NAMESPACE_END ) {
    if( m_parents.size() ) {
      stmt = m_parents.back()->get_child_by_id_type(id,t);
      // Special case handling for namespaces because
      // Namespaces can be referenced from multiple packages.
      if( stmt && t == statement::base::STMT_NAMESPACE_BEGIN ) {
	statement::base* parent = m_parents.back();
	if( parent &&
	    stmt->get_parent() != parent &&
	    parent->get_type() == statement::base::STMT_PACKAGE ) {
	  // Add the namespace id to the parent package token
	  // list. It can't be added to the child list because
	  // the namespace object is shared.
	  parent->add_token( stmt->get_id() );
	}
      }
    }
  }

  // ================================================
  // Change FUNCTION to METHOD if it is inside of
  // a class.
  // ================================================
  if( t == statement::base::STMT_FUNCTION ) {
    if( m_parents.size() ) {
      statement::base* p = m_parents.back();
      if( p->get_type() == statement::base::STMT_CLASS_BEGIN ||
	  p->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
	t = statement::base::STMT_METHOD;
      }
    }
  }

  // ================================================
  // Change VARIABLE to ATTRIBUTE if it is inside of
  // a class.
  // ================================================
  if( t == statement::base::STMT_VARIABLE ) {
    if( m_parents.size() ) {
      statement::base* p = m_parents.back();
      if( p->get_type() == statement::base::STMT_CLASS_BEGIN ||
	  p->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
	t = statement::base::STMT_ATTRIBUTE;
      }
    }
  }

  // ================================================
  // Change VARIABLE_FUNCTION to ATTRIBUTE_FUNCTION
  // if it is inside of a class.
  // ================================================
  if( t == statement::base::STMT_VARIABLE_FUNCTION ) {
    if( m_parents.size() ) {
      statement::base* p = m_parents.back();
      if( p->get_type() == statement::base::STMT_CLASS_BEGIN ||
	  p->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
	t = statement::base::STMT_ATTRIBUTE_FUNCTION;
      }
    }
  }

  // ================================================
  // Create the new statement, if necessary.
  // ================================================
  if( !stmt ) {
    stmt = new statement::base;
    stmt->set_id( id );
    stmt->set_type( t );
    stmt->set_access( m_access_specifier.back() );
    stmt->set_lineno( m_scanner.get_lineno() );
    stmt->set_file( m_scanner.get_file() );
    if( m_parents.size() ) {
      // Ignore the extern statement.
      statement::base* parent = m_parents.back();
      if( parent->get_type() == statement::base::STMT_EXTERN ) {
	if( parent->get_tokens().size() > 0 ) {
	  // Set the external linkage.
	  string x = parent->get_tokens()[0];
	  stmt->set_extern(x);
	}
	while( parent->get_type() == statement::base::STMT_EXTERN ) {
	  parent = parent->get_parent();
	}
	if( parent ) {
	  stmt->set_parent( parent );
	}
      }
      else {
	stmt->set_parent( parent );
      }
    }
  }

  // ================================================
  // Cache some special members for later processing.
  // ================================================
  switch( t ) {
  case statement::base::STMT_METHOD_CONSTRUCTOR:
  case statement::base::STMT_METHOD_DESTRUCTOR:
    m_special_members.push_back(stmt);
    break;
  case statement::base::STMT_METHOD_OPERATOR:
  case statement::base::STMT_FUNCTION_OPERATOR:
    {
      string id = stmt->get_id();
      if( id == "operator =" ) {
	m_special_members.push_back(stmt);
      }
    }
    break;
  default:
    break;
  }

  // ================================================
  // Update the comments.
  // ================================================
  if( m_statements.size() ) {
    statement::base* last_stmt = m_statements.back();
    if( statement::base::STMT_COMMENT_PREFIX == last_stmt->get_type() ) {
      stmt->set_comment( last_stmt );
      last_stmt->set_comment( stmt );
    }
    if( statement::base::STMT_COMMENT_SUFFIX == stmt->get_type() ) {
      last_stmt->set_comment( stmt );
      stmt->set_comment( last_stmt );
    }
  }
  return stmt;
}
// ================================================================
// Create a statement object.
// ================================================================
ccdoc::statement::base*
ccdoc::phase1::parser::make_statement( const string& in_id,
				       statement::base::TYPE t,
				       vector<string>& tokens )
{
  string id = in_id;

  // ================================================
  // Check for the special case of a template
  // instantiation.
  // ================================================
  if( id == ">" ) {
    // This is some sort of template instantiation.
    // Backup until we find the matching < then grab
    // the token before that to construct the full name.
    vector<string> id_tokens;
    vector<string>::reverse_iterator itr = tokens.rbegin();
    int depth = 0;
    for(;itr!=tokens.rend();++itr) {
      string& token = *itr;
      id_tokens.push_back(token);
      if( token == ">" ) {
	depth++;
      }
      else if( token == "<" ) {
	depth--;
	if( depth == 0 ) {
	  ++itr;
	  if( itr != tokens.rend() ) {
	    // The id tokens are in reverse order,
	    // re-order them for output.
	    id = "";
	    id_tokens.push_back(*itr);
	    vector<string>::reverse_iterator r1 = id_tokens.rbegin();
	    for(;r1!=id_tokens.rend();++r1) {
	      id += *r1;
	    }
	    //id = *itr;
	    break;
	  }
	}
      }
    }
  }

  // ================================================
  // Check for the special cases for functions.
  // ================================================
  if( t == statement::base::STMT_FUNCTION ) {

    // Check constructors and destructors.
    if( m_parents.size() ) {
      statement::base* parent = m_parents.back();
      if( id == parent->get_id() ) {
	if( parent->get_type() == statement::base::STMT_CLASS_BEGIN ||
	    parent->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
	  t = statement::base::STMT_METHOD_CONSTRUCTOR;
	  // Check for the special case of a destructor by
	  // looking for the preceding tilde.
	  vector<string>::iterator itr = tokens.begin();
	  for(;itr!=tokens.end();++itr) {
	    string& tok = *itr;
	    if( tok == id ) {
	      if( itr != tokens.begin() ) {
		--itr;
		if( *itr == "~" ) {
		  t = statement::base::STMT_METHOD_DESTRUCTOR;
		}
	      }
	      break;
	    }
	  }
	}
      }
    }

    // Check for macro instantiations. These are defined
    // by the heuristic:
    //   NAME ( ... )
    if( t == statement::base::STMT_FUNCTION ) {
      if( tokens.size()>2 &&
	  tokens[0] == id &&
	  tokens[1] == "(" ) {
	t = statement::base::STMT_MACROINST_FUNCTION;
      }
    }
  }

  statement::base* stmt = make_statement(id,t);
  stmt->set_tokens(tokens);

  // ================================================
  // Adjust the ids of functions and methods to
  // correctly reflect scoping.
  // ================================================
  switch( stmt->get_type() ) {
  case statement::base::STMT_FUNCTION:
  case statement::base::STMT_METHOD:
  case statement::base::STMT_METHOD_CONSTRUCTOR:
  case statement::base::STMT_METHOD_DESTRUCTOR:
    {
      string newid;
      get_fct_id(newid,stmt);
      if( newid.size() )
	stmt->set_id(newid);
    }
    break;
  default:
    break;
  }

  return stmt;
}
// ================================================================
// Add statement.
// ================================================================
void ccdoc::phase1::parser::add_statement(statement::base* stmt)
{
  if( stmt ) {
    if(m_debug) {
      stmt->debug_dump("CCDOC_PHASE1_DEBUG: ");
    }
    m_statements.push_back( stmt );
  }
}
// ================================================================
// Get next statement.
// ================================================================
bool ccdoc::phase1::parser::get_next_statement()
{
  // ================================================
  // Initialize and loop through the tokens.
  // ================================================
  int angle_bracket_nesting_level = 0;
  unsigned lineno = m_scanner.get_lineno();
  vector<string> tokens;
  string token;
  while( m_scanner.get_token(token) ) {
    // ================================================
    // Skip all statements except pre-processing
    // directives unless we are in the accept
    // state.
    // ================================================
    if( m_action.back() != ACCEPT ) {
      if( token == "#" ) {
	// This is a pre-processing directive.
	parse_cpp();
	tokens.clear();
      }
      continue;
    }

    // ================================================
    // ACCEPT state, process all tokens and return
    // them.
    // ================================================
    if( token == "#" ) {
      // This is a pre-processing directive.
      parse_cpp();
      tokens.clear();
    }
    else if( token == "@{" ) {
      parse_comment();
      return true;
    }
    else if( token == "<" ) {
      tokens.push_back(token);
      angle_bracket_nesting_level++;
    }
    else if( token == ">" ) {
      tokens.push_back(token);
      angle_bracket_nesting_level--;
    }
    else if( token == ";" ) {
      if( parse_var_or_fct(tokens) ) {
	return true;
      }
    }
    else if( token == "{" ) {
      // This is an unrecognized statement type
      // which means that it is either a function
      // or a variable.
      if( parse_fct_or_var(tokens) )
	return true;
    }
    else if( token == "}" ) {
      if( parse_scoping_stmt_end() )
	return true;
      tokens.clear();
    }
    else if( token == "asm" ) {
      // Ignore and scan to the end of the statement.
      while( m_scanner.get_token(token) ) {
	if( token == ";" )
	  break;
      }
    }
    else if( token == "operator" ) {
      tokens.push_back(token);
      if( parse_operator(tokens) )
	return true;
    }
    else if( token == "using" ) {
      // Ignore and scan to the end of the statement.
      while( m_scanner.get_token(token) ) {
	if( token == ";" )
	  break;
      }
    }
    else if( token == "enum" ) {
      tokens.push_back(token);
      if(parse_enum( tokens ))
	return true;
    }
    else if( token == "namespace" ) {
      tokens.push_back(token);
      if(parse_scoping_stmt_beg(tokens,statement::base::STMT_NAMESPACE_BEGIN))
	m_access_specifier.push_back( statement::base::STMT_PUBLIC );
      return true;
    }
    else if( angle_bracket_nesting_level == 0 && token == "class" ) {
      // The angle bracket nesting level is used to
      // avoid a false positive for template declarations
      // of the form:
      //   template <class T> void f(T* a) {...}
      tokens.push_back(token);
      if(parse_scoping_stmt_beg(tokens,statement::base::STMT_CLASS_BEGIN))
	m_access_specifier.push_back( statement::base::STMT_PRIVATE );
      return true;
    }
    else if( angle_bracket_nesting_level == 0 && token == "struct" ) {
      tokens.push_back(token);
      if(parse_scoping_stmt_beg(tokens,statement::base::STMT_STRUCT_BEGIN))
	m_access_specifier.push_back( statement::base::STMT_PUBLIC );
      return true;
    }
    else if( angle_bracket_nesting_level == 0 && token == "union" ) {
      tokens.push_back(token);
      if(parse_scoping_stmt_beg(tokens,statement::base::STMT_UNION_BEGIN))
	m_access_specifier.push_back( statement::base::STMT_PUBLIC );
      return true;
    }
    else if( token == "typedef" ) {
      tokens.push_back(token);
      if( parse_typedef( tokens ) )
	return true;
    }
    else if( token == "friend" ) {
      tokens.push_back(token);
      if( parse_friend( tokens ) )
	return true;
    }
    else if( token == "extern" ) {
      if( parse_extern( tokens ) )
	return true;
    }
    else if( token == "public" ) {
      parse_access_specifier(tokens,token,statement::base::STMT_PUBLIC);
    }
    else if( token == "protected" ) {
      parse_access_specifier(tokens,token,statement::base::STMT_PROTECTED);
    }
    else if( token == "private" ) {
      parse_access_specifier(tokens,token,statement::base::STMT_PRIVATE);
    }
    else if( token != "\n" ) {
      // New lines aren't really tokens.
      // They are only significant for
      // parsing pre-processing directives.
      // Ignore them.
      tokens.push_back(token);
    }
  }

  // ================================================
  // Parsing is finished. If there are any
  // pending tokens, report a problem.
  // ================================================
  if( tokens.size() ) {
    // There are tokens left over,
    // report a parsing problem.
    string s;
    vector<string>::iterator itr = tokens.begin();
    for(;itr!=tokens.end();++itr) {
      s += " ";
      s += *itr;
    }
    s_log.warning()
      << "Unknown statement tokens found between lines "
      << lineno
      << " and "
      << m_scanner.get_lineno()
      << " in "
      << m_scanner.get_file()
      << ".\n"
      << "\ttokens: '" << s << "'\n"
      << "\tThey will be ignored.\n"
      << s_log.enable();
  }
  return false;
}
// ================================================================
// Get next token. Handle pre-processing directives and ignore
// new lines.
// This method is only used to get tokens that exist after
// pre-processing.
// ================================================================
bool ccdoc::phase1::parser::get_next_token(string& token)
{
  while( m_scanner.get_token(token) ) {
    // ================================================
    // Skip all statements except pre-processing
    // directives unless we are in the accept
    // state.
    // ================================================
    if( m_action.back() != ACCEPT ) {
      if( token == "#" ) {
	// This is a pre-processing directive.
	parse_cpp();
      }
      continue;
    }

    // ================================================
    // ACCEPT state, process all tokens and return
    // them.
    // ================================================
    if( token == "#" ) {
      // This is a pre-processing directive.
      parse_cpp();
    }
    else if( token == "@{" ) {
      // Embedded comments are ignored.
      // This occurs for things like:
      //    enum FOO {
      //       //@{ ccdoc comment //@}
      //       F1,
      //       //@{ ccdoc comment //@}
      //       F2
      //    }
      parse_comment();
      continue;
    }
    else if( token == "\n" ) {
      continue;
    }
    else {
      return true;
    }
  }
  return false;
}
// ================================================================
// Parse the access specifier.
// ================================================================
void
ccdoc::phase1::parser::parse_access_specifier(vector<string>& tokens,
					      string& in_token,
					      statement::base::ACCESS access)
{
  string token;
  if( get_next_token(token) ) {
    if( token == ":" ) {
      // This is a know access specifier of the form:
      //    public:
      //    protected:
      //    private:
      tokens.clear();
      m_access_specifier.pop_back();
      m_access_specifier.push_back(access);
    }
    else {
      tokens.push_back(token);
      tokens.push_back(in_token);
    }
  }
  else {
    tokens.push_back(in_token);
  }
}
// ================================================================
// Parse the beginning of a scoping statement.
// ================================================================
bool ccdoc::phase1::parser::parse_scoping_stmt_beg( vector<string>& tokens,
						    statement::base::TYPE t )
{
  string token;
  string id;

  // Issue 0033.
  // Make sure that we haven't encountered something like this:
  //   void foo(struct bar* a);
  vector<string>::iterator itr = tokens.begin();
  int lp_depth = 0;
  for(;itr!=tokens.end();++itr) {
    if( *itr == "(" ) {
      lp_depth++;
    }
    else if( *itr == ")" ) {
      lp_depth--;
    }
    else if( *itr == "struct" || *itr == "class" || *itr == "union" ) {
      if( lp_depth ) {
	// This is the special case of a function with
	// one of these keywords in the argument list.
	// Scan ahead looking for a '{' or a ';'.
	while( get_next_token(token) ) {
	  if( token == "{" )
	    break;
	  if( token == ";" )
	    break;
	  tokens.push_back(token);
	}

	if( token == ";" ) {
	  // This is a declaration of the form:
	  //   void foo(struct bar* spam);
	  if( parse_var_or_fct(tokens) )
	    return false;
	}
	else if( token == "{" ) {
	  // This is a declaration of the form:
	  //   void foo(struct bar* spam) {..}
	  if( parse_fct_or_var(tokens) )
	    return false;
	}
      }
    }
  }

  while( get_next_token(token) ) {
    if( token == ":" ) {
      // This is the special case of class foo : public bar {}
      if( tokens.size() )
	id = tokens.back();
    }
    if( token == "{" )
      break;
    if( token == ";" )
      break;
    tokens.push_back(token);
  }
  if( token == "{" ) {
    if( id.size() == 0 )
      id = tokens.back();
    statement::base* stmt = make_statement(id,t,tokens);
    m_parents.push_back( stmt );
    add_statement( stmt );
    return true;
  }
  // This was a forward declaration,
  // ignore it.
  tokens.clear();
  return false;
}
// ================================================================
// Parse the end of a scoping statement.
// ================================================================
bool ccdoc::phase1::parser::parse_scoping_stmt_end()
{
  statement::base::TYPE t = statement::base::STMT_IGNORE;
  string id;

  // This is the end of the statement.
  switch( m_parents.back()->get_type() ) {
  case statement::base::STMT_CLASS_BEGIN:
    t = statement::base::STMT_CLASS_END;
    id = m_parents.back()->get_id();
    m_access_specifier.pop_back();
    parse_special_members(m_parents.back());
    m_parents.pop_back();
    break;
  case statement::base::STMT_NAMESPACE_BEGIN:
    t = statement::base::STMT_NAMESPACE_END;
    id = m_parents.back()->get_id();
    m_access_specifier.pop_back();
    m_parents.pop_back();
    break;
  case statement::base::STMT_STRUCT_BEGIN:
    t = statement::base::STMT_STRUCT_END;
    id = m_parents.back()->get_id();
    m_access_specifier.pop_back();
    parse_special_members(m_parents.back());
    m_parents.pop_back();
    break;
  case statement::base::STMT_UNION_BEGIN:
    t = statement::base::STMT_UNION_END;
    id = m_parents.back()->get_id();
    m_access_specifier.pop_back();
    m_parents.pop_back();
    break;
  case statement::base::STMT_EXTERN:
    {
      statement::base* stmt = m_parents.back();
      m_parents.pop_back();
      if( stmt->get_children().size() ) {
	// This is purely defensive programming.
	// This should never be executed.
	// Make a copy of the children list to avoid
	// iterator problems when their parents are
	// changed.
	string x;
	if( stmt->get_tokens().size() ) {
	  x = stmt->get_tokens()[0];
	}
	statement::base::stmts_t children = stmt->get_children();
	statement::base::stmts_itr_t itr = children.begin();
	for(;itr!=children.end();++itr) {
	  statement::base* child = *itr;
	  child->set_parent( m_parents.back() );
	  child->set_extern( x );
	}
      }
      delete stmt;
    }
    return true; // do not create an end statement.
  default:
    break;
  }

  // Update the statement type, if necessary.
  if( t != statement::base::STMT_IGNORE ) {
    statement::base* stmt = make_statement(id,t);
    add_statement( stmt );
    return true;
  }
  return false;
}
// ================================================================
// Parse special members.
// These are the default methods on classes.
// ================================================================
void ccdoc::phase1::parser::parse_special_members(statement::base* class_stmt)
{
  if( m_sw.cdsm() ) {
    bool default_constructor = false;
    bool copy_constructor = false;
    bool destructor = false;
    bool copy_operator = false;

    // ================================================
    // Figure out whether the default methods were
    // defined.
    // ================================================
    if( m_special_members.size() ) {
      statement::base::stmts_itr_t itr = m_special_members.begin();
      for(;itr!=m_special_members.end();++itr) {
	statement::base* stmt = *itr;
	switch( stmt->get_type() ) {
	case statement::base::STMT_METHOD_CONSTRUCTOR:
	  {
            // Issue 0099
            // If any constructor is defined, we do not
            // have to define the default_constructor.
            default_constructor=true;
            
	    if( copy_constructor && default_constructor )
	      break;

	    // Look for the default constructor and the
	    // copy constructor.
	    // Start by getting the arguments.
	    parse_special_members(stmt,
				  class_stmt->get_id(),
				  default_constructor,
				  copy_constructor);
	  }
        break;
	case statement::base::STMT_METHOD_DESTRUCTOR:
	  destructor = true;
	  break;
	case statement::base::STMT_METHOD_OPERATOR:
	case statement::base::STMT_FUNCTION_OPERATOR:
	  {
	    if( copy_operator )
	      break;

	    string id = stmt->get_id();
	    if( id == "operator =" ) {
	      bool dummy = false;
	      parse_special_members(stmt,
				    class_stmt->get_id(),
				    dummy,
				    copy_operator);
	    }
	  }
        break;
	default:
	  break;
	}
	if( default_constructor &&
	    copy_constructor &&
	    destructor &&
	    copy_operator )
	  break;
      }
    }
    // ================================================
    // Create the default constructor if it was not
    // already defined.
    // ================================================
    if( !default_constructor ) {
      string id;
      id = class_stmt->get_id();
      vector<string> tokens;
      tokens.push_back( class_stmt->get_id() );
      tokens.push_back( "(" );
      tokens.push_back( ")" );

      statement::base::TYPE t = statement::base::STMT_METHOD_CONSTRUCTOR;
      statement::base* tmp = make_statement(id,t,tokens);
      tmp->set_access( statement::base::STMT_PUBLIC );
      tmp->set_lineno( 0 ); // special line number
      add_statement( tmp );
    }
    // ================================================
    // Create the copy constructor if it was not
    // already defined.
    // ================================================
    if( !copy_constructor ) {
      string id;
      id = class_stmt->get_id();
      vector<string> tokens;
      tokens.push_back( class_stmt->get_id() );
      tokens.push_back( "(" );
      tokens.push_back( "const" );
      tokens.push_back( class_stmt->get_id() );
      tokens.push_back( "&" );
      tokens.push_back( ")" );

      statement::base::TYPE t = statement::base::STMT_METHOD_CONSTRUCTOR;
      statement::base* tmp = make_statement(id,t,tokens);
      tmp->set_access( statement::base::STMT_PUBLIC );
      tmp->set_lineno( 0 ); // special line number
      add_statement( tmp );
    }
    // ================================================
    // Create the destructor if it was not
    // already defined.
    // ================================================
    if( !destructor ) {
      string id;
      id = class_stmt->get_id();
      vector<string> tokens;
      tokens.push_back( "~" );
      tokens.push_back( class_stmt->get_id() );
      tokens.push_back( "(" );
      tokens.push_back( ")" );

      statement::base::TYPE t = statement::base::STMT_METHOD_DESTRUCTOR;
      statement::base* tmp = make_statement(id,t,tokens);
      tmp->set_access( statement::base::STMT_PUBLIC );
      tmp->set_lineno( 0 ); // special line number
      add_statement( tmp );
    }
    // ================================================
    // Create the copy operator if it was not
    // already defined.
    // ================================================
    if( !copy_operator ) {
      string id;
      id = "operator =";
      vector<string> tokens;
      tokens.push_back( class_stmt->get_id() );
      tokens.push_back( "&" );
      tokens.push_back( "operator" );
      tokens.push_back( "=" );
      tokens.push_back( "(" );
      tokens.push_back( "const" );
      tokens.push_back( class_stmt->get_id() );
      tokens.push_back( "&" );
      tokens.push_back( ")" );

      statement::base::TYPE t = statement::base::STMT_METHOD_OPERATOR;
      statement::base* tmp = make_statement(id,t,tokens);
      tmp->set_access( statement::base::STMT_PUBLIC );
      tmp->set_lineno( 0 ); // special line number
      add_statement( tmp );
    }
  }
  // ================================================
  // Clean up for the next pass.
  // ================================================
  m_special_members.clear();
}
// ================================================================
// Parse special members.
// These are the default methods on classes.
// ================================================================
void ccdoc::phase1::parser::parse_special_members(statement::base* stmt,
						  const char* arg_id,
						  bool& default_constructor,
						  bool& copy_constructor)
{
  // Look for the default constructor and the
  // copy constructor.
  // Start by getting the arguments.
  vector<string> args;
  string id;
  const vector<const char*>& tokens = stmt->get_tokens();
  if( tokens.size() ) {
    vector<const char*>::const_iterator titr = tokens.begin();
    int depth = 0;
    int br = 0;
    for(;titr!=tokens.end();++titr) {
      id = *titr;
      if( id == "(" ) {
	depth++;
      }
      else if( id == ")" ) {
	depth--;
	if( depth == 0 ) {
	  break;
	}
      }
      else if( id == "<" ) {
	br++;
      }
      else if( id == ">" ) {
	br--;
      }
      else if( depth ) {
	args.push_back( id );
	if( br == 0 && ( id == "," || id == "*" ) ) {
	  // This guarantees that we don't get a
	  // false positive for this:
	  //   (const A<T,B>& x)
	  return;
	}
	if( id == "&" )
	  break;
	}
    }
    // ================================================
    // Now look ahead to see whether there are additional
    // arguments.
    // ================================================
    for(;titr!=tokens.end();++titr) {
      id = *titr;
      if( id == "(" ) {
	depth++;
      }
      else if( id == ")" ) {
	depth--;
	if( depth == 0 ) {
	  break;
	}
      }
      else if( id == "<" ) {
	br++;
      }
      else if( id == ">" ) {
	br--;
      }
      else if( depth ) {
	if( br == 0 && ( id == "," || id == "*" ) ) {
	  // This guarantees that we don't get a
	  // false positive for this:
	  //   (const A<T,B>& x)
	  return;
	}
      }
    }
    // ================================================
    // Check for the default constructor.
    // ================================================
    if( args.size() == 0 ) {
      default_constructor = true;
      return;
    }
    // ================================================
    // Check for the copy constructor or copy operator
    // arg list.
    // ================================================
    if( args[0] == "const" && args.back() == "&" ) {
      // Look for this:
      //   (const A&)
      args.pop_back(); // get rid of the trailing &
      vector<string>::iterator i = args.begin();
      id = "";
      for(++i;i!=args.end();++i) {
	id += *i;
      }
      if( id == arg_id ) {
	copy_constructor = true;
      }
    }
  }
}
// ================================================================
// Parse the beginning of a scoping statement.
// ================================================================
bool ccdoc::phase1::parser::parse_enum( vector<string>& tokens )
{
  string id;
  string token;
  while( get_next_token(token) ) {
    if( token == "{" ) {
      if( tokens.size() > 1 ) {
	id = tokens.back();
      }
      else {
	id = "$anonymous$";
      }
      break;
    }
    if( token == ";" )
      break;
    tokens.push_back(token);
  }
  if( token == "{" ) {
    tokens.push_back(token);

    // Now get the rest of the tokens.
    // Parse to the enclosing rb.
    int level = 1;
    while( get_next_token(token) ) {
      tokens.push_back(token);
      if( token == "{" ) {
	level++;
      }
      if( token == "}" ) {
	level--;
	if( level == 0 )
	  break;
      }
    }
    statement::base* stmt = make_statement(id,
					   statement::base::STMT_ENUM,tokens);
    add_statement(stmt);
    return true;
  }
  // This was a forward declaration,
  // ignore it.
  tokens.clear();
  return false;
}
// ================================================================
// Parse a function or a variable.
// ================================================================
bool ccdoc::phase1::parser::parse_operator( vector<string>& tokens )
{
  // Parse to the '{' or ';'.
  string token;
  while( get_next_token(token) ) {
    if( token == "{" || token == ";" ) {
      break;
    }
    tokens.push_back(token);
  }

  // Figure out the token id.
  string id;
  {
    // Figure out the id for an operator.
    // This is tricky because the operator id
    // can look like this:
    //
    //  X& operator() (const char*);
    //
    // The heuristic used is the first lp after
    // the first token after the operator.
    //
    //  X& operator() (const char*);
    //              ^
    //              +--- start looking for the lp here.
    //
    vector<string>::iterator itr = tokens.begin();
    for(;itr!=tokens.end();++itr) {
      if( *itr == "operator" ) {
	id = "operator ";
	unsigned cnt = 0;
        // Issue 0114:
        // This loop mashes the operator name together to make
        // it look more reasonable for things like () and [].
        // Unfortunately this causes problems for cast operators
        // like: unsigned long. To workaround this, I use a heuristic
        // that says if the previous token was not a punctuator, a
        // leading space is added.
        string prev;
	for(++itr;itr!=tokens.end();++itr,++cnt) {
	  if( cnt ) {
            if( *itr == "(" )
              break;
            if( is_id(prev) )
              id += " ";
          }
          prev = *itr;
	  id += prev;
	}
	break;
      }
    }
  }

  // Create the statement.
  statement::base::TYPE t = statement::base::STMT_FUNCTION_OPERATOR;
  if( m_parents.size() ) {
    if( m_parents.back()->get_type() == statement::base::STMT_CLASS_BEGIN ||
	m_parents.back()->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
      // Method operators are defined inside of classes.
      t = statement::base::STMT_METHOD_OPERATOR;
    }
  }
  if( token == ";" ) {
    statement::base* stmt;
    stmt = make_statement(id,t,tokens);
    add_statement( stmt );
    return true;
  }
  if( token == "{" ) {
    statement::base* stmt;
    stmt = make_statement(id,t,tokens);
    add_statement( stmt );

    // Scan to the end of the operator function.
    int level = 1;
    while( get_next_token(token) ) {
      if( token == "{" ) {
	level++;
      }
      else if( token == "}" ) {
	level--;
	if( level == 0 )
	  break;
      }
    }
    return true;
  }
  return false;
}
// ================================================================
// Parse a function or a variable.
// ================================================================
bool ccdoc::phase1::parser::parse_fct_or_var( vector<string>& tokens )
{
  // ================================================
  // We found a left brace, back up to find the
  // id.
  // ================================================
  string id;
  {
    int num_parens = 0;
    int paren_level = 0;

    // Check for this special case:
    //   Point() : x(0), y(0).
    // It is identified by a standalone colon outside
    // of the parentheses.
    vector<string>::iterator itr1_id = tokens.begin();
    vector<string>::iterator itr1 = tokens.begin();
    for(;itr1!=tokens.end();++itr1) {
      string& tok = *itr1;
      if( tok == "(" ) {
	itr1_id = itr1;
	paren_level++;
      }
      else if( tok == ")" ) {
	paren_level--;
      }
      else if( paren_level == 0 && tok == ":" ) {
	break;
      }
    }
    if( itr1 != tokens.end() && itr1_id != tokens.begin() ) {
      // This is the special case:
      --itr1_id;
      id = *itr1_id;
    }
    else {
      vector<string>::reverse_iterator itr = tokens.rbegin();
      for(;itr!=tokens.rend();++itr) {
	string& tok = *itr;
	if( tok == "(" ) {
	  num_parens++;
	  paren_level--;
	  if( paren_level == 0 ) {
	    ++itr;
	    if( itr != tokens.rend() ) {
	      id = *itr;
	      break;
	    }
	  }
	}
	else if( tok == ")" ) {
	  paren_level++;
	}
	else if( paren_level == 0 ) {
	  if( tok == "[" ||
	      tok == "=" ) {
	    ++itr;
	    if( itr != tokens.rend() ) {
	      if( is_id(*itr) )
		id = *itr;
	      // Don't break in case we encounter
	      // this:
	      //   int a[5][3][2];
	      //   int b = {..};
	    }
	  }
	}
      }
    }
  }

  // ================================================
  // Figure out whether this is a fct or a var.
  // The heuristic I use to distinguish functions
  // from variables is to look ahead for a closing
  // brace or a semi-colon.
  //
  // If a semi-colon is encountered, this is a function
  // of the form:
  //   void foo() { <stmt>; ... }
  //
  // If a closing brace is encountered at the same level,
  // this is an empty function or a variable.
  // ================================================
  int level = 1;
  statement::base::TYPE t = statement::base::STMT_IGNORE;
  vector<string> look_ahead;
  string token;
  while( get_next_token(token) ) {
    look_ahead.push_back(token);
    if( token == "{" ) {
      level++;
    }
    else if( token == "}" ) {
      level--;
      if( level == 0 )
	break;
    }
    else if( token == ";" ) {
      t = statement::base::STMT_FUNCTION;
      break;
    }
  }

  // ================================================
  // Function or variable?
  // ================================================
  if( t == statement::base::STMT_IGNORE ) {
    // Handle the following cases for functions.
    // void f() {
    //   /** comment *\/
    // #ifdef ...
    // #endif
    // }
    t = statement::base::STMT_FUNCTION;
    vector<string>::iterator itr = look_ahead.begin();
    for(;itr!=look_ahead.end();++itr) {
      string& tok = *itr;
      if( tok[0] == '@' )
	continue;
      if( tok[0] == '#' )
	continue;
      if( tok[0] == '}' )
	continue;
      t = statement::base::STMT_VARIABLE;
      break;
    }
  }

  // ================================================
  // Parse to the enclosing rb at the correct
  // level.
  //
  // This algorithm ignores all pre-processing
  // directives and comments inside of the
  // braces.
  //
  // For example, the following stuff will be
  // ignored:
  //
  //   void fct() {
  //   /** Define the BAR macro here *\/ <== IGNORED
  //   #define BAR                       <== IGNORED
  //   }
  //
  // To recognize pre-processing directives and
  // comments inside of a function body, the parser
  // would have to save state so that it could
  // return multiple statements: the fct decl stmt
  // + the other statements.
  //
  // I don't think that this is necessary.
  // --jdl 2001/07/26
  // ================================================
  if( level > 0 ) {
    while( get_next_token(token) ) {
      if( token == "{" ) {
	level++;
      }
      else if( token == "}" ) {
	level--;
	if( level == 0 )
	  break;
      }
    }
  }

  // ================================================
  // Create the statement.
  // ================================================
  if( t != statement::base::STMT_IGNORE ) {
    statement::base* stmt = make_statement(id,t,tokens);
    add_statement( stmt );
    return true;
  }
  return false;
}
// ================================================================
// Parse a variable.
// ================================================================
bool ccdoc::phase1::parser::parse_var_or_fct( vector<string>& tokens )
{
  // ================================================
  // Check for the special case of an empty declartion.
  // This occurs in the following cases:
  //   int a; ; ;
  //         ^ ^
  //         | +------ empty declaration
  //         +-------- empty declaration
  //   class b {} ;
  //             ^
  //             +---- empty declaration
  // ================================================
  if( tokens.size() == 0 )
    return false;

  string id;

  statement::base::TYPE t = statement::base::STMT_VARIABLE;
  // ================================================
  // This could be variable declaration, a forward declaration of
  // a function or a flow control statement. Here are examples of each:
  //
  //   int a;
  //   int a();
  //   int (*a)(int x);
  //   if( a==b ) x=1;
  //
  // Flow control statements are handled by the function
  // processing so they can be ignored.
  // ================================================

  // ================================================
  // Special case:
  //   Ignore forward declarations of function.
  //   Forward declarations of functions are a tricky
  //   matter as can be seen from the following example:
  //
  //     int (*x)(int a=5);  // variable
  //     int (y)(int a=5);   // forward declaration
  //     int z(int a=5);     // forward declaration
  //     int (((w)))(int a); // forward declaration
  //     int (*(u))(int);    // variable
  //
  //   The heuristic involves looking for the '(*' tokens
  //   at the top level. If they exist, this is a variable.
  //   Of course this doesn't help much because everything
  //   is assumed to be a variable.
  //   Look for an lp, these can only exist for forward
  //   declared functions or function variables.
  // ================================================
  {
    // Figure out the number of paren sets:
    //   statement                  lp_set
    //   int a;                       0
    //   int a();                     1
    //   int (*a)()                   2
    //   int a( int (*b)() )          1
    //   int int a() throw (b)        1  throw is ignored
    //   int (*a)() throw (b)         2  throw is ignored
    //   int a( int (*b)() ) throw(c) 1  throw is ignored
    vector<string>::iterator itr = tokens.begin();
    bool eq_found_before_lp = false;
    int level = 0;
    int lp_set = 0;
    for(;itr != tokens.end();++itr) {
      string& tok = *itr;
      if( tok == "(" ) {
	if( level == 0 )
	  lp_set++;
	level++;
      }
      else if( tok == ")" ) {
	level--;
      }
      else if( tok == "throw" ) {
	break; // ignore exception clauses
      }
      else if( lp_set == 0 && level == 0 && tok == "=" ) {
	// Issue 0078:
	//   Catch the following case:
	//      int foo = (1+1);
	eq_found_before_lp = true;
      }
    }
    bool var = true;
    bool eq_found = false; // Issue 0042
    if( lp_set == 0 || eq_found_before_lp ) {
      // ================================
      // This is definitely a variable.
      // ================================
      // Get the id.
      int lb = 0;
      for(itr=tokens.begin();itr != tokens.end();++itr) {
	string& tok = *itr;
	if( tok == "=" ) {
	  break;
        }
	// Issue 0077:
	//   Get the correct name for char FOO[BAR];
	else if( tok == "<" || tok == "[" ) {
	  lb++;
	}
	else if( tok == ">" || tok == "]" ) {
	  lb--;
	}
	else if( lb == 0 && is_id(tok) ) {
	  id = tok;
	}
      }
    }
    else if( lp_set == 1 ) {
      // ================================
      // This is a function, get the id.
      // ================================
      t = statement::base::STMT_FUNCTION;
      var = false;
      int lb = 0;
      for(itr=tokens.begin();itr != tokens.end();++itr) {
	string& tok = *itr;
	if( tok == "(" )
	  break;
	// Issue 0077:
	//   Get the correct name for void* FOO()[BAR];
	else if( tok == "<" || tok == "[" ) {
	  lb++;
	}
	else if( tok == ">"  || tok == "]" ) {
	  lb--;
	}
	else if( lb == 0 && is_id(tok) ) {
	  id = tok;
	}
      }
      statement::base* stmt = make_statement(id,t,tokens);
      add_statement(stmt);
      return true;
    }
    else {
      // ================================
      // This is a function variable.
      // ================================
      t = statement::base::STMT_VARIABLE_FUNCTION;
      // Find the id token after the first asterisk that follows
      // a left paren.
      vector<string>::iterator itr = tokens.begin();
      for(;itr!=tokens.end();++itr) {
	if( *itr == "(" ) {
	  vector<string>::iterator itr1 = itr;
	  ++itr1;
	  if( itr1 != tokens.end() ) {
	    if( *itr1 == "*" ) {
	      ++itr1;
	      if( itr1 != tokens.end() ) {
		while( *itr1 == "(" ) {
		  ++itr1;
		  if( itr1 == tokens.end() )
		    break;
		}
		if( itr1 != tokens.end() && is_id(*itr1) ) {
		  id = *itr1;
		  break;
		}
	      }
	    }
	  }
	}
      }
    }
  }

  // ================================================
  // Special case:
  //   Handle the special case of multiple variables of the
  //   general form:
  //
  //     int a,b,c;
  //
  //   The tricky aspect of this heuristic is to
  //   determine the prefix.
  // ================================================
  {
    // Start by finding the separators. These
    // are commas at the outermost level.
    vector<string>::iterator itr = tokens.begin();
    vector<int> sep;
    int lb = 0;
    int level = 0;
    int i=0;
    for(;itr != tokens.end();++itr,++i) {
      string& tok = *itr;
      if( tok == "(" ) {
	level++;
      }
      else if( tok == ")" ) {
	level--;
      }
      else if( level == 0 ) {
        // Issue 0101
        // Make sure that we ignore template variables
        // such as:
        //   FOO<BAR<A,B> > var;
        if( tok == "<" ) {
          lb++;
        }
        else if( tok == ">" ) {
          lb--;
        }
        else if( lb == 0 && tok == "," ) {
          sep.push_back(i);
        }
      }
    }
    if( sep.size() ) {
      // Separators were found.
      // Create the separate the statements.
      // Return the first one put the others
      // on the pending stack.

      // But first figure out where the
      // the type specification ends.
      // Here are some of the cases
      // handled by this heuristic:
      //   int a,b,c;
      //   int (*a)(int),(*b)();
      //   int a=c,b;
      //   int a[5],b[5];
      int j=0;
      for(;j<sep[0];++j) {
	string& tok = tokens[j];
	if( tok == "(" ) {
	  // Check for the special case of '(*'.
	  string& tok1 = tokens[j+1];
	  if( tok1 == "*" ) {
	    break;
	  }
	}
	if( tok == "[" || tok == "=" ) {
	  if(j>1) {
	    j--;
	  }
	  break;
	}
      }
      if( j == sep[0] ) {
	// This happens in the following case:
	//   int a,b;
	j--;
      }
      // Create the statements and put them on the pending
      // stack. Skip the first entry.
      vector<string> prefix;
      {for(int k=0;k<j;++k) {
	prefix.push_back( tokens[k] );
      }}
      sep.push_back( tokens.size() );

      // Create the first variable stmt.
      {
	vector<string> new_tokens;
	for(int n=0;n<sep[0];++n) {
	  new_tokens.push_back(tokens[n]);
	  if( is_id(tokens[n]) ) {
	    id = tokens[n];
	  }
	}
	statement::base* stmt = make_statement(id,t,new_tokens);
	add_statement(stmt);
      }

      // Create the other variable stmts.
      int b = sep[0];
      int e = b;
      {for(int k=1;k<sep.size();++k) {
	vector<string> new_tokens;
	new_tokens = prefix;
	e = sep[k];
	id = "";
	for(int n=b+1;n<e;++n) {
	  new_tokens.push_back( tokens[n] );
	  if( id.size() == 0 && is_id(tokens[n]) ) {
	    id = tokens[n];
	  }
	}
	statement::base* stmt = make_statement(id,t,new_tokens);
	add_statement(stmt);
	b = e;
      }}
      return true;
    }
  }

  // ================================================
  // Special case:
  //   A macro is treated as a variable. This occurs when
  //   a macro is instantiated. The code looks like this:
  //
  //     #define M int a
  //     M;
  //
  //   We must be careful not to lose these:
  //
  //     #define uchar unsigned char
  //     uchar a;
  //
  //   Of course a typedef is much better choice in
  //   this case but...
  // ================================================
  if( tokens.size() == 1 ) {
    if( defined(tokens[0]) ) {
      // This is a macro instantiation. Ccdoc is only
      // interested in declarations, ignore it.
      t = statement::base::STMT_MACROINST_VARIABLE;
      id = tokens[0];
    }
    else {
      // Check for variables that are really macros.
      // These are variables of the form:
      //
      //    AAA;
      //
      // This is tricky because the previous declaration
      // is valid if there was a previously defined
      // type:
      //
      //   enum {A,B,C} AAA;
      //   class A { ... } AAA;
      //   struct A { ... } AAA;
      //
      // The parsing algorithm makes it impossible to detect
      // the following condition because it discards the
      // semi-colon:
      //
      //   class A { ... } ;
      //   AAA ;
      //
      // My hope is that this occurrence is
      // rare and that folks can work around it
      // as follows:
      //
      //  class A { ... } ;
      //  #ifndef __ccdoc__
      //  AAA ;
      //  #endif
      // Back up to the next non-comment statement.
      statement::base* parent = m_parents.back();
      statement::base::stmts_t& vec = parent->get_children();
      statement::base::stmts_ritr_t itr = vec.rbegin();
      for(;itr!=vec.rend();++itr) {
	if( (*itr)->get_type() == statement::base::STMT_COMMENT_PKGDOC ||
	    (*itr)->get_type() == statement::base::STMT_COMMENT_PKGDOC_URL ||
	    (*itr)->get_type() == statement::base::STMT_COMMENT_PREFIX ||
	    (*itr)->get_type() == statement::base::STMT_COMMENT_SUFFIX )
	  continue;
	break;
      }
      if( itr!=vec.rend() ) {
	switch( (*itr)->get_type() ) {
	case statement::base::STMT_ENUM:
	case statement::base::STMT_CLASS_END:
	case statement::base::STMT_STRUCT_END:
	case statement::base::STMT_UNION_END:
	  // TODO: KNOWN BUG
	  //
	  // This implies that the following
	  // case will fall through the cracks:
	  //
	  //   enum {A,B,C};
	  //   XXX;
	  //
	  // whereas this case will not:
	  //
	  //   enum {A,B,C} x;
	  //   XXX;
	  //
	  break;
	default:
	  // This is not one of the recognized types
	  // that can precede a legal variable. Assume
	  // it is a macro instance.
	  t = statement::base::STMT_MACROINST_VARIABLE;
	  break;
	}
      }
    }
  }

  statement::base* stmt = make_statement(id,t,tokens);
  add_statement(stmt);
  return true;
}
// ================================================================
// Parse a typedef.
// ================================================================
bool ccdoc::phase1::parser::parse_typedef( vector<string>& tokens )
{
  // Get all of the tokens in the statement.
  // Issue 0054
  // Handle the following special case:
  //   typedef struct s1 { int a, int b } s1_type;
  //   typedef struct { int a, int b } s2_type;
  // At this time ccdoc will not attempt to understand
  // the contents of the typedef.
  string token;
  int depth = 0;
  while( get_next_token(token) ) {
    if( depth == 0 && token == ";" ) {
      break;
    }
    else if( token == "{" ) {
      depth++;
    }
    else if( token == "}" ) {
      depth--;
    }
    tokens.push_back(token);
  }

  // Here is my heuristic for distinguishing between:
  //
  //   typedef int a;
  //
  // and
  //
  //   typedef (*fct)(a,b,c);
  //
  // and
  //
  //   typedef Node* (Node:*NodeConstMeth) () const;
  //
  // and
  //
  //   typedef void fct1(int arg1);
  //
  // If the last token line (prior to the ';') is an
  // identifier, this is a variable typedef otherwise
  // it is a function typedef.
  string id;
  statement::base::TYPE t = statement::base::STMT_TYPEDEF_FUNCTION;
  if( token.size() )
    token = tokens.back();
    if( token != "const" && is_id(token) ) {
      // Handle the special case of:
      //   typedef Node* (Node:*NodeConstMeth) () const;
      t = statement::base::STMT_TYPEDEF_VARIABLE;
      id = token;
    }
    else {
      // Issue 0075: check for typedef void fct();
      int paren_sets = 0;
      int depth = 0;
      vector<string>::iterator itr = tokens.begin();
      for(;itr!=tokens.end();++itr) {
	token = *itr;
	if( token == "(" ) {
	  if( !depth ) {
	    if( !paren_sets ) {
	      if( itr != tokens.begin() ) {
		--itr;
		id = *itr;
		++itr;
	      }
	    }
	    paren_sets++;
	  }
	  depth++;
	}
	else if( token == ")" ) {
	  depth--;
	}
      }
      if( 1 != paren_sets ) {
	// Look for function pointers.
	depth = 0;
	bool found_asterisk = false;
	itr = tokens.begin();
	for(;itr!=tokens.end();++itr) {
	  token = *itr;
	  if( token == "(" ) {
	    depth++;
	  }
	  else if( token == ")" ) {
	    depth--;
	  }
	  else if( depth ) {
	    if( token == "*" ) {
	      found_asterisk = true;
	    }
	    else if( is_id(token) ) {
	      // Grab the first id token after the
	      // first asterisk inside the type parens.
	      id = token;
	      if( found_asterisk )
		break;
	    }
	  }
	}
      }
    }

  statement::base* stmt = make_statement(id,t,tokens);
  add_statement(stmt);
  return true;
}
// ================================================================
// Parse friend.
// ================================================================
bool ccdoc::phase1::parser::parse_friend( vector<string>& tokens )
{
  statement::base::TYPE t = statement::base::STMT_FRIEND_FUNCTION;
  bool make_global_function = false;
  // Get all of the tokens in the statement.
  string token;
  while( get_next_token(token) ) {
    if( token == ";" )
      break;
    if( token == "{" ) {
      // This is a function definition
      // When this occurs in C++ two statements are created,
      // a friend declaration and a global function.
      // Skip the function innards.
      int depth = 1;
      while( get_next_token(token) ) {
	if( token == "{" ) {
	  depth++;
	}
	else if( token == "}" ) {
	  depth--;
	  if( depth == 0 ) {
	    break;
	  }
	}
      }
      make_global_function = true;
      break;
    }
    tokens.push_back(token);
  }

  // We need to do a little work here to make
  // sure that the id is correct.
  if( tokens[0] != "friend" || tokens.size() == 1 ) {
    // Something got confused.
    // Warn and bail.
    s_log.warning()
      << "Unrecognized friend syntax at line "
      << m_scanner.get_lineno()-1
      << " in "
      << m_scanner.get_file()
      << ".\n"
      << s_log.enable();
    tokens.clear(); // ignore the tokens.
    return false;
  }
  if( tokens[1] == "class" && tokens.size() == 2) {
    s_log.warning()
      << "Unrecognized friend syntax at line "
      << m_scanner.get_lineno()-1
      << " in "
      << m_scanner.get_file()
      << ".\n"
      << s_log.enable();
    tokens.clear(); // ignore the tokens.
    return false;
  }

  // Process friend classes
  if( tokens[1] == "class" ) {
    vector<string>::iterator itr = tokens.begin();
    itr++;
    itr++;
    string id;
    for(;itr!=tokens.end();++itr) {
      id += *itr;
    }
    t = statement::base::STMT_FRIEND_CLASS;
    statement::base* stmt = make_statement(id,t,tokens);
    add_statement(stmt);
    return true;
  }

  // This is not a friend class, this is a friend function.
  // Parse to the first open paren.
  vector<string>::iterator itr = tokens.begin();
  itr++;
  string id;
  for(;itr!=tokens.end();++itr) {
    if( *itr == "(" ) {
      // Add the FRIEND_FUNCTION
      statement::base* stmt = make_statement(id,t,tokens);
      add_statement(stmt);

      if( make_global_function ) {
	// Create the global function.
	// First capture the state of the current parents
	// so that the modal state of m_parents is correct
	// for the new statement.
	statement::base::stmts_t parents = m_parents;
	for( ;m_parents.size(); ) {
	  if( m_parents.back()->get_type() == statement::base::STMT_PACKAGE ) {
	    t = statement::base::STMT_FUNCTION;
	    statement::base* stmt = make_statement(id,t,tokens);
	    add_statement(stmt);
	    break;
	  }
	  m_parents.pop_back();
	}
	m_parents = parents;
      }
      return true;
    }
    id += *itr;
  }

  tokens.clear();
  return false;
}
// ================================================================
// Parse extern.
// ================================================================
bool ccdoc::phase1::parser::parse_extern( vector<string>& tokens )
{
  // Get all of the tokens in the statement.
  string token1;
  if( !get_next_token(token1) ) {
    return false;
  }
  string token2;
  if( !get_next_token(token2) ) {
    m_scanner.put_token(token1);
    return false;
  }
  if( token2 != "{" ) {
    // This is not a external linkage statement.
    m_scanner.put_token(token2);
    m_scanner.put_token(token1);
    return false;
  }

  // This is an external link statement of the form:
  //   extern "C" {
  //   }
  tokens.clear();
  tokens.push_back(token1);
  statement::base* stmt = make_statement(token1,
					 statement::base::STMT_EXTERN,
					 tokens);
  m_parents.push_back( stmt );
  // Note that this statement is not added to the list,
  // this is because it won't exist after parsing.
  return true;
}
// ================================================================
// Get function id.
// ================================================================
void ccdoc::phase1::parser::get_fct_id(string& id,statement::base* stmt) const
{
  // ================================================
  // This heuristic deduces the function id.
  //
  //   1. Find the beginning of the last pair of outermost
  //      parentheses unless a single colon is encountered
  //      or a throws keyword is found. The single colon is
  //      used in constructor declarations. The throws keyword
  //      delimits addition parentheses.
  //
  //        ex: int (*fct) fct(int);
  //
  //   2. Back up through '<', '>', and '::''s.
  //
  //   3. Iterate forward to create the string.
  // ================================================

  // NOTES:
  // This is tricky.
  // Here are some sample cases:
  //
  //    int foo();
  //    A& operator () (const A&);
  //    int (*fct)() get_fct(const string&);
  //    int fct<int,string>(int);
  //    static const char* Escape();
  //    template <class T> A<T>::A() : foo(B) {}
  //    Point2D() : x(0), y(0) {}
  //    void foo() throws (myx);
  //

  // ================================================
  // Step 1:
  //   Find the last set of outer level parentheses.
  // ================================================
  int lb_depth = 0;
  int lp_depth = 0;
  const vector<const char*>& tokens = stmt->get_tokens();
  vector<const char*>::const_iterator lp_itr = tokens.end();
  vector<const char*>::const_iterator tokens_itr = tokens.begin();
  for(;tokens_itr!=tokens.end();++tokens_itr) {
    string token = *tokens_itr;
    if( token == "<" ) {
      lb_depth++;
    }
    else if( token == ">" ) {
      lb_depth--;
    }
    else if( lb_depth == 0 ) {
      if( token == "(" ) {
	if( lp_depth == 0 ) {
	  lp_itr = tokens_itr;
	}
	lp_depth++;
      }
      else if( token == ")" ) {
	lp_depth--;
      }
      else if( lp_depth == 0 ) {
	if( token == ":" ) {
	  // This is a special case of the following:
	  //   template <class T>
	  //   A<T>::A() : foo(a,b), bar(c,d)
	  break;
	}
	if( token == "throw" ) {
	  // Issue 0015
	  // This is a special case of the following:
	  //   class A {
	  //   public:
	  //     void foo() throw (myx);
	  //   };
	  break;
	}
      }
    }
  }

  // ================================================
  // Step 2:
  //   Walk backwards to get the id.
  // ================================================
  if( lp_itr == tokens.end() ) return;
  --lp_itr;
  if( lp_itr == tokens.begin() ) return;
  string token = *lp_itr;
  vector<const char*>::const_iterator id_end = lp_itr;
  vector<const char*>::const_iterator id_itr = tokens.begin();
  string prev_token;
  // Step 2a: if the first token is a ">" back up.
  if( token == ">" ) {
    int rb_depth = 0;
    for(;;--lp_itr) {
      token = *lp_itr;
      if( token == ">" ) {
	rb_depth++;
      }
      else if( token == "<" ) {
	rb_depth--;
	if( rb_depth == 0 ) {
	  break;
	}
      }
      if(lp_itr == tokens.begin())
	break;
    }
  }
  // Step 2b: process the other tokens
  for(;;--lp_itr) {
    token = *lp_itr;
    if( token == "::" ) {
      id_itr = lp_itr;
      // Check for the special case of
      //   X<A,B>::Y();
      //        ^
      if( lp_itr != tokens.begin() ) {
	--lp_itr;
	token = *lp_itr;
	if( token == ">" ) {
	  int rb_depth = 0;
	  for(;;--lp_itr) {
	    token = *lp_itr;
	    if( token == ">" ) {
	      rb_depth++;
	    }
	    else if( token == "<" ) {
	      rb_depth--;
	      if( rb_depth == 0 ) {
		break;
	      }
	    }
	    if(lp_itr == tokens.begin())
	      break;
	  }
	  id_itr = lp_itr;
	}
	else {
	  ++lp_itr;
	  token = *lp_itr;
	}
      }
    }
    else if( is_id(token) ) {
      if( is_id(prev_token) ) {
	break;
      }
      id_itr = lp_itr;
    }
    else if( token == "~" ) {
      // ~foo()
      // Check for the special case of:
      //  foo::~foo()
      id_itr = lp_itr;
      if(lp_itr == tokens.begin())
	break;
      --lp_itr;
      token = *lp_itr;
      if( token != "::" ) {
	break;
      }
      token = "~";
      ++lp_itr;
    }
    else if( token == "*" ) {
      // char* foo();
      break;
    }
    else if( token == "&" ) {
      // char& foo();
      break;
    }
    else if( token == "]" ) {
      // char[3] foo();
      break;
    }
    else if( token == ">" ) {
      // Z<A,B> X();
      break;
    }
    prev_token = token;
    if(lp_itr == tokens.begin())
      break;
  }

  // ================================================
  // Step 3:
  //   Iterate to get the full id name.
  // ================================================
  id = "";
  for(;id_itr!=id_end;++id_itr) {
    token = *id_itr;
    id += token;
    // Special case handling for operators,
    // append a space.
    if( token == "operator" )
      id += " ";
  }
  id += *id_itr; // Get the last one.
}
// ================================================================
// Is this an id?
// ================================================================
bool ccdoc::phase1::parser::is_id(const string& token) const {
  if( ( 'a' <= token[0] && token[0] <= 'z' ) ||
      ( 'A' <= token[0] && token[0] <= 'Z' ) ||
      ( '$' == token[0] || token[0] == '_' ) ) {
    return true;
  }
  return false;
}
// ================================================================
// Parse a pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp()
{
  // ================================================
  // Get the tokens in the directive.
  // Ignore the leading '#'.
  // ================================================
  string token;
  vector<string> tokens;
  while( m_scanner.get_token(token) ) {
    if( token == "\n" ) {
      break;
    }
    tokens.push_back(token);
  }

  // ================================================
  // These are the directives that ccdoc ignores.
  //   #[0-9]+  <-- found this on a Solaris compiler and gnu compiler
  //   #
  //   #error
  //   #include
  //   #line
  //   #pragma
  // ================================================
  if( tokens.size() == 0 )
    return;
  token = tokens[0];
  if( ( token == "error"   ) ||
      ( token == "include" ) ||
      ( token == "line"    ) ||
      ( token == "pragma"  ) ||
      ( '0' <= token[0] && token[0] <= '9' ) ) {
    return;
  }

  // #define
  if( token == "define" ) {
    parse_cpp_define(tokens);
  }
  // #elif
  else if( token == "elif" ) {
    parse_cpp_elif(tokens);
  }
  // #else
  else if( token == "else" ) {
    parse_cpp_else(tokens);
  }
  // #endif
  else if( token == "endif" ) {
    parse_cpp_endif(tokens);
  }
  // #if
  else if( token == "if" ) {
    parse_cpp_if(tokens);
  }
  // #ifdef
  else if( token == "ifdef" ) {
    parse_cpp_ifdef(tokens);
  }
  // #ifndef
  else if( token == "ifndef" ) {
    parse_cpp_ifndef(tokens);
  }
  // #undef
  else if( token == "undef" ) {
    parse_cpp_undef(tokens);
  }
  else {
    parse_cpp_warn(tokens,"Unrecognized directive.");
  }
}
// ================================================================
// Parse #define pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_define( vector<string>& tokens )
{
  // ================================================
  // Macro handling is very challenging because ccdoc
  // treats macros as first class entities. This is
  // significantly different than C++ compilers
  // because compilers strip out all macros during
  // the pre-processing phase.
  // ================================================

  // ================================================
  // How are define entities removed after they are
  // converted to statements?
  //
  // This is especially troubling for the following
  // case:
  //
  //   //@{ M1 comment //@}
  //   #define M1
  //   #undef M1
  //   //@{ M1 comment //@}
  //   #define M1
  //
  // Where should ccdoc state that the macro was defined?
  // My guess is that should list the first source file
  // and line number.
  // For now, ccdoc will capture them all in the parsing
  // statements and figure out what to do later.
  // ================================================

  // ================================================
  // Another problem is what to do with define
  // statements if the user specified -D<macro> on
  // the command line.
  // The solution in this case is to accept all
  // definitions.
  // ================================================

  // ================================================
  // Ignore the #define statement unless we are in an
  // accept state.
  // ================================================
  if( m_action.back() == ACCEPT ) {
    string& type = tokens[0];
    if( tokens.size() < 2 ) {
      parse_cpp_warn(tokens,"Expected 1 or more tokens after #define.");
      return;
    }
    string& key = tokens[1];

    // ================================================
    // Check for multiple definitions.
    // ================================================
    if( m_defines.find(key) != m_defines.end() ) {
      // Warn the user that this macro has already
      // been defined.
      // For now, this state is ignored and the
      // macro is re-added. In the future, ccdoc
      // may choose to ignore multiple definitions.
      s_log.warning()
	<< "Multiply defined macro '"
	<< key
	<< "' at line "
	<< m_scanner.get_lineno()-1
	<< " in "
	<< m_scanner.get_file()
	<< ".\n"
	<< s_log.enable();
    }

    // ================================================
    // A single token.
    //   #define a
    // ================================================
    if( tokens.size() == 2 ) {
      string value = "";
      m_defines.insert( make_pair(key, value) );
      statement::base* stmt;
      stmt = make_statement(key,statement::base::STMT_MACRODEF_0_0,tokens);
      stmt->set_access( statement::base::STMT_PUBLIC );
      add_statement(stmt);
      return;
    }

    // ================================================
    // Two tokens.
    //   #define a b
    // ================================================
    if( tokens.size() == 3 ) {
      // This is another easy one.
      // #define a b
      string value = tokens[2];
      m_defines.insert( make_pair(key, value) );
      statement::base* stmt;
      stmt = make_statement(key,statement::base::STMT_MACRODEF_0_1,tokens);
      stmt->set_access( statement::base::STMT_PUBLIC );
      add_statement(stmt);
      return;
    }

    // ================================================
    // More than two tokens.
    //   #define M ( <arg list> ) <stuff>
    //   #define M <stuff>
    // ================================================
    else {
      string& lp = tokens[2];
      if( lp != "(" ) {
	// This is a macro of the form:
	// #define M a+b
	string value;
	for(int i=2;i<tokens.size();++i) {
	  if( i!=2 )
	    value += " ";
	  value += tokens[i];
	}
	m_defines.insert( make_pair(key, value) );
	statement::base* stmt;
	stmt = make_statement(key,statement::base::STMT_MACRODEF_0_N,tokens);
	stmt->set_access( statement::base::STMT_PUBLIC );
	add_statement(stmt);
	return;
      }

      // Find the end of the argument list.
      // Nested arguments are not supported.
      // #define MM(a,b,c,d) stuff
      int i=3;
      for(;i<tokens.size();++i) {
	string& rp = tokens[i];
	if( rp == ")" )
	  break;
      }

      // The RP was not found.
      if( i >= tokens.size() ) {
	parse_cpp_warn(tokens,"Missing argument list terminator ')'.");
	return;
      }

      // Update the value.
      string value;
      for(int j=i+1;j<tokens.size();++j) {
	if( j!=(i+1) )
	  value += " ";
	value += tokens[j];
      }
      m_defines.insert( make_pair(key, value) );
      statement::base* stmt;
      stmt = make_statement(key,statement::base::STMT_MACRODEF_N_N,tokens);
      stmt->set_access( statement::base::STMT_PUBLIC );
      add_statement(stmt);
      return;
    }
    parse_cpp_warn(tokens,"Unrecognized syntax.");
  }
}
// ================================================================
// Parse #elif pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_elif(vector<string>& tokens)
{
  if( tokens.size() < 2 ) {
    parse_cpp_warn(tokens,"Expected one or more tokens.");
    return;
  }
  if( m_action.size() < 2 ) {
    parse_cpp_warn(tokens,"Illegal nesting state.");
    return;
  }

  if( m_action.back() == REJECT_ALL ) {
    return;
  }
  if( m_action.back() == ACCEPT ) {
    // The previous #if or #elif was
    // accepted, reject all others until
    // we are out of scope.
    m_action.pop_back();
    m_action.push_back( REJECT_ALL );
  }
  else if( m_action.back() == REJECT ) {
    if( true == parse_cpp_if_expr(tokens) ) {
      m_action.pop_back();
      m_action.push_back( ACCEPT );
    }
  }
}
// ================================================================
// Parse #else pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_else(vector<string>& tokens)
{
  if( m_action.size() < 2 ) {
    parse_cpp_warn(tokens,"Illegal nesting state.");
    return;
  }
  if( m_action.back() == REJECT_ALL ) {
    return;
  }
  ACTION a = m_action.back();
  if( a == ACCEPT ) {
    m_action.pop_back();
    m_action.push_back(REJECT);
  }
  else if( a == REJECT ) {
    m_action.pop_back();
    m_action.push_back(ACCEPT);
  }
  // Do not change the state if it was REJECT_ALL
}
// ================================================================
// Parse #endif pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_endif(vector<string>& tokens)
{
  if( m_action.size() < 2 ) {
    parse_cpp_warn(tokens,"Illegal nesting state.");
    return;
  }
  m_action.pop_back();
}
// ================================================================
// Parse #if pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_if(vector<string>& tokens)
{
  if( tokens.size() < 2 ) {
    parse_cpp_warn(tokens,"Expected one or more tokens.");
    return;
  }
  if( m_action.back() != ACCEPT ) {
    m_action.push_back( REJECT_ALL );
    return;
  }
  if( true == parse_cpp_if_expr(tokens) ) {
    m_action.push_back( ACCEPT );
  }
  else {
    m_action.push_back( REJECT );
  }
}
// ================================================================
// Parse #ifdef pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_ifdef(vector<string>& tokens)
{
  if( tokens.size() < 2 ) {
    parse_cpp_warn(tokens,"Expected a single token after #ifdef.");
    return;
  }
  if( m_action.back() != ACCEPT ) {
    m_action.push_back( REJECT_ALL );
    return;
  }
  string& key = tokens[1];
  if( m_defines.find(key) == m_defines.end() ) {
    // Not found.
    m_action.push_back( REJECT );
  }
  else {
    // Found.
    m_action.push_back( ACCEPT );
  }
}
// ================================================================
// Parse #ifndef pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_ifndef(vector<string>& tokens)
{
  if( tokens.size() < 2 ) {
    parse_cpp_warn(tokens,"Expected a single token after #ifdef.");
    return;
  }
  if( m_action.back() != ACCEPT ) {
    m_action.push_back( REJECT_ALL );
    return;
  }
  string& key = tokens[1];
  if( m_defines.find(key) == m_defines.end() ) {
    // Not found.
    m_action.push_back( ACCEPT );
  }
  else {
    // Found.
    m_action.push_back( REJECT );
  }
}
// ================================================================
// Parse #undef pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_undef(vector<string>& tokens)
{
  if( m_action.back() == ACCEPT ) {
    if( tokens.size() < 2 ) {
      parse_cpp_warn(tokens,"Expected a single token after #undef.");
      return;
    }
    string& key = tokens[1];
    m_defines.erase( key );
  }
}
// ================================================================
// Parse #undef pre-processing directive.
// ================================================================
void ccdoc::phase1::parser::parse_cpp_warn(vector<string>& tokens,
					   const char* desc)
{
  // Warn that the directive was not processed.
  string& type = tokens[0];
  s_log.warning()
    << "Error found while processing cpp directive '#"
    << type << "' at line "
    << m_scanner.get_lineno()-1
    << " in "
    << m_scanner.get_file()
    << ".\n"
    << "\t" << desc << "\n"
    << "\tIt will be ignored.\n"
    << s_log.enable();
}
// ================================================================
// Parse #if and #elif expressions.
// ================================================================
bool ccdoc::phase1::parser::parse_cpp_if_expr(vector<string>& tokens)
{
  // Evaluate the expression.
  cpp_expr x(*this);
  bool eval_flag = x.eval(tokens,1);
  bool ok_flag = x.ok();
  if( m_debug )
    x.debug_dump("CCDOC_PHASE1_DEBUG: ");
  if( !ok_flag ) {
    // ================================================
    // The expression parsing failed.
    // Report a warning and ignore it.
    // ================================================
    eval_flag = false;
    string stmt_string;
    stmt_string = "Expression processing failed for '";
    vector<string>::iterator itr = tokens.begin();
    for(;itr!=tokens.end();++itr) {
      stmt_string += *itr;
      stmt_string += " ";
    }
    stmt_string += "'.";
    parse_cpp_warn(tokens,stmt_string.c_str());
  }

  // ================================================
  // Return the result of the evaluation.
  // ================================================
  if( m_debug ) {
    s_log << "CCDOC_PHASE1_DEBUG: eval: ";
    if( eval_flag )
      s_log << "true";
    else
      s_log << "false";
    s_log << "\n";
  }
  return eval_flag;
}
// ================================================================
// defined?
// ================================================================
bool ccdoc::phase1::parser::defined( const string& name ) const
{
  return m_defines.find( name ) != m_defines.end() ;
}
// ================================================================
// Parse a comment.
// ================================================================
void ccdoc::phase1::parser::parse_comment()
{
  vector<string> tokens;
  tokens.push_back("@{");
  if( m_debug ) {
    s_log << "CCDOC_PHASE1_DEBUG: ccdoc_begin:\n";
  }
  string token;

  ccdoc_assert( true == m_scanner.get_token(token) ); // @file
  tokens.push_back(token);
  ccdoc_assert( token == "@file" );

  ccdoc_assert( true == m_scanner.get_token(token) ); // "2"
  tokens.push_back(token);

  ccdoc_assert( true == m_scanner.get_token(token) ); // <file>
  tokens.push_back(token);

  ccdoc_assert( true == m_scanner.get_token(token) ); // <lineno>
  tokens.push_back(token);

  ccdoc_assert( true == m_scanner.get_token(token) ); // @type
  tokens.push_back(token);
  ccdoc_assert( token == "@type" );

  ccdoc_assert( true == m_scanner.get_token(token) ); // "1"
  tokens.push_back(token);

  ccdoc_assert( true == m_scanner.get_token(token) ); // <type>
  tokens.push_back(token);

  statement::base::TYPE t = statement::base::STMT_IGNORE;
  if( token == "@prefix" ) {
    t = statement::base::STMT_COMMENT_PREFIX;
  }
  else if( token == "@suffix" ) {
    t = statement::base::STMT_COMMENT_SUFFIX;
  }
  else {
    // Expected @prefix or @suffix.
    ccdoc_assert( token == "@prefix" );
  }

  // Collect the rest of the tokens.
  while( m_scanner.get_token(token) ) {
    tokens.push_back(token);
    if( token == "@pkgdoc" ) {
      // Handle the special case where the user
      // specified an @pkgdoc directive.
      // When this occurs, reset the parent for
      // this comment.
      string url;
      statement::base* pkgdoc = parse_comment_pkg_info(tokens,url);
      if( pkgdoc ) {
	// This @pkgdoc statement had an entry.
	t = statement::base::STMT_COMMENT_PKGDOC;
	if( url.size() ) // Issue 0025
	  t = statement::base::STMT_COMMENT_PKGDOC_URL;
	m_parents.push_back(pkgdoc);
      }
    }
    else if( token == "@pkg" && m_sw.pkg().size() == 0 ) {
      // The user specified that the pkg
      // has changed from here forward and
      // they did not override the directive
      // from the command line.
      string url; // This will never be set, it is guaranteed by the scanner.
      statement::base* pkg = parse_comment_pkg_info(tokens,url);
      if( pkg ) {
	t = statement::base::STMT_IGNORE;
	if( m_parents.size() == 1 ) {
	  // This @pkg statement had an entry.
	  m_parents.pop_back();
	  m_parents.push_back(pkg);
	}
	else {
	  // The parser is nested which means that it is
	  // not safe to change the package specification
	  // as in the following example:
	  //
	  //   namespace A {
	  //     //@{ @pkg Z.Y.Z //@}
	  //   }
	  //
	  // It would be difficult to figure out that
	  // the parent (scope) stack is in an illegal
	  // state.
	  //
	  // Report a warning and ignore the directive.
	  s_log.warning()
	    << "A nested @pkg directive is not allowed because "
	    << "it changes the modal scope\n"
	    << "\tat line "
	    << m_scanner.get_lineno()
	    << " in "
	    << m_scanner.get_file()
	    << ".\n"
	    << "\tIt will be ignored.\n"
	    << s_log.enable();
	}
      }
    }
    if( token == "@}" ) {
      break;
    }
  }

  if( t != statement::base::STMT_IGNORE ) {
    // Set the id.
    string id;
    m_db.get_next_comment_id(id);

    // Create the statement.
    statement::base* stmt = make_statement(id,t,tokens);
    add_statement(stmt);
    if( t == statement::base::STMT_COMMENT_PKGDOC ||
	t == statement::base::STMT_COMMENT_PKGDOC_URL ) {
      // Attach the comment to the package.
      statement::base* parent = m_parents.back();
      parent->set_comment(stmt);
      stmt->set_comment(parent);
      m_parents.pop_back();
    }
  }
  // Debug
  if( m_debug ) {
    s_log << "CCDOC_PHASE1_DEBUG: ccdoc_end:\n";
  }
}
// ================================================================
// Parse comment pkg info.
// ================================================================
ccdoc::statement::base*
ccdoc::phase1::parser::parse_comment_pkg_info(vector<string>& tokens,
					      string& url)
{
  string token;
  m_scanner.get_token(token);
  tokens.push_back(token);
  int num = get_int_value(token);
  if( num == 0 )
    return 0;

  // Issue 0025
  // Issue 0031
  // Load the package name.
  // There is a special case. If the @url or @tid keywords are found,
  // they indicate that the next entry is not a part of the path.
  // This only occurs in the case of a special form of the @pkgdoc
  // directive.
  string id;
  for(;num>0;--num) {
    m_scanner.get_token(token);
    tokens.push_back(token);
    if( token == "@url" || token == "@tid" ) {
      // Skip the next token.
      --num;
      m_scanner.get_token(token);
      tokens.push_back(token);
      if( token == "@url" )
	url = token;
      continue;
    }
    if(id.size())
      id += "::";
    id += token;
  }
  statement::base* pkg = m_db.get_create_package(id);
  return pkg;
}
// ================================================================
// Get the macro value.
// ================================================================
void ccdoc::phase1::parser::get_macro_value(const string& key,
					    string& value)
{
  // While value is a reference to another
  // macro, substitute it.
  //
  // This handles the following case:
  //   #define A 10
  //   #define B A
  //   #define C B
  // so that:
  //   A ==> 10
  //   B ==> 10
  //   C ==> 10
  switches::defines_type::iterator itr = m_defines.find(key);
  while( itr != m_defines.end() ) {
    if( (*itr).first == (*itr).second ) {
      break;
    }
    if( ((*itr).second).size() == 0 ) {
      // This covers the following case:
      //   #define A
      //   #define B A
      //   #if A == B
      //   #endif
      break;
    }
    value = (*itr).second;
    itr = m_defines.find(value);
  }
}
// ================================================================
// Get the macro value.
// ================================================================
int ccdoc::phase1::parser::get_macro_value(const string& key)
{
  string value;
  get_macro_value(key,value);
  return get_int_value( value );
}
// ================================================================
// Get the numeric value.
// ================================================================
int ccdoc::phase1::parser::get_int_value(const string& num)
{
  // Handle octal, decimal and hex values here.
  // 0x[0-9]+    ==> hex
  // 0X[0-9]+    ==> hex
  // 0[0-7]+     ==> octal
  // [1-9][0-9]+ ==> dec
  long nv = 0;
  const char* p = num.c_str();
  if( *p == 0 )
    return 0;
  if( *p == '0' ) {
    // This is octal, hexadecimal or 0.
    ++p;
    if( *p == 'x' || *p == 'X' ) {
      // It is hex.
      ++p;
      for(;*p;++p) {
	if( '0' <= *p && *p <= '9' ) {
	  nv = (nv*16) + (*p - '0');
	}
	else if( 'a' <= *p && *p <= 'f' ) {
	  nv = (nv*16) + (10 + *p - 'a');
	}
	else if( 'A' <= *p && *p <= 'F' ) {
	  nv = (nv*16) + (10 + *p - 'A');
	}
	else {
	  for(;*p;++p) {
	    if( 'U' == *p || 'u' == *p || 'L' == *p || 'l' == *p ) {
	      continue;
	    }
	    break;
	  }
	  if( *p ) {
	    // Parsing error, report a problem.
	    s_log.warning()
	      << "Invalid hex number encountered '"
	      << num
	      << "' around token '"
	      << *p
	      << "' at line "
	      << m_scanner.get_lineno()
	      << " in "
	      << m_scanner.get_file()
	      << ".\n"
	      << s_log.enable();
	    return 0;
	  }
	  return nv;
	}
      }
      return nv;
    }
    else {
      for(;*p;++p) {
	if( '0' <= *p && *p <= '7' ) {
	  nv = (nv*8) + (*p - '0');
	}
	else {
	  for(;*p;++p) {
	    if( 'U' == *p || 'u' == *p || 'L' == *p || 'l' == *p ) {
	      continue;
	    }
	    break;
	  }
	  if( *p ) {
	    // Parsing error, report a problem.
	    s_log.warning()
	      << "Invalid octal number encountered '"
	      << num
	      << "' around token '"
	      << *p
	      << "' at line "
	      << m_scanner.get_lineno()
	      << " in "
	      << m_scanner.get_file()
	      << ".\n"
	      << s_log.enable();
	    return 0;
	  }
	  return nv;
	}
      }
      return nv;
    }
  }

  // This is a decimal number.
  for(;*p;++p) {
    if( '0' <= *p && *p <= '9' ) {
      nv = (nv*10) + (*p - '0');
    }
    else {
      for(;*p;++p) {
	if( 'U' == *p || 'u' == *p || 'L' == *p || 'l' == *p ) {
	  continue;
	}
	break;
      }
      if( *p ) {
	// Parsing error, report a problem.
	s_log.warning()
	  << "Invalid decimal number encountered '"
	  << num
	  << "' around token '"
	  << *p
	  << "' at line "
	  << m_scanner.get_lineno()
	  << " in "
	  << m_scanner.get_file()
	  << ".\n"
	  << s_log.enable();
	return 0;
      }
      return nv;
    }
  }
  return nv;
}
// ================================================================
//
// cpp_expr
//
// ================================================================
// ================================================================
// Expression constructor.
// ================================================================
ccdoc::phase1::parser::cpp_expr::cpp_expr(parser& in_parser)
  : m_parser(in_parser),
    m_errors(0),
    m_node_id(0)
{
  // Create the root node.
  m_root_name = m_parser.get_sw().root();
  m_root = new node(*this,node::nd_type_root,m_root_name);
}
// ================================================================
// Expression destructor.
// ================================================================
ccdoc::phase1::parser::cpp_expr::~cpp_expr()
{
  delete m_root;
}
// ================================================================
// Evaluate.
// ================================================================
bool ccdoc::phase1::parser::cpp_expr::eval( vector<string>& stmt,
					    int start )
{
  // Populate the tree.
  node* parent = m_root;
  for(int i=start;i<stmt.size();++i) {
    // Get the node type.
    node::nd_type t = node::get_type( stmt[i] );
    if( t == node::nd_type_unknown ) {
      // This is not a known token type for the
      // expression. This might occur because I
      // did not fully implement the expression
      // handling.
      // The phase1 cpp expression processing will
      // report the error at the correct line
      // number.
      s_log.warning()
	<< "Unknown expression token encountered '"
	<< stmt[i]
	<< "'.\n"
	<< s_log.enable();
      inc_errors();
      return false;
    }

    // Create the node and insert it into the tree.
    node* nd = new node(*this,t,stmt[i]);
    if( m_errors )
      return false;

    nd = nd->insert(parent);
    if( m_errors )
      return false;

    parent = nd;
  }

  // Evaluate the tree.
  return m_root->eval() ? true : false;
}
// ================================================================
// Debug dump.
// ================================================================
void ccdoc::phase1::parser::cpp_expr::debug_dump( const char* prefix ) const
{
  ccdoc_assert( prefix );
  m_root->debug_dump(prefix);
}
// ================================================================
//
// cpp_expr::node
//
// ================================================================
// ================================================================
// Macro for doing local error reporting.
// ================================================================
#define ccdoc_phase1_expr_node_assert(expr) \
  if( !(expr) ) { \
    error(#expr,__FILE__,__LINE__); \
    return 0; \
  }
// ================================================================
// Constructor.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node::node(cpp_expr& x,
				nd_type t,
				const string& name)
  : m_expr(x),
    m_parent(0),
    m_name(name),
    m_type(t),
    m_left(0),
    m_right(0),
    m_id(0)
{
  m_id = m_expr.get_next_node_id();
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node::~node()
{
  if( m_parent ) {
    if( m_parent->m_left == this )
      m_parent->m_left = 0;
    if( m_parent->m_right == this )
      m_parent->m_right = 0;
    m_parent = 0;
  }
  if( m_left ) {
    delete m_left;
    m_left = 0;
  }
  if( m_right ) {
    delete m_right;
    m_right = 0;
  }
  m_type = nd_type_unknown;
}
// ================================================================
// Insert the node into the tree.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node*
ccdoc::phase1::parser::cpp_expr::node::insert( node* parent )
{
  ccdoc_phase1_expr_node_assert(parent);

  // The parent argument is actually the proposed parent.
  switch(m_type) {
  case nd_type_id:
  case nd_type_num:
  case nd_type_def:
  case nd_type_lp:
  case nd_type_not:
    return insert_lor(parent);
  case nd_type_rp:
    return insert_rp(parent);
  case nd_type_and:
    return insert_and(parent);
  case nd_type_or:
    return insert_or(parent);
  case nd_type_eq:
  case nd_type_ne:
  case nd_type_lt:
  case nd_type_le:
  case nd_type_gt:
  case nd_type_ge:
    return insert_cmp(parent);
  case nd_type_root:
    return 0;
  default:
    break;
  }
  s_log.warning()
    << "Unimplemented expression token encountered '"
    << m_name
    << "'.\n"
    << s_log.enable();
  m_expr.inc_errors();
  return 0;
}
// ================================================================
// Insert and node into the tree.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node*
ccdoc::phase1::parser::cpp_expr::node::insert_and( node* parent )
{
  // Backup until you find operator of the same or greater
  // precedence(&& or lp with no rp) and insert this as the
  // right operand.
  // If you reach the top, replace the left operand
  // with this.
  while( parent->m_type != nd_type_root ) {
    // Handle nested parentheses.
    parent = parent->m_parent;
    if( ( parent->m_type == nd_type_and ) ) {
      m_left = parent->m_right;
      parent->m_right = this;
      m_parent = parent;
      return this;
    }
    if( parent->m_type == nd_type_lp && parent->m_right == 0 ) {
      m_left = parent->m_left;
      parent->m_left = this;
      m_parent = parent;
      return this;
    }
  }
  ccdoc_phase1_expr_node_assert(parent->m_type == nd_type_root);
  m_left = parent->m_left;
  parent->m_left = this;
  m_parent = parent;
  return this;

}
// ================================================================
// Insert comparison op node into the tree.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node*
ccdoc::phase1::parser::cpp_expr::node::insert_cmp( node* parent )
{
  // Backup until you find operator of the same or greater
  // precedence(&&, ||, ==, !=, <, <=, >, >= or lp with no rp)
  // and insert this as the right operand.
  // If you reach the top, replace the left operand
  // with this.
  while( parent->m_type != nd_type_root ) {
    // Handle nested parentheses.
    parent = parent->m_parent;
    if( ( parent->m_type == nd_type_and ) ||
	( parent->m_type == nd_type_or  ) ||
	( parent->m_type == nd_type_eq  ) ||
	( parent->m_type == nd_type_ne  ) ||
	( parent->m_type == nd_type_lt  ) ||
	( parent->m_type == nd_type_le  ) ||
	( parent->m_type == nd_type_gt  ) ||
	( parent->m_type == nd_type_ge  ) ) {
      m_left = parent->m_right;
      parent->m_right = this;
      m_parent = parent;
      return this;
    }
    if( parent->m_type == nd_type_lp && parent->m_right == 0 ) {
      m_left = parent->m_left;
      parent->m_left = this;
      m_parent = parent;
      return this;
    }
  }
  ccdoc_phase1_expr_node_assert(parent->m_type == nd_type_root);
  m_left = parent->m_left;
  parent->m_left = this;
  m_parent = parent;
  return this;
}
// ================================================================
// Insert node into the tree on the left or the right of the parent.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node*
ccdoc::phase1::parser::cpp_expr::node::insert_lor( node* parent )
{
  // All parents are legal except an rp.
  // These nodes insert on the left or right of the
  // parent.
  ccdoc_phase1_expr_node_assert(!parent->m_left || !parent->m_right);
  m_parent = parent;
  if( m_parent->m_left == 0 )
    m_parent->m_left = this;
  else if( m_parent->m_right == 0 )
    m_parent->m_right = this;
  return this;
}
// ================================================================
// Insert or node into the tree.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node*
ccdoc::phase1::parser::cpp_expr::node::insert_or( node* parent )
{
  // Backup until you find operator of the same or greater
  // precedence (&&, || or lp with no rp) and insert this as the
  // right operand.
  // If you reach the top, replace the left operand
  // with this.
  while( parent->m_type != nd_type_root ) {
    // Handle nested parentheses.
    parent = parent->m_parent;
    if( ( parent->m_type == nd_type_and ) ||
	( parent->m_type == nd_type_or  ) ) {
      m_left = parent->m_right;
      parent->m_right = this;
      m_parent = parent;
      return this;
    }
    if( parent->m_type == nd_type_lp && parent->m_right == 0 ) {
      m_left = parent->m_left;
      parent->m_left = this;
      m_parent = parent;
      return this;
    }
  }
  ccdoc_phase1_expr_node_assert(parent->m_type == nd_type_root);
  m_left = parent->m_left;
  parent->m_left = this;
  m_parent = parent;
  return this;
}
// ================================================================
// Insert rp node into the tree.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node*
ccdoc::phase1::parser::cpp_expr::node::insert_rp( node* parent )
{
  // The only legal parent is an lp.
  while( parent->m_type != nd_type_root ) {
    // Handle nested parentheses.
    parent = parent->m_parent;
    if( parent->m_type == nd_type_lp && parent->m_right == 0 ) {
      ccdoc_phase1_expr_node_assert(parent->m_type == nd_type_lp);
      ccdoc_phase1_expr_node_assert(!parent->m_right);
      m_parent = parent;
      m_parent->m_right = this;
      return parent;
    }
  }
  // Mismatched parentheses.
  ccdoc_phase1_expr_node_assert(parent->m_type == nd_type_lp);
  return 0;
}
// ================================================================
// Get the type name.
// ================================================================
const char* ccdoc::phase1::parser::cpp_expr::node::get_node_type_name(nd_type t)
{
  const char* ret = "unknown";
  switch(t) {
  case nd_type_id:      ret = "id";   break;
  case nd_type_num:     ret = "num";  break;
  case nd_type_def:     ret = "def";  break;
  case nd_type_lp:      ret = "lp";   break;
  case nd_type_rp:      ret = "rp";   break;
  case nd_type_not:     ret = "not";  break;
  case nd_type_and:     ret = "and";  break;
  case nd_type_or:      ret = "or";   break;
  case nd_type_eq:      ret = "eq";   break;
  case nd_type_ne:      ret = "ne";   break;
  case nd_type_lt:      ret = "lt";   break;
  case nd_type_le:      ret = "le";   break;
  case nd_type_gt:      ret = "gt";   break;
  case nd_type_ge:      ret = "ge";   break;
  case nd_type_root:    ret = "root"; break;
  case nd_type_unknown: ret = "unknown"; break;
  default:              ret = "unknown"; break;
  }
  return ret;
}
// ================================================================
// Get the node type.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node::nd_type
ccdoc::phase1::parser::cpp_expr::node::get_type(const string& id)
{
  if(id == "" ) return nd_type_root;
  if(id == "defined" ) return nd_type_def;
  if(id == "(" ) return nd_type_lp;
  if(id == ")" ) return nd_type_rp;
  if(id == "!" ) return nd_type_not;
  if(id == "&&") return nd_type_and;
  if(id == "||") return nd_type_or;
  if(id == "==") return nd_type_eq;
  if(id == "!=") return nd_type_ne;
  if(id == "<" ) return nd_type_lt;
  if(id == "<=") return nd_type_le;
  if(id == ">" ) return nd_type_gt;
  if(id == ">=") return nd_type_ge;
  if( '0' <= id[0] && id[0] <= '9' )
    return nd_type_num;
  if( ( 'a' <= id[0] && id[0] <= 'z' ) ||
      ( 'A' <= id[0] && id[0] <= 'Z' ) ||
      ( '$' == id[0] || '_' == id[0] ) ) {
    return nd_type_id;
  }
  return nd_type_unknown;
}
// ================================================================
// Get the root node.
// ================================================================
ccdoc::phase1::parser::cpp_expr::node* ccdoc::phase1::parser::cpp_expr::node::get_root() const
{
  node* nd = const_cast<node*>(this);
  while( nd->get_parent() ) {
    nd = nd->get_parent();
  }
  return nd;
}
// ================================================================
// Get the hierarchical depth.
// ================================================================
unsigned ccdoc::phase1::parser::cpp_expr::node::depth() const {
  unsigned depth = 0;
  node* nd = const_cast<node*>(this);
  while( nd->get_parent() ) {
    depth++;
    nd = nd->get_parent();
  }
  return depth;
}
// ================================================================
// Get the hierarchical name.
// ================================================================
string ccdoc::phase1::parser::cpp_expr::node::get_hier_name() const
{
  vector<string> names;
  node* nd = const_cast<node*>(this);
  while( nd->get_parent() ) {
    names.push_back(nd->m_name);
    nd = nd->get_parent();
  }
  names.push_back(nd->m_name);

  string out;
  vector<string>::reverse_iterator itr = names.rbegin();
  for(;itr!=names.rend();++itr) {
    out += "/";
    out += *itr;
  }
  return out;
}
// ================================================================
// Get the hierarchical id path.
// ================================================================
string ccdoc::phase1::parser::cpp_expr::node::get_hier_id_path() const
{
  vector<int> ids;
  node* nd = const_cast<node*>(this);
  while( nd->get_parent() ) {
    ids.push_back(nd->m_id);
    nd = nd->get_parent();
  }
  ids.push_back(nd->m_id);

  char nbuf[64];
  string out;
  vector<int>::reverse_iterator itr = ids.rbegin();
  for(;itr!=ids.rend();++itr) {
    sprintf(nbuf,"/%d",*itr);
    out += nbuf;
  }
  return out;
}
// ================================================================
// Debug dump.
// ================================================================
void ccdoc::phase1::parser::cpp_expr::node::debug_dump(const char* prefix) const
{
  ccdoc_assert( prefix );

  s_log
    << prefix
    << "CPP_EXPR_NODE: node: "
    << get_node_id()
    << "\n";
  s_log
    << prefix
    << "CPP_EXPR_NODE:   parent: ";
  if( m_parent)
    s_log << m_parent->m_id;
  s_log << "\n";

  s_log
    << prefix
    << "CPP_EXPR_NODE:   path: "
    << get_hier_id_path()
    << "\n";
  s_log
    << prefix
    << "CPP_EXPR_NODE:   depth: "
    << depth()
    << "\n";
  s_log
    << prefix
    << "CPP_EXPR_NODE:   type: "
    << get_node_type_name(m_type)
    << "\n";
  s_log
    << prefix
    << "CPP_EXPR_NODE:   name: "
    << m_name
    << "\n";

  if( m_type == nd_type_num ) {
    s_log
      << prefix
      << "CPP_EXPR_NODE:   value: "
      << m_expr.get_parser().get_int_value(m_name)
      << "\n";
  }
  else if( m_type == nd_type_id ) {
    string val;
    m_expr.get_parser().get_macro_value(m_name,val);
    s_log
      << prefix
      << "CPP_EXPR_NODE:   value: '"
      << val
      << "'\n";
  }

  s_log
    << prefix
    << "CPP_EXPR_NODE:   left: ";
  if( m_left )
    s_log << m_left->get_node_id();
  s_log << "\n";

  s_log
    << prefix
    << "CPP_EXPR_NODE:   right: ";
  if( m_right )
    s_log << m_right->get_node_id();
  s_log << "\n";

  if(m_left)
    m_left->debug_dump(prefix);
  if(m_right)
    m_right->debug_dump(prefix);
}
// ================================================================
// Evaluate a sub-expression.
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval()
{
  switch(m_type) {
  case nd_type_and:     return eval_and();
  case nd_type_def:     return eval_def();
  case nd_type_id:      return eval_id();
  case nd_type_lp:      return eval_lp();
  case nd_type_not:     return eval_not();
  case nd_type_num:     return eval_num();
  case nd_type_or:      return eval_or();
  case nd_type_rp:      return eval_rp();
  case nd_type_eq:      return eval_eq() ;
  case nd_type_ne:      return eval_ne() ;
  case nd_type_lt:      return eval_lt() ;
  case nd_type_le:      return eval_le() ;
  case nd_type_gt:      return eval_gt() ;
  case nd_type_ge:      return eval_ge() ;
  case nd_type_root:    return eval_root();
  default:              break;
  }

  // Unknown token type, report a problem.
  s_log.warning()
    << "Illegal expression eval condition encountered around token '"
    << m_name
    << "'.\n"
    << s_log.enable();
  m_expr.inc_errors();
  return false;
}
// ================================================================
// Report a token error.
// ================================================================
void ccdoc::phase1::parser::cpp_expr::node::error(const char* x,
						  const char* file,
						  int line)
{
  s_log.warning()
    << "Illegal expression eval condition encountered\n\taround token '"
    << m_name
    << "' for node "
    << m_id
    << ", type '"
    << get_node_type_name(m_type)
    << "'.\n\tThis error occurred because assertion '"
    << x
    << "' failed\n\tin source file "
    << file
    << " at line "
    << line
    << ".\n"
    << s_log.enable();
  m_expr.inc_errors();
}
// ================================================================
// eval_and
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_and()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_and );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  if( m_left->eval() && m_right->eval() )
    return 1;
  return 0;
}
// ================================================================
// eval_def
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_def()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_def );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right == 0 );
  // Drill down to the leaf, it should be an id.
  // This handles the following cases:
  //  #if defined ( M1 )
  //  #if defined ( ( ( M1 ) ) )
  node* nd = m_left;
  while( nd->m_left )
    nd = nd->m_left;
  ccdoc_phase1_expr_node_assert( nd->m_type == nd_type_id );
  return m_expr.get_parser().defined( nd->m_name );
}
// ================================================================
// eval_eq
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_eq()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_eq );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  // Check for the special case of <id> == <id>.
  if( m_left->m_type == nd_type_id &&
      m_right->m_type == nd_type_id ) {
    string val1;
    string val2;
    m_expr.get_parser().get_macro_value(m_left->m_name,val1);
    m_expr.get_parser().get_macro_value(m_right->m_name,val2);
    if( val1.size() && val2.size() == 0 ) {
      // This a comparison of the form:
      //   #define LEFT RIGHT
      //   #if LEFT == RIGHT
      return val1 == m_right->m_name;
    }
    if( val1.size() == 0 && val2.size() ) {
      // This a comparison of the form:
      //   #define RIGHT LEFT
      //   #if RIGHT == LEFT
      return val2 == m_left->m_name;
    }
    if( val1.size() == 0 && val2.size() == 0 ) {
      // This a comparison of the form:
      //   #define RIGHT LEFT
      //   #if RIGHT == LEFT
      return m_left->m_name == m_right->m_name;
    }
    return val1 == val2;
  }
  return m_left->eval() == m_right->eval() ? 1 : 0;
}
// ================================================================
// eval_id
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_id()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_id );
  ccdoc_phase1_expr_node_assert( m_left == 0 );
  ccdoc_phase1_expr_node_assert( m_right == 0 );
  return m_expr.get_parser().get_macro_value( m_name );
}
// ================================================================
// eval_ge
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_ge()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_ge );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  return m_left->eval() >= m_right->eval() ? 1 : 0;
}
// ================================================================
// eval_gt
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_gt()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_gt );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  return m_left->eval() > m_right->eval() ? 1 : 0;
}
// ================================================================
// eval_le
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_le()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_le );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  return m_left->eval() <= m_right->eval() ? 1 : 0;
}
// ================================================================
// eval_lp
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_lp()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_lp );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  ccdoc_phase1_expr_node_assert( m_right->m_type == nd_type_rp );
  return m_left->eval();
}
// ================================================================
// eval_lt
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_lt()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_lt );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  return m_left->eval() < m_right->eval() ? 1 : 0;
}
// ================================================================
// eval_ne
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_ne()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_ne );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  // Check for the special case of <id> == <id>.
  if( m_left->m_type == nd_type_id &&
      m_right->m_type == nd_type_id ) {
    string val1;
    string val2;
    m_expr.get_parser().get_macro_value(m_left->m_name,val1);
    m_expr.get_parser().get_macro_value(m_right->m_name,val2);
    if( val1.size() && val2.size() == 0 ) {
      // This a comparison of the form:
      //   #define LEFT RIGHT
      //   #if LEFT != RIGHT
      return val1 != m_right->m_name;
    }
    if( val1.size() == 0 && val2.size() ) {
      // This a comparison of the form:
      //   #define RIGHT LEFT
      //   #if RIGHT != LEFT
      return val2 != m_left->m_name;
    }
    if( val1.size() == 0 && val2.size() == 0 ) {
      // This a comparison of the form:
      //   #define RIGHT LEFT
      //   #if RIGHT == LEFT
      return m_left->m_name != m_right->m_name;
    }
    return val1 != val2;
  }
  return m_left->eval() != m_right->eval() ? 1 : 0;
}
// ================================================================
// eval_not
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_not()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_not );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right == 0 );
  return m_left->eval() ? 0 : 1;
}
// ================================================================
// eval_num
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_num()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_num );
  ccdoc_phase1_expr_node_assert( m_left == 0 );
  ccdoc_phase1_expr_node_assert( m_right == 0 );
  return m_expr.get_parser().get_int_value( m_name );
}
// ================================================================
// eval_or
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_or()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_or );
  ccdoc_phase1_expr_node_assert( m_left );
  ccdoc_phase1_expr_node_assert( m_right );
  if( m_left->eval() || m_right->eval() )
    return 1;
  return 0;
}
// ================================================================
// eval_root
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_root()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_root );
  int f = 0;
  if( m_left ) {
    f = m_left->eval();
    if( f && m_right ) {
      f = m_right->eval();
    }
  }
  return f;
}
// ================================================================
// eval_rp
// ================================================================
int ccdoc::phase1::parser::cpp_expr::node::eval_rp()
{
  ccdoc_phase1_expr_node_assert( m_type == nd_type_rp );
  ccdoc_phase1_expr_node_assert( m_left == 0 );
  ccdoc_phase1_expr_node_assert( m_right == 0 );
  return 1;
}
