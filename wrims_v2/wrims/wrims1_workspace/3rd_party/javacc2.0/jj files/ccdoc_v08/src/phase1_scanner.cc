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
#include "phase1_scanner.h"
#include <cstdio>

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: phase1_scanner.cc,v 1.14 2001/11/27 01:42:20 Administrator Exp $";

// ================================================================
// Constructor.
// ================================================================
ccdoc::phase1::scanner::scanner(switches& sw)
  : m_lineno(0),
    m_debug(false),
    m_sw(sw)
{
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::phase1::scanner::~scanner()
{
}
// ================================================================
// Open
// ================================================================
void ccdoc::phase1::scanner::open(const string& name)
{
  m_name = name;
  m_lineno = 1;
  m_put_tokens.clear();
  m_put_chars.clear();
  if( m_is.is_open() )
    m_is.close();
  m_is.open( m_name.c_str() );
}
// ================================================================
// Close
// ================================================================
void ccdoc::phase1::scanner::close()
{
  if( m_is.is_open() )
    m_is.close();
}
// ================================================================
// Get character.
// ================================================================
char ccdoc::phase1::scanner::get_char()
{
  if( m_is.eof() || m_is.bad() || m_is.fail() )
    return 0;
  char ch;
  if( m_put_chars.size() ) {
    ch = m_put_chars.back();
    m_put_chars.pop_back();
  }
  else if(!m_is.get(ch)) {
    return 0;
  }
  // Ignore carriage returns under Windows.
  if( '\r' == ch )
    return get_char();
  if( '\n' == ch )
    m_lineno++;
  return ch;
}
// ================================================================
// Put character. Use a vector so that characters can
// be put even when the end of the stream is reached.
// ================================================================
void ccdoc::phase1::scanner::put_char(char ch)
{
  if(ch) {
    m_put_chars.push_back(ch);
    if( '\n' == ch && m_lineno>1 )
      m_lineno--;
  }
}
// ================================================================
// Put token.
// ================================================================
void ccdoc::phase1::scanner::put_token(const string& token)
{
  m_put_tokens.push_back(token);
  if(m_debug) {
    if( token == "\n" )
      s_log << "CCDOC_PHASE1_DEBUG: put_line: " << m_lineno << "\n";
    else if( token == "" )
      s_log << "CCDOC_PHASE1_DEBUG: put_eof: " << m_lineno << "\n";
    else
      s_log << "CCDOC_PHASE1_DEBUG: put_token: '" << token << "'\n";
  }
}
// ================================================================
// Get token.
// ================================================================
const char* ccdoc::phase1::scanner::get_token()
{
  const char* token = scan_token();
  if(m_debug) {
    if( *token == '\n' )
      s_log << "CCDOC_PHASE1_DEBUG: get_line: " << m_lineno << "\n";
    else if( *token == 0 )
      s_log << "CCDOC_PHASE1_DEBUG: get_eof: " << m_lineno << "\n";
    else
      s_log << "CCDOC_PHASE1_DEBUG: get_token: '" << token << "'\n";
  }
  return token;
}
// ================================================================
// Scan token.
// ================================================================
const char* ccdoc::phase1::scanner::scan_token()
{
  static char tokenbuf[0x100000]; // 2^20
  *tokenbuf = 0;

  // ================================================
  // A token was put back, return that one.
  // ================================================
  if( m_put_tokens.size() ) {
    ::strcpy(tokenbuf,m_put_tokens.back().c_str());
    m_put_tokens.pop_back();
    return tokenbuf;
  }

  char ch = skip_ws();

  // ================================================
  // If this the end of the file, return
  // an empty token.
  // ================================================
  if(!ch) {
    return tokenbuf;
  }

  // ================================================
  // Translate trigraph sequences.
  // This must be done early because they can
  // be part of another token.
  // ================================================
  if( '?' == ch ) {
    ch = scan_trigraph();
    if( '?' == ch ) {
      tokenbuf[0] = '?';
      tokenbuf[1] = 0;
      return tokenbuf;
    }
  }

  // ================================================
  // Eliminate the "\\\n" white space. This cannot
  // be done in skip_ws() because of the backslash
  // trigraph sequence "??/".
  // ================================================
  if( '\\' == ch ) {
    char ch1 = get_char();
    if( '\n' ) {
      return get_token();
    }
    put_char(ch1);
  }

  // ================================================
  // Check for character and string literals with
  // the 'L' prefix.
  // ================================================
  if( ch == 'L' ) {
    char ch1 = get_char();
    if( '"' == ch1 ) {
      tokenbuf[0] = ch;
      tokenbuf[1] = ch1;
      tokenbuf[2] = 0;
      get_string_literal(&tokenbuf[2],sizeof(tokenbuf)-3);
      return tokenbuf;
    }
    if( '\'' == ch1 ) {
      tokenbuf[0] = ch;
      tokenbuf[1] = ch1;
      tokenbuf[2] = 0;
      get_char_literal(&tokenbuf[2],sizeof(tokenbuf)-3);
      return tokenbuf;
    }
    put_char(ch1);
  }

  // ================================================
  // This is a quoted string. Look for the next
  // un-escaped quote.
  // ================================================
  if( ch == '"' ) {
    tokenbuf[0] = ch;
    tokenbuf[1] = 0;
    get_string_literal(&tokenbuf[1],sizeof(tokenbuf)-2);
    return tokenbuf;
  }

  // ================================================
  // This is a single quoted character. Look for the
  // next un-escaped quote.
  // ================================================
  if( '\'' == ch ) {
    tokenbuf[0] = ch;
    tokenbuf[1] = 0;
    get_char_literal(&tokenbuf[1],sizeof(tokenbuf)-2);
    return tokenbuf;
  }
  
  // ================================================
  // This is some sort of number.
  // The special case of ".333" is handled in the '.'
  // processing.
  // ================================================
  if( '0' <= ch && ch <= '9' ) {
    put_char(ch);
    get_number_literal(tokenbuf,sizeof(tokenbuf)-1);
    return tokenbuf;
  }
  
  // ================================================
  // Convert alternative token forms to their
  // primary form. This is handled for each
  // separate character analysis.
  //
  //  alt     pri     alt     pri     alt     pri
  //  ======  ===     ======  ===     ======  ===
  //  <%      {       and     &&      and_eq  &=
  //  %>      }       bitor   |       or_eq   |=
  //  <:      [       or      ||      xor_eq  ^=
  //  :>      ]       xor     ^       not     !
  //  %:      #       compl   ~       not_eq  !=
  //  %:%:    ##      bitand  &
  //
  // We don't care about the identifier based
  // alternative forms.
  // ================================================

  // ================================================
  // This is an identifier.
  // Ccdoc must support the non-standard $ character
  // because some compilers support it.
  // ================================================
  if( ( 'a' <= ch && ch <= 'z' ) ||
      ( 'A' <= ch && ch <= 'Z' ) ||
      ( '_' == ch || '$' == ch ) ) {
    put_char(ch);
    get_identifier(tokenbuf,sizeof(tokenbuf)-2);
    return tokenbuf;
  }

  // ================================================
  // This is a operator/punctuator.
  // Do a longest match.
  // ================================================
  if( '{' == ch || '}' == ch ||
      '[' == ch || ']' == ch ||
      '(' == ch || ')' == ch ||
      '~' == ch ||
      ',' == ch ||
      ';' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: !, !=
  // ================================================
  if( '!' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: #, ##
  // ================================================
  if( '#' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '#' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: %, %:, %:%:, %=, %>
  // ================================================
  if( '%' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else if( '>' == ch ) {
      // %> --> }
      pstr = tokenbuf;
      *pstr++ = '}';
      *pstr++ = 0;
      return tokenbuf;
    }
    else if( ':' == ch ) {
      ch = get_char();
      if( '%' == ch ) {
	ch = get_char();
	if( ':' == ch ) {
	  // %:%: --> ##
	  pstr = tokenbuf;
	  *pstr++ = '#';
	  *pstr++ = '#';
	  *pstr++ = 0;
	  return tokenbuf;
	}
	else {
	  put_char('%');
	  put_char(ch);
	}
      }
      else {
	// %: --> #
	pstr = tokenbuf;
	*pstr++ = '#';
	*pstr++ = 0;
	put_char(ch);
	return tokenbuf;
      }
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: &, &&, &=
  // ================================================
  if( '&' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '&' == ch || '=' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: *, *=
  // ================================================
  if( '*' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: +, ++, +=
  // ================================================
  if( '+' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '+' == ch || '=' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: -, --, -=, ->, ->*
  // ================================================
  if( '-' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '-' == ch || '=' == ch ) {
      *pstr++ = ch;
    }
    else if( '>' == ch ) {
      *pstr++ = ch;
      ch = get_char();
      if( '*' == ch ) {
	*pstr++ = ch;
      }
      else {
	put_char(ch);
      }
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: ., .*, ..., .[0-9]+
  // ================================================
  if( '.' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '*' == ch ) {
      *pstr++ = ch;
    }
    else if( '0' <= ch && ch <= '9' ) {
      // Handle floating number of the form: .333
      put_char(ch);
      put_char('.');
      get_number_literal(tokenbuf,sizeof(tokenbuf)-1);
      return tokenbuf;
    }
    else if( '.' == ch ) {
      ch = get_char();
      if( '.' == ch ) {
	*pstr++ = '.';
	*pstr++ = '.';
      }
      else {
	put_char(ch);
	put_char('.');
      }
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: /, /=, //, /*
  // ================================================
  if( '/' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
      *pstr = 0;
      return tokenbuf;
    }
    else if( '/' == ch ) {
      // Found '//'
      // Check for '//@{' or a '// @{' comment.
      return scan_ccdoc_style2(tokenbuf,sizeof(tokenbuf)-1);
    }
    else if( '*' == ch ) {
      // Found '/*'
      // Check for a '/**' comment.
      return scan_ccdoc_style1(tokenbuf,sizeof(tokenbuf)-1);
    }
    else {
      put_char(ch); // issue 0069
      *pstr = 0;
      return tokenbuf;
    }
  }

  // ================================================
  // OP: :, ::, :>
  // ================================================
  if( ':' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( ':' == ch ) {
      *pstr++ = ch;
    }
    else if( '>' == ch ) {
      // :> --> ]
      pstr = tokenbuf;
      *pstr++ = ']';
      *pstr++ = 0;
      return tokenbuf;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: <, <%, <:, <<, <<=, <=
  // ================================================
  if( '<' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else if( '%' == ch ) {
      // <% --> {
      pstr = tokenbuf;
      *pstr++ = '{';
      *pstr++ = 0;
      return tokenbuf;
    }
    else if( ':' == ch ) {
      // <: --> ]
      pstr = tokenbuf;
      *pstr++ = '[';
      *pstr++ = 0;
      return tokenbuf;
    }
    else if( '<' == ch ) {
      *pstr++ = ch;
      ch = get_char();
      if( '=' == ch ) {
	*pstr++ = ch;
      }
      else {
	put_char(ch);
      }
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: =, ==
  // ================================================
  if( '=' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: >, >=, >>, >>=
  // ================================================
  if( '>' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else if( '>' == ch ) {
      *pstr++ = ch;
      ch = get_char();
      if( '=' == ch ) {
	*pstr++ = ch;
      }
      else {
	put_char(ch);
      }
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: ^, ^=
  // ================================================
  if( '^' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // ================================================
  // OP: |, |=, ||
  // ================================================
  if( '|' == ch ) {
    char* pstr = tokenbuf;
    *pstr++ = ch;
    ch = get_char();
    if( '=' == ch || '|' == ch ) {
      *pstr++ = ch;
    }
    else {
      put_char(ch);
    }
    *pstr = 0;
    return tokenbuf;
  }

  // Catch all at the end.
  tokenbuf[0] = ch;
  tokenbuf[1] = 0;
  return tokenbuf;
}
// ================================================================
// Skip w/s. Note that new lines are not w/s because they
// are significant for pre-processing directives.
// ================================================================
char ccdoc::phase1::scanner::skip_ws()
{
  // Ignore white space, keep new lines
  // because they are used for pre-processing
  // analysis.
  char ch = get_char();
  while( (ch > 0 && ch <= ' ') && ch != 0 && ch != '\n' ) {
    ch = get_char();
  }
  return ch;
}
// ================================================================
// scan trigraph sequences.
// ================================================================
char ccdoc::phase1::scanner::scan_trigraph()
{
  // ================================================
  // Check for tri-graph sequences.
  // The standard states that these sequences are
  // converted first. I interpret this to mean all
  // sequences except strings and comments.
  // OP: ?, ??=, ??/, ??', ??(, ??), ??!, ??<, ??>, ??-
  // ================================================
  char ch1 = get_char();
  if( ch1 != '?' ) {
    put_char(ch1);
    return '?';
  }
  char ch2 = get_char();

  switch(ch2) {
  case '=':  ch2 = '#' ; break;
  case '/':  ch2 = '\\'; break;
  case '\'': ch2 = '^' ; break;
  case '(':  ch2 = '[' ; break;
  case ')':  ch2 = ']' ; break;
  case '!':  ch2 = '|' ; break;
  case '<':  ch2 = '{' ; break;
  case '>':  ch2 = '}' ; break;
  case '-':  ch2 = '~' ; break;
  default:
    put_char(ch2);
    put_char(ch1);
  }

  return ch2;
}
// ================================================================
// get_string_literal
// ================================================================
void ccdoc::phase1::scanner::get_string_literal(char* token,int max)
{
  int ch = '"';
  int pch = ch;
  while( max>0 && (ch = get_char()) ) {
    max--;
    *token++ = ch;
    if( ch == '"' && pch != '\\' ) {
      *token = 0;
      return;
    }
    // Issue 0116
    //   Contributed by Chris Martin 2001/11/25
    if( ch == '\\' && pch == '\\' ) {
      // Reset pch to 0 to make sure that
      // the terminating double quote is
      // found correctly for the case of "\\".
      ch = 0;
    }
    pch = ch;
  }
  *token = 0;

  // The end of the string was never reached.
  // We should never reach this point because all input should
  // be from a legal C++ file.
  s_log.warning()
    << "Unterminated string literal found in "
    << m_name.c_str()
    << " at line "
    << m_lineno
    << ".\n"
    << s_log.enable();
}
// ================================================================
// get_char_literal
// ================================================================
void ccdoc::phase1::scanner::get_char_literal(char* token,int max)
{
  // Issue 0052: Handle the special case of '\\'.
  int ch = get_char(); --max; *token++ = ch;
  if( '\\' == ch ) {
    ch = get_char(); --max; *token++ = ch;
  }
  ch = get_char(); --max; *token++ = ch;
  // Also handle the special cases of '\xad'.
  while( ch != '\'' ) {
    ch = get_char(); --max; *token++ = ch;
    if( max <= 2 ) {
      *token = 0;
      // The end of the character was never reached.
      // We should never reach this point because all input should
      // be from a legal C++ file.
      s_log.warning()
	<< "Unterminated character literal found in "
	<< m_name.c_str()
	<< " at line "
	<< m_lineno
	<< ".\n"
	<< s_log.enable();
      return;
    }
  }
  *token = 0;
}
// ================================================================
// get_number_literal
// ================================================================
void ccdoc::phase1::scanner::get_number_literal(char* token,int max)
{
  // Some of the numeric processing is confusing because
  // the types overlap. Consider the following
  // number:
  //
  //   07777.35
  //
  // This is a floating pointer number not an ill-formed
  // octal integer literal.
  //
  // This analyzer takes advantage of the fact that the
  // input is guaranteed to be legal C++.
  char ch = get_char();

  if( '0' == ch ) {
    *token++ = ch;
    max--;
    char ch1 = get_char();
    if( 'x' == ch1 || 'X' == ch1 ) {
      // This is a hex number.
      *token++ = ch1;
      max--;
      while( max>0 && (ch = get_char()) ) {
	if( ( 'a' <= ch && ch <= 'f' ) ||
	    ( 'A' <= ch && ch <= 'Z' ) ||
	    ( '0' <= ch && ch <= '9' ) ) {
	  *token++ = ch;
	  max--;
	}
	else {
	  break;
	}
      }
    }
    else if( '0' <= ch1 && ch1 <= '9' ) {
      // This is octal or floating point.
      *token++ = ch1;
      max--;
      while( max>0 && (ch = get_char()) ) {
	if( '0' <= ch && ch <= '9' ) {
	  *token++ = ch;
	  max--;
	}
	else {
	  break;
	}
      }
    }
    else {
      ch = ch1;
    }
  }
  else if( '1' <= ch && ch <= '9' ) {
    *token++ = ch;
    max--;
    // This is decimal or floating point.
    while( max>0 && (ch = get_char()) ) {
      if( '0' <= ch && ch <= '9' ) {
	*token++ = ch;
	max--;
      }
      else {
	break;
      }
    }
  }

  // At this point, ch is not a digit. Check
  // for a floating point decimal point, an
  // exponent or the UL suffix.

  // Scan for a floating point literal decimal point.
  if( '.' == ch ) {
    *token++ = ch;
    max--;
    while( max>0 && (ch = get_char()) ) {
      if( '0' <= ch && ch <= '9' ) {
	*token++ = ch;
	max--;
      }
      else {
	break;
      }
    }
  }

  // Scan for a floating point literal exponent.
  if( 'E' == ch || 'e' == ch ) {
    *token++ = ch;
    max--;
    if( max>0 && (ch = get_char()) ) {
      // Get the sign of the exponent or a digit if
      // the sign was not specified.
      if( ( '0' <= ch && ch <= '9' ) ||
	  ( '-' == ch || '+' == ch ) ) {
	*token++ = ch;
	max--;

	// Get the rest of the exponent digits.
	while( max>0 && (ch = get_char()) ) {
	  if( '0' <= ch && ch <= '9' ) {
	    *token++ = ch;
	    max--;
	  }
	  else {
	    break;
	  }
	}
      }
    }
  }

  // Scan for the ULF suffix.
  if( 'U' == ch || 'u' == ch ||
      'L' == ch || 'l' == ch ||
      'F' == ch || 'f' == ch) {
    *token++ = ch;
    max--;
    while( max>0 && (ch = get_char()) ) {
      if( 'U' == ch || 'u' == ch ||
	  'L' == ch || 'l' == ch ||
	  'F' == ch || 'f' == ch ) {
	*token++ = ch;
	max--;
      }
      else {
	break;
      }
    }
  }
  put_char(ch);
  *token = 0;
}
// ================================================================
// get_identifier
// ================================================================
void ccdoc::phase1::scanner::get_identifier(char* token,int max)
{
  char ch;
  while( max>0 && (ch = get_char()) ) {
    max--;
    if( ( 'a' <= ch && ch <= 'z' ) ||
	( 'A' <= ch && ch <= 'Z' ) ||
	( '0' <= ch && ch <= '9' ) ||
	( '_' == ch || ch == '$' ) ) {
      *token++ = ch;
    }
    else {
      // Non-identifier token found, we are done.
      *token = 0;
      put_char(ch);
      return;
    }
  }

  // We reached the end of the file.
  // This is legal for things like:
  // #endif
  *token = 0;
}
// ================================================================
// scan ccdoc style1
// ================================================================
const char* ccdoc::phase1::scanner::scan_ccdoc_style1(char* token,
						      int max)
{
  char ch = get_char();
  if( '*' != ch ) {
    // This is not a ccdoc comment,
    // skip to the end of the comment.
    while( ch != 0 ) {
      char pch = ch;
      ch = get_char();
      if( '*' == pch && '/' == ch )
	break;
    }
    return scan_token();
  }

  // Found: '/**'
  // ================================================
  // Check for the special pre-processing directives:
  // ================================================
  if( scan_ccdoc_style1_special() )
    return scan_token();

  // Re-format the comment for consumption by the parser.
  // Here are the comment fields.
  scanner_doc doc(*this,m_sw);

  // Define the processing mode.
  static char line[65536]; // maximum line length
  unsigned lineno = m_lineno;
  bool first = true;
  bool done = false;
  while(!done) {
    // ================================================
    // Skip leading w/s
    // ================================================
    char ch = get_char();
    while(ch && ch != '\n' && (ch > 0 && ch <= ' ') ) {
      ch = get_char();
    }

    // ================================================
    // Skip the leading asterisk if it exists.
    // ================================================
    if( !first && '*' == ch ) {
      ch = get_char();
      if( '/' == ch ) {
	done = true;
	break;
      }
    }

    // ================================================
    // Now load the line for directive processing.
    // Don't trim w/s, it may be needed for <pre></pre>.
    // ================================================
    char* pline = line;
    while( ch && ch != '\n' ) {
      *pline++ = ch;
      char pch = ch;
      ch = get_char();
      if( '*' == pch && ch == '/' ) {
	// This is the end of the comment.
	//
	// Make sure that any preceding directives
	// are processed, such as:
	//   '/**'
	//   ' * @return Foo bar spam */'
	//
	// Also make sure that all trailing asterisks are eaten:
	//  '/**'
	//  ' **/'
	//
	pline--; // *pline == '*'
	ccdoc_assert( '*' == *pline );
	while( pline>line && '*' == *pline )
	  --pline;
	if( pline == line ) {
	  // We are done.
	  done = true;
	  break;
	}
	// There may be some stuff on this line that
	// we need to parse.
	pline++;
	put_char('\n');
	put_char('/');
	put_char('*');
	break;
      }
    }
    *pline = 0;

    // ================================================
    // EOF
    // ================================================
    if( !ch ) {
      // The end of the file was reached before the
      // comment was terminated.
      s_log.warning()
	<< "Unexpected EOF found, unterminated ccdoc comment "
	<< "specified at line "
	<< m_lineno << " in " << m_name.c_str()
	<< ".\n"
	<< s_log.enable();
      return scan_token();
    }
    
    // ================================================
    // At this point we have the line.
    // Terminate it and write it out in debug mode.
    // ================================================
    if( m_debug ) {
      s_log << "CCDOC_PHASE1_DEBUG: ccdoc_line: '" << line << "'\n";
    }

    // ================================================
    // Skip the first line, if it is empty to
    // avoid conflicts when trying to determine
    // the long description for the following case:
    //   /**            | <-- line 1 (blank - ignore)
    //    * short       | <-- line 2 (short description)
    //    *             | <-- line 3 (blank - separator)
    //    * long        | <-- line 4 (long description)
    //    */            | <-- line 5 (end of comment)
    // ================================================
    if(first) {
      first = false;
      if(*line == 0)
	continue;
    }

    // ================================================
    // Issue 0082: only do this if -nojdsds is specified.
    // Set the short description flag to false
    // if a blank line (other than the first one)
    // is encountered.
    // ================================================
    if(!m_sw.jdsds() && *line == 0 && doc.m_mode == scanner_doc::SHORT) {
      doc.m_mode = scanner_doc::LONG;
      continue;
    }

    doc.parse_line(line);
  }

  // ================================================
  // At this point we have a valid ccdoc comment.
  // Format it for the parser to make things easy.
  // If it is empty, ignore it.
  // ================================================
  if( doc.empty() )
    return scan_token();
  return doc.format(token,max);
}
// ================================================================
// scan ccdoc style1 special
//   /**@#-*\/       - Start ignoring characters
//   /**@#+*\/       - Stop ignoring characters.
//   /**@#=<char>*\/ - Insert the specified token
//                     into the input stream.
// ================================================================
bool ccdoc::phase1::scanner::scan_ccdoc_style1_special()
{
  char ch = get_char();
  if( '@' != ch ) {
    put_char(ch);
    return false;
  }
  ch = get_char();
  if( '#' != ch ) {
    put_char(ch);
    put_char('@');
    return false;
  }
  ch = get_char();
  if( '-' != ch && '+' != ch && '=' != ch ) {
    put_char(ch);
    put_char('#');
    put_char('@');
    return false;
  }
  if( '-' == ch || '+' == ch ) {
    char ch1 = get_char();
    if( '*' != ch1 ) {
      put_char(ch1);
      put_char(ch);
      put_char('#');
      put_char('@');
      return false;
    }
    ch1 = get_char();
    if( '/' != ch1 ) {
      put_char(ch1);
      put_char('*');
      put_char(ch);
      put_char('#');
      put_char('@');
      return false;
    }
    if( '-' == ch ) {
      // We found: "/**@#-*\/", Ignore all characters
      // until we find "/**@#+*\/".
      for(ch=get_char();ch;ch=get_char()) {
	if( '/' != ch ) continue;
	// The put char handles cases like this:
	//   "/**@/**@#+*\/"
	ch = get_char(); if(!ch) break;
	if( '*' != ch ) {put_char(ch);continue;}
	ch = get_char(); if(!ch) break;
	if( '*' != ch ) {put_char(ch);continue;}
	ch = get_char(); if(!ch) break;
	if( '@' != ch ) {put_char(ch);continue;}
	ch = get_char(); if(!ch) break;
	if( '#' != ch ) {put_char(ch);continue;}
	ch = get_char(); if(!ch) break;
	if( '+' != ch ) {put_char(ch);continue;}
	ch = get_char(); if(!ch) break;
	if( '*' != ch ) {put_char(ch);continue;}
	ch = get_char(); if(!ch) break;
	if( '/' != ch ) {put_char(ch);continue;}
	break;
	// Don't warn about nested "/**@#-*\/", they are
	// legal.
      }
      // Don't warn about the EOF, that is legal as well.
    }
    // Ignore "/**@#+*\/". It is only used during
    // "/**@#-*\/" processing.
    return true;
  }

  // At this point we have: "/**@#=".
  ch = get_char(); // This is the character to insert.
  char ch1 = get_char();
  if( '*' != ch1 ) {
    put_char(ch1);
    put_char(ch);
    put_char('=');
    put_char('#');
    put_char('@');
    return false;
  }
  ch1 = get_char();
  if( '/' != ch1 ) {
    put_char(ch1);
    put_char('*');
    put_char(ch);
    put_char('=');
    put_char('#');
    put_char('@');
    return false;
  }

  // We found: "/**@#=<char>*\/".
  // Insert the character into the input stream.
  put_char(ch);
  return true;
}
// ================================================================
// scan ccdoc style2
// ================================================================
const char* ccdoc::phase1::scanner::scan_ccdoc_style2(char* token,
						      int max)
{
  // ================================================
  // Check for ccdoc comment designators:
  //   '//[ \t]*@{' 'vi}'  -- brace balancing for vi, added by bzoe
  //     or
  //   '//[ \t]*/**'
  // ================================================
  enum {
    NOT_A_COMMENT,
    STYLE2A,
    STYLE2B,
    STYLE2C} // Issue 0086: STYLE2C, added by bzoe 2001/11/26
  ccdoc_flag = NOT_A_COMMENT;
  char ch = get_char();
  while( ' ' == ch || '\t' == ch ) {
    ch = get_char();
  }
  if( '@' == ch ) {
    // Look for: '//[ \t]*@{' 'vi}'  -- brace balancing for vi, added by bzoe
    ch = get_char();
    if( '{' == ch ) {
      ccdoc_flag = STYLE2A;
    }
    else if( '-' == ch ) {
      // Issue 0086
      // Added by bzoe to support the new
      // single line, suffix syntax:
      //  int foo; //@- this is a comment
      ccdoc_flag = STYLE2C;
    }
    else {
      put_char(ch); // in case it is a '\n'
    }
  }
  else if( '/' == ch ) {
    // Look for: '//[ \t]*/**'
    // Discard: '///***'
    ch = get_char();
    if( '*' == ch ) {
      ch = get_char();
      if( '*' == ch ) {
	ch = get_char();
	if( '*' != ch ) {
	  ccdoc_flag = STYLE2B;
	  put_char(ch);
	}
	else {
	  put_char(ch);
	  put_char('*');
	  put_char('*');
	  put_char('/');
	}
      }
      else {
	put_char(ch);
	put_char('*');
	put_char('/');
      }
    }
    else {
      put_char(ch);
      put_char('/');
    }
  }
  else {
    put_char(ch);
  }

  // ================================================
  // This is not a ccdoc comment,
  // skip to the end of the line.
  // ================================================
  if(ccdoc_flag == NOT_A_COMMENT) {
    while( ch != 0 && ch != '\n' ) {
      ch = get_char();
    }
    if( '\n' == ch ) {
      put_char(ch);
    }
    return scan_token();
  }

  // ================================================
  // Get the rest of the tokens on the line.
  // Here are the comment fields.
  // ================================================
  scanner_doc doc(*this,m_sw);

  // Define the processing mode.
  static char line[65536]; // maximum line length
  unsigned lineno = m_lineno;
  bool first = true;
  bool done = false;
  while(!done) {
    // ================================================
    // Skip leading w/s
    // ================================================
    char ch = get_char();
    while(ch && ch !='\n' && (ch > 0 && ch <= ' ') ) {
      ch = get_char();
    }

    // ================================================
    // This must be either a comment or a blank line.
    // Both are valid.
    // ================================================
    if( !first ) {
      if( '/' == ch ) {
	ch = get_char();
	if( '/' != ch ) {
	  s_log.warning()
	    << "Invalid ccdoc comment specified at line "
	    << m_lineno << " in " << m_name.c_str()
	    << ".\n"
	    << s_log.enable();
	  put_char(ch);
	  put_char('/');
	  return scan_token();
	}
	// Check for end of comment.
	// Skip the lead '//'
	ch = get_char();
	
        // 'vi{' -- brace balancing for vi, added by bzoe
	// Is this '//@}'?
	if( ccdoc_flag == STYLE2A ) {
	  if( '@' == ch ) {
	    char ch1 = get_char();
            // 'vi{' -- brace balancing for vi, added by bzoe
	    if( '}' == ch1 ) {
	      while( ch1 && ch1 != '\n' )
		ch1 = get_char();
	      line[0] = 0;
	      done = true;
	      break;
	    }
	    put_char(ch1);
	  }
	}
	else if( ccdoc_flag == STYLE2B ) {
	  // Is this '//\*/'?
	  if( '*' == ch ) {
	    char ch1 = get_char();
	    if( '/' == ch1 ) {
	      while( ch1 && ch1 != '\n' )
		ch1 = get_char();
	      line[0] = 0;
	      done = true;
	      break;
	    }
	    // Skip the asterisk in this form.
	    ch = ch1;
	  }
	  // Is this '// \*/'?
	  else if( ' ' == ch || '\t' == ch ) {
	    char ch1 = get_char();
	    if( '*' == ch1 ) {
	      char ch2 = get_char();
	      if( '/' == ch2 ) {
		while( ch2 && ch2 != '\n' )
		  ch2 = get_char();
		line[0] = 0;
		done = true;
		break;
	      }
	      // Skip the asterisk and preceding space in this form.
	      ch = ch2;
	    }
	    else {
	      put_char(ch1);
	    }
	  }
	}
      }
    }

    // ================================================
    // Now load the line for directive processing.
    // Don't trim w/s, it may be needed for <pre></pre>.
    // ================================================
    char* pline = line;
    while( ch && ch != '\n' ) {
      *pline++ = ch;
      char pch = ch;
      ch = get_char();
      if( pline>&line[3] ) {
	if( ccdoc_flag == STYLE2A ) {
          // 'vi{' -- brace balancing for vi, added by bzoe
	  if( '@' == pch && ch == '}' ) {
	    // This may be the end of the comment.
	    //
	    // Make sure that any preceding directives
	    // are processed, such as:
	    //   '//@{ @return Foo bar spam //@}'
	    //
	    char* mark = pline;
	    pline--; // *pline == '@'
	    ccdoc_assert( '@' == *pline );
	    char ch1 = *--pline;
	    char ch2 = *--pline;
	    if( '/' == ch1 && '/' == ch2 ) {
	      if( pline == line ) {
		// We are done.
		done = true;
		break;
	      }

	      // 'vi{' -- brace balancing for vi, added by bzoe
	      // This was an end token '//@}',
	      // parse the other stuff on the line.
	      pline = mark;
	      pline--; // *pline == '@'
	      pline--; // *pline == '/'
	      pline--; // *pline == '/'
	      *pline = 0;
	      done = true;
	      
	      // Ignore everything to the end of the line.
	      while( ch && ch != '\n' ) {
		ch = get_char();
	      }
	      break;
	    }
	    else {
              // 'vi{' -- brace balancing for vi, added by bzoe
	      // This was not an end token '//@}',
	      // continue parsing.
	      pline = mark;
	    }
	  }
	}
	else if( ccdoc_flag == STYLE2B ) {
	  if( '*' == pch && ch == '/' ) {
	    // This may be the end of the comment.
	    //
	    // Make sure that any preceding directives
	    // are processed, such as:
	    //   '///** @return Foo bar spam //\*/'
	    //
	    char* mark = pline;
	    pline--; // *pline == '*'
	    ccdoc_assert( '*' == *pline );
	    char ch1 = *--pline;
	    char ch2 = *--pline;
	    if( '/' == ch1 && '/' == ch2 ) {
	      if( pline == line ) {
		// We are done.
		done = true;
		break;
	      }
	      
	      // This was an end token '//\*/',
	      // parse the other stuff on the line.
	      pline = mark;
	      pline--; // *pline == '*'
	      pline--; // *pline == '/'
	      pline--; // *pline == '/'
	      *pline = 0;
	      done = true;
	      
	      // Ignore everything to the end of the line.
	      while( ch && ch != '\n' ) {
		ch = get_char();
	      }
	      break;
	    }
	    else {
	      // This was not an end token '//\*/',
	      // continue parsing.
	      pline = mark;
	    }
	  }
	}
	else if( ccdoc_flag == STYLE2C ) {
          // Issue 0086
          if( '\n' == ch ) {
            done = true;
            break;
          }
        }
      }
    }
    *pline = 0;

    // ================================================
    // EOF
    // ================================================
    if( !ch ) {
      // The end of the file was reached before the
      // comment was terminated.
      s_log.warning()
	<< "Unexpected EOF found, unterminated ccdoc comment "
	<< "specified at line "
	<< m_lineno << " in " << m_name.c_str()
	<< ".\n"
	<< s_log.enable();
      return scan_token();
    }
    
    // ================================================
    // At this point we have the line.
    // Terminate it and write it out in debug mode.
    // ================================================
    if( m_debug ) {
      s_log << "CCDOC_PHASE1_DEBUG: ccdoc_line: '" << line << "'\n";
    }

    // ================================================
    // Skip the first line, if it is empty to
    // avoid conflicts when trying to determine
    // the long description for the following case:
    //   //@{           | <-- line 1 (blank - ignore)
    //   // short       | <-- line 2 (short description)
    //   //             | <-- line 3 (blank - separator)
    //   // long        | <-- line 4 (long description)
    //   //             | <-- line 5 (end of comment)
    // ================================================
    if( ccdoc_flag == STYLE2C ) { // bzoe
      // Issue 0086
      first = false;
      if (*line == 0) {
        s_log.warning()
          << "Empty single suffix comment\n"
          << s_log.enable();
      }
      if (m_debug) {
        s_log << "STYLE2C: " << line << "\n";
      }
    }
    else {
      if(first) {
        first = false;
        if(*line == 0)
          continue;
      }
      
      // ================================================
      // Issue 0082: only do this if -nojdsds is specified.
      // Set the short description flag to false
      // if a blank line (other than the first one)
      // is encountered.
      // ================================================
      if(!m_sw.jdsds() && *line == 0 && doc.m_mode == scanner_doc::SHORT) {
        doc.m_mode = scanner_doc::LONG;
        continue;
      }
    }

    doc.parse_line(line);

    // Issue 0086
    if (ccdoc_flag == STYLE2C)
      doc.m_comment.add_suffix(true);  // bzoe
  }

  // ================================================
  // At this point we have a valid ccdoc comment.
  // Format it for the parser to make things easy.
  // If it is empty, ignore it.
  // ================================================
  if( doc.empty() )
    return scan_token();
  return doc.format(token,max);
}
// ================================================================
//
// scanner doc object.
//
// ================================================================
ccdoc::phase1::scanner_doc::scanner_doc(scanner& scan,switches& sw)
  : m_mode(SHORT),
    m_scanner(scan),
    m_sw(sw)
{
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::phase1::scanner_doc::~scanner_doc()
{
}
// ================================================================
// Empty?
// ================================================================
bool ccdoc::phase1::scanner_doc::empty() const
{
  return m_comment.empty();
}
// ================================================================
// Format
// ================================================================
const char* ccdoc::phase1::scanner_doc::format(char* token,int max)
{
  char lineno[64];
  sprintf(lineno,"%d",m_scanner.get_lineno() - 1);
  vector<string> tokens;
  m_comment.add_file(m_scanner.get_file());
  m_comment.add_lineno(lineno);
  m_comment.get(tokens);
  vector<string>::reverse_iterator itr = tokens.rbegin();
  for(;itr!=tokens.rend();++itr) {
    m_scanner.put_token(*itr);
  }
  m_scanner.get_token();
  token[0] = '@';
  token[1] = '{';
  token[2] = 0;
  return token;
}
// ================================================================
// Scan a single argument.
// ================================================================
bool ccdoc::phase1::scanner_doc::scan_1arg(const char* pline,
					   string& arg,
					   const char* directive,
					   const char* argid,
					   bool report_errors)
{
  // Skip white space.
  for(;*pline!=0 && (*pline==' ' || *pline=='\t');++pline)
    ;
  if( 0 != *pline ) {
    arg = pline;
    return true;
  }

  if(report_errors) {
    s_log.warning()
      << "Ignored the "
      << directive
      << " directive because argument "
      << argid
      << " is missing at line "
      << m_scanner.get_lineno()-1
      << " in "
      << m_scanner.get_file()
      << ".\n"
      << s_log.enable();
  }
  return false;
}
// ================================================================
// Parse the ccdoc directive with 2 arguments, like @param
// and @exception.
// ================================================================
bool ccdoc::phase1::scanner_doc::scan_2args(char* pstr,
					    string& arg1,
					    string& arg2,
					    const char* directive,
					    const char* arg1id,
					    const char* arg2id,
					    bool arg2_required)
{
  // Make sure that the user specified both arguments.

  // Skip white space.
  for(;*pstr!=0 && (*pstr==' ' || *pstr=='\t');++pstr);
  if( 0 == *pstr ) {
    s_log.warning()
      << "Ignored the "
      << directive
      << " directive because argument "
      << arg1id
      << " is missing at line "
      << m_scanner.get_lineno()-1
      << " in "
      << m_scanner.get_file()
      << ".\n\tThe correct specification is "
      << directive
      << " "
      << arg1id
      << " "
      << arg2id
      << ".\n"
      << s_log.enable();
    return false;
  }
  char* arg1_beg = pstr;

  // Skip to next w/s unless the user specified an HTML
  // directive explicitly, this is determined by looking
  // at the first character. If it is a '<', pass everything
  // back as one argument.
  if( '<' == *pstr ) {
    for(;*pstr!=0;++pstr);
  }
  else {
    for(;*pstr!=0 && *pstr!=' ' && *pstr!='\t';++pstr);
  }
  if( 0 == *pstr ) {
    if( arg2_required ) {
      s_log.warning()
	<< "Ignored the "
	<< directive
	<< " directive because argument "
	<< arg2id
	<< " is missing at line "
	<< m_scanner.get_lineno()-1
	<< " in "
	<< m_scanner.get_file()
	<< ".\n\tThe correct specification is "
	<< directive
	<< " "
	<< arg1id
	<< " "
	<< arg2id
	<< ".\n"
	<< s_log.enable();
      return false;
    }
    char* arg1_end = pstr;
    arg1 = arg1_beg;
    arg2 = "";
    return true;
  }
  char* arg1_end = pstr;

  // Skip white space.
  for(;*pstr!=0 && (*pstr==' ' || *pstr=='\t');++pstr);
  if( 0 == *pstr) {
    // Trailing w/s but no name.
    // Assume that there is no arg.
    arg1 = arg1_beg;
    arg2 = "";
    return true;
  }
  char* arg2_beg = pstr;

  // Trailing white space is not ignored.
  *arg1_end = 0;
  arg1 = arg1_beg;
  arg2 = arg2_beg;
  return true;
}
// ================================================================
// Is this a directive?
// Derived from code submitted by bzoe 10/18/01.
// ================================================================
bool ccdoc::phase1::scanner_doc::is_directive(const char* pstr1,
					      const char* pstr2,
					      bool null_term) const
{
  for(;*pstr1;++pstr1,++pstr2) {
    if( *pstr1 != *pstr2 ) {
      return false;
    }
  }
  if( *pstr2 == ' ' || *pstr2 == '\t' )
    return true;
  // Accept a terminating NULL?
  if( null_term && *pstr2 == 0 )
    return true;
  return false;
}
// ================================================================
// Parse line.
// ================================================================
void ccdoc::phase1::scanner_doc::parse_line(char* line)
{
  // ================================================
  // Check for ccdoc directives.
  // Skip the leading whitespace to look for a '@'
  // character.
  // ================================================
  string pkgdoctid; // Used for the @pkgdoctid directive.
  char* pline = line;
  for(;*pline && (*pline==' ' || *pline=='\t');++pline)
    ;
  if('@' == pline[0]) {
    // ================================================
    // ccdoc directive: '@@'
    // ================================================
    if('@' == pline[1]) {
      // '@@' directive.
      // Convert < -> &lt;
      // Convert > -> &gt;
      // Convert & -> &amp;
      const char* src = &pline[2];
      static char line1[65536]; // maximum line length
      char* dst = line1;
      for(;*src;++src) {
	if( '<' == *src ) {
	  *dst++ = '&';
	  *dst++ = 'l';
	  *dst++ = 't';
	  *dst++ = ';';
	}
	else if( '>' == *src ) {
	  *dst++ = '&';
	  *dst++ = 'g';
	  *dst++ = 't';
	  *dst++ = ';';
	}
	else if( '&' == *src ) {
	  *dst++ = '&';
	  *dst++ = 'a';
	  *dst++ = 'm';
	  *dst++ = 'p';
	  *dst++ = ';';
	}
	else {
	  *dst++ = *src;
	}
      }
      *dst = 0;
      add_line(line1);
    }
    // ================================================
    // ccdoc directives: '@{' or '@}'
    // ================================================
    else if('{' == pline[1] || '}' == pline[1]) {
      // Silently ignored, it is used the '//' processing.
    }
    // ================================================
    // ccdoc directive: '@$' - same as @link
    // ================================================
    else if('$' == pline[1] &&
	    ( ' ' == pline[2] || '\t' == pline[2] ) ) {
      pline += 2;
      string arg1;
      string arg2;
      if(scan_2args(pline,arg1,arg2,"@$","<entity>","<name>",false)) {
	if( arg2.size() == 0 )
	  arg2 = arg1;
	add_line("@link");
	add_line(arg1.c_str());
	add_line(arg2.c_str());
      }
    }
    // ================================================
    // ccdoc directive: '@author'
    // ================================================
    else if(is_directive("author",&pline[1])) {
      pline+=7;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg;
      if( scan_1arg(pline,arg,"@author","<name>") )
	m_comment.add_author(arg);
    }
    // ================================================
    // ccdoc directive: '@deprecated'
    // ================================================
    else if(is_directive("deprecated",&pline[1],true)) {
      pline += 11;
      m_mode = DEPRECATED;
      string arg;
      if( scan_1arg(pline,arg,"@deprecated","<description>",false) )
	m_comment.add_deprecated(arg);
    }
    // ================================================
    // ccdoc directive: '@exception'
    // ================================================
    else if(is_directive("exception",&pline[1])) {
      pline += 10;
      m_mode = EXCEPTION;
      string arg1;
      string arg2;
      if(scan_2args(pline,arg1,arg2,"@exception","<name>","<desc>",false)) {
	if(arg2.size()) {
	  m_comment.add_new_exception(arg1,arg2);
	}
	else {
	  m_comment.add_new_exception(arg1);
	}
      }
    }
    // ================================================
    // ccdoc directive: '@link'
    // ================================================
    else if(is_directive("link",&pline[1])) {
      pline += 5;
      string arg1;
      string arg2;
      if(scan_2args(pline,arg1,arg2,"@link","<entity>","<name>",false)) {
	if( arg2.size() == 0 )
	  arg2 = arg1;
	add_line("@link");
	add_line(arg1.c_str());
	add_line(arg2.c_str());
      }
    }
    // ================================================
    // ccdoc directive: '@param'
    // ================================================
    else if(is_directive("param",&pline[1])) {
      pline += 6;
      m_mode = PARAM;
      string arg1;
      string arg2;
      if(scan_2args(pline,arg1,arg2,"@param","<name>","<desc>",false)) {
	if(arg2.size()) {
	  m_comment.add_new_param(arg1,arg2);
	}
	else {
	  m_comment.add_new_param(arg1);
	}
      }
    }
    // ================================================
    // ccdoc directive: '@pkg'
    // ================================================
    else if(is_directive("pkg",&pline[1])) {
      pline += 4;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg;
      if( scan_1arg(pline,arg,"@pkg","<name>") ) {
	// Break the argument down into '.' separated
	// package entries.
	if( m_comment.get_pkg().size() ) {
	  report_multiply_defined_error("@pkg");
	}
	else if( m_comment.get_pkgdoc().size() ) {
	  // pkg and pkgdoc cannot simultaneously exist
	  // in the same ccdoc comment.
	  s_log.warning()
	    << "Illegal declaration, @pkgdoc and @pkg are "
	    << "mutually exclusive, @pkg will be ignored at line "
	    << m_scanner.get_lineno()-1
	    << " in "
	    << m_scanner.get_file()
	    << ".\n"
	    << s_log.enable();
	}
	else {
	  vector<string> vec;
	  parse_pkg_path(arg,"@pkg",vec);
	  vector<string>::iterator i = vec.begin();
	  for(;i!=vec.end();++i) {
	    m_comment.add_pkg(*i);
	  }
	}
      }
    }
    // ================================================
    // ccdoc directive: '@pkgdoc'
    // ================================================
    else if(is_directive("pkgdoc",&pline[1])) {
      pline += 7;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg1;
      string arg2;
      if( scan_2args(pline,arg1,arg2,"@pkgdoc","<name>","[<url>]",false) ) {
	// Break the argument down into '.' separated
	// package entries.
	if( m_comment.get_pkgdoc().size() ) {
	  report_multiply_defined_error("@pkgdoc");
	}
	else if( m_comment.get_pkg().size() ) {
	  // pkg and pkgdoc cannot simultaneously exist
	  // in the same ccdoc comment.
	  s_log.warning()
	    << "Illegal declaration, @pkgdoc and @pkg are "
	    << "mutually exclusive, @pkgdoc will be ignored at line "
	    << m_scanner.get_lineno()-1
	    << " in "
	    << m_scanner.get_file()
	    << ".\n"
	    << s_log.enable();
	}
	else if( arg1.size() ) {
	  // If the user specified a URL for the @pkgdoc directive,
	  // insert it first so that it is easy to find.
	  if( arg2.size() ) {
	    // Issue 0025
	    // There is a special form of the @pkgdoc directive
	    // that allows the user to specify their own URL
	    // for the package page. This must be used very carefully
	    // because the links to child entities may be lost.
	    string urlid = "@url";
	    m_comment.add_pkgdoc( urlid );
	    m_comment.add_pkgdoc( arg2 );
	  }
	  vector<string> vec;
	  parse_pkg_path(arg1,"@pkgdoc",vec);
	  vector<string>::iterator i = vec.begin();
	  for(;i!=vec.end();++i) {
	    m_comment.add_pkgdoc(*i);
	  }
	}
      }
    }
    // ================================================
    // ccdoc directive: '@pkgdoctid'
    // This must appear after an @pkgdoc directive.
    // ================================================
    else if(is_directive("pkgdoctid",&pline[1])) {
      pline += 10;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg;
      if( scan_1arg(pline,arg,"@pkgdoctid","<name>") ) {
	if( pkgdoctid.size() ) {
	  report_multiply_defined_error("@pkgdoctid");
	}
	else if( m_comment.get_pkgdoc().size() == 0 ) {
	  // @pkgdoc must be specified first.
	  s_log.warning()
	    << "Illegal declaration, @pkgdoc must be specified before "
	    << "@pkgdoctid at line "
	    << m_scanner.get_lineno()-1
	    << " in "
	    << m_scanner.get_file()
	    << ".\n"
	    << s_log.enable();
	}
	else {
	  pkgdoctid = arg;
	  m_comment.add_pkgdoc_tid(arg);
	}
      }
    }
    // ================================================
    // ccdoc directive: '@return'
    // ================================================
    else if(is_directive("return",&pline[1],true)) {
      pline += 7;
      m_mode = RETURNS;
      string arg;
      if( scan_1arg(pline,arg,"@returns","<description>",false) )
	m_comment.add_returns(arg);
    }
    // ================================================
    // ccdoc directive: '@returns'
    // ================================================
    else if(is_directive("returns",&pline[1],true)) {
      pline += 8;
      m_mode = RETURNS;
      string arg;
      if( scan_1arg(pline,arg,"@returns","<description>",false) )
	m_comment.add_returns(arg);
    }
    // ================================================
    // ccdoc directive: '@see'
    // ================================================
    else if(is_directive("see",&pline[1])) {
      pline += 4;
      m_mode = SEE;
      string arg1;
      string arg2;
      if(scan_2args(pline,arg1,arg2,"@see","<entity>","[<number>]",false)) {
	// Make sure that arg2 is numeric
	if( arg2.size() ) {
	  if( arg2 != "*" ) {
	    // Make sure that the argument is numeric
	    // unless the user specified the special
	    // case of '*' which means all.
	    const char* p = arg2.c_str();
	    for(;*p;++p) {
	      if( *p < '0' || *p > '9' ) {
		s_log.warning()
		  << "Invalid number "
		  << arg2.c_str()
		  << " in @see directive at line "
		  << m_scanner.get_lineno()-1
		  << " in "
		  << m_scanner.get_file()
		  << " was ignored.\n"
		  << s_log.enable();
		arg2 = "0";
		break;
	      }
	    }
	  }
	}
	else {
	  // The user did not specify an index, set the default
	  // value of zero.
	  arg2 = "0";
	}
	m_comment.add_new_see(arg1,arg2);
      }
    }
    // ================================================
    // ccdoc directive: '@source'
    // ================================================
    else if(is_directive("source",&pline[1])) {
      pline += 7;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg;
      if( scan_1arg(pline,arg,"@source","<file>") ) {
	if( m_comment.get_source().size() ) {
	  report_multiply_defined_error("@source");
	}
	else {
	  m_comment.add_source(arg);
	}
      }
    }
    // ================================================
    // ccdoc directive: '@since'
    // ================================================
    else if(is_directive("since",&pline[1])) {
      pline += 6;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg;
      if( scan_1arg(pline,arg,"@since","<version>") ) {
	if( m_comment.get_source().size() ) {
	  report_multiply_defined_error("@since");
	}
	else {
	  m_comment.add_since(arg);
	}
      }
    }
    // ================================================
    // ccdoc directive: '@suffix'
    // ================================================
    else if(is_directive("suffix",&pline[1],true)) {
      pline += 7;
      m_comment.add_suffix(true); 
    }
    // ================================================
    // Issue 0084.
    // ccdoc directive: '@throws'
    // Derived from code submitted by bzoe 10/18/01.
    // ================================================
    else if(is_directive("throws",&pline[1])) {
      pline += 7;
      m_mode = EXCEPTION;
      string arg1;
      string arg2;
      if(scan_2args(pline,arg1,arg2,"@throws","<name>","<desc>",false)) {
	if(arg2.size()) {
	  m_comment.add_new_exception(arg1,arg2);
	}
	else {
	  m_comment.add_new_exception(arg1);
	}
      }
    }
    // ================================================
    // ccdoc directive: '@version'
    // ================================================
    else if(is_directive("version",&pline[1])) {
      pline += 8;
      if( m_mode == SHORT ) m_mode = LONG;
      string arg;
      if( scan_1arg(pline,arg,"@version","<id>") ) {
	if( m_comment.get_version().size() ) {
	  report_multiply_defined_error("@version");
	}
	else {
	  m_comment.add_version(arg);
	}
      }
    }
    else {
      // ================================================
      // Although this line looks like a ccdoc directive,
      // it isn't. Add it the the current mode collection.
      // ================================================
      add_line(line);
    }
  }
  else {
    parse_comment_line(line);
  }
}
// ================================================================
// Issue 0090:
// This line is not ccdoc directive, add it the the current mode
// collection unless it has embedded {@link ... } directives.
// ================================================================
void ccdoc::phase1::scanner_doc::parse_comment_line(char* line)
{
  vector<string> new_lines;
  const char* ps = line;
  const char* pend = ps;
  for(;*ps;++ps) {
    if( '{' == *ps &&
	'@' == ps[1] &&
	'l' == ps[2] &&
	'i' == ps[3] &&
	'n' == ps[4] &&
	'k' == ps[5] &&
	( ' ' == ps[6] || '\t' == ps[6] ) ) {
      // It looks we have an embedded link, now scan ahead to
      // the terminating right brace.
      unsigned num_ws = 1;
      const char* pbeg = &ps[1]; // point to '@link'
      const char* ps1 = pbeg;
      size_t len2 = 0;
      for(;*ps1 && *ps1 != '{' && *ps1 != '}' ;++ps1,++len2) {
	if( *ps1 == ' ' || *ps1 == '\t' )
	  ++num_ws;
      }
      if( *ps1 == '}' && num_ws>1 ) {
	// We definitely have an embedded link.
	// Create a new line from the end of the previous link
	// and the beginning of this one.
	string line;
	
	// Figure out the length of the first string.
	size_t len1 = 0;
	for(const char* ps2=pend;ps2!=ps;++ps2,++len1)
	  ;
	if( len1 ) {
	  // Special case handling to work around
	  // paragraph recognition.
	  if ( len1 == 1 && *pend == ' ' )
	    line = "  ";
	  else
	    line.assign(pend,len1);
	  new_lines.push_back(line); // prefix text
	}
	line.assign(pbeg,len2); // trailing w/s is handled later
	new_lines.push_back(line); // @link directive
	
	// Add in the new @link line.
	len1 = 0;
	pend = ps1+1; // point just past the trailing right brace
	ps = ps1; // The for() increment will fix this.
      }
    }
  }
  if( new_lines.size() ) {
    // Get the trailing characters:
    //  * These {@link foo::bar xx} are the trailing characters.
    if( pend != ps ) {
      string line;
      size_t len1 = 0;
      for(const char* ps2=pend;ps2!=ps;++ps2,++len1)
	;
      if( len1 ) {
	// Special case handling to work around
	// paragraph recognition.
	if ( len1 == 1 && *pend == ' ' )
	  line = "  ";
	else
	  line.assign(pend,len1);
	new_lines.push_back(line); // suffix text
      }
    }
    static char s_line[65536]; // maximum line length
    vector<string>::iterator itr = new_lines.begin();
    for(;itr!=new_lines.end();++itr) {
      strcpy(s_line,itr->c_str());
      parse_line(s_line);
    }
  }
  else {
    add_line(line);
  }
}
// ================================================================
// Add line.
// ================================================================
void ccdoc::phase1::scanner_doc::add_line(const char* line)
{
  // If a line is NULL, it means that the user
  // wants a paragraph separator. It is stored
  // as a space.
  string desc;
  if( !line || *line == 0 ) {
    desc = " "; // This is where blank lines are defined.
  }
  else {
    desc = line;
    if( m_sw.jdsds() && m_mode == SHORT ) {
      // Issue 0089: Extract short comments the javadoc way.
      size_t sz = 0;
      for(const char* p=line;*p;++p,++sz) {
	if( *p == '.' ) {
	  // In javadoc, a short description is terminated by
	  // a period that is followed by a SPACE, TAB, NEWLINE.
	  ++p;
	  if( *p == 0 || // same as new line
	      *p == ' ' ||
	      *p == '\t' || 
	      *p == '\r' ||
	      *p == '\n' ) {
	    ++sz; // include the period.
	    string short_desc;
	    short_desc.assign(desc,0,sz);
	    m_comment.add_short_desc(short_desc);
	    if( *p ) {
	      // Handle this case:
	      //  Short description. Start of long description.
	      size_t first = sz+1;
	      for(sz=0;*p;++p) sz++;
	      string long_desc;
	      long_desc.assign(desc,first,sz);
	      if( long_desc.size() ) {
		m_comment.add_long_desc(long_desc);
	      }
	    }
	    m_mode = LONG;
	    return;
	  }
	}
      }
    }
  }
  switch(m_mode) {
  case DEPRECATED: m_comment.add_deprecated     (desc); break;
  case EXCEPTION:  m_comment.add_exception_desc (desc); break;
  case LONG:       m_comment.add_long_desc      (desc); break;
  case PARAM:      m_comment.add_param_desc     (desc); break;
  case RETURNS:    m_comment.add_returns        (desc); break;
  case SHORT:      m_comment.add_short_desc     (desc); break;
  case SEE:        m_comment.add_see_desc       (desc); break;
  }
}
// ================================================================
// Add line.
// ================================================================
void ccdoc::phase1::scanner_doc::add_line(const char* line,
					  vector<string>& vec)
{
  // This is where we handle embedded blank lines.
  if(*line)
    vec.push_back(line);
  else
    vec.push_back(" ");
}
// ================================================================
// Report multiply defined entities.
// ================================================================
void ccdoc::phase1::scanner_doc::report_multiply_defined_error(const char* directive) const
{
  s_log.warning()
    << "Directive " << directive << " is multiply defined at line "
    << m_scanner.get_lineno()-1
    << " in "
    << m_scanner.get_file()
    << ", the first definition will be used.\n"
    << s_log.enable();
}
// ================================================================
// Parse a pkg or pkgdoc path.
// ================================================================
void ccdoc::phase1::scanner_doc::parse_pkg_path(string& arg,
						const char* directive,
						vector<string>& vec)
{
  // Look for '::' and '.' separators.
  // The following are legal:
  //   A.B.C
  //   A::B::C
  //   A::B.C   <-- not recommended
  static char token[65536];
  ::strcpy(token,arg.c_str());
  vec.clear();
  char* b = token;
  char* e = b;
  for(;*e;++e) {
    if( ':' == *e ) {
      ++e;
      if( ':' == *e ) {
	--e; // back up to the first colon.
	*e = 0; // set the end of the previous token
	++e; // point to the start of the next token
	if(!parse_pkg_path_entry(b,directive)) {
	  vec.clear();
	  return;
	}
	vec.push_back(b);
	*e = '.';
	b = e+1;
      }
    }
    else if( '.' == *e ) {
      *e = 0;
      if(!parse_pkg_path_entry(b,directive)) {
	vec.clear();
	return;
      }
      vec.push_back(b);
      *e = '.';
      b = e+1;
    }
  }
  if(!parse_pkg_path_entry(b,directive)) {
    vec.clear();
    return;
  }
  vec.push_back(b);
}
// ================================================================
// Parse a pkg or pkgdoc path.
// ================================================================
bool ccdoc::phase1::scanner_doc::parse_pkg_path_entry(char* token,
						      const char* directive)
{
  // Trim off trailing w/s.
  char* ptr = token;
  for(;*ptr;++ptr); // First get to the end of the string.
  for(ptr--;ptr>token;--ptr) {
    if( ' ' != *ptr &&
	'\t' != *ptr &&
	'\n' != *ptr ) {
      ptr++;
      *ptr = 0;
      break;
    }
  }
  if(*token == 0) {
    // We found a zero length entry.
    // Report it as a warning and ignore it.
    s_log.warning()
      << "Illegal zero length subpkg name found in the "
      << directive
      << " directive at line "
      << m_scanner.get_lineno()-1
      << " in "
      << m_scanner.get_file()
      << ", the directive was ignored.\n"
      << s_log.enable();
    return false;
  }
  return true;
}

