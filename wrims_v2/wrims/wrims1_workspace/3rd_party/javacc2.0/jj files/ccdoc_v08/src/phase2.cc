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
#include "phase2.h"

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase2.cc,v 1.5 2001/11/26 20:10:50 Administrator Exp $";

// ================================================================
// Process a match.
// ================================================================
static bool phase2_process_match(ccdoc::database& db,
				 ccdoc::statement::base* match,
				 ccdoc::statement::base* stmt)
{
  if( match->get_type() == ccdoc::statement::base::STMT_METHOD ||
      match->get_type() == ccdoc::statement::base::STMT_METHOD_CONSTRUCTOR ||
      match->get_type() == ccdoc::statement::base::STMT_METHOD_DESTRUCTOR ||
      match->get_type() == ccdoc::statement::base::STMT_METHOD_OPERATOR ) {
    ccdoc::statement::base* comm0 = match->get_comment();
    ccdoc::statement::base* comm1 = stmt->get_comment();
    if( comm0 == 0 ) {
      // Don't override the original comment if it
      // exists.
      if( comm1 ) {
	ccdoc::statement::comment comm(comm1);
	if( comm.get_suffix() ) {
	  comm1->insert_after(match);
	}
	else {
	  comm1->insert_before(match);
	}
	match->set_comment(comm1);
	comm1->set_comment(match);
      }
    }
    else if( comm1 ) {
      // Comments were specified for the original and
      // for the reference. Use the original comment.
      delete comm1;
      stmt->set_comment(0);
    }
    // Issue 0068
    db.erase_from_path_map(stmt);
    delete stmt;
    return true;
  }
  return false;
}
// ================================================================
// Get the function argument list.
// ================================================================
static void phase2_get_arg_list(ccdoc::statement::base* stmt,
				string& args)
{
  const vector<const char*>& vec = stmt->get_tokens();
  vector<const char*>::const_iterator itr = vec.begin();
  vector<const char*>::const_iterator lp_itr = itr;
  int depth = 0;
  for(;itr!=vec.end();++itr) {
    string token = *itr;
    if( token == "(" ) {
      if( !depth )
	lp_itr = itr;
      depth++;
    }
    else if( token == ")" ) {
      depth--;
    }
  }
  args = "";
  for(;lp_itr!=vec.end();++lp_itr) {
    if( args.size() )
      args += " ";
    args += *lp_itr;
  }
}
// ================================================================
// Phase2
// ================================================================
bool ccdoc::phase2::run(switches& sw,database& db)
{
  database& m_db = db;
  switches& m_sw = sw;
  if(m_sw.verbose()) {
    s_log << "phase 2: begins\n";
  }

  m_db.load_path_map(); // Issue 0039

  // Look for spurious function definitions that result from
  // the following code:
  //    class A {
  //    public:
  //      A();
  //      ~A();
  //    };
  //    A::A() {}
  //    A::~A() {}
  vector<statement::base*> stmts;
  m_db.load(stmts,statement::base::STMT_FUNCTION);
  m_db.load(stmts,statement::base::STMT_FUNCTION_OPERATOR);
  vector<statement::base*>::iterator itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    statement::base* stmt = *itr;

    // Issue 0117:
    //  The name scoping was not done properly in some cases.
    string id;
    stmt->get_hier_id_no_pkgs(id);

    // ================================================
    // Quick check to determine whether it is possible
    // that this is a scoped id.
    // ================================================
    bool found_sep = false;
    const char* p = id.c_str();
    for(;*p;p++) {
      if( ':' == *p ) {
	++p;
	if( ':' == *p ) {
	  found_sep = true;
	  break;
	}
	--p;
      }
    }
    if( !found_sep )
      continue;

    // Look for matching classes.
    vector<statement::base*> matching_stmts;
    m_db.get_stmt_no_pkgs(id,matching_stmts);

    if( matching_stmts.size() == 1 ) {
      // Remember that we are looking for the hier entry.
      // If only one was found, that is good, we can
      // do the substitution here.
      // delete this statement from the database.
      // Make sure that we capture the comment.
      phase2_process_match(m_db,matching_stmts[0],stmt);
    }
    else if( matching_stmts.size() > 1 ) {
      // There are multiple matching statements.
      // Find an exact match in the argument list.
      string args0;
      string args1;
      phase2_get_arg_list(stmt,args0);

      vector<statement::base*>::iterator mitr = matching_stmts.begin();
      for(;mitr!=matching_stmts.end();++mitr) {
	statement::base* match = *mitr;
	phase2_get_arg_list(match,args1);
	if( args0 == args1 ) {
	  // We have a match, convert it.
	  if( phase2_process_match(m_db,match,stmt) )
	    break;
	}
      }
    }
  }

  if(m_sw.verbose()) {
    s_log << "phase 2: ends\n";
  }
  return true;
}
