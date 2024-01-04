package gov.ca.dwr.hecdssvue.monthly;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import rma.swing.*;

/**
 * Title:        General CWMS Project
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      RMA
 * @author Mark Ackerman
 *
 */

public class RmaModJTableFillDialog extends RmaJDialog
{
	private boolean _canceled;

	private RmaJRadioButton _linearFillCB;
	private RmaJRadioButton _repeatFillCB;
	private RmaJRadioButton _constantFillCB;
	private RmaJRadioButton _factorFillCB;
	private RmaJDecimalField _constantTxt;
	private RmaJDecimalField _factorTxt;
	private ButtonCmdPanel _cmdPanel;

	private MonthlyTable _table;

    private RmaModJTableFillDialog(Frame frame)
    {
		super(frame, true);
		buildControls();
    }
	private RmaModJTableFillDialog(Dialog dialog)
	{
		super(dialog, true);
		buildControls();
	}

	public static RmaModJTableFillDialog getFillDialog(MonthlyTable table)
	{
		Window w = SwingUtilities.windowForComponent(table);
		RmaModJTableFillDialog dialog;
		if ( w instanceof Dialog)
		{
			dialog =  new RmaModJTableFillDialog((Dialog)w);
		}
		else
		{
			dialog =  new RmaModJTableFillDialog((Frame)w);
		}
		dialog._table  = table;
		return dialog;
	}

	protected void buildControls()
	{
		getContentPane().setLayout(new GridBagLayout());
		setTitle("Table Fill Options");

		_linearFillCB = new RmaJRadioButton("Linear Fill",true);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		//gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_linearFillCB, gbc);

		_repeatFillCB = new RmaJRadioButton("Repeat Fill");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		//gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_repeatFillCB, gbc);

		_constantFillCB = new RmaJRadioButton("Add Constant:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		//gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_constantFillCB, gbc);

		_constantTxt = new RmaJDecimalField();
		_constantTxt.setEnabled(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_constantTxt, gbc);


		_factorFillCB = new RmaJRadioButton("Multiple Factor:");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		//gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_factorFillCB, gbc);

		_factorTxt = new RmaJDecimalField();
		_factorTxt.setEnabled(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_factorTxt, gbc);

		_cmdPanel = new ButtonCmdPanel(ButtonCmdPanel.OK_CANCEL_BUTTONS);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		//gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.insets(5,5,0,5);
		getContentPane().add(_cmdPanel, gbc);

		_cmdPanel.addCmdPanelListener(new ButtonCmdPanelListener()
		{
			public void buttonCmdActionPerformed(ActionEvent e)
			{
				switch (e.getID())
				{
					case ButtonCmdPanel.OK_BUTTON:
						fillTable();
						_canceled = false;
						setVisible(false);
					break;
					case ButtonCmdPanel.CANCEL_BUTTON:
						_canceled = true;
						setVisible(false);
					break;
				}
			}
		});

		_constantFillCB.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent ie)
			{
				_constantTxt.setEnabled(ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		_factorFillCB.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent ie)
			{
				_factorTxt.setEnabled(ie.getStateChange() == ItemEvent.SELECTED);
			}
		});
		ButtonGroup bg = new ButtonGroup();
		bg.add(_linearFillCB);
		bg.add(_repeatFillCB);
		bg.add(_constantFillCB);
		bg.add(_factorFillCB);

		registerEnterKey(true);
		registerEscapeKey(true);
		pack();
		setSize(getPreferredSize());
		setLocationRelativeTo(_table);
		setResizable(false);
	}

	public boolean isCanceled()
	{
		return _canceled;
	}

	protected boolean fillTable()
	{

		if ( _linearFillCB.isSelected())
		{
			_table.linearFill();
		}
		else if ( _repeatFillCB.isSelected())
		{
			_table.repeatFill();
		}
		else if ( _constantFillCB.isSelected() )
		{
			if ( _constantTxt.getText().length() == 0 )
			{
				JOptionPane.showMessageDialog(this, "Please enter a constant to add to the selected cells.",
					"No Constant Entered", JOptionPane.ERROR_MESSAGE);
				_constantTxt.requestFocus();
				return false;
			}
			_table.constantFill(_constantTxt.getValue());
		}
		else if ( _factorFillCB.isSelected())
		{
			if ( _factorTxt.getText().length() == 0 )
			{
				JOptionPane.showMessageDialog(this, "Please enter a Factor to multiple the selected cells by.",
					"No Factor Entered", JOptionPane.ERROR_MESSAGE);
				_factorTxt.requestFocus();
				return false;
			}
			_table.factorFill(_factorTxt.getValue());
		}
		return true;
	}
}