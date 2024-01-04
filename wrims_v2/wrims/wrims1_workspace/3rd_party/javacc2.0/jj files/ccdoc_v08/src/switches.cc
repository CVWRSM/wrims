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
#include "switches.h"
#include <fstream>
#include <cstdio>

// ================================================================
// This static variable allows the header version
// to be queried at runtime.
// ================================================================
static char ccdoc_rcsid[] = "$Id: switches.cc,v 1.48 2001/11/28 22:03:30 Administrator Exp $";

// ================================================================
// Constructor.
// ================================================================
ccdoc::switches::switches(int argc,char** argv) {
  ccdoc_assert( argc );
  ccdoc_assert( argv != 0 );

  m_ok = true;
  m_help = false;
  m_program_name = argv[0];
  m_verbose = false;
  m_verbose_format = true;
  m_index = false;
  m_default_root = "@root";
  m_root = m_default_root;
  m_rootfile = "";
  m_dospaths = false;
  m_jdsds = true; // Issue 0082

  m_rptcsd = true; // Issue 0026
  m_rptcsi = 4; // Issue 0044
  m_rptdpa = false; // Issue 0028
  m_rptdpd = false; // Issue 0030
  m_rptdpv = false; // Issue 0029
  m_rptfwcf = false; // Issue 0045
  m_rpthpc = true; // Issue 0026
  m_rptim = true;
  m_rptmac = false;
  m_rptpri = false;
  m_rptpro = false;
  m_rptpub = true;
  m_rptsci = true; // Issue 0072
  m_rpttyp = true;
  m_rptun = true;

  string rptctcs = "iso-8859-1";

  m_rptdefa = "unascribed"; // Issue 0063
  m_rptdefasd = "automatically generated"; // Issue 0063
  m_rptdefsd = "undocumented"; // Issue 0063
  m_rptdefv = "unknown"; // Issue 0063
  m_rptctcs = rptctcs; // Issue 0074

  m_maxpathlen = 128;
  m_cdsm = true;
  m_version = "ccdoc v0.8 r26 2001/11/28 ";
  m_version += CCDOC_CID;
  bool report_args = false;
  bool version_flag = false;

  // Insert the definition for __ccdoc__
  {
    string key = "__ccdoc__";
    string value = "1";
    m_defines.insert( make_pair(key,value) );
  }

  // Process the switches.
  for(int i=0;i<argc;++i) {
    if(argv[i]) {
      string str = argv[i];
      m_switches.push_back(str);

      // ================================================
      // Help.
      // ================================================
      if( str == "-h" || str == "-help" ) {
	// Help was specified, report
	// the usage and exit.
	help();
	m_ok = false;
	break;
      }

      // ================================================
      // Simple flag switches.
      // ================================================
      if( str == "-args"       ) {report_args      = true;  continue;}
      if( str == "-cdsm"       ) {m_cdsm           = true;  continue;}
      if( str == "-cid"        ) {version_flag     = true;  continue;}
      if( str == "-dospaths"   ) {m_dospaths       = true;  continue;}
      if( str == "-jdsds"      ) {m_jdsds          = true;  continue;}
      if( str == "-index"      ) {m_index          = true;  continue;}
      if( str == "-macros"     ) {m_rptmac         = true;  continue;}
      if( str == "-nocdsm"     ) {m_cdsm           = false; continue;}
      if( str == "-nocout"     ) {s_log.remove(&cout); continue;}
      if( str == "-nojdsds"    ) {m_jdsds          = false;  continue;}
      if( str == "-nomacros"   ) {m_rptmac         = false; continue;}
      if( str == "-noprivate"  ) {m_rptpri         = false; continue;}
      if( str == "-noprotected") {m_rptpro         = false; continue;}
      if( str == "-nopublic"   ) {m_rptpub         = false; continue;}
      if( str == "-norptcsd"   ) {m_rptcsd         = false; continue;}
      if( str == "-norptcsi"   ) {m_rptcsi         = 4;     continue;}
      if( str == "-norptdpa"   ) {m_rptdpa         = false; continue;}
      if( str == "-norptdpd"   ) {m_rptdpd         = false; continue;}
      if( str == "-norptdpv"   ) {m_rptdpv         = false; continue;}
      if( str == "-norptfwcf"  ) {m_rptfwcf        = false; continue;}
      if( str == "-norpthpc"   ) {m_rpthpc         = false; continue;}
      if( str == "-norptim"    ) {m_rptim          = false; continue;}
      if( str == "-norptmac"   ) {m_rptmac         = false; continue;}
      if( str == "-norptpri"   ) {m_rptpri         = false; continue;}
      if( str == "-norptpro"   ) {m_rptpro         = false; continue;}
      if( str == "-norptpub"   ) {m_rptpub         = false; continue;}
      if( str == "-norptsci"   ) {m_rptsci         = false; continue;}
      if( str == "-norpttyp"   ) {m_rpttyp         = false; continue;}
      if( str == "-norptun"    ) {m_rptun          = false; continue;}
      if( str == "-notypedefs" ) {m_rpttyp         = false; continue;}
      if( str == "-nounions"   ) {m_rptun          = false; continue;}
      if( str == "-nov"        ) {m_verbose        = false; continue;}
      if( str == "-novf"       ) {m_verbose_format = false; continue;}
      if( str == "-nowarn"     ) {s_log.disable_warnings(); continue;}
      if( str == "-private"    ) {m_rptpri         = true;  continue;}
      if( str == "-protected"  ) {m_rptpro         = true;  continue;}
      if( str == "-public"     ) {m_rptpub         = true;  continue;}
      if( str == "-rptcsd"     ) {m_rptcsd         = true;  continue;}
      if( str == "-rptdpa"     ) {m_rptdpa         = true;  continue;}
      if( str == "-rptdpd"     ) {m_rptdpd         = true;  continue;}
      if( str == "-rptdpv"     ) {m_rptdpv         = true;  continue;}
      if( str == "-rptfwcf"    ) {m_rptfwcf        = true;  continue;}
      if( str == "-rpthpc"     ) {m_rpthpc         = true;  continue;}
      if( str == "-rptim"      ) {m_rptim          = true;  continue;}
      if( str == "-rptmac"     ) {m_rptmac         = true;  continue;}
      if( str == "-rptpri"     ) {m_rptpri         = true;  continue;}
      if( str == "-rptpro"     ) {m_rptpro         = true;  continue;}
      if( str == "-rptpub"     ) {m_rptpub         = true;  continue;}
      if( str == "-rptsci"     ) {m_rptsci         = true;  continue;}
      if( str == "-rpttyp"     ) {m_rpttyp         = true;  continue;}
      if( str == "-rptun"      ) {m_rptun          = true;  continue;}
      if( str == "-typedefs"   ) {m_rpttyp         = true;  continue;}
      if( str == "-unions"     ) {m_rptun          = true;  continue;}
      if( str == "-v"          ) {m_verbose        = true;  continue;}
      if( str == "-version"    ) {version_flag     = true;  continue;}
      if( str == "-vf"         ) {m_verbose_format = true; continue;}
      if( str == "-warn"       ) {s_log.enable_warnings(); continue;}

      // ================================================
      // Value switches.
      // ================================================
      if( get_arg("-bg"        ,str,i,argc,argv) ) {m_bgcolor      = str; continue;}
      if( get_arg("-db"        ,str,i,argc,argv) ) {m_db           = str; continue;}
      if( get_arg("-fg"        ,str,i,argc,argv) ) {m_fgtextcolor  = str; continue;}
      if( get_arg("-fglink"    ,str,i,argc,argv) ) {m_fglinkcolor  = str; continue;}
      if( get_arg("-fgtext"    ,str,i,argc,argv) ) {m_fgtextcolor  = str; continue;}
      if( get_arg("-fgvlink"   ,str,i,argc,argv) ) {m_fgvlinkcolor = str; continue;}
      if( get_arg("-files"     ,str,i,argc,argv) ) {load_files(str); continue;}
      if( get_arg("-header"    ,str,i,argc,argv) ) {m_header       = str; continue;}
      if( get_arg("-htm"       ,str,i,argc,argv) ) {m_html         = str; continue;}
      if( get_arg("-html"      ,str,i,argc,argv) ) {m_html         = str; continue;}
      if( get_arg("-imageurl"  ,str,i,argc,argv) ) {m_imgurl       = str; continue;}
      if( get_arg("-imgurl"    ,str,i,argc,argv) ) {m_imgurl       = str; continue;}
      if( get_arg("-log"       ,str,i,argc,argv) ) {s_log.insert(str); continue;}
      if( get_arg("-meta"      ,str,i,argc,argv) ) {m_meta         = str; continue;}
      if( get_arg("-pkg"       ,str,i,argc,argv) ) {m_pkg          = str; continue;}
      if( get_arg("-root"      ,str,i,argc,argv) ) {m_root         = str; continue;}
      if( get_arg("-rootfile"  ,str,i,argc,argv) ) {m_rootfile     = str; continue;}
      if( get_arg("-rootpurl"  ,str,i,argc,argv) ) {m_rooturl      = str; continue;}
      if( get_arg("-rooturl"   ,str,i,argc,argv) ) {m_rooturl      = str; continue;}
      if( get_arg("-rptctcs"   ,str,i,argc,argv) ) {m_rptctcs      = str; continue;}
      if( get_arg("-rptdefa"   ,str,i,argc,argv) ) {m_rptdefa      = str; continue;}
      if( get_arg("-rptdefasd" ,str,i,argc,argv) ) {m_rptdefasd    = str; continue;}
      if( get_arg("-rptdefsd"  ,str,i,argc,argv) ) {m_rptdefsd     = str; continue;}
      if( get_arg("-rptdefv"   ,str,i,argc,argv) ) {m_rptdefv      = str; continue;}
      if( get_arg("-sourceurl" ,str,i,argc,argv) ) {m_srcurl       = str; continue;}
      if( get_arg("-srcurl"    ,str,i,argc,argv) ) {m_srcurl       = str; continue;}
      if( get_arg("-trailer"   ,str,i,argc,argv) ) {m_trailer      = str; continue;}

      if( get_arg("-maxpathlen",str,i,argc,argv) ) {
	m_maxpathlen = atoi( str.c_str() );
	continue;
      }
      if( get_arg("-rptcsi",str,i,argc,argv) ) {
	m_rptcsi = atoi( str.c_str() );
	continue;
      }
      if( get_arg("-putenv",str,i,argc,argv) ) {
	putenv(str);
	continue;
      }
      if( get_arg("-ctf",str,i,argc,argv) ) {
	s_log.warning()
	  << "The -ctf switch has been deprecated, use -db instead.\n"
	  << s_log.enable();
	m_db = str;
	continue;
      }
      if( str[0] == '-' && str[1] == 'D' ) {
	string errstr = str;
	str.erase(0,2);
	//s_log << "DEBUG: -D '" << str << "'\n";
	// Did the user did specify a value?
	string::size_type pos = str.find('=');
	string value = "1";
	if( pos < str.size() ) {
	  value = str;
	  value.erase(0,pos+1);
	  str.erase(pos,str.size());
	}
	//s_log << "DEBUG: -D key='" << str << "' val='" << value << "'\n";
	if( str.size() == 0 ) {
	  s_log.warning()
	    << "Illegal NULL key argument to '"
	    << errstr
	    << "' was ignored.\n"
	    << s_log.enable();
	}
	m_defines.insert( make_pair(str,value) );
	continue;
      }
      if( str[0] == '-' && str[1] == 'U' ) {
	string errstr = str;
	str.erase(0,2);
	if( str.size() == 0 ) {
	  s_log.warning()
	    << "Illegal NULL argument to '"
	    << errstr
	    << "' was ignored.\n"
	    << s_log.enable();
	}
	m_undefines.insert(str);
	//s_log << "DEBUG: -U '" << str << "'\n";
	continue;
      }

      // ================================================
      // Files
      // ================================================
      if(i) {
	load_file(str);
      }
    }
  }

  if( report_args || m_verbose ) {
    for(int i=0;i<argc;++i) {
      if(i)
	s_log << " ";
      s_log << argv[i];
    }
    s_log << "\n";
  }
  if( version_flag ) {
    s_log << m_version << "\n";
    exit(0);
  }

  // Make sure the -db switch was specified
  // (if help wasn't specified).
  if( ok() ) {
    // Make sure that the db exists.
    if( m_db.size() == 0 ) {
      m_ok = false;
      if( version_flag == false ) {
	s_log << "\n";
	s_log << "ERROR: Ccdoc database (-db <file>) not specified.\n";
	s_log << "       Type ccdoc -h for more information.\n";
	s_log << "\n";
      }
    }
  }
  // Make sure that the user didn't specify -meta and -rptctcs at
  // the same time.
  if( ok() ) {
    if( m_meta.size() && m_rptctcs != rptctcs ) {
      s_log.warning()
	<< "The -meta and -rptctcs switches are mutually exclusive.\n"
	<< "\tThe -rptctcs switch will be ignored.\n"
	<< s_log.enable();
    }
  }
}
// ================================================================
// Destructor.
// ================================================================
ccdoc::switches::~switches() {
}
// ================================================================
// Get arg.
// ================================================================
bool ccdoc::switches::get_arg(const char* sw,
			      string& str,
			      int& i,
			      int argc,
			      char** argv)
{
  if( str == sw ) {
    if( ++i >= argc ) {
      s_log << "ERROR: Missing argument for switch '" 
		   << argv[i-1]
		   << "'\n";
      m_ok = false;
      return false;
    }
    str = argv[i];
    return true;
  }
  return false;
}
// ================================================================
// Load a file.
// ================================================================
void ccdoc::switches::load_file(const string& file)
{
  if( file[0] == '-' ) {
    // The user might have specified an invalid
    // switch, warn them but then add it as a file.
    s_log.warning()
      << "Switch '"
      << file
      << "' is treated as a file.\n"
      << s_log.enable();
  }
  // This is assumed to be a file.
  ifstream is(file.c_str());
  if( !is ) {
#if defined(_MSC_VER)
    // Issue 0007
    // This only works for the MSVC version of the program.
    // Under DOS, this error may have occurred because the
    // user specified a wildcard. Try to convert the file to a list.
    string tmp;
    string cmd;
    int st;
    tmp = "ccdoc_msdos.tmp";
    cmd = "DIR /B /S /O:N " + file + " > " + tmp + "\n";
    if( verbose() ) {
      s_log << cmd << "\n";
    }
    st = system(cmd.c_str());
    if( verbose() ) {
      s_log << "status = " << st << "\n";
    }
    if( st == 0 ) {
      load_files(tmp);
      cmd = "DEL " + tmp + "\n";
      system(cmd.c_str());
      return;
    }
#endif
    s_log.warning()
      << "File '"
      << file
      << "' cannot be read so it will be ignored.\n"
      << s_log.enable();
  }
  else {
    // Only add files that can be read.
    m_files.push_back( file );
  }
}
// ================================================================
// Load a file that contains a list of files.
// ================================================================
void ccdoc::switches::load_files(const string& file)
{
  // The user specified a list of files,
  // open it and load the files in the internal m_files
  // vector.
  ifstream is(file.c_str());
  if( is.fail() || is.bad() ) {
    s_log.warning()
      << "Can't read -files " << file.c_str() << " so it will be ignored.\n"
      << s_log.enable();
  }
  else {
    static char nbuf[65536];
    while( is.getline(nbuf,sizeof(nbuf)) ) {
      if( *nbuf > 32 && *nbuf < 128) {
	if( verbose() ) {
	  s_log << "loading file " << nbuf << " ...\n";
	}
	m_files.push_back( nbuf );
      }
    }
  }
}
// ================================================================
// Put env
// ================================================================
void ccdoc::switches::putenv( string& str ) const
{
  // We need to keep a permanent copy of the environment
  // variables to be safe on different platforms.
  const int MAX_ENVS = 32;
  static unsigned s_putenv_idx = 0;
  static char s_putenv[MAX_ENVS][4096];
  ccdoc_assert( s_putenv_idx < MAX_ENVS );
  ccdoc_assert( str.size() < 4096 );
  char* putenv_string = s_putenv[ s_putenv_idx ];
  strcpy( putenv_string, str.c_str() );
  ::putenv( putenv_string );
  s_putenv_idx++;
}
// ================================================================
// Load the defines map.
// ================================================================
void ccdoc::switches::defines( defines_type& out ) const
{
  for( defines_type::const_iterator i=m_defines.begin();i!=m_defines.end();++i) {
    out.insert( make_pair((*i).first,(*i).second) );
  }
}
// ================================================================
// Load the undefines set.
// ================================================================
void ccdoc::switches::undefines( undefines_type& out ) const
{
  for( undefines_type::const_iterator i=m_undefines.begin();i!=m_undefines.end();++i) {
    out.insert( *i );
  }
}
