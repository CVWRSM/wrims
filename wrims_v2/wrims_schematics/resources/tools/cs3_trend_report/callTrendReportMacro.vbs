Option Explicit

'On Error Resume Next

RunExcelMacro

Sub RunExcelMacro() 
	
	Dim n
	Dim dir
	Dim excelFilePath 
	'Dim filePathCopy
	Dim macroName
	Dim reportSheetName
	Dim inputSheetName
	Dim xlApp 
	Dim xlBook
	Dim dss1
	Dim dss2
	Dim dss3
	Dim dss4
	Dim study1
	Dim study2
	Dim study3	
	Dim study4
	Dim active1
	Dim active2
	Dim active3
	Dim active4
	Dim startDate
	Dim stopDate
	Dim dssApart
	Dim dssFpart
	
	'n = CInt(Wscript.Arguments.Item(0)) 
	
	excelFilePath    = Wscript.Arguments.Item(0)   
	
	startDate  = Wscript.Arguments.Item(1) 
	stopDate = Wscript.Arguments.Item(2)
	dssApart   = Wscript.Arguments.Item(3)
	dssFpart  = Wscript.Arguments.Item(4)
	
	active1 = Wscript.Arguments.Item(5)
	active2 = Wscript.Arguments.Item(6)
	active3 = Wscript.Arguments.Item(7)
	active4 = Wscript.Arguments.Item(8)
		
	study1 = Wscript.Arguments.Item(9)
	dss1   = Wscript.Arguments.Item(10) 
	study2 = Wscript.Arguments.Item(11)
    dss2   = Wscript.Arguments.Item(12)
	study3 = Wscript.Arguments.Item(13)
	dss3   = Wscript.Arguments.Item(14)
	study4 = Wscript.Arguments.Item(15)
	dss4   = Wscript.Arguments.Item(16)		
REM ==================================================
	'filePath = dir & "\Trend_Reporting_Ver3.0.xlsb"
	'filePathCopy = dir & "\report\TrendReport.xlsb"
	macroName = "Control.cmdImportAll_Click"
	reportSheetName = "Report - ALL"
	inputSheetName = "Control"
REM ==================================================
	
	Set xlApp = CreateObject("Excel.Application") 
	xlApp.Visible = False
	xlApp.DisplayAlerts = False
	Set xlBook = xlApp.Workbooks.Open(excelFilePath, 0, False) 
	xlBook.Sheets(inputSheetName).Activate
	
	xlApp.Cells( 2, 7).Value = dssApart
	xlApp.Cells( 4, 7).Value = dssFpart
	xlApp.Cells(20, 7).Value = startDate
	xlApp.Cells(22, 7).Value = stopDate
	
	xlBook.Sheets("Control").OLEObjects("chkImportA").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportB").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportC").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportD").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportE").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportF").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportG").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkImportH").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartA").Object.Value = True
	xlBook.Sheets("Control").OLEObjects("chkCHartB").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartC").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartD").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartE").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartF").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartG").Object.Value = False
	xlBook.Sheets("Control").OLEObjects("chkCHartH").Object.Value = False	
	
	If ( StrComp(active2,"true",1 ) = 0 ) then
	    xlApp.Cells(10,  2).Value = study2
		xlApp.Cells(10,  7).Value = dss2 
		xlBook.Sheets("Control").OLEObjects("chkImportB").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartB").Object.Value = True
		xlApp.Run "Control.chkChartB_Click"
	else
	    xlApp.Cells(10,  2).Value = "study2"
		xlApp.Cells(10,  7).Value = " "
		xlBook.Sheets("Control").OLEObjects("chkImportB").Object.Value = False	
		xlBook.Sheets("Control").OLEObjects("chkCHartB").Object.Value = False
	End If
	
	If ( StrComp(active3,"true",1 ) = 0 ) then	
	    xlApp.Cells(11,  2).Value = study3
		xlApp.Cells(11,  7).Value = dss3 
		xlBook.Sheets("Control").OLEObjects("chkImportC").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartC").Object.Value = True
		xlApp.Run "Control.chkChartC_Click"
	else
		xlApp.Cells(11,  2).Value = "study3"
		xlApp.Cells(11,  7).Value = " " 
		xlBook.Sheets("Control").OLEObjects("chkImportC").Object.Value = False	
		xlBook.Sheets("Control").OLEObjects("chkCHartC").Object.Value = False
	End If
	
	If ( StrComp(active4,"true",1 ) = 0 ) then	
	    xlApp.Cells(12,  2).Value = study4
		xlApp.Cells(12,  7).Value = dss4 
		xlBook.Sheets("Control").OLEObjects("chkImportD").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartD").Object.Value = True
		xlApp.Run "Control.chkChartD_Click"		
	else
		xlApp.Cells(12,  2).Value = "study4"
		xlApp.Cells(12,  7).Value = " " 
		xlBook.Sheets("Control").OLEObjects("chkImportD").Object.Value = False	
		xlBook.Sheets("Control").OLEObjects("chkCHartD").Object.Value = False
	End If

	If ( StrComp(active1,"true",1 ) = 0 ) then
	    xlApp.Cells(9,  2).Value = study1
		xlApp.Cells(9,  7).Value = dss1 
		xlBook.Sheets("Control").OLEObjects("chkImportA").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartA").Object.Value = True
		xlApp.Run "Control.chkChartA_Click"
	else
		xlApp.Cells(9,  2).Value = "study1"
		xlApp.Cells(9,  7).Value = " " 
		xlBook.Sheets("Control").OLEObjects("chkImportA").Object.Value = False	
		xlBook.Sheets("Control").OLEObjects("chkCHartA").Object.Value = False
	End If
	
	xlApp.Run macroName
	xlBook.Sheets(reportSheetName).Activate
	'xlBook.SaveAs filePathCopy
	xlBook.Save
	'xlBook.Saved = True
	xlBook.Close
	xlApp.Quit 
	
	Set xlBook = Nothing 
	Set xlApp = Nothing 
	
	CreateObject("WScript.Shell").Run "excel.exe " & excelFilePath
	
End Sub 