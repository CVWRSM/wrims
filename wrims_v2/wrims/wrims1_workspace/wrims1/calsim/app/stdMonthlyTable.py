# Open DSS file and filter by specified string
def space(number):
 from java.lang import StringBuffer
 buf = StringBuffer(number)
 for i in range(number):
   buf.append(' ')
 return buf.toString()
 #
def monthlyTable(file,search,twstring):
 # Create Data Reference from Inputs
 g = opendss(file)
 from java.lang import System
 lineSeparator = System.getProperty('line.separator' ) # '\r\n' # '\n' on unix
 desiredSpaceCount=6
 g.filterBy(search)
 # Set time window and create a new ref with this window
 tw = timeWindow(twstring)
 ref = DataReference.create(g[0],tw)
 
 pref = PeriodAverageProxy(ref,DSSUtil.getTimeFactory().createTimeInterval('1month'))
 dsi = pref.getData().getIterator()
 tm = DSSUtil.getTimeFactory().getTimeInstance();
 tmf = DSSUtil.getTimeFactory().getTimeFormatInstance().create("MMM yyyy");
 tmf1 = DSSUtil.getTimeFactory().getTimeFormatInstance().create("MMM");
 tmf2 = DSSUtil.getTimeFactory().getTimeFormatInstance().create("yyyy");
 from java.lang import Math
 from java.lang import Float, Math
 from java.util import Date
 sys.add_package('java.text')
 from java.text import NumberFormat
 nf = NumberFormat.getInstance(); nf.setGroupingUsed(0); 
 nf.setMinimumFractionDigits(1)
 nf.setMinimumIntegerDigits(1)
 nf.setMaximumFractionDigits(1)
 fp = FieldPosition(nf.INTEGER_FIELD)
 lines = []
 lines.append('STUDY: ' + ref.getPathname().getPart(Pathname.F_PART) + space(10) + 'FILE: ' + file + space(10) + Date().toString())
 lines.append('')
 lines.append('Searched: ' + search)
 lines.append('Units: ' + ref.getData().getAttributes().getYUnits() )
 lines.append('Project:  ' + ref.getPathname().toString())
 lines.append('')
 desiredSpace = space(desiredSpaceCount+nf.getMinimumIntegerDigits()+nf.getMinimumFractionDigits() -3) 
 startSpace = desiredSpace + space(4)
 lines.append('YEAR'+
          desiredSpace + 'OCT' +
          desiredSpace + 'NOV' + 
          desiredSpace + 'DEC' + 
          desiredSpace + 'JAN' + 
          desiredSpace + 'FEB' + 
          desiredSpace + 'MAR' + 
          desiredSpace + 'APR' + 
          desiredSpace + 'MAY' + 
          desiredSpace + 'JUN' + 
          desiredSpace + 'JUL' + 
          desiredSpace + 'AUG' + 
          desiredSpace + 'SEP' + 
          space(desiredSpaceCount-3) + 'TOTAL')
 cl = ''
 gotOct = 0
 yrsum = 0.0
 allyrtot = 0.0
 yravg = 0.0
 totavg = 0.0
 totmin = Float.MAX_VALUE
 totmax= Float.MIN_VALUE
 nsum = 0
 nyears=0
 while not dsi.atEnd() :    
   e = dsi.getElement()
   date = tm.create(Math.round(e.getX())).toString()
   mon = date[2:5]
   if mon == 'OCT' : 
       if nsum > 0 : 
           yravg = yrsum
           allyrtot = allyrtot+yrsum
           nyears = nyears+1
           totmin = Math.min(totmin,yrsum)
           totmax = Math.max(totmax,yrsum)
       if cl != '' : 
           strbuf = StringBuffer(10)
           nf.format(yravg,strbuf,fp)
           cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
           lines.append(cl)
       #
       yrsum = 0.0; nsum = 0
       # water year 
       cl = repr(int(tm.create(Math.round(e.getX())).format(tmf2))+1)
       gotOct = 1
   #
   if gotOct :
       if ( e.getY() == -901 ) :
           strbuf = StringBuffer(10)
           nf.format(yravg,strbuf,fp)
           cl  = cl + space(desiredSpaceCount) + \
                 space(nf.getMinimumIntegerDigits()+nf.getMinimumIntegerDigits())
       else :
           strbuf = StringBuffer(10)
           nf.format(e.getY(),strbuf,fp)
           cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
           yrsum = yrsum + e.getY();
           nsum = nsum+1
       #print tm.create(Math.round(e.getX())).format(tmf), e.getY()
   #
   dsi.advance()
 #
 # Get average, min, max for each month
 totavg = allyrtot/nyears
 minV = Float.MAX_VALUE
 maxV = Float.MIN_VALUE
 monavg = {'OCT':0.0,'NOV':0.0,'DEC':0.0,'JAN':0.0,'FEB':0.0,'MAR':0.0,'APR':0.0,'MAY':0.0,'JUN':0.0,'JUL':0.0,'AUG':0.0,'SEP':0.0}
 monmin = {'OCT':minV,'NOV':minV,'DEC':minV,'JAN':minV,'FEB':minV,'MAR':minV,'APR':minV,'MAY':minV,'JUN':minV,'JUL':minV,'AUG':minV,'SEP':minV}
 monmax = {'OCT':maxV,'NOV':maxV,'DEC':maxV,'JAN':maxV,'FEB':maxV,'MAR':maxV,'APR':maxV,'MAY':maxV,'JUN':maxV,'JUL':maxV,'AUG':maxV,'SEP':maxV}
 numavg = {'OCT':0,'NOV':0,'DEC':0,'JAN':0,'FEB':0,'MAR':0,'APR':0,'MAY':0,'JUN':0,'JUL':0,'AUG':0,'SEP':0}
 gotOct=0
 dsi.resetIterator()
 while not dsi.atEnd() :
     e = dsi.getElement()
     date = tm.create(Math.round(e.getX())).toString()
     mon = date[2:5]
     if mon == 'OCT' : 
         gotOct = 1
     if gotOct :
         if ( e.getY() == -901 ) :
             pass
         else :
             monmin[mon] = Math.min(monmin[mon],e.getY())
             monmax[mon] = Math.max(monmax[mon],e.getY())
             monavg[mon] = monavg[mon]+e.getY()
             numavg[mon] = numavg[mon]+1
         #
     #
     dsi.advance()
 #
 lines.append(' ')
 cl = 'AVG:'
 for val in monavg.keys() :
     monavg[val] = monavg[val]/numavg[val]
     strbuf = StringBuffer(10)
     nf.format(monavg[val],strbuf,fp)
     cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
 #
 strbuf = StringBuffer(10)
 nf.format(totavg,strbuf,fp)
 cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
 lines.append(cl)
 #
 cl = 'MIN:'
 for val in monmin.keys() :
     strbuf = StringBuffer(10)
     nf.format(monmin[val],strbuf,fp)
     cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
 #
 strbuf = StringBuffer(10)
 nf.format(totmin,strbuf,fp)
 cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
 lines.append(cl)
 #
 cl = 'MAX:'
 for val in monmax.keys() :
     strbuf = StringBuffer(10)
     nf.format(monmax[val],strbuf,fp)
     cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
 #
 strbuf = StringBuffer(10)
 nf.format(totmax,strbuf,fp)
 cl = cl + space(desiredSpaceCount-fp.getEndIndex()) + strbuf.toString()
 lines.append(cl)
 #
 #
 #f=open(search + '.txt','w')
# for line in lines :
#     f.write(line + lineSeparator)
#     print line
 #
# f.close()
 return lines
