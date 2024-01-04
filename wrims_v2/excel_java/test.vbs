Option Explicit

'On Error Resume Next

RunExcelMacro

Sub RunExcelMacro() 
	
	Dim n
	Dim dir
	Dim filePath 
	Dim filePathCopy
	Dim macroName
	Dim activeSheetName
	Dim xlApp 
	Dim xlBook 
	
	n = CInt(Wscript.Arguments.Item(0)) 
	dir = Wscript.Arguments.Item(1)   
	filePath = dir & "\Trend_Reporting_Ver3.0.xlsb"
	filePathCopy = dir & "\report\TrendReport.xlsb"
	macroName = "Control.cmdImportAll_Click"
	activeSheetName = "Report - ALL"
	
	
	Set xlApp = CreateObject("Excel.Application") 
	Set xlBook = xlApp.Workbooks.Open(filePath, 0, False) 
	
	REM xlApp.Run "ThisWorkbook.Workbook_Open"
	

	If n > 1 then
		xlBook.Sheets("Control").OLEObjects("chkImportB").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartB").Object.Value = True
		xlApp.Run "Control.chkChartB_Click"
	End If
	
	If n > 2 then
		xlBook.Sheets("Control").OLEObjects("chkImportC").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartC").Object.Value = True
		xlApp.Run "Control.chkChartC_Click"
	End If
	
	If n > 3 then
		xlBook.Sheets("Control").OLEObjects("chkImportD").Object.Value = True
		xlBook.Sheets("Control").OLEObjects("chkCHartD").Object.Value = True
		xlApp.Run "Control.chkChartD_Click"		
	End If
	
	
	xlApp.Run macroName
	xlBook.Sheets(activeSheetName).Activate
	xlBook.SaveAs filePathCopy
	xlBook.Close
	xlApp.Quit 
	
	Set xlBook = Nothing 
	Set xlApp = Nothing 
	
	REM MsgBox(filePath)
	
	CreateObject("WScript.Shell").Run "excel.exe " & filePathCopy
	
End Sub 