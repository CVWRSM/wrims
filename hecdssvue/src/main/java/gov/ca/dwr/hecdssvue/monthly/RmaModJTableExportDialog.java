package gov.ca.dwr.hecdssvue.monthly;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import rma.swing.ButtonCmdPanel;
import rma.swing.ButtonCmdPanelListener;
import rma.swing.RmaInsets;
import rma.swing.RmaJDialog;
import rma.swing.RmaJCheckBox;
import rma.swing.RmaJComboBox;
import rma.swing.RmaJTable;
import rma.swing.RmaJTextField;
import rma.swing.table.TableExportOptions;
import rma.swing.IExportOptions;

import java.util.prefs.Preferences;

/**
 *  Title: General CWMS Project Description: Copyright: Copyright (c) 2000
 *  Company: RMA
 *
 *@author     Mark Ackerman
 *@created    October 10, 2001
 */

public class RmaModJTableExportDialog extends RmaJDialog implements IExportOptions
{

	private boolean _canceled;

	private RmaJComboBox _delimiterCombo;
	private RmaJCheckBox _fixedWidthColumnsCB;
	private RmaJCheckBox _quotedStringsCB;
	private RmaJCheckBox _columnHeadersCB;
	private RmaJCheckBox _rowHeadersCB;
	private RmaJCheckBox _gridLinesCB;
	private RmaJCheckBox _titleCB;
	private RmaJTextField _titleField;
	private ButtonCmdPanel _cmdPanel;

	private MonthlyTable _table;


	/**
	 *  Constructor for the RmaJTableExportDialog object
	 *
	 *@param  frame  Description
	 */
	public RmaModJTableExportDialog(Frame frame)
	{
		super(frame, true);
		buildControls();
		if (frame != null)
		{
			_titleField.setText(frame.getTitle());
		}
	}

	/**
	 *  Constructor for the RmaJTableExportDialog object
	 *
	 *@param  dialog  Description
	 */
	public RmaModJTableExportDialog(Dialog dialog)
	{
		super(dialog, true);
		buildControls();
		if (dialog != null)
		{
			_titleField.setText(dialog.getTitle());
		}
        doPreferences(false);
	}

    public void setRowHeadersEndabled(boolean b) {
        _rowHeadersCB.setEnabled(b);
    }

	/**
	 *  Gets the ExportDialog attribute of the RmaJTableExportDialog class
	 *
	 *@param  table  Description
	 *@return        The ExportDialog value
	 */
	public static RmaModJTableExportDialog getExportDialog(MonthlyTable table)
	{
		Window w = SwingUtilities.windowForComponent(table);
		RmaModJTableExportDialog dialog;
		if (w instanceof Dialog)
		{
			dialog = new RmaModJTableExportDialog((Dialog) w);
		}
		else
		{
			dialog = new RmaModJTableExportDialog((Frame) w);
		}
		dialog._table = table;
		dialog._rowHeadersCB.setEnabled(dialog._table.getRowHeaderEnabled());
		return dialog;
	}

	/**
	 *  Method Description
	 */
	protected void buildControls()
	{
		getContentPane().setLayout(new GridBagLayout());
		setTitle("Table Export Options");
		JLabel label = new JLabel("Field Delimiter:", JLabel.RIGHT);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		//gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(7, 5, 0, 5);
		getContentPane().add(label, gbc);

		_delimiterCombo = new RmaJComboBox(DELIMITERS);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_delimiterCombo, gbc);

		_fixedWidthColumnsCB = new RmaJCheckBox("Fixed Width Columns");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_fixedWidthColumnsCB, gbc);

		_quotedStringsCB = new RmaJCheckBox("Quoted Strings");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_quotedStringsCB, gbc);

		_columnHeadersCB = new RmaJCheckBox("Include Column Headers",true);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_columnHeadersCB, gbc);

		_rowHeadersCB = new RmaJCheckBox("Include Row Headers");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_rowHeadersCB, gbc);

		_gridLinesCB = new RmaJCheckBox("Print GridLines");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_gridLinesCB, gbc);

		JPanel panel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(0, 0, 0, 0);
		getContentPane().add(panel, gbc);

		_titleCB = new RmaJCheckBox("Print Title:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		panel.add(_titleCB, gbc);

		_titleField = new RmaJTextField(17);
		_titleField.setEnabled(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		panel.add(_titleField, gbc);

		_cmdPanel = new ButtonCmdPanel(ButtonCmdPanel.OK_CANCEL_BUTTONS);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5, 5, 0, 5);
		getContentPane().add(_cmdPanel, gbc);

		_cmdPanel.addCmdPanelListener(
			new ButtonCmdPanelListener()
			{
				public void buttonCmdActionPerformed(ActionEvent e)
				{
					switch (e.getID())
					{
						case ButtonCmdPanel.OK_BUTTON:
							_canceled = false;
                            doPreferences(true);
							setVisible(false);
							break;
						case ButtonCmdPanel.CANCEL_BUTTON:
							_canceled = true;
							setVisible(false);
							break;
					}
				}
			});

		_titleCB.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					_titleField.setEnabled(_titleCB.isSelected());
				}
			});
		_gridLinesCB.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					boolean selected = _gridLinesCB.isSelected();
					_delimiterCombo.setEnabled(!selected);
					_fixedWidthColumnsCB.setEnabled(!selected);
					if ( selected )
					{
						_fixedWidthColumnsCB.setSelected(true);
					}
				}
			});

		registerEnterKey(true);
		registerEscapeKey(true);
		pack();
		setSize(getPreferredSize());
		setResizable(false);
		setLocationRelativeTo(_table);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {_canceled = true;}
        });
        doPreferences(false);
	}


	/**
	 *  Gets the Canceled attribute of the RmaJTableExportDialog object
	 *
	 *@return    The Canceled value
	 */
	public boolean isCanceled()
	{
		return _canceled;
	}

	/**
	 *  Gets the ExportOptions attribute of the RmaJTableExportDialog object
	 *
	 *@return    The ExportOptions value
	 */
	public TableExportOptions getExportOptions()
	{
		TableExportOptions teo = new TableExportOptions();
		teo.delimiter = DELIMITER_CHARS[_delimiterCombo.getSelectedIndex()];
		teo.fixedWidthCols = _fixedWidthColumnsCB.isSelected();
		teo.quotedStrings = _quotedStringsCB.isSelected();
		teo.columnHeader = _columnHeadersCB.isSelected();
		teo.rowHeader = _rowHeadersCB.isSelected();
		teo.gridLines = _gridLinesCB.isSelected();
		if (_titleCB.isSelected())
		{
			teo.title = _titleField.getText();
		}
		return teo;
	}

    protected void doPreferences(boolean set) {
        Preferences preferences = Preferences.userNodeForPackage(getClass());
        if (set) {
            preferences.putInt("Delimiter", _delimiterCombo.getSelectedIndex());
            preferences.putBoolean("FixedWidthColumns",
                                   _fixedWidthColumnsCB.isSelected());
            preferences.putBoolean("QuotedStrings", _quotedStringsCB.isSelected());
            preferences.putBoolean("ColumnHeaders", _columnHeadersCB.isSelected());
            preferences.putBoolean("RowHeaders", _rowHeadersCB.isSelected());
            preferences.putBoolean("PrintGridLines", _gridLinesCB.isSelected());
            preferences.putBoolean("PrintTitle", _titleCB.isSelected());
        } else {
            _delimiterCombo.setSelectedIndex(preferences.getInt("Delimiter", 0));
            _fixedWidthColumnsCB.setSelected(preferences.getBoolean(
                    "FixedWidthColumns", false));
            _quotedStringsCB.setSelected(preferences.getBoolean("QuotedStrings", false));
            _columnHeadersCB.setSelected(preferences.getBoolean("ColumnHeaders", false));
            _rowHeadersCB.setSelected(preferences.getBoolean("RowHeaders", false));
            _gridLinesCB.setSelected(preferences.getBoolean("PrintGridLines", false));
            _titleCB.setSelected(preferences.getBoolean("PrintTitle", false));
        }
    }

    public void setExportOptions(TableExportOptions teo)
    {
        for(int i = 0; i < DELIMITER_CHARS.length; i++) {
            if(teo.delimiter == DELIMITER_CHARS[i]) {
                _delimiterCombo.setSelectedIndex(i);
                break;
            }
        }
        _fixedWidthColumnsCB.setSelected(teo.fixedWidthCols);
        _quotedStringsCB.setSelected(teo.quotedStrings);
        _columnHeadersCB.setSelected(teo.columnHeader);
        _rowHeadersCB.setSelected(teo.rowHeader);
        _gridLinesCB.setSelected(teo.gridLines);
        boolean selected = (teo.gridLines);
        _delimiterCombo.setEnabled(!selected);
        _fixedWidthColumnsCB.setEnabled(!selected);
        if ( selected )
        {
            _fixedWidthColumnsCB.setSelected(true);
        }

        if (teo.title == null)
        {
            _titleCB.setSelected(false);
            _titleField.setEnabled(false);
            _titleField.setText("");
        }
        else
        {
            _titleField.setText(teo.title);
            _titleField.setEnabled(true);
            _titleCB.setSelected(true);
        }
    }
}


