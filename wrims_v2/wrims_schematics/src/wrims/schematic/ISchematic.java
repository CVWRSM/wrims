package wrims.schematic;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;

public interface ISchematic {

	int TAF = 100;
	int CFS = 200;

	void disableMonthItems();

	void disableLongtermItems();

	void setUnitsButtons(int units);

	int getSelectedUnits();

	JComboBox getDateBox();

	Vector<String> getTimeWindows();

	void updateDSSMenu(String filename, boolean wasAdded);

	void resetValueNodeNames();

	void setMonthlyProgress(int value);

	void setLongtermProgress(int value);

	void updateValues(String date);

	void updateDateBoxTWs(boolean b, boolean c);

	void resetProgressBars();

	void setProgressVisibility(boolean b);

	Hashtable<String, Object> getAllVariables();

	Vector<String> getSelectedNames();

	void clearValues();

}
