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
#if defined(_MSC_VER)
#pragma warning ( disable : 4786 4251 )
#endif

#include <cstdio>
#include <algorithm>
#include <iomanip>
#include "exceptions.h"
#include "phase3_html.h"

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase3_html.cc,v 1.45 2001/11/27 19:02:39 Administrator Exp $";

// ================================================================
// Forward declarations for sort compare functions.
// ================================================================
static bool compare_stmts_full_paths(const ccdoc::statement::base* a,
				     const ccdoc::statement::base* b);
static bool compare_stmts(const ccdoc::statement::base* a,
			  const ccdoc::statement::base* b);

// ================================================================
// Html
// ================================================================
ccdoc::phase3::html::html(switches& sw,database& db)
  : m_sw(sw),
    m_db(db),
    m_debug(false),
    m_ok(false),
    m_multiple_ref_index(0)
{
}
// ================================================================
// Html
// ================================================================
ccdoc::phase3::html::~html() {
}
// ================================================================
// Run
// ================================================================
bool ccdoc::phase3::html::run()
{
  // Load the customized header and trailer stuff.
  load(m_sw.header(),m_header);
  load(m_sw.trailer(),m_trailer);
  load(m_sw.meta(),m_meta);

  m_db.load_path_map(); // Issue 0039

  // Issue 0044
  // Allow the user to control the hierarchical indent level.
  {
    m_rptcsi = "";
    for(int i=m_sw.rptcsi();i>0;--i) {
      m_rptcsi += "&nbsp;";
    }
  }

  write_ccdoc_pkgs_html();
  write_ccdoc_namespaces_html();
  write_ccdoc_classes_html();
  write_ccdoc_class_summary_html();
  write_ccdoc_functions_html();
  write_ccdoc_variables_html();
  write_ccdoc_enums_html();
  write_ccdoc_typedefs_html();
  write_ccdoc_macros_html();

  m_ok = true;
  return m_ok;
}
// ================================================================
// Write the package files.
// Each package file contains the following:
//   - The ccdoc description section.
//   - The table of contents: child packages and namespaces.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_pkgs_html()
{
  statement::base::stmts_t stmts;
  m_db.load(stmts,statement::base::STMT_PACKAGE);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " packages ...\n";
  }

  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for package "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,m_sw.rptdpa(),m_sw.rptdpv());
    write_unindent(os);
    write_contents(os,stmt);
    write_common_trailer_info(os);
  }
}
// ================================================================
// write the namespace files.
// Each namespace file contains the following:
//   - The ccdoc description section.
//   - The table of contents: child namespaces.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_namespaces_html()
{
  statement::base::stmts_t stmts;
  m_db.load(stmts,statement::base::STMT_NAMESPACE_BEGIN);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " namespaces ...\n";
  }
  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for namespace "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_unindent(os);
    write_contents(os,stmt);
    write_common_trailer_info(os);
  }
}
// ================================================================
// write the class files.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_classes_html()
{
  statement::base::stmts_t stmts;
  m_db.load(stmts,statement::base::STMT_CLASS_BEGIN);
  m_db.load(stmts,statement::base::STMT_STRUCT_BEGIN);
  if( m_sw.rptun() ) {
    m_db.load(stmts,statement::base::STMT_UNION_BEGIN);
  }
  statement::base::stmts_itr_t itr = stmts.begin();
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " classes ...\n";
  }
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for class "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_extends_clause(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_friends_info(os,stmt);
    write_unindent(os);
    write_contents(os,stmt,m_sw.rptsci());
    write_class_details(os,stmt,m_sw.rptsci());
    write_common_trailer_info(os);
  }
}
// ================================================================
// Generating the class summary.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_class_summary_html()
{
  statement::base::stmts_t stmts;
  m_db.load(stmts,statement::base::STMT_CLASS_BEGIN);
  m_db.load(stmts,statement::base::STMT_STRUCT_BEGIN);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for class summary\n";
  }

  // Open the HTML file.
  string fn;
  fn = m_sw.html() + "ccdoc.class_summary.html";
  ofstream os(fn.c_str());
  if(!os) {
    throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						     __LINE__,
						     fn.c_str());
  }
  write_common_header_info(os,fn,"Class Summary");
  os << "<table border=0 width=\"100%\"><tr><td align=left>";
  if( m_sw.rooturl().size() ) {
    os << "<a href=\"" << m_sw.rooturl() << "\" target=_top>Home</a>";
    os << " :: ";
  }
  write_link(os,m_db.root());
  os << "</td><td align=right>";
  string url;
  string link_name = m_sw.html() + "ccdoc.class_summary.html";
  make_file_url(url,link_name);
  os << "<a href=\"" << url << "\">classes</a></td></tr></table>\n";

  write_section_header(os,"Class Summary",false);
  write_summary_tree(os,stmts,m_sw.rptcsd(),0,m_rptcsi.c_str());
  write_common_trailer_info(os);
}
// ================================================================
// Generating the class summary.
// ================================================================
void ccdoc::phase3::html::write_summary_tree(ostream& os,
					     statement::base::stmts_t& stmts,
					     bool rpthpc,
					     statement::base* start,
					     const char* in_indent,
					     bool sort_flag)
{
  // ================================================
  // Order the entries.
  // ================================================
  if( sort_flag ) {
    sort(stmts.begin(),stmts.end(),compare_stmts_full_paths);
  }

  // ================================================
  // Initialize the tags.
  // These are used later to identify when the parents
  // have been visited.
  // ================================================
  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    statement::base* stmt = *itr;
    stmt->set_tag(0);
    statement::base::stmts_t parents;
    stmt->get_parents(parents);
    statement::base::stmts_itr_t pitr = parents.begin();
    for(;pitr!=parents.end();++pitr) {
      statement::base* parent = *pitr;
      parent->set_tag(0);
    }
  }

  // ================================================
  // Output the information.
  // ================================================
  if( stmts.size() == 0 )
    return;
  if( rpthpc ) {
    // Issue 0026
    os << "<table border=0>\n"
       << "<tr>"
       << "<th valign=bottom align=left>Entity</th>"
       << "<th valign=bottom align=left>Type</th>"
       << "<th valign=bottom align=left>Scope</th>"
       << "<th valign=bottom align=left>Short Description</th>"
       << "</tr>\n";
  }
  else {
    os << "<table border=0 cellpadding=0 cellspacing=0>\n";
  }
  itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    statement::base* stmt = *itr;
    if( stmt->get_tag() == 0 ) {
      string indent = "";
      statement::base::stmts_t parents;
      stmt->get_parents(parents);
      statement::base::stmts_itr_t pitr = parents.begin();
      if( start ) {
	// The start parent was specified,
	// skip ahead until a match is found.
	for(;pitr!=parents.end();++pitr) {
	  statement::base* parent = *pitr;
	  if( parent == start ) {
	    ++pitr;
	    break;
	  }
	}
      }
      for(;pitr!=parents.end();++pitr) {
	statement::base* parent = *pitr;
	if( parent->get_tag() == 0 ) {
	  parent->set_tag(1);
	  pitr = parents.begin();
	  write_summary_tree_entry(os,parent,indent.c_str(),rpthpc);
	  indent = "";
	}
	indent += in_indent;
      }
      stmt->set_tag(1);
      write_summary_tree_entry(os,stmt,indent.c_str(),rpthpc);
    }
  }
  os << "</table>\n";
}
// ================================================================
// Generating the class summary.
// ================================================================
void ccdoc::phase3::html::write_summary_tree_entry(ostream& os,
						   statement::base* stmt,
						   const char* indent,
						   bool rpthpc)
{
  os << "<tr>";
  os << "<td align=left valign=top>";
  if(indent)
    os << indent;
  write_link(os,stmt);
  os << "</td>";
  if( rpthpc ) {
    // Issue 0026

    // Type
    string type = stmt->get_type_name1();
    if( stmt->get_type() == statement::base::STMT_PACKAGE &&
	stmt->get_comment() ) {
      statement::comment doc(stmt->get_comment());
      string tid = doc.get_pkgdoc_tid();
      if( tid.size() )
	type = tid;
    }
    os << "<td align=left valign=top>"
       << type
       << "&nbsp;</td>";
    
    // Access
    os << "<td align=left valign=top>";
    os << stmt->get_access_name();
    os << "</td>";

    // Short description
    os << "<td align=left valign=top>";
    write_short_desc(os,stmt);
    os << "</td>";
  }
  os << "</tr>\n";
}
// ================================================================
// Generating the class summary.
// ================================================================
void ccdoc::phase3::html::write_short_desc(ostream& os,
					   statement::base* stmt)
{
  if( stmt->get_comment() ) {
    statement::comment doc(stmt->get_comment());
    if( doc.get_short_desc().size() == 0 ) {
      // Issue 0030
      if( stmt->get_type() != statement::base::STMT_PACKAGE ||
	  m_sw.rptdpd() == false ) {
	os << m_sw.rptdefsd();
      }
    }
    else {
      statement::base* scope = stmt->get_parent();
      write_ccdoc_desc_info(os,doc.get_short_desc(),scope);
    }
  }
  else {
    if( stmt->get_lineno() == 0 ) {
      // Lineno zero flags the fact that it
      // was automatically generated by the
      // compiler.
      os << m_sw.rptdefasd();
    }
    else {
      // Issue 0030
      if( stmt->get_type() != statement::base::STMT_PACKAGE ||
	  m_sw.rptdpd() == false ) {
	os << m_sw.rptdefsd();
      }
    }
  }
}
// ================================================================
// write the function files.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_functions_html()
{
  statement::base::stmts_t stmts;
  m_db.load_top(stmts,statement::base::STMT_FUNCTION);
  m_db.load_top(stmts,statement::base::STMT_FUNCTION_OPERATOR);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " global functions ...\n";
  }
  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for global function "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_code_section(os,stmt);
    write_unindent(os);
    write_common_trailer_info(os);
  }
}
// ================================================================
// write the variable files.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_variables_html()
{
  statement::base::stmts_t stmts;
  m_db.load_top(stmts,statement::base::STMT_ATTRIBUTE);
  m_db.load_top(stmts,statement::base::STMT_ATTRIBUTE_FUNCTION);
  m_db.load_top(stmts,statement::base::STMT_VARIABLE);
  m_db.load_top(stmts,statement::base::STMT_VARIABLE_FUNCTION);
  statement::base::stmts_itr_t itr = stmts.begin();
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " global variables ...\n";
  }
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for global variable "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_code_section(os,stmt);
    write_unindent(os);
    write_common_trailer_info(os);
  }
}
// ================================================================
// write the enum files.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_enums_html()
{
  statement::base::stmts_t stmts;
  m_db.load_top(stmts,statement::base::STMT_ENUM);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " global enums ...\n";
  }
  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for global enum "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_code_section(os,stmt);
    write_unindent(os);
    write_common_trailer_info(os);
  }
}
// ================================================================
// write the typedef files.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_typedefs_html()
{
  if( m_sw.rpttyp() == false )
    return;
  statement::base::stmts_t stmts;
  m_db.load_top(stmts,statement::base::STMT_TYPEDEF_FUNCTION);
  m_db.load_top(stmts,statement::base::STMT_TYPEDEF_VARIABLE);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " global typedefs ...\n";
  }
  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for global typedef "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_code_section(os,stmt);
    write_unindent(os);
    write_common_trailer_info(os);
  }
}
// ================================================================
// write the macro files.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_macros_html()
{
  if( !m_sw.rptmac() )
    return;
  statement::base::stmts_t stmts;
  m_db.load_top(stmts,statement::base::STMT_MACRODEF_0_0);
  m_db.load_top(stmts,statement::base::STMT_MACRODEF_0_1);
  m_db.load_top(stmts,statement::base::STMT_MACRODEF_0_N);
  m_db.load_top(stmts,statement::base::STMT_MACRODEF_N_N);
  if( m_sw.verbose() ) {
    s_log << "phase3: generating HTML for "
	  << stmts.size()
	  << " macro definitions ...\n";
  }
  statement::base::stmts_itr_t itr = stmts.begin();
  for(;itr!=stmts.end();++itr) {
    string fn;
    statement::base* stmt = *itr;
    make_unique_file_name(fn,stmt);
    ofstream os(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
						       __LINE__,
						       fn.c_str());
    }
    if( m_sw.verbose() ) {
      string hier_id;
      stmt->get_hier_id(hier_id);
      s_log << "phase3: generating HTML for macro "
	    << hier_id
	    << " ...\n";
    }
    write_common_header_info(os,fn,stmt);
    write_entity_header_info(os,stmt);
    write_indent(os);
    write_ccdoc_info(os,stmt,true,true);
    write_code_section(os,stmt);
    write_unindent(os);
    write_common_trailer_info(os);
  }
}
// ================================================================
// Write the class details.
// ================================================================
void ccdoc::phase3::html::write_class_details(ostream& os,
					      statement::base* stmt,
					      bool sort_flag)
{
  if( stmt ) {
    statement::base::stmts_t contents;
    make_contents(stmt,contents,sort_flag);
    string prev_tag;
    string next_tag;
    statement::base::stmts_itr_t next_itr;
    statement::base::stmts_itr_t itr = contents.begin();
    for(;itr!=contents.end();++itr) {
      statement::base* child = *itr;
      string tag;
      make_tag_id(child,tag);
      os << "<a name=\"" << tag << "\"></a>\n";

      // Get the next tag.
      next_itr = itr;
      ++next_itr;
      if( next_itr != contents.end() ) {
	make_tag_id(*next_itr,next_tag);
      }
      write_section_header(os,
			   child,
			   true,
			   prev_tag.c_str(),
			   next_tag.c_str());
      write_indent(os);
      if( child->get_parent() == stmt ) {
	write_ccdoc_info(os,child,false,false,false);
      }
      else {
	write_ccdoc_info(os,child,false,false,true);
      }
      write_code_section(os,child);
      write_unindent(os);
      prev_tag = tag;
    }
  }
}
// ================================================================
// write contents
// ================================================================
void ccdoc::phase3::html::write_contents(ostream& os,
					 statement::base* stmt,
					 bool sort_flag)
{
  bool inherited_from_col = false;
  if( m_sw.rptim() ) {
    if( stmt->get_type() == statement::base::STMT_CLASS_BEGIN ||
	stmt->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
      // Don't create the inherited from column unless this
      // class is derived.
      int depth = 0;
      const statement::base::cstrs_t& tokens = stmt->get_tokens();
      statement::base::cstrs_citr_t itr = tokens.begin();
      for(;itr!=tokens.end();++itr) {
	string token = *itr;
	if( token == "<" ) {
	  depth++;
	}
	else if( token == ">" ) {
	  depth--;
	}
	else if( depth == 0 && token == ":" ) {
	  inherited_from_col = true;
	  break;
	}
      }
    }
  }

  if( m_sw.rpthpc() && stmt->get_type() == statement::base::STMT_PACKAGE ) {
    os << "<a name=\"ccdoc_contents\"></a>\n";
    write_section_header(os,"Contents",false);
    write_indent(os);
    
    statement::base::stmts_t unfiltered_contents;
    statement::base::stmts_t filtered_contents;
    stmt->get_all_children(unfiltered_contents);
    statement::base::stmts_itr_t itr = unfiltered_contents.begin();
    for(;itr!=unfiltered_contents.end();++itr) {
      statement::base* child = *itr;
      if(ignore_contents_stmt(child))
	continue;
      if( child->get_type() == statement::base::STMT_PACKAGE ) {
	// Always report the nested packages.
	filtered_contents.push_back(child);
      }
      else {
	// Issue 0034
	if( child->get_parent() == stmt )
	  filtered_contents.push_back(child);
      }
    }
    // Issue 0047: -rptcsi also controls the contents column.
    write_summary_tree(os,filtered_contents,true,stmt,m_rptcsi.c_str());
    write_unindent(os);
    return;
  }
  // ================================================
  // This is the standard contents table.
  // ================================================
  statement::base::stmts_t contents;
  make_contents(stmt,contents,sort_flag);
  if( contents.size() ) {
    os << "<a name=\"ccdoc_contents\"></a>\n";
    write_section_header(os,"Contents",false);
    write_indent(os);
    
    os << "<table cellspacing=4>\n"
       << "<tr>\n"
       << "<th valign=bottom align=left>Entity</th>"; // Issue 0038
    
    if( inherited_from_col ) {
      os << "<th valign=bottom align=left>Inherited From</th>"; // Issue 0037
    }
    
    os << "<th valign=bottom align=left>Type</th>"
       << "<th valign=bottom align=left>Scope</th>"
       << "<th valign=bottom align=left>Short Description</th>"
       << "</tr>\n";
    
    statement::base::stmts_itr_t itr = contents.begin();
    for(;itr!=contents.end();++itr) {
      statement::base* child = *itr;
      
      os << "<tr>\n";
      os << "<td align=left valign=top>";
      if( child->get_type() == statement::base::STMT_PACKAGE ||
	  child->get_type() == statement::base::STMT_NAMESPACE_BEGIN ) {
	string link;
	if( stmt->get_parent() )
	  make_rel_path(stmt,child,link);
	else
	  make_abs_path(child,link);
	write_link(os,child,link.c_str());
      }
      else {
	if( child->get_parent() == stmt ) {
	  write_link(os,child);
	}
	else {
	  // These are children from other parents.
	  // They are created when derived classes
	  // inherit methods.
	  string url;
	  string id;
	  make_file_url(url,stmt);
	  make_tag_id(child,id);
	  //write_link(os,child->get_parent());
	  //os << " :: ";
	  os << "<a href=\"" << url << "#" << id << "\">";
	  write_html_formatted_string(os,child->get_id());
	  os << "</a>";
	}
      }
      os << "</td>";
      
      if( inherited_from_col ) {
	os << "<td align=left valign=top>";
	if( child->get_parent() && child->get_parent() != stmt ) {
	  string id;
	  child->get_parent()->get_hier_id_no_pkgs(id);
	  write_link(os,child->get_parent(),id.c_str());
	}
	os << "</td>";
      }
      
      // Type
      string type = child->get_type_name1();
      if( child->get_type() == statement::base::STMT_PACKAGE &&
	  child->get_comment() ) {
	statement::comment doc(child->get_comment());
	string tid = doc.get_pkgdoc_tid();
	if( tid.size() )
	  type = tid;
      }
      os << "<td align=left valign=top>"
	 << type
	 << "&nbsp;</td>";
      
      // Access
      os << "<td align=left valign=top>";
      os << child->get_access_name();
      os << "</td>";
      
      // Short description
      os << "<td align=left valign=top>";
      write_short_desc(os,child);
      os << "</td>";
      
      os << "</tr>\n";
    }
    os << "</table>\n";
  }
  write_unindent(os);
}
// ================================================================
// Indent.
// ================================================================
void ccdoc::phase3::html::write_indent(ostream& os) const
{
#if 0
  // <blocquote> uses fewer characters
  os << "<table><tr><td>&nsp;&nsp;&nsp;&nsp;</td><td>\n";
#endif
  os << "<blockquote>\n";
}
// ================================================================
// Unindent.
// ================================================================
void ccdoc::phase3::html::write_unindent(ostream& os) const
{
#if 0
  os << "</td></table>\n";
#endif
  os << "</blockquote>\n";
}
// ================================================================
// Section header.
// ================================================================
void ccdoc::phase3::html::write_section_header(ostream& os,
					       const char* title,
					       bool contents,
					       const char* prev,
					       const char* next)
{
  os << "<hr>"
     << "<table width=\"100%\" border=0 cellpadding=0 cellspacing=0>"
     << "<tr>"
     << "<td align=left valign=top>"
     << "<h2>" << title << "</h2>"
     << "</td>"
     << "<td align=right valign=top>";
  if( contents ) {
    os << "<a href=\"#ccdoc_contents\">?</a>&nbsp;";
  }
  os << "<a href=\"#ccdoc_top\">^</a>&nbsp;\n";
  if( prev && *prev ) {
    os << "<a href=\"#" << prev << "\">&lt;</a>&nbsp;";
  }
  if( next && *next ) {
    os << "<a href=\"#" << next << "\">&gt;</a>&nbsp;";
  }
  os << "</td>"
     << "</tr>"
     << "</table>\n";
}
// ================================================================
// Section header.
// ================================================================
void ccdoc::phase3::html::write_section_header(ostream& os,
					       statement::base* stmt,
					       bool contents,
					       const char* prev,
					       const char* next)
{
  if( stmt ) {
    string id = format_string_for_html(stmt->get_id()) ;
    string title;
    if( stmt->get_type() == statement::base::STMT_PACKAGE ) {
      // Issue 31 - The user might have specified their own package tag.
      title = stmt->get_type_name1();
      if( stmt->get_comment() ) {
	statement::comment doc(stmt->get_comment());
	string tid = doc.get_pkgdoc_tid();
	if( tid.size() ) {
	  title = tid;
	}
      }
    }
    // The "operator" keyword is already built into the operator id.
    else if( stmt->get_type() != statement::base::STMT_FUNCTION_OPERATOR &&
	     stmt->get_type() != statement::base::STMT_METHOD_OPERATOR ) {
      title = stmt->get_type_name1();
    }
    title += " ";
    title += id;
    write_section_header(os,title.c_str(),contents,prev,next);
  }
  else {
    write_section_header(os,"unknown",contents,prev,next);
  }
}
// ================================================================
// Code begin
// ================================================================
void ccdoc::phase3::html::code_begin(ostream& os)
{
  os << "<blockquote>";
  os << "<pre>";
  os << endl;
}
// ================================================================
// Code end
// ================================================================
void ccdoc::phase3::html::code_end(ostream& os) {
  os << "</pre>";
  os << "</blockquote>";
  os << endl;
}
// ================================================================
// Load
// ================================================================
void ccdoc::phase3::html::load(const string& fn,
			       string& contents)
{
  if( fn.size() ) {
    if( m_sw.verbose() ) {
      s_log << "loading " << fn.c_str() << " ...\n";
    }
    ifstream is(fn.c_str());
    if( !is ) {
      s_log.warning()
	<< "Cannot read file '"
	<< fn.c_str()
	<< "'\n"
	<< s_log.enable();
	return;
    }
    // Read the file (including whitespace) into the contents string.
    char ch;
    while( is.get(ch) ) {
      contents += ch;
    }
  }
}
// ================================================================
// Write out the code section.
// ================================================================
void ccdoc::phase3::html::write_code_section(ostream& os,
					     statement::base* stmt)
{
  // The first step is to group the tokens that are
  // associated by '::'.
  statement::base::strs_t grouped_tokens;
  {
    const statement::base::cstrs_t& src = stmt->get_tokens();
    statement::base::cstrs_citr_t itr = src.begin();
    for(;itr!=src.end();++itr) {
      string x = *itr;
      if( x == "::" ) {
	string token;
	if( grouped_tokens.size() ) {
	  token = grouped_tokens.back();
	  grouped_tokens.pop_back();
	}
	token += *itr;
	++itr;
	if( itr != src.end() ) {
	  token += *itr;
	}
	grouped_tokens.push_back(token);
      }
      else {
	grouped_tokens.push_back(x);
      }
    }
  }

  // Output the code portion.
  os << "<dl><dt><b>Code:</b></dt><dd>";
  if( stmt->get_type() == statement::base::STMT_ATTRIBUTE ||
      stmt->get_type() == statement::base::STMT_ATTRIBUTE_FUNCTION ) {
    write_code_subsection_var(os,stmt,grouped_tokens);
  }
  else if( stmt->get_type() == statement::base::STMT_FUNCTION ||
	   stmt->get_type() == statement::base::STMT_METHOD_CONSTRUCTOR ||
	   stmt->get_type() == statement::base::STMT_METHOD_DESTRUCTOR ||
	   stmt->get_type() == statement::base::STMT_METHOD ) {
    write_code_subsection_fct(os,stmt,grouped_tokens);
  }
  else if( stmt->get_type() == statement::base::STMT_FUNCTION_OPERATOR ||
	   stmt->get_type() == statement::base::STMT_METHOD_OPERATOR ) {
    write_code_subsection_opr(os,stmt,grouped_tokens);
  }
  else if( stmt->get_type() == statement::base::STMT_ENUM ) {
    write_code_subsection_enum(os,stmt,grouped_tokens);
  }
  else if( stmt->get_type() == statement::base::STMT_VARIABLE ||
	   stmt->get_type() == statement::base::STMT_VARIABLE_FUNCTION ) {
    write_code_subsection_var(os,stmt,grouped_tokens);
  }
  else {
    if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    os << stmt->get_access_name() << " ";
    string external_linkage = stmt->get_extern();
    if( external_linkage.size() )
      os << "extern " << external_linkage << " ";
    statement::base::strs_itr_t itr = grouped_tokens.begin();
    for(;itr!=grouped_tokens.end();++itr) {
      string& token = *itr;
      if( itr != grouped_tokens.begin() ) {
	os << " ";
      }
      write_code_subsection_token(os,stmt,token);
    }
    if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
    os << "\n";
  }
  os << "</dd></dl>\n";
}
// ================================================================
// Write out the code section for variables.
// ================================================================
void ccdoc::phase3::html::write_code_subsection_var(ostream& os,
						    statement::base* stmt,
						    statement::base::strs_t& group)
{
  // ================================================
  // There is some special case handling here for
  // variables with no type.
  // ================================================
  if( m_sw.rptfwcf() )
    os << "<code>"; // Issue 0045
  os << stmt->get_access_name() << " ";
  string external_linkage = stmt->get_extern();
  if( external_linkage.size() )
    os << "extern " << external_linkage << " ";
  statement::base::strs_t& vec1 = group;
  if( vec1.size() == 1 ) {
    // This is the special case of a variable that
    // was declared as part of a class, struct, union
    // or enum declaration of the form:
    //   enum AA {A,B,C} var;
    statement::base* parent = stmt->get_parent();
    bool found = false;
    if( parent ) {
      statement::base::stmts_t& vec2 = parent->get_children();
      statement::base::stmts_itr_t itr2 = vec2.begin();
      for(;itr2!=vec2.end();++itr2) {
	if( *itr2 == stmt ) {
	  break;
	}
      }
      // We found the statement, now back up until we find
      // an enum, class, struct or union. Variables of the
      // form int a,b,c; are handled in the parsing phase.
      if( stmt == *itr2 ) {
	for(;itr2!=vec2.begin();--itr2) {
	  statement::base* end = *itr2;
	  bool done = false;
	  switch( end->get_type() ) {
	  case statement::base::STMT_ENUM:
	    done = true;
	    write_link(os,end);
	    found = true;
	    break;
	  case statement::base::STMT_CLASS_END:
	  case statement::base::STMT_STRUCT_END:
	  case statement::base::STMT_UNION_END:
	    {
	      done = true;
	      statement::base* begin = end->get_matching_begin();
	      if( begin ) {
		write_link(os,begin);
		found = true;
	      }
	      break;
	    }
	  default:
	    break;
	  }
	  if(done)
	    break;
	}
      }
    }
    if( !found ) {
      os << "<font color=red>unknown_type</font>";
    }
    os << " ";
  }
  
  statement::base::strs_itr_t itr1 = vec1.begin();
  for(;itr1!=vec1.end();++itr1) {
    string& token = *itr1;
    if( itr1 != vec1.begin() ) {
      os << " ";
    }
    write_code_subsection_token(os,stmt,token);
  }
  os << "\n";
  if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
}
// ================================================================
// Write out the code section for functions.
// ================================================================
void ccdoc::phase3::html::write_code_subsection_fct(ostream& os,
						    statement::base* stmt,
						    statement::base::strs_t& group)
{
  // Here is where we get a bit fancy. The arguments are aligned
  // somewhat.
  statement::base::strs_t& vec1 = group;
  statement::base::strs_itr_t itr1 = vec1.begin();
  bool found_id = false;
  bool found_arg_lp = false;
  int depth = 0;
  int arg = 0;
  os << "<table><tr><td>";
  if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
  os << stmt->get_access_name() << " ";
  string external_linkage = stmt->get_extern();
  if( external_linkage.size() )
    os << "extern " << external_linkage << " ";
  for(;itr1!=vec1.end();++itr1) {
    string& token = *itr1;
    if( itr1 != vec1.begin() ) {
      os << " ";
    }
    if( !found_id && token == stmt->get_id() ) {
      found_id = true;
      os << "<b>";
      write_code_subsection_token(os,stmt,token);
      os << "</b>";
    }
    else {
      write_code_subsection_token(os,stmt,token);
    }
    
    if( token == "(" )
      depth++;
    else if( token == ")" )
      depth--;
    if(!found_arg_lp && found_id && depth==1) {
      found_arg_lp = true;
      if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
      os << "</td><td>";
      if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    }
    if( depth == 1 && token == "," ) {
      if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
      os << "</td></tr><tr><td> </td><td>";
      if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    }
  }
  if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
  os << "</td></tr></table>\n";
}
// ================================================================
// Write out the code section for operator functions.
// ================================================================
void ccdoc::phase3::html::write_code_subsection_opr(ostream& os,
						    statement::base* stmt,
						    statement::base::strs_t& group)
{
  // Here is where we get a bit fancy. The arguments are aligned
  // somewhat.
  statement::base::strs_t& vec1 = group;
  statement::base::strs_itr_t itr1 = vec1.begin();
  bool found_id = false;
  bool found_arg_lp = false;
  int depth = 0;
  int arg = 0;
  os << "<table><tr><td>";
  if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
  os << stmt->get_access_name() << " ";
  string external_linkage = stmt->get_extern();
  if( external_linkage.size() )
    os << "extern " << external_linkage << " ";
  // Walk through the statement tokens.
  // If it is an operator, it will be recognized
  // by the presence of the "operator" keyword.
  for(;itr1!=vec1.end();++itr1) {
    string& token = *itr1;
    if( itr1 != vec1.begin() ) {
      os << " ";
    }
    if( !found_id && token == "operator" ) {
      found_id = true;
      os << "<b>";
      token = stmt->get_id();
      write_code_subsection_token(os,stmt,token);
      os << "</b>";
      // Now skip ahead to the lp.
      if( token == "operator ()" ) {
	// Special case.
	++itr1;
	++itr1;
      }
      else {
	for(;itr1!=vec1.end();++itr1) {
	  if( *itr1 == "(" ) {
	    --itr1;
	    break;
	  }
	}
      }
    }
    else {
      write_code_subsection_token(os,stmt,token);
    }
    
    if( token == "(" ) {
      depth++;
    }
    else if( token == ")" ) {
      depth--;
    }
    if( !found_arg_lp && found_id && depth==1 ) {
      found_arg_lp = true;
      if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
      os << "</td><td>";
      if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    }
    if( depth == 1 && token == "," ) {
      if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
      os << "</td></tr><tr><td> </td><td>";
      if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    }
  }
  if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
  os << "</td></tr></table>\n";
}
// ================================================================
// Write out the code section for enums.
// ================================================================
void ccdoc::phase3::html::write_code_subsection_enum(ostream& os,
						     statement::base* stmt,
						     statement::base::strs_t& group)
{
  // Here is where we get a bit fancy. The arguments are aligned
  // somewhat.
  statement::base::strs_t& vec1 = group;
  statement::base::strs_itr_t itr1 = vec1.begin();
  bool found_id = false;
  bool found_arg_lp = false;
  int depth = 0;
  int arg = 0;
  os << "<table><tr><td>";
  if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
  os << stmt->get_access_name() << " ";
  string external_linkage = stmt->get_extern();
  if( external_linkage.size() )
    os << "extern " << external_linkage << " ";
  for(;itr1!=vec1.end();++itr1) {
    string& token = *itr1;
    if( itr1 != vec1.begin() ) {
      os << " ";
    }
    if( !found_id && token == stmt->get_id() ) {
      found_id = true;
      os << "<b>";
      write_code_subsection_token(os,stmt,token);
      os << "</b>";
    }
    else {
      write_code_subsection_token(os,stmt,token);
    }
    
    if( token == "{" )
      depth++;
    else if( token == "}" )
      depth--;
    // The id is not checked for here because anonymuous
    // enums do not have ids.
    if(!found_arg_lp && depth==1) {
      found_arg_lp = true;
      if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
      os << "</td><td>";
      if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    }
    if( depth == 1 && token == "," ) {
      if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
      os << "</td></tr><tr><td> </td><td>";
      if( m_sw.rptfwcf() ) os << "<code>"; // Issue 0045
    }
  }
  if( m_sw.rptfwcf() ) os << "</code>"; // Issue 0045
  os << "</td></tr></table>\n";
}
// ================================================================
// Write out token for code section.
// ================================================================
void ccdoc::phase3::html::write_code_subsection_token(ostream& os,
						      statement::base* stmt,
						      const string& token)
{
  if( token.size() == 0 )
    return;
  if( token == "<" )
    os << "&lt;";
  else if( token == ">" )
    os << "&gt;";
  else if( token == "&" )
    os << "&amp;";
  else if( token == "..." )
    os << "...";
  else if( token == stmt->get_id() ) {
    os << "<b>" << format_string_for_html(token) << "</b>";
  }
  else if( is_cxx_keyword(token) ) {
    os << "<font color=green><b>" << token << "</b></font>";
  }
  else if( ('a' <= token[0] && token[0] <= 'z' ) ||
	   ('A' <= token[0] && token[0] <= 'Z' ) ||
	   ('_' == token[0] ) ||
	   ('$' == token[0] ) ) {

    // ================================================
    // Do a simple search for local matches in the
    // parent.
    // This matches things like:
    //   class A {
    //   public:
    //     A& operator = (const A&);
    //   };
    // ================================================
    if( stmt->get_parent() ) {
      if( stmt->get_parent()->get_type() == statement::base::STMT_CLASS_BEGIN ||
	  stmt->get_parent()->get_type() == statement::base::STMT_STRUCT_BEGIN ||
	  stmt->get_parent()->get_type() == statement::base::STMT_UNION_BEGIN ) {
	if( token == stmt->get_parent()->get_id() ) {
	  os << "<b>" << format_string_for_html(token) << "</b>";
	  return;
	}
      }
    }
    
    // ================================================
    // Issue 0118
    // tokenize the id
    // A::B::C becomes A,B,C
    // ================================================
    vector<string> tokens;
    size_t beg=0;
    size_t end=beg;
    for(;end<token.size();++end) {
      if( end>beg && token[end] == ':' && token[end-1] == ':') {
        tokens.push_back( token.substr(beg,end-1) );
        beg = end+1;
      }
    }
    if(beg<end)
      tokens.push_back( token.substr(beg,end) );

    // ================================================
    // Issue 0118
    // Recursively find matching statements, including
    // partial matches.
    // ================================================
    vector<statement::base*> matches;
    if( stmt->get_parent() ) {
      statement::base* parent = stmt->get_parent();
      for( ;parent; parent = parent->get_parent() ) {
        if( ignore_contents_stmt(parent) )
          break;
        write_code_subsection_token(matches,parent,tokens,0);
        if( matches.size() ) {
          write_links(os,matches,token.c_str());
          return;
        }
      }
    }

    // ================================================
    // Check for links here by looking up entities
    // in the database. Begin by looking in the same
    // namespace.
    // ================================================
    statement::base::stmts_t token_stmts;
    m_db.get_stmt_no_pkgs(token,token_stmts);
    if( token_stmts.size() ) {
      // Filter the token statements by type.
      statement::base::stmts_t valid_token_stmts;
      statement::base::stmts_itr_t itr = token_stmts.begin();
      for(;itr!=token_stmts.end();++itr) {
	switch( (*itr)->get_type() ) {
	case statement::base::STMT_CLASS_BEGIN:
	case statement::base::STMT_STRUCT_BEGIN:
	case statement::base::STMT_UNION_BEGIN:
	case statement::base::STMT_ENUM:
	case statement::base::STMT_TYPEDEF_FUNCTION:
	case statement::base::STMT_TYPEDEF_VARIABLE:
	case statement::base::STMT_VARIABLE: // Issue 0091:
	case statement::base::STMT_VARIABLE_FUNCTION:
	case statement::base::STMT_FUNCTION:
	case statement::base::STMT_FUNCTION_OPERATOR:
	  valid_token_stmts.push_back(*itr);
	  break;
	default:
	  break;
	}
      }
      if( valid_token_stmts.size() == 1 ) {
	// If there is only one, use it.
	write_link(os,valid_token_stmts[0],token.c_str());
	return;
      }
      else if( valid_token_stmts.size() > 1 ) {
	// If there is more than one statement that matches,
	// try finding it in the same namespace as the stmt.
	statement::base* nsp = 0;
	statement::base* parent = stmt->get_parent();
	while(parent) {
	  if(parent->get_type() == statement::base::STMT_NAMESPACE_BEGIN) {
	    nsp = parent;
	    break;
	  }
	  parent = parent->get_parent();
	}
	statement::base::stmts_t valid_token_stmts_in_nsp;
	itr = valid_token_stmts.begin();
	for(;itr!=valid_token_stmts.end();++itr) {
	  statement::base* nsp1 = 0;
	  parent = *itr;
	  while(parent) {
	    if(parent->get_type() == statement::base::STMT_NAMESPACE_BEGIN) {
	      nsp1 = parent;
	      break;
	    }
	    parent = parent->get_parent();
	  }
	  if( nsp == nsp1 ) {
	    valid_token_stmts_in_nsp.push_back(*itr);
	    // Exit after one for efficiency.
	    break;
	  }
	}
	if( valid_token_stmts_in_nsp.size() ) {
	  write_link(os,valid_token_stmts_in_nsp[0],token.c_str());
	  return;
	}
	else {
	  write_link(os,valid_token_stmts[0],token.c_str());
	  return;
	}
      }
    }
    // No matching statements were found.
    os << format_string_for_html(token);
  }
  else {
    os << format_string_for_html(token);
  }
}
// ================================================================
// Write out token for code section if it matches an id in
// the specified scope.
// ================================================================
bool ccdoc::phase3::html::write_code_subsection_token(ostream& os,
						      statement::base::stmts_t& vec,
						      const string& token)
{
  statement::base::stmts_itr_t itr = vec.begin();
  for(;itr!=vec.end();++itr) {
    statement::base* stmt = *itr;
    if( token == stmt->get_id() ) {
      switch( stmt->get_type() ) {
      case statement::base::STMT_CLASS_BEGIN:
      case statement::base::STMT_STRUCT_BEGIN:
      case statement::base::STMT_UNION_BEGIN:
      case statement::base::STMT_ENUM:
      case statement::base::STMT_TYPEDEF_FUNCTION:
      case statement::base::STMT_TYPEDEF_VARIABLE:
      case statement::base::STMT_VARIABLE: // Issue 0091:
      case statement::base::STMT_VARIABLE_FUNCTION:
      case statement::base::STMT_FUNCTION:
      case statement::base::STMT_FUNCTION_OPERATOR:
	// Attach to the first one of the correct type.
	write_link(os,stmt,token.c_str());
	return true;
      default:
	break;
      }
    }
  }
  return false;
}
// ================================================================
// Issue 0118
// Match a partial specification.
// the specified scope.
// ================================================================
void ccdoc::phase3::html::write_code_subsection_token(statement::base::stmts_t& vec,
                                                      statement::base* stmt,
                                                      const vector<string>& tokens,
                                                      size_t idx) const
{
  vector<statement::base*> local_matches;
  stmt->get_children_by_id(local_matches,tokens[idx]);
  if( local_matches.size() ) {
    ++idx;
    vector<statement::base*>::iterator itr = local_matches.begin();
    for(;itr!=local_matches.end();++itr) {
      if( ignore_contents_stmt(*itr) )
        continue;
      if( idx < tokens.size() ) {
        write_code_subsection_token(vec,*itr,tokens,idx);
      }
      else {
        vec.push_back(*itr);
      }
    }
  }
}
// ================================================================
// Is this a keyword.
// ================================================================
bool ccdoc::phase3::html::is_cxx_keyword(const string& token)
{
  if( m_keywords.size() == 0 ) {
    // ================================================
    // Load the keywords:
    // Table 3 (2.11)
    // ================================================
    m_keywords.insert("asm");
    m_keywords.insert("auto");
    m_keywords.insert("bool");
    m_keywords.insert("break");
    m_keywords.insert("case");
    m_keywords.insert("catch");
    m_keywords.insert("char");
    m_keywords.insert("class");
    m_keywords.insert("const");
    m_keywords.insert("const_cast");
    m_keywords.insert("continue");
    m_keywords.insert("default");
    m_keywords.insert("delete");
    m_keywords.insert("do");
    m_keywords.insert("double");
    m_keywords.insert("dynamic_cast");
    m_keywords.insert("else");
    m_keywords.insert("enum");
    m_keywords.insert("explicit");
    m_keywords.insert("export");
    m_keywords.insert("extern");
    m_keywords.insert("false");
    m_keywords.insert("float");
    m_keywords.insert("for");
    m_keywords.insert("friend");
    m_keywords.insert("goto");
    m_keywords.insert("if");
    m_keywords.insert("inline");
    m_keywords.insert("int");
    m_keywords.insert("long");
    m_keywords.insert("mutable");
    m_keywords.insert("namespace");
    m_keywords.insert("new");
    m_keywords.insert("operator");
    m_keywords.insert("private");
    m_keywords.insert("protected");
    m_keywords.insert("public");
    m_keywords.insert("register");
    m_keywords.insert("reinterpret_cast");
    m_keywords.insert("return");
    m_keywords.insert("short");
    m_keywords.insert("signed");
    m_keywords.insert("sizeof");
    m_keywords.insert("static");
    m_keywords.insert("static_cast");
    m_keywords.insert("struct");
    m_keywords.insert("switch");
    m_keywords.insert("template");
    m_keywords.insert("this");
    m_keywords.insert("throw");
    m_keywords.insert("true");
    m_keywords.insert("try");
    m_keywords.insert("typedef");
    m_keywords.insert("typeid");
    m_keywords.insert("typename");
    m_keywords.insert("union");
    m_keywords.insert("unsigned");
    m_keywords.insert("using");
    m_keywords.insert("virtual");
    m_keywords.insert("void");
    m_keywords.insert("volatile");
    m_keywords.insert("wchar_t");
    m_keywords.insert("while");
    
    // ================================================
    // Add in the alternate representations for some
    // of the operators and punctuators.
    // ================================================
    m_keywords.insert("and");
    m_keywords.insert("and_eq");
    m_keywords.insert("bitand");
    m_keywords.insert("bitor");
    m_keywords.insert("compl");
    m_keywords.insert("not");
    m_keywords.insert("not_eq");
    m_keywords.insert("or");
    m_keywords.insert("or_eq");
    m_keywords.insert("xor");
    m_keywords.insert("xor_eq");
  }
  set<string>::const_iterator itr = m_keywords.find(token);
  return itr != m_keywords.end();
}
// ================================================================
// Compare statements for the make_contents() algorithm.
// ================================================================
bool compare_stmts_full_paths(const ccdoc::statement::base* a,
			      const ccdoc::statement::base* b)
{
  string a_id;
  string b_id;
  a->get_hier_id(a_id);
  b->get_hier_id(b_id);

  // Do a case insensitive compare to make things easier
  // to find.
  const char* p1 = a_id.c_str();
  const char* p2 = b_id.c_str();
  for(;*p1 && *p2;++p1,++p2) {
    if( *p1 != *p2 ) {
      int ch1 = *p1;
      int ch2 = *p2;
      // Convert to upper case.
      if( 'a' <= ch1 && ch1 <= 'z' )
	ch1 = ( ch1 - 'a' ) + 'A';
      if( 'a' <= ch2 && ch2 <= 'z' )
	ch2 = ( ch2 - 'a' ) + 'A';
      if( ch1 != ch2 )
	return ch1 < ch2;
    }
  }
  return *p1 < *p2;
}
// ================================================================
// Compare statements for the make_contents() algorithm.
// ================================================================
bool compare_stmts(const ccdoc::statement::base* a,
		   const ccdoc::statement::base* b)
{
  // Special cases:
  // Destructors always show up before first.
  if( a->get_type() == ccdoc::statement::base::STMT_METHOD_DESTRUCTOR ) {
    return true; // a<b
  }
  if( b->get_type() == ccdoc::statement::base::STMT_METHOD_DESTRUCTOR ) {
    return false; // b<a
  }
  if( a->get_type() == ccdoc::statement::base::STMT_METHOD_CONSTRUCTOR ) {
    // Constructors always match.
    if( b->get_type() == ccdoc::statement::base::STMT_METHOD_CONSTRUCTOR ) {
      return false;
    }
    // Constructors always show up first.
    return true; // a<b
  }
  if( b->get_type() == ccdoc::statement::base::STMT_METHOD_CONSTRUCTOR ) {
    // Constructors always show up first.
    return false; // b<a
  }

  string a_id;
  string b_id;
  if( a->get_type() == ccdoc::statement::base::STMT_PACKAGE ||
      a->get_type() == ccdoc::statement::base::STMT_NAMESPACE_BEGIN ) {
    if( ( b->get_type() == ccdoc::statement::base::STMT_PACKAGE ||
	  b->get_type() == ccdoc::statement::base::STMT_NAMESPACE_BEGIN ) ) {
      // a and b are both packages or namespaces,
      // compare them hierarchically
      a->get_hier_id(a_id);
      b->get_hier_id(b_id);
    }
    else {
      // a is a package or namespace and b isn't
      // a is less than b.
      // Always put packages and namespaces first.
      return true;
    }
  }
  else if( ( b->get_type() == ccdoc::statement::base::STMT_PACKAGE ||
	     b->get_type() == ccdoc::statement::base::STMT_NAMESPACE_BEGIN ) ) {
    // b is a package or namespace and a isn't
    // b is less than a.
    return false;
  }
  else {
    a_id = a->get_id();
    b_id = b->get_id();
  }

  // Do a case insensitive compare to make things easier
  // to find.
  const char* p1 = a_id.c_str();
  const char* p2 = b_id.c_str();
  for(;*p1 && *p2;++p1,++p2) {
    if( *p1 != *p2 ) {
      int ch1 = *p1;
      int ch2 = *p2;
      // Convert to upper case.
      if( 'a' <= ch1 && ch1 <= 'z' )
	ch1 = ( ch1 - 'a' ) + 'A';
      if( 'a' <= ch2 && ch2 <= 'z' )
	ch2 = ( ch2 - 'a' ) + 'A';
      if( ch1 != ch2 )
	return ch1 < ch2;
    }
  }
  return *p1 < *p2;
}
// ================================================================
// Make contents.
// This is used to get list of entities for contents and sections.
// ================================================================
void ccdoc::phase3::html::make_contents(statement::base* stmt,
					statement::base::stmts_t& contents,
					bool sort_flag)
{
  // ================================================
  // Filter out the children that we don't care
  // about on this run.
  // ================================================
  statement::base::stmts_t& children = stmt->get_children();
  if( children.size() ) {
    statement::base::stmts_itr_t itr = children.begin();
    for(;itr!=children.end();++itr) {
      statement::base* child = *itr;
      if(ignore_contents_stmt(child))
	continue;
      
      // ================================================
      // Special case handling for namespaces and packages,
      // we provide nested information for them.
      // ================================================
      if( child->get_type() == statement::base::STMT_PACKAGE ) {
	statement::base::stmts_t vec1;
	make_pkg_index_children(child,vec1);
	statement::base::stmts_itr_t itr1 = vec1.begin();
	for(;itr1!=vec1.end();++itr1) {
	  contents.push_back(*itr1);
	}
	// Additional special case handling for packages.
	// Look for child namespaces that are stored as tokens.
	const statement::base::cstrs_t& nsps = child->get_tokens();
	statement::base::cstrs_citr_t nsps_itr = nsps.begin();
	for(;nsps.begin()!=nsps.end();++nsps_itr) {
	  string token = *nsps_itr;
	  statement::base::stmts_t nsp_stmts;
	  m_db.get_stmt_no_pkgs(token,nsp_stmts);

	  // Filter out the statements that don't have packages as
	  // parents. Add the top namespaces to the contents list.
	  statement::base::stmts_itr_t nsp_stmts_itr = nsp_stmts.begin();
	  for(;nsp_stmts_itr!=nsp_stmts.end();++nsp_stmts_itr) {
	    statement::base* nsp = *nsp_stmts_itr;
	    if( nsp->get_parent()->get_type() == statement::base::STMT_PACKAGE ) {
	      vec1.clear();
	      make_pkg_index_children(nsp,vec1);
	      for(itr1 = vec1.begin();itr1!=vec1.end();++itr1) {
		contents.push_back(*itr1);
	      }
	    }
	  }
	}
      }
      else if( child->get_type() == statement::base::STMT_NAMESPACE_BEGIN ) {
	statement::base::stmts_t vec1;
	make_pkg_index_children(child,vec1);
	statement::base::stmts_itr_t itr1 = vec1.begin();
	for(;itr1!=vec1.end();++itr1) {
	  contents.push_back(*itr1);
	}
      }
      else {
	contents.push_back(child);
      }
    }
  }

  // ================================================
  // Load parent class contents.
  // ================================================
  if( stmt->get_type() == statement::base::STMT_CLASS_BEGIN ||
      stmt->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
    make_class_contents(stmt,contents);
  }

  // ================================================
  // Sort the remaining children by name.
  // ================================================
  if( sort_flag ) {
    sort(contents.begin(),contents.end(),compare_stmts);
  }
}
// ================================================================
// Make class contents.
// This is used to get list of entities for contents and sections.
// Classes are a special case because we also get the inherited
// methods.
// ================================================================
void ccdoc::phase3::html::make_class_contents(statement::base* stmt,
					      statement::base::stmts_t& contents)
{
  if( m_sw.rptim() ) {
    // ================================================
    // Get the classes that this class derives from.
    // ================================================
    statement::base::stmts_t classes;
    load_inheritance_classes(stmt,classes);
    if( classes.size() == 0 )
      return;
    
    // ================================================
    // Populate the used_method_ids with the contents
    // of the original class.
    // ================================================
    statement::base::stmts_t used_classes;
    set<string> used_method_ids;
    statement::base::stmts_itr_t contents_itr = contents.begin();
    for(;contents_itr!=contents.end();++contents_itr) {
      statement::base* rec = *contents_itr;
      if( rec->get_type() == statement::base::STMT_METHOD ) {
	string key = rec->get_id();
	used_method_ids.insert( key );
      }
    }
    if( classes.size() )
      make_class_contents(contents,classes,used_classes,used_method_ids);
  }
}
// ================================================================
// Make class contents.
// This is used to get list of entities for contents and sections.
// Classes are a special case because we also get the inherited
// methods.
// ================================================================
void ccdoc::phase3::html::make_class_contents(statement::base::stmts_t& contents,
					      statement::base::stmts_t& classes,
					      statement::base::stmts_t& used_classes,
					      set<string>& used_method_ids)
{
  // ================================================
  // Iterate over each of the parent classes to
  // capture their methods. Ignore methods that
  // have already been defined.
  // ================================================
  statement::base::stmts_itr_t classes_itr = classes.begin();
  for(;classes_itr!=classes.end();++classes_itr) {
    statement::base* class_rec = *classes_itr;

    // This is an O(N^2) test to verify that we don't have
    // an inheritance loop. The performance s/b ok because
    // the inheritance depth s/b small (<10).
    statement::base::stmts_itr_t used_classes_itr = used_classes.begin();
    for(;used_classes_itr!=used_classes.end();++used_classes_itr) {
      if( *used_classes_itr == class_rec ) {
	break;
      }
    }
    if( used_classes_itr != used_classes.end() )
      continue;
    used_classes.push_back(class_rec);

    // Add methods to the contents.
    statement::base::stmts_t& children = class_rec->get_children();
    statement::base::stmts_itr_t children_itr = children.begin();
    for(;children_itr!=children.end();++children_itr) {
      statement::base* rec = *children_itr;
      if( rec->get_type() == statement::base::STMT_METHOD ) {
	if( !ignore_contents_stmt(rec) ) { // Issue 0040
	  string key = rec->get_id();
	  if( used_method_ids.find( key ) == used_method_ids.end() ) {
	    contents.push_back( rec );
	  }
	}
      }
    }

    // Walk back through and update the use_method_ids.
    // This is not done as the methods are traversed
    // because we want to capture functions with the
    // same ids but different signatures.
    children_itr = children.begin();
    for(;children_itr!=children.end();++children_itr) {
      statement::base* rec = *children_itr;
      if( rec->get_type() == statement::base::STMT_METHOD ) {
	if( !ignore_contents_stmt(rec) ) { // Issue 0040
	  string key = rec->get_id();
	  if( used_method_ids.find( key ) == used_method_ids.end() ) {
	    used_method_ids.insert( key );
	  }
	}
      }
    }

    // Load the parents of this class recursively.
    statement::base::stmts_t parent_classes;
    load_inheritance_classes(class_rec,parent_classes);
    if( parent_classes.size() )
      make_class_contents(contents,parent_classes,used_classes,used_method_ids);
  }
}
// ================================================================
// Load the inheritance classes.
// Load the classes that this class derives from.
// ================================================================
void ccdoc::phase3::html::load_inheritance_classes(statement::base* stmt,
						   statement::base::stmts_t& classes)
{
  const statement::base::cstrs_t& tokens = stmt->get_tokens();
  statement::base::cstrs_citr_t tokens_itr = tokens.begin();
  for(;tokens_itr!=tokens.end();++tokens_itr) {
    string token = *tokens_itr;
    if( token == ":" ) {
      // This is where the interesting stuff starts.
      tokens_itr++;
      break;
    }
  }
  if( tokens_itr == tokens.end() ) {
    // No tokens.
    // This class does not derive from any other classes.
    return;
  }

  bool found_id = false;
  int depth = 0;
  string id;
  for(;tokens_itr!=tokens.end();++tokens_itr) {
    string token = *tokens_itr;
    if( !found_id ) {
      if( token == "virtual" ||
	  token == "public" ||
	  token == "protected" ||
	  token == "private" ) {
	continue;
      }
      found_id = true;
    }
    if( found_id ) {
      if( token == "<" ) {
	// This handles cases like:
	//   class A : public foo<My1,My2> ;
	//   class A : public foo<My1,My2<XX> > ;
	depth++;
      }
      else if( token == ">" ) {
	depth--;
      }
      else if( depth == 0 ) {
	if( token == "," ) {
	  // Output the extends information.
	  statement::base::stmts_t matches;
	  if( 1 == load_extend_classes( id, matches, stmt ) ) {
	    classes.push_back( matches[0] );
	  }
	  else {
	    // TODO: Warn there there were multiple classes.
	  }
	  id = "";
	  found_id = false;
	  depth = 0;
	  continue;
	}
      }
      id += token;
    }
  }
  statement::base::stmts_t matches;
  if( 1 == load_extend_classes( id, matches, stmt ) ) {
    classes.push_back( matches[0] );
  }
  else {
    // TODO: Warn there there were multiple classes.
  }
}
// ================================================================
// Get the class record.
// ================================================================
unsigned ccdoc::phase3::html::load_extend_classes(const string& id,
						  statement::base::stmts_t& out,
						  statement::base* stmt)
{
  statement::base::stmts_t id_stmts;
  m_db.get_stmt_no_pkgs(id,id_stmts);
  if( id_stmts.size() == 0 ) {
    // Issue 0048
    string scoped_id;
    statement::base::stmts_t parents;
    stmt->get_parents_no_pkgs(parents);
    get_fully_scoped_name(scoped_id,id,parents);
    m_db.get_stmt_no_pkgs(scoped_id,id_stmts);
    if( id_stmts.size() == 0 )
      return 0;
  }
  if( id_stmts.size() > 0 ) {
    // Filter out the statements that are not classes or structs.
    statement::base::stmts_itr_t sitr = id_stmts.begin();
    for(;sitr!=id_stmts.end();++sitr) {
      statement::base* id_stmt = *sitr;
      if( id_stmt->get_type() == statement::base::STMT_CLASS_BEGIN ||
	  id_stmt->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
	out.push_back(id_stmt);
      }
    }
  }
  return out.size();
}
// ================================================================
// Get the fully scoped name.
// ================================================================
void ccdoc::phase3::html::get_fully_scoped_name(string& out_id,
						const string& in_id,
						statement::base::stmts_t parents) const
{
  statement::base::strs_t ids;
  m_db.parse_path(in_id,ids);
  if( parents.size() ) {
    bool doit = true;
    size_t last1 = 0;
    if( ids.size() > 1 ) {
      size_t sz1 = ids.size() - 1;
      size_t last2 = parents.size() - 1;
      last1 = sz1 - 1;
      for(;last1!=0 && last2!=0;--last1,--last2) {
	string nm = parents[last2]->get_id();
	if( nm != ids[last1] ) {
	  doit = false;
	  break;
	}
      }
      if( doit ) {
	// The match was found.
	last1 = sz1 - 1;
      }
    }
    if( doit ) {
      out_id = "";
      bool first = true;
      statement::base::stmts_itr_t itr = parents.begin();
      for(;itr!=parents.end();++itr) {
	if( !first )
	  out_id += "::";
	out_id += (*itr)->get_id();
	first = false;
      }
      if( !first )
	out_id += "::";
      out_id += ids[last1];
      return;
    }
  }
  out_id = in_id;
}
// ================================================================
// Ignore a statement?
// ================================================================
bool ccdoc::phase3::html::ignore_contents_stmt(statement::base* stmt) const
{
  switch( stmt->get_type() ) {
  case statement::base::STMT_FRIEND_CLASS:
  case statement::base::STMT_FRIEND_FUNCTION:
  case statement::base::STMT_CLASS_END:
  case statement::base::STMT_COMMENT_PKGDOC:
  case statement::base::STMT_COMMENT_PKGDOC_URL:
  case statement::base::STMT_COMMENT_PREFIX:
  case statement::base::STMT_COMMENT_SUFFIX:
  case statement::base::STMT_IGNORE:
  case statement::base::STMT_NAMESPACE_END:
  case statement::base::STMT_STRUCT_END:
  case statement::base::STMT_UNION_END:
  case statement::base::STMT_MACROINST_FUNCTION:
  case statement::base::STMT_MACROINST_VARIABLE:
    // Ignore some of the statements all of the time.
    return true;
  case statement::base::STMT_MACRODEF_0_0:
  case statement::base::STMT_MACRODEF_0_1:
  case statement::base::STMT_MACRODEF_0_N:
  case statement::base::STMT_MACRODEF_N_N:
    return !m_sw.rptmac();
  case statement::base::STMT_TYPEDEF_FUNCTION:
  case statement::base::STMT_TYPEDEF_VARIABLE:
    if( !m_sw.rpttyp() )
      return true;
    // Don't return false until we check the
    // accessibility.
    break;
  case statement::base::STMT_UNION_BEGIN:
    if( !m_sw.rptun() )
      return true;
    // Don't return false until we check the
    // accessibility.
    break;
#if 0
    // Issue 0073
    // This section of code was commented out because
    // ccdoc was reporting private methods when -norptpri was
    // specified.
  case statement::base::STMT_METHOD_CONSTRUCTOR:
  case statement::base::STMT_METHOD_DESTRUCTOR:
    // Ccdoc never ignores constructors, destructors and
    // assignment operators.
    return false;
  case statement::base::STMT_FUNCTION_OPERATOR:
  case statement::base::STMT_METHOD_OPERATOR:
    // Ccdoc never ignores assignment operators.
    if( stmt->get_id() ) {
      string id = stmt->get_id();
      if( id == "operator =" )
	return false;
    }
    break;
#endif
  case statement::base::STMT_PACKAGE:
    if( stmt->get_parent() == m_db.root() ) {
      string id = stmt->get_id();
      if( id == "@null" || id == "[NULL]" ) {
	// Ignore top level packages that have the names
	// @null and [NULL].
	return true;
      }
    }
  default:
    break;
  }

  // Check the accessibility.
  switch( stmt->get_access() ) {
  case statement::base::STMT_PRIVATE:
    return !m_sw.rptpri();
  case statement::base::STMT_PROTECTED:
    return !m_sw.rptpro();
  case statement::base::STMT_PUBLIC:
    return !m_sw.rptpub();
  default:
    break;
  }
  return false;
}
// ================================================================
// Write the package index html.
// ================================================================
void ccdoc::phase3::html::make_pkg_index_children(statement::base* stmt,
						  statement::base::stmts_t& outvec)
{
  if( stmt ) {
    if( stmt->get_type() != statement::base::STMT_NAMESPACE_BEGIN &&
	stmt->get_type() != statement::base::STMT_PACKAGE ) {
      return;
    }
    outvec.push_back(stmt);
    statement::base::stmts_t& vec = stmt->get_children();
    statement::base::stmts_itr_t itr = vec.begin();
    for(;itr!=vec.end();++itr) {
      make_pkg_index_children(*itr,outvec);
    }
  }
}
// ================================================================
// Get the tag value for a statement.
// ================================================================
void ccdoc::phase3::html::make_tag_id(const statement::base* stmt,
				      string& id) const
{
  if(stmt) {
    id = stmt->get_terse_type_name();
    id += "-";
    id += format_name(stmt->get_id());
    if( stmt->get_type() == statement::base::STMT_FUNCTION ||
	stmt->get_type() == statement::base::STMT_FUNCTION_OPERATOR ||
	stmt->get_type() == statement::base::STMT_METHOD_CONSTRUCTOR ||
	stmt->get_type() == statement::base::STMT_METHOD_DESTRUCTOR ||
	stmt->get_type() == statement::base::STMT_METHOD_OPERATOR ||
	stmt->get_type() == statement::base::STMT_METHOD ) {
      // Append uniquifying tokens:
      const statement::base::cstrs_t& vec = stmt->get_tokens();
      statement::base::cstrs_citr_t itr = vec.begin();
      for(;itr!=vec.end();++itr) {
	id += "-";
	id += format_name( *itr );
      }
    }
  }
}
// ================================================================
// Get the parent statement that defines a file entity.
// ================================================================
const ccdoc::statement::base*
ccdoc::phase3::html::get_file_stmt(const statement::base* stmt) const
{
  // Other statements, such as functions are only in separate
  // files if they are at the top level. Otherwise they are
  // subordinated to a tag within a file.
  for(;stmt;stmt=stmt->get_parent()) {
    if( stmt->get_parent() == 0 )
      return stmt;
    // The only statement that does not have its own file is a
    // statement that is embedded in a class unless it is a subclass.
    if( stmt->get_type() == statement::base::STMT_CLASS_BEGIN ||
	stmt->get_type() == statement::base::STMT_STRUCT_BEGIN ||
	stmt->get_type() == statement::base::STMT_UNION_BEGIN ) {
      // Classes (even subclasses) always have their own files.
      return stmt;
    }
    if( stmt->get_parent()->get_type() != statement::base::STMT_CLASS_BEGIN &&
	stmt->get_parent()->get_type() != statement::base::STMT_STRUCT_BEGIN &&
	stmt->get_parent()->get_type() != statement::base::STMT_UNION_BEGIN ) {
      // Other statements that are not embedded in classes
      // always have their own files.
      return stmt;
    }
  }
  return stmt;
}
// ================================================================
// Make file URL, strip off the leading directories.
// ================================================================
void ccdoc::phase3::html::make_file_url(string& url,string& fn) const
{
  // Strip off the leading directories to
  // make the relative reference work.
  string::iterator itr = fn.begin();
  string::iterator relpath_itr = fn.begin();
  for(;itr!=fn.end();++itr) {
    if( '/' == *itr ) {
      relpath_itr = itr;
      ++relpath_itr; // Issue 0001 FIX
    }
  }
  url = "";
  for(;relpath_itr!=fn.end();++relpath_itr) {
    url += *relpath_itr;
  }
}
// ================================================================
// Make file URL
// ================================================================
bool ccdoc::phase3::html::make_file_url(string& url,
					const statement::base* stmt)
{
  if( stmt ) {
    const statement::base* file_stmt = get_file_stmt(stmt);
    string fn;
    make_unique_file_name(fn,file_stmt);
    make_file_url(url,fn);

    // If the parent is the file statement, a tag needs to be
    // generated, return true.
    return file_stmt == stmt->get_parent();
  }
  return false;
}
// ================================================================
// Make a unique file name that consistent between runs.
// ================================================================
void ccdoc::phase3::html::make_unique_file_name(string& fn,
						const statement::base* stmt)
{
  if(stmt) {
    if( stmt->get_parent() == 0 ) {
      // This is the top node.
      // Perform special processing here to support the -rootfile
      // switch.
      if( m_sw.rootfile().size() ) {
	fn = m_sw.rootfile();
	return;
      }
    }
    bool append_tokens = false;
    if( stmt->get_type() == statement::base::STMT_FUNCTION ||
	stmt->get_type() == statement::base::STMT_FUNCTION_OPERATOR ||
	stmt->get_type() == statement::base::STMT_METHOD_CONSTRUCTOR ||
	stmt->get_type() == statement::base::STMT_METHOD_DESTRUCTOR ||
	stmt->get_type() == statement::base::STMT_METHOD_OPERATOR ||
	stmt->get_type() == statement::base::STMT_METHOD ) {
      append_tokens = true;
    }
    fn = m_sw.html() + "ccdoc";

    // ================================================
    // Get the hierarchical names.
    // ================================================
    statement::base::stmts_t parents;
    stmt->get_parents(parents);
    if(parents.size()) {
      statement::base::stmts_itr_t itr = parents.begin();

      // Handle the first argument as a special case.
      // We don't want the '@' sign in the file name.
      fn += ".";
      if( m_sw.default_root() == (*itr)->get_id() ) {
	fn += "root"; // strip of the leading @ for the file name.
      }
      else {
	fn += format_name( (*itr)->get_id() );
      }
      ++itr;
      for(;itr!=parents.end();++itr) {
	fn += ".";
	fn += format_name( (*itr)->get_id() );
      }
      // Tack on the statement id.
      fn += ".";
      fn += format_name( stmt->get_id() );
    }
    else {
      // This is the root package statement.
      // Handle the first argument as a special case.
      // We don't want the '@' sign in the file name.
      fn += ".";
      if( m_sw.default_root() == stmt->get_id() ) {
	fn += "root"; // strip of the leading @ for the file name.
      }
      else {
	fn += format_name( stmt->get_id() );
      }
    }
    string fn1 = fn;

    // ================================================
    // Append the tokens for uniqueness for some types.
    // ================================================
    if( append_tokens ) {
      const statement::base::cstrs_t& vec = stmt->get_tokens();
      statement::base::cstrs_citr_t itr = vec.begin();
      for(;itr!=vec.end();++itr) {
	fn += ".";
	fn += format_name( *itr );
      }
    }

    // ================================================
    // Append the type and html suffix.
    // ================================================
    fn += ".";
    fn += stmt->get_terse_type_name();
    fn += ".html";
    if( m_sw.maxpathlen() && fn.size() > m_sw.maxpathlen() ) {
      // Generate a checksum if the file name is too big.
      checksum c;
      c.set(fn.c_str());

      char nbuf[32];
      sprintf(nbuf,"%08x",c.get());

      fn = fn1;
      fn += ".checksum.";
      fn += nbuf;
      fn += ".";
      fn += stmt->get_terse_type_name();
      fn += ".html";
    }
  }
}
// ================================================================
// Format the characters in an id so they are suitable for a file
// name.
// ================================================================
const char* ccdoc::phase3::html::format_name(const char* token) const
{
  static char out[65536];
  char nbuf[16];
  const char* src = token;
  char* dst = out;
  char tmp[32];
  for(;*src;++src) {
    if( *src <= 32 || *src >= 127 ) {
      unsigned val = static_cast<unsigned>(*src);
      sprintf(nbuf,"-%02x",val);
      for(const char* p=nbuf;*p;++p)
	*dst++ = *p;
    }
    else {
      if( '{' == *src ) {
	// This only occurs for enum types and is
	// not necessary for uniquifying the name.
	*dst = 0;
	return out;
      }
      switch( *src ) {
      case '(':
      case ')':
      case '[':
      case ']':
      case '<':
      case '>':
      case ';':
      case ':':
      case '~':
      case '?':
      case '=':
      case ',':
      case '@':
      case '&':
      case '*':
      case '"':
      case '\'':
      case '/': // Issue 0018: filter embedded directory separators
      case '|': // Issue 0112: handle operator |
      case '\\':
	{
	  unsigned val = static_cast<unsigned>(*src);
	  sprintf(nbuf,"-%02x",val);
	  for(const char* p=nbuf;*p;++p)
	    *dst++ = *p;
	  break;
	}
      default:
	*dst++ = *src;
	break;
      }
    }
  }
  *dst = 0;
  return out;
}
// ================================================================
// format string (format for html)
// ================================================================
const char* ccdoc::phase3::html::format_string_for_html(const char* pstr) const
{
  static char id[65536];
  char* p = id;
  if(pstr) {
    for(;*pstr;++pstr) {
      if( *pstr == '<' ) {
	*p++ = '&';
	*p++ = 'l';
	*p++ = 't';
	*p++ = ';';
      }
      else if( *pstr == '>' ) {
	*p++ = '&';
	*p++ = 'g';
	*p++ = 't';
	*p++ = ';';
      }
      else if( *pstr == '&' ) {
	*p++ = '&';
	*p++ = 'a';
	*p++ = 'm';
	*p++ = 'p';
	*p++ = ';';
      }
      else {
	*p++ = *pstr;
      }
    }
  }
  *p = 0;
  return id;
}
// ================================================================
// format string (format for html)
// ================================================================
const char*
ccdoc::phase3::html::format_string_for_html(const string& str) const
{
  return format_string_for_html(str.c_str());
}
// ================================================================
// write string (format for html)
// ================================================================
void ccdoc::phase3::html::write_html_formatted_string(ostream& os,
						      const char* str) const
{
  os << format_string_for_html(str);
}
// ================================================================
// write string (format for html)
// ================================================================
void ccdoc::phase3::html::write_html_formatted_string(ostream& os,
						      const string& str) const
{
  write_html_formatted_string(os,str.c_str());
}
// ================================================================
// write common header info
// ================================================================
void ccdoc::phase3::html::write_common_header_info(ostream& os,
						   const string& fn,
						   const char* title)
{
  os << "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
     << "<!--NewPage-->\n"
     << "<html>\n"
     << "<head>\n"
    ;
  if(m_meta.size()) {
    // Issue 0003: Write out the custom meta variables.
    os << m_meta << endl;
  }
  else {
    // Issue 0059:
    // Write out the content type to satisfy the HTML verifier
    // at http://validator.w3.org/.
    // Issue 0076:
    // Don't write out standard HTML meta elements if the -meta
    // switch was specified.
    os << "<meta http-equiv=\"Content-Type\" ";
    os << "content=\"text/html; ";
    os << "charset=" << m_sw.rptctcs() << "\">\n";
  }
  write_meta(os,"ccdoc_copyright"     ,"(C) Joe Linoff 1998-2001");
  write_meta(os,"ccdoc_author"        ,"Joe Linoff");
  write_meta(os,"ccdoc_version"       ,m_sw.version().c_str());
  write_meta(os,"ccdoc_file"          ,fn.c_str());
  write_meta(os,"ccdoc_creation_date" ,date_time());
  write_meta(os,"keywords"            ,"ccdoc, source code, documentation");
  os << "<title>" << title << "</title>\n";
  os << "</head>\n" << "\n";

  // ================================================
  // Write out the <body> prefix, including the
  // optional prefix that was supplied by the
  // user.
  // ================================================
  os << "<body";
  if(m_sw.bgcolor().size()) {
    os << " bgcolor=" << m_sw.bgcolor().c_str();
  }
  if(m_sw.fgtextcolor().size()) {
    os << " text=" << m_sw.fgtextcolor().c_str();
  }
  if(m_sw.fglinkcolor().size()) {
    os << " link=" << m_sw.fglinkcolor().c_str();
  }
  if(m_sw.fgvlinkcolor().size()) {
    os << " vlink=" << m_sw.fgvlinkcolor().c_str();
  }
  os << ">\n";
  os << "<a name=ccdoc_top></a>\n"; // Issue 0002
  if(m_header.size()) {
    os << m_header << endl;
  }
}
// ================================================================
// write common header info
// ================================================================
void ccdoc::phase3::html::write_common_header_info(ostream& os,
						   const string& fn,
						   statement::base* stmt)
{
  string title = "ccdoc ";
  if( stmt ) {
    char nbuf[32];
    sprintf(nbuf,"%d",stmt->get_lineno());
    title += format_string_for_html(stmt->get_id());
    title += " ";
    title += stmt->get_file();
    title += ":";
    title += nbuf; // Issue 0010
    title += ":";
    title += stmt->get_type_name1();
  }
  else {
    title += "<null>";
  }
  write_common_header_info(os,fn,title.c_str());

  // ================================================
  // Write out the package/namespace path:
  // ================================================
  if( stmt ) {
    os << "<table border=0 width=\"100%\"><tr><td align=left>\n";
    statement::base::stmts_t parents;
    stmt->get_parents(parents);
    if( parents.size() ) {
      statement::base::stmts_itr_t itr = parents.begin();
      statement::base* parent = *itr;

      // If the user specfied -rooturl on the command line,
      // output it.
      if( m_sw.rooturl().size() ) {
	os << "<a href=\"" << m_sw.rooturl() << "\" target=_top>Home</a>\n";
	os << " :: ";
      }

      // Now write out the path.
      write_link(os,parent);
      os << "\n";
      itr++;
      for(;itr!=parents.end();++itr) {
	parent = *itr;
	os << " :: ";
	write_link(os,parent);
	os << "\n";
      }
    }
    else {
      // This is the root.
      // Write out the Home link.
      if( m_sw.rooturl().size() ) {
	os << "<a href=\"" << m_sw.rooturl() << "\" target=_top>Home</a>\n";
      }
    }
    os << "</td><td align=right>";
    string url;
    string link_name = m_sw.html() + "ccdoc.class_summary.html";
    make_file_url(url,link_name);
    os << "<a href=\"" << url << "\">"
       << "classes</a></td></tr></table>\n";
  }
}
// ================================================================
// Write the entity header.
// ================================================================
void ccdoc::phase3::html::write_entity_header_info(ostream& os,
						   statement::base* stmt)
{
  write_section_header(os,stmt,false);
}
// ================================================================
// Make the absolute path.
// ================================================================
void ccdoc::phase3::html::make_abs_path(statement::base* stmt,
					string& link) const
{
  int j = 0;
  statement::base::stmts_t parents;
  stmt->get_parents(parents);
  if( parents.size() ) {
    statement::base::stmts_itr_t itr = parents.begin();
    ++itr; // skip the root
    for(;itr!=parents.end();++itr,++j) {
      if( j )
	link += "::";
      link += (*itr)->get_id();
    }
  }
  if( j )
    link += "::";
  link += stmt->get_id();
}
// ================================================================
// Make relative path.
// ================================================================
void ccdoc::phase3::html::make_rel_path(statement::base* parent,
					statement::base* stmt,
					string& link) const
{
  int j = 0;
  statement::base::stmts_t parents;
  stmt->get_parents(parents);
  if( parents.size() ) {
    statement::base::stmts_itr_t itr = parents.begin();
    ++itr; // skip the root
    // Skip until we find the relative top.
    for(;itr!=parents.end();++itr) {
      if( *itr == parent ) {
	// This is the parent,
	// point to the child.
	++itr;
	break;
      }
    }
    for(;itr!=parents.end();++itr,++j) {
      if( j )
	link += "::";
      link += (*itr)->get_id();
    }
  }
  if( j )
    link += "::";
  link += stmt->get_id();
}
// ================================================================
// Find and write out a links.
// ================================================================
unsigned ccdoc::phase3::html::find_and_write_links(ostream& os,
						   const char* in_link,
						   const char* in_link_id,
						   statement::base* scope)
{
  // Nothing is written if there is no valid link.
  unsigned count = 0;
  if( in_link && in_link_id ) {
    string link = in_link;
    // Check for a leading # as in:
    //   {@link #Fct}
    if( in_link[0] == '#' ) {
      // Issue 0092:
      if( !scope )
	return count;
      scope->get_hier_id_no_pkgs(link);
      if( link.size() )
	link += "::";
      link += &in_link[1]; // strip off the leading '#'
    }
    statement::base::stmts_t stmts;
    m_db.get_stmt_no_pkgs(link,stmts);
    if( stmts.size() ) {
      statement::base::stmts_t output_stmts;
      statement::base::stmts_itr_t itr1 = stmts.begin();
      for(;itr1!=stmts.end();++itr1) {
	statement::base* stmt = *itr1;
	if( stmt->get_type() != statement::base::STMT_CLASS_END &&
	    stmt->get_type() != statement::base::STMT_NAMESPACE_END &&
	    stmt->get_type() != statement::base::STMT_STRUCT_END &&
	    stmt->get_type() != statement::base::STMT_UNION_END ) {
          if( ignore_contents_stmt(stmt) == false )
            output_stmts.push_back(stmt);
	}
      }
      count = output_stmts.size();
      write_links(os,output_stmts,in_link_id);
    }
  }
  return count;
}
// ================================================================
// Write out multiple links to the same reference in a separate
// file.
// ================================================================
void ccdoc::phase3::html::write_links(ostream& os,
				      const statement::base::stmts_t& stmts,
				      const char* link_name)
{
  if( stmts.size() == 0 || link_name == 0 )
    return;
  if( stmts.size() == 1 ) {
    write_link(os,stmts[0],link_name);
    return;
  }

  // Issue 0111
  // Issue 0083
  // First generate the new HTML file name.
  // Generate a unique file name for the multiple
  // links file so that they can be re-used.
  string fn = m_sw.html() + "ccdoc";
  const statement::base* stmt = stmts[0];
  statement::base::stmts_t parents;
  stmt->get_parents(parents);
  if(parents.size()) {
    statement::base::stmts_itr_t itr = parents.begin();
    
    // Handle the first argument as a special case.
    // We don't want the '@' sign in the file name.
    fn += ".";
    if( m_sw.default_root() == (*itr)->get_id() ) {
      fn += "root"; // strip of the leading @ for the file name.
    }
    else {
      fn += format_name( (*itr)->get_id() );
    }
    ++itr;
    for(;itr!=parents.end();++itr) {
      fn += ".";
      fn += format_name( (*itr)->get_id() );
    }
    // Tack on the statement id.
    fn += ".";
    fn += format_name( stmt->get_id() );
  }
  else {
    // This is the root package statement.
    // Handle the first argument as a special case.
    // We don't want the '@' sign in the file name.
    fn += ".";
    if( m_sw.default_root() == stmt->get_id() ) {
      fn += "root"; // strip of the leading @ for the file name.
    }
    else {
      fn += format_name( stmt->get_id() );
    }
  }
  fn += ".xrf.html";

  // Make the url.
  string url;
  make_file_url(url,fn);

  // Now write out the link to this file.
  // Use italics as a visual cue to indicate that there are multiple
  // links.
  os << "<a href=\"" << url << "\">" << link_name << "</a>";

  // See whether the file exists.
  bool exists = true;
  {
    ifstream is(fn.c_str());
    if(!is) {
      exists = false;
    }
  }

  // If the link file doesn't exist, create it.
  if( !exists ) {
    ofstream os1(fn.c_str());
    if(!os) {
      throw ccdoc::exceptions::unwriteable_output_file(__FILE__,
                                                       __LINE__,
                                                       fn.c_str());
    }
    string title;
    title = "ccdoc xrf links for ";
    title += link_name;
    write_common_header_info(os1,fn,title.c_str());
    
    // Populate the file.
    os1 << "<center>\n";
    os1 << "<h3>Multiply defined links for " << link_name << "</h3>\n";
    os1 << "<table border=1 cellspacing=1 cellpadding=2>";
    os1 << "<tr><th>Link</th><th>Name</th><th>Type</th><th>Source</th><th>Short Description</th></tr>\n";
    statement::base::stmts_citr_t itr = stmts.begin();
    for(;itr!=stmts.end();++itr) {
      statement::base* stmt = *itr;
      
      // Link
      os1 << "<tr><td>";
      write_link(os1,stmt,link_name);
      
      // Name
      os1 << "</td><td>";
      string id;
      stmt->get_hier_id_no_pkgs(id);
      os1 << id;
      
      // Type
      os1 << "</td><td>";
      os1 << stmt->get_type_name1();
      
      // Path
      os1 << "</td><td>";
      string path;
      write_ccdoc_src_info(path,stmt);
      if( path.size() )
        os1 << path;
      
      // Short description.
      os1 << "</td><td>\n";
      write_short_desc(os1,stmt);
      
      os1 << "</td></tr>\n";
    }
    os1 << "</table>\n";
    os1 << "</center>\n";
    
    write_common_trailer_info(os1);
  }
}
// ================================================================
// Write out a link.
// ================================================================
void ccdoc::phase3::html::write_link(ostream& os,
				     const statement::base* stmt,
				     const char* link_name)
{
  if( stmt ) {
    // There are three cases that have to be handled here:
    //
    //   Case 1: The statement is an object in a standalone file.
    //   Case 2: The statement is a tag in an existing file.
    //   Case 3: A package with a user defined URL.
    //
    // Case 2 occurs when the stmt is not a class and the parent is
    // not a namespace or a package.
    string url;
    string tag;
    if( statement::base::STMT_PACKAGE == stmt->get_type() &&
	stmt->get_comment() ) {
      // Issue 0025
      // The user specified a custom URL. Use the package name
      // along with the users URL.
      // The URL is obtained from the associated comment.
      statement::comment doc(stmt->get_comment());
      url = doc.get_pkgdoc_url();
    }
    if( url.size() == 0 ) {
      if ( make_file_url(url,stmt) ) {
	string id;
	make_tag_id(stmt,id);
	tag = "#" + id;
      }
    }      
    // Write out the link.
    os << "<a href=\"" << url << tag << "\">";
    if( link_name ) {
      write_html_formatted_string(os,link_name);
    }
    else {
      write_html_formatted_string(os,stmt->get_id());
    }
    os << "</a>";
  }
}
// ================================================================
// write meta info
// ================================================================
void ccdoc::phase3::html::write_meta(ostream& os,
				     const char* name,
				     const char* content)
{
  os << "<meta name=\""
     << name
     << "\" content=\""
     << content
     << "\">\n";
}
// ================================================================
// write common trailer info
// ================================================================
void ccdoc::phase3::html::write_common_trailer_info(ostream& os)
{
  os << "<a name=ccdoc_bottom></a>\n"
     << "<hr>\n";
  if(m_trailer.size()) {
    // The user specified a customized trailer, use it.
    os << m_trailer << endl;
  }
  else {
    // Output the default message.
    // Note that the copyright is not displayed here because
    // I don't own the contents of the page.
    os << "<center>\n"
       << "<p>\n"
       << "<font size=\"-1\">\n"
       << "Created " << date_time() << ".\n"
       << "<br>\n"
       << "This documentation was generated automatically by\n"
       << "<br>\n"
       << m_sw.version() << ".\n"
       << "<br>\n"
       << "Click "
       << "<a href=\"mailto:joe@joelinoff.com,jdl@xilinx.com"
       << "?subject="
       << "Bug report or feature request for "
       << m_sw.version()
       << "\">here</a>"
       << " to submit a bug report or feature request.\n"
       << "<br>\n"
       << "Click <a href=\"#ccdoc_top\">here</a>"
       << " to return to the top of the page.\n"
       << "</font>\n"
       << "</center>\n"
      ;
  }
  os << "</body>\n"
     << "</html>\n"
    ;
}
// ================================================================
// Write the extends clause for classes and structs.
// ================================================================
void ccdoc::phase3::html::write_extends_clause(ostream& os,
					       statement::base* stmt)
{
  if( stmt ) {
    // Write the extends clause.
    // extends <class> as <public|protected|private>
    bool first = true;
    string extends_prefix = "&nbsp;&nbsp;&nbsp;&nbsp;extends ";
    const statement::base::cstrs_t& vec1 = stmt->get_tokens();
    statement::base::cstrs_citr_t itr1 = vec1.begin();
    for(;itr1!=vec1.end();++itr1) {
      string token = *itr1;
      if( token == ":" ) {
	itr1++;
	break;
      }
    }
    if( itr1!=vec1.end() ) {
      // This is a derived class of the form:
      //   class A : ... { ... };
      statement::base::cstrs_citr_t end = itr1;
      while( end!=vec1.end() ) {
	string id;
	statement::base::strs_t access;
	statement::base::cstrs_citr_t beg = end;
	string token;

	// ================================================
	// Get the type and the access specifiers.
	// Do this the easy way.
	// ================================================
	bool found_id = false;
	int depth = 0;
	for(;end!=vec1.end();++end) {
	  token = *end;
	  if( !found_id ) {
	    if( token == "virtual" ||
		token == "public" ||
		token == "protected" ||
		token == "private" ) {
	      access.push_back(token);
	      continue;
	    }
	    found_id = true;
	  }
	  if( found_id ) {
	    if( token == "<" ) {
	      // This handles cases like:
	      //   class A : public foo<My1,My2> ;
	      //   class A : public foo<My1,My2<XX> > ;
	      depth++;
	    }
	    else if( token == ">" ) {
	      depth--;
	    }
	    else if( depth == 0 ) {
	      if( token == "," ) {
		// Output the extends information.
		++end;
		break;
	      }
	    }
	    id += token;
	  }
	}

	// ================================================
	// At this point the id and access vector are set.
	// ================================================
	// Write out the extends clause.
	if( !first )
	  os << "<br>";
	if( first )
	  first = false;
	os << extends_prefix;

	// ================================================
	// Write out the link.
	// ================================================
	statement::base::stmts_t id_class_stmts;
	load_extend_classes(id,id_class_stmts,stmt);
	if( id_class_stmts.size() == 0 ) {
	  os << "<font color=red>"
	     << format_string_for_html(id)
	     << "</font>";
	}
	else if( id_class_stmts.size() == 1 ) {
	  write_link(os,id_class_stmts[0],id.c_str());
	}
	else {
	  // There are too many links.
	  // Write all of the links.
	  statement::base::stmts_itr_t sitr = id_class_stmts.begin();
	  os << "{ ";
	  for(;sitr!=id_class_stmts.end();++sitr) {
	    statement::base* id_stmt = *sitr;
	    if( sitr != id_class_stmts.begin() )
	      os << ", ";
	    write_link(os,id_stmt,id.c_str());
	  }
	  os << " }";
	}

	// ================================================
	// Write out the "as" clause.
	// ================================================
	os << " as";
	if( access.size() ) {
	  statement::base::strs_itr_t titr = access.begin();
	  for(;titr!=access.end();++titr) {
	    os << " " << *titr;
	  }
	}
	else {
	  // 11.2 ISO/IEC 14882:1998(E)
	  if( stmt->get_type() == statement::base::STMT_CLASS_BEGIN )
	    os << " private";
	  else if( stmt->get_type() == statement::base::STMT_STRUCT_BEGIN )
	    os << " public";
	  else
	    os << " <font color=red>unknown</font>";
	}
	os << "\n";
      }
      os << "<p>\n";
    }
  }
}
// ================================================================
// Write out the friends info
// ================================================================
void ccdoc::phase3::html::write_friends_info(ostream& os,
					     statement::base* stmt)
{
  statement::base* scope = stmt;
  statement::base::stmts_t friends;
  statement::base::stmts_t& children = stmt->get_children();
  statement::base::stmts_itr_t itr = children.begin();
  for(;itr!=children.end();++itr) {
    if( (*itr)->get_type() == statement::base::STMT_FRIEND_CLASS ||
	(*itr)->get_type() == statement::base::STMT_FRIEND_FUNCTION ) {
      friends.push_back(*itr);
    }
  }
  if( friends.size() ) {
    if( friends.size() == 1 ) {
      os << "<dt><b>Friend:</b></dt><dd><table cellspacing=4>\n";
    }
    else {
      os << "<dt><b>Friends:</b></dt><dd><table cellspacing=4>\n";
    }
    itr = friends.begin();
    for(;itr!=friends.end();++itr) {
      statement::base* f = *itr;

      os << "<tr>\n";

      os << "<td align=left valign=top>\n";
      if( f->get_type() == statement::base::STMT_FRIEND_CLASS )
	os << "<font color=green><b>class</b></font>";
      else if( f->get_type() == statement::base::STMT_FRIEND_FUNCTION )
	os << "<font color=green><b>function</b></font>";
      os << "</td>\n";

      os << "<td align=left valign=top>\n";
      statement::base::stmts_t matches;
      string id = f->get_id();
      m_db.get_stmt_no_pkgs(id,matches);
      if( matches.size() == 0 ) {
	// Issue 0064
	// The name was not found, try looking in the namespace
	// of the parent.
	if(stmt->get_parent()) {
	  stmt->get_parent()->get_hier_id_no_pkgs(id);
	  id += "::";
	  id += f->get_id();
	  m_db.get_stmt_no_pkgs(id,matches);
	}
      }
      if( matches.size() == 1 ) {
	write_link(os,matches[0],f->get_id());
      }
      else if( matches.size() > 1 ) {
	// Filter to match the type.
	statement::base::stmts_t filter_matches;
	statement::base::stmts_itr_t mitr = matches.begin();
	for(;mitr!=matches.end();++mitr) {
	  if( f->get_type() == statement::base::STMT_FRIEND_CLASS ) {
	    if( (*mitr)->get_type() == statement::base::STMT_CLASS_BEGIN ||
		(*mitr)->get_type() == statement::base::STMT_STRUCT_BEGIN ) {
	      filter_matches.push_back( *mitr );
	      break; // avoid length searches for now
	    }
	  }
	  else if( f->get_type() == statement::base::STMT_FRIEND_FUNCTION ) {
	    if( (*mitr)->get_type() == statement::base::STMT_FUNCTION ||
		(*mitr)->get_type() == statement::base::STMT_FUNCTION_OPERATOR ||
		(*mitr)->get_type() == statement::base::STMT_METHOD_CONSTRUCTOR ||
		(*mitr)->get_type() == statement::base::STMT_METHOD_DESTRUCTOR ||
		(*mitr)->get_type() == statement::base::STMT_METHOD_OPERATOR ||
		(*mitr)->get_type() == statement::base::STMT_METHOD ) {
	      filter_matches.push_back( *mitr );
	      break; // avoid length searches for now
	    }
	  }
	}
	if( filter_matches.size() ) {
	  write_link(os,filter_matches[0],f->get_id());
	}
	else {
	  write_link(os,matches[0],f->get_id());
	}
      }
      else {
	os << "<font color=red>"
	   << format_string_for_html(f->get_id())
	   << "</font>";
      }
      os << "</td>\n";

      os << "<td align=left valign=top>\n";
      if( f->get_comment() ) {
	// Special case:
	// Only the descriptions are written for friends.
	statement::comment doc(f->get_comment());
	write_ccdoc_desc_info(os,doc.get_short_desc(),scope);
	if(doc.get_short_desc().size() && doc.get_long_desc().size())
	  os << "<p>\n";
	write_ccdoc_desc_info(os,doc.get_long_desc(),scope);
      }
      else {
	os << m_sw.rptdefsd();
      }
      os << "</td>\n";
      os << "</tr>\n";
    }
    os << "</table>\n";
    os << "</dd>\n";
  }
}
// ================================================================
// Write out the ccdoc information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_info(ostream& os,
					   statement::base* stmt,
					   bool author_required,
					   bool version_required,
					   bool inherited)
{
  statement::base* scope = stmt->get_parent();
  if( stmt->get_comment() ) {
    statement::comment doc(stmt->get_comment());

    // Short description.
    write_ccdoc_desc_info(os,doc.get_short_desc(),scope);

    if(doc.get_short_desc().size() && doc.get_long_desc().size())
      os << "<p>\n";
    
    // Long description.
    write_ccdoc_desc_info(os,doc.get_long_desc(),scope);
    
    os << "<dl>\n";

    // Inherited from
    if( inherited && stmt->get_parent() )
      write_inherited_from_info(os,stmt);

    // Source file
    write_ccdoc_src_info(os,stmt,scope);

    // Author
    string id = "Author";
    if(doc.get_authors().size()>1)
      id = "Authors";
    if( author_required )
      write_ccdoc_directive_info(os,id.c_str(),doc.get_authors(),m_sw.rptdefa(),scope);
    else
      write_ccdoc_directive_info(os,id.c_str(),doc.get_authors(),0,scope);
    
    // Version
    if( version_required )
      write_ccdoc_directive_info(os,"Version",doc.get_version(),m_sw.rptdefv(),scope);
    else
      write_ccdoc_directive_info(os,"Version",doc.get_version(),0,scope);
    
    // Since
    // Issue 0082: 10/18/01 bzoe
    write_ccdoc_directive_info(os,"Since",doc.get_since(),0,scope);

    // Deprecated
    write_ccdoc_directive_info(os,"Deprecated",doc.get_deprecated(),0,scope,false);

    // Params
    write_ccdoc_param_directive_info(os,doc.get_params(),scope);

    // Returns
    write_ccdoc_directive_info(os,"Returns",doc.get_returns(),0,scope,false);

    // Exceptions
    write_ccdoc_exception_directive_info(os,doc.get_exceptions(),scope);

    // See
    write_ccdoc_see_directive_info(os,doc.get_sees(),stmt);

    // The following directives are not output in HTML
    //   @pkg
    //   @pkgdoc
    //   @suffix
    os << "</dl>\n";
  }
  else {
    // What do we do with entities that do not have a ccdoc
    // comment?
    // Write out the source information and the author stuff.
    statement::base::strs_t empty_vec;
    string empty_str;

    if( stmt->get_type() == statement::base::STMT_PACKAGE ) {
      // Special case handling for packages with no comments.
      // Packages don't have source.
      if( m_sw.rptdpd() == true ) {
	// Report package comments if -rptdpd was specified, otherwise
	// don't.
	os << m_sw.rptdefsd();
      }
      if( inherited || author_required || version_required ) {
	os << "<dl>\n";
	if( inherited && stmt->get_parent() )
	  write_inherited_from_info(os,stmt);
	if( author_required )
	  write_ccdoc_directive_info(os,"Author",empty_vec,m_sw.rptdefa(),scope);
	if( version_required )
	  write_ccdoc_directive_info(os,"Version",empty_str,m_sw.rptdefv(),scope);
	os << "</dl>\n";
      }
      return;
    }

    // Tell them that it is not documented.
    if(stmt->get_lineno() == 0 ) {
      // Packages never have line numbers.
      // Lineno zero flags the fact that it
      // was automatically generated by the
      // compiler.
      os << m_sw.rptdefasd();
    }
    else {
      os << m_sw.rptdefsd();
    }
    
    os << "<dl>\n";
    if( inherited && stmt->get_parent() )
      write_inherited_from_info(os,stmt);
    write_ccdoc_src_info(os,stmt,scope);
    if( author_required )
      write_ccdoc_directive_info(os,"Author",empty_vec,m_sw.rptdefa(),scope);
    if( version_required )
      write_ccdoc_directive_info(os,"Version",empty_str,m_sw.rptdefv(),scope);
    os << "</dl>\n";
  }
}
// ================================================================
// Write out the source file information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_src_info(ostream& os,
					       statement::base* stmt,
					       statement::base* scope)
{
  string path;
  if( write_ccdoc_src_info(path,stmt) ) {
    write_ccdoc_directive_info(os,"Source",path,0,scope);
  }
}
// ================================================================
// Write out the source file information.
// ================================================================
bool ccdoc::phase3::html::write_ccdoc_src_info(string& path,
					       statement::base* stmt)
{
  // Source file
  if(stmt &&
     stmt->get_type() != statement::base::STMT_PACKAGE ) { // Issue 0027
    string file;
    if(stmt->get_file())
      file = stmt->get_file();
    else
      file = "unknown";
    if( stmt->get_file() && m_sw.srcurl().size() ) {
      // Build an anchor reference to the source file.
      string urlfn = stmt->get_file();
      if( m_sw.dospaths() ) {
	// Convert the DOS backslashes to forward slashes for HTML.
	replace(urlfn.begin(),urlfn.end(),'\\','/');
      }
      if( urlfn.size() ) {
	path = "<a href=\"" + m_sw.srcurl() + urlfn + "\">" + file + "</a>";
      }
    }
    else {
      path = file;
    }
    if( path.size() ) {
      // Don't append the line numbers of zero. They are meaningless.
      if( stmt->get_lineno() ) {
	char nbuf[32];
	sprintf(nbuf,"%d",stmt->get_lineno());
	path += ":";
	path += nbuf;
      }
    }
    return true;
  }
  return false;
}
// ================================================================
// Write out the ccdoc description information.
// Dereference the @link and @$ directives.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_desc_info(ostream& os,
						const statement::base::strs_t& vec,
						statement::base* scope)
{
  statement::base::strs_citr_t itr = vec.begin();
  for(;itr!=vec.end();++itr) {
    write_ccdoc_line_info(os,itr,vec.end(),scope);
  }
}
// ================================================================
// Write out link info.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_line_info(ostream& os,
						statement::base::strs_citr_t& itr,
						statement::base::strs_citr_t itr_end,
						statement::base* scope)
{
  const string& line = *itr;
  if( line == "@link" ) {
    // This is a link line.
    if( ++itr == itr_end ) {
      os << line << "\n";
      return;
    }
    string link = *itr;
    if( ++itr == itr_end ) {
      os << line << "\n"
	 << link << "\n";
      return;
    }
    // Issue 0088.
    string id = *itr;
    if( !find_and_write_links(os,link.c_str(),id.c_str(),scope) ) {
      os << "<font color=red>"
	 << format_string_for_html(id)
	 << "</font>";
    }
  }
  else if( line == " " ) {
    // This is how the scanner tells us to insert a 
    // paragraph separator.
    os << "<p>\n";
  }
  else {
    os << line << "\n";
  }
}
// ================================================================
// Write out the ccdoc directive information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_param_directive_info(ostream& os,
							   const statement::base::strss_t& vecvec,
							   statement::base* scope)
{
  if( vecvec.size() == 0 ) {
    return;
  }
  string name = "Param";
  if( vecvec.size() > 1 ) {
    name = "Params";
  }
  os << "<dt><b>" << name << ":</b></dt><dd><table cellspacing=4>\n";
  statement::base::strss_citr_t i = vecvec.begin();
  statement::base::strss_citr_t e = vecvec.end();
  for(;i!=e;++i) {
    const statement::base::strs_t& vec = *i;
    statement::base::strs_citr_t i1 = vec.begin();
    os << "<tr><td align=left valign=top><i>";
    write_ccdoc_line_info(os,i1,vec.end(),scope);
    os << "</i></td><td align=left valign=top>";
    for(++i1;i1!=vec.end();++i1) {
      write_ccdoc_line_info(os,i1,vec.end(),scope);
    }
    os << "</td></tr>\n";
  }
  os << "</table></dd>\n";
}
// ================================================================
// Write out the ccdoc exception directive information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_exception_directive_info(ostream& os,
							       const statement::base::strss_t& vecvec,
							       statement::base* scope)
{
  if( vecvec.size() == 0 ) {
    return;
  }
  string name = "Exception";
  if( vecvec.size() > 1 ) {
    name = "Exceptions";
  }
  os << "<dt><b>" << name << ":</b></dt><dd><table cellspacing=4>\n";
  statement::base::strss_citr_t i = vecvec.begin();
  statement::base::strss_citr_t e = vecvec.end();
  for(;i!=e;++i) {
    const statement::base::strs_t& vec = *i;
    statement::base::strs_citr_t i1 = vec.begin();
    os << "<tr><td align=left valign=top><i>";
    // Issue 0087.
    // Write out the exception as a link, if possible.
    string id = *i1;
    if( !find_and_write_links(os,id.c_str(),id.c_str(),scope) ) {
      os << format_string_for_html(id);
    }
    os << "</i></td><td align=left valign=top>";
    // Write out the rest of the exception info.
    for(++i1;i1!=vec.end();++i1) {
      write_ccdoc_line_info(os,i1,vec.end(),scope);
    }
    os << "</td></tr>\n";
  }
  os << "</table></dd>\n";
}
// ================================================================
// Write out the ccdoc directive information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_see_directive_info(ostream& os,
							 const statement::base::strss_t& vecvec,
							 statement::base* stmt)
{
  if( vecvec.size() == 0 ) {
    return;
  }
  string name = "See Also";
  os << "<dt><b>" << name << ":</b></dt><dd>";

  statement::base::strss_citr_t i = vecvec.begin();
  statement::base::strss_citr_t e = vecvec.end();
  for(int j=0;i!=e;++i,++j) {
    const statement::base::strs_t& vec = *i;
    statement::base::strs_citr_t i1 = vec.begin();
    string link = *i1;
    ++i1;
    string index = *i1;
    string name = link;

    // Issue 0019
    if(link[0] == '<' ) {
      // Handle the special case where the user defined
      // an explicit link.
      // Just use what they specified.
      if(j)
	os << ", "; // Issue 0024
      os << link << "\n";
      continue;
    }

    // Issue 0043.
    if( link[0] == '#' ) {
      // The parent defines the scope.
      const char* ps = link.c_str();
      name = &ps[1]; // strip of the leading '#"
      if( stmt->get_parent() )
	stmt->get_parent()->get_hier_id_no_pkgs(link);
      if( link.size() )
	link += "::";
      link += name;
    }

    statement::base::stmts_t stmts;
    m_db.get_stmt_no_pkgs(link,stmts);

    if(index != "*" ) {
      // Output the indexed reference.
      unsigned num = atoi(index.c_str());
      if( stmts.size() > num ) {
	statement::base* stmt = stmts[num];
	if(j)
	  os << ", ";
	write_link(os,stmt,name.c_str());
      }
      else {
	if(j)
	  os << ", ";
	os << "<font color=red>"
	   << format_string_for_html(name)
	   << "[" << index << "]</font>";
      }
    }
    else {
      if( stmts.size() ) {
	// Output all of the references.
	statement::base::stmts_itr_t itr1 = stmts.begin();
	for(;itr1!=stmts.end();++itr1,++j) {
	  statement::base* stmt = *itr1;
	  if(j)
	    os << ", ";
	  write_link(os,stmt,name.c_str());
	}
      }
      else {
	if(j)
	  os << ", ";
	os << "<font color=red>"
	   << format_string_for_html(name)
	   << "</font>";
      }
    }
  }

  os << "</dd>\n";
}
// ================================================================
// Write out the inherited from info.
// ================================================================
void ccdoc::phase3::html::write_inherited_from_info(ostream& os,
						    statement::base* stmt)
{
  os << "<dt><b>Inherited From:</b></dt><dd>";
  string id;
  stmt->get_parent()->get_hier_id_no_pkgs(id);
  write_link(os,stmt->get_parent(),id.c_str());
  os << "</dd>\n";
}
// ================================================================
// Write out the ccdoc directive information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_directive_info(ostream& os,
						     const char* name,
						     const statement::base::strs_t& vec,
						     const char* default_field,
						     statement::base* scope,
                                                     bool commas)
{
  if( vec.size() == 0 && default_field == 0 ) {
    return;
  }
  os << "<dt><b>" << name << ":</b></dt><dd>";
  if( vec.size() == 0 ) {
    // There are no entries, output the default
    // value.
    os << default_field;
  }
  else {
    statement::base::strs_citr_t i = vec.begin();
    statement::base::strs_citr_t e = vec.end();
    if( i!=e ) {
      write_ccdoc_line_info(os,i,vec.end(),scope);
    }
    for(++i;i!=e;++i) {
      if( commas ) // issue 0095
        os << ", ";
      write_ccdoc_line_info(os,i,vec.end(),scope);
    }
  }
  os << "</dd>\n";
}
// ================================================================
// Write out the ccdoc directive information.
// ================================================================
void ccdoc::phase3::html::write_ccdoc_directive_info(ostream& os,
						     const char* name,
						     const string& val,
						     const char* default_field,
						     statement::base* scope)
{
  if( val.size() == 0 && default_field == 0 ) {
    return;
  }
  os << "<dt><b>" << name << ":</b></dt><dd>";
  if( val.size() == 0 ) {
    // There are no entries, output the default
    // value.
    os << default_field;
  }
  else {
    os << val;
  }
  os << "</dd>\n";
}
// ================================================================
// Date and time.
// ================================================================
#include <time.h>
const char* ccdoc::phase3::html::date_time() const
{
  static char tbuf[128];
  time_t ltime;
  ::time(&ltime);
  ::strcpy(tbuf,::ctime(&ltime));

  // Strip off the new line.
  char* p = tbuf;
  const char* e = &tbuf[127]; // detect overflows
  for(;*p != '\n' && *p != 0 && p < e; *p++);
  *p = 0;
  return tbuf;
}

// ================================================================
// CrcTable
// ================================================================
unsigned ccdoc::phase3::html::checksum::m_crc_table[256] = {
 0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f,
 0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988,
 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91, 0x1db71064, 0x6ab020f2,
 0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
 0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
 0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172,
 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b, 0x35b5a8fa, 0x42b2986c,
 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
 0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423,
 0xcfba9599, 0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
 0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190, 0x01db7106,
 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
 0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d,
 0x91646c97, 0xe6635c01, 0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
 0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950,
 0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
 0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7,
 0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0,
 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9, 0x5005713c, 0x270241aa,
 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
 0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81,
 0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
 0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12, 0x94643b84,
 0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
 0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb,
 0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc,
 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8, 0xa1d1937e,
 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
 0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55,
 0x316e8eef, 0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
 0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f, 0xc5ba3bbe, 0xb2bd0b28,
 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
 0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f,
 0x72076785, 0x05005713, 0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38,
 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
 0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
 0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69,
 0x616bffd3, 0x166ccf45, 0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2,
 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db, 0xaed16a4a, 0xd9d65adc,
 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
 0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693,
 0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d
};
