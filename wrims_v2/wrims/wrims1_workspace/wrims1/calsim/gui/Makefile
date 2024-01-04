# Makefile to update the catalogs for dss files
# get all files under current directory

JAVA_FILES = DefaultFrame.java MPanel.java GuiUtils.java DTSTable.java MTSTable.java MonthlyTableDisplay.java  DTSMenuListener.java MTSMenuListener.java ListDTSListener.java RetrieveDTSListener.java DeleteDTSListener.java ListMTSListener.java RetrieveMTSListener.java DeleteMTSListener.java TextDisplay.java GeneralRetrievePanel.java MainMenuBar.java MessagePanel.java NodeArcMenuBar.java MainPanel.java CalsimGui.java GuiTaskListener.java DefaultActionListener.java TabbedPane.java StudyTab.java GeneralPanel.java OptionPanel.java ResultPanel.java ReportPane.java DTSComparator.java MTSComparator.java InputDataUI.java MTab.java InputPanel.java LookupDataUI.java LookupUI.java WsiDiTable.java MultiStudyRunner.java MSRGui.java CalsimTree.java GeneralTreeModel.java DtsTreeModel.java DtsTreePanel.java 
#
PACKAGE_LOC=calsim/gui
# local first then common
include ../local.makefile
include ../common.makefile

input:	
	$(JAVA) -classpath $(CLASSPATH_DEF) calsim.gui.InputFrame	
main: 
	$(JAVA) -classpath $(CLASSPATH_DEF) calsim.gui.CalsimGui










