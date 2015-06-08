package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.InfoTextArea;
import id3.gui.functionpanel.FunctionPanel;
import id3.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jaudiotagger.tag.FieldKey;

import java.awt.Rectangle;

public class SwapFieldsPanel extends FunctionPanel
{
	private static final String INFO_TEXT = "These operations are applied to all tracks:\r\n\r\n"
			+ "Swap Fields: Exchanges two like-fields.\r\nCopy Fields: Copies one like-field to another,\r\n"
			+ "leaving the copy from field untouched.\r\n\r\nLike-fields are fields that take the same input\r\n"
			+ "(i.e. year and disc count only takes numbers)";

	private String[] FIELDS;
	
	private JCheckBox chckSwap;
	private JComboBox comboFieldOne;
	private JComboBox comboFieldTwo;
	private JCheckBox chckCopy;
	private JComboBox comboCopy;
	private JComboBox comboReplace;
	
	/** Creates a new {@code SwapFieldsPanel}
	 * @see FunctionPanel
	 */
	public SwapFieldsPanel()
	{
		FIELDS = Utils.getFieldDisplay(); //Get all valid iTunes fields
		
		JLabel lblFieldOne = new JLabel("Field One");
		lblFieldOne.setEnabled(false);
		lblFieldOne.setBounds(16, 37, 123, 14);
		this.add(lblFieldOne);
		
		JLabel lblFieldTwo = new JLabel("Field Two");
		lblFieldTwo.setEnabled(false);
		lblFieldTwo.setBounds(178, 37, 123, 14);
		this.add(lblFieldTwo);
		
		comboFieldOne = new JComboBox(FIELDS);
		comboFieldOne.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int index = comboFieldOne.getSelectedIndex();
				if(index > 0)
				{
					lblFieldTwo.setEnabled(true);
					comboFieldTwo.setEnabled(true);
					if(index < 8) // TODO
					{
						comboFieldTwo.setModel(new DefaultComboBoxModel(Utils.FIELDS_STRINGS));
					}
					else
					{
						comboFieldTwo.setModel(new DefaultComboBoxModel(Utils.FIELDS_INTS));
					}
				}
				else
				{
					comboFieldTwo.setEnabled(false);
				}
			}
		});
		comboFieldOne.setEnabled(false);
		comboFieldOne.setBounds(16, 62, 123, 20);
		this.add(comboFieldOne);
		
		comboFieldTwo = new JComboBox(FIELDS);
		comboFieldTwo.setEnabled(false);
		comboFieldTwo.setBounds(178, 62, 123, 20);
		this.add(comboFieldTwo);
		
		chckSwap = new JCheckBox("Swap Fields");
		chckSwap.setToolTipText("Swaps ID3 tag fields. NOTE: Not all fields are compatible with each other (i.e. year field only accepts numbers)");
		chckSwap.setBounds(6, 7, 97, 23);
		this.add(chckSwap);
		
		chckCopy = new JCheckBox("Copy Fields");
		chckCopy.setToolTipText("Copies one ID3 field to another. NOTE: Not all fields are compatible with each other (i.e. year field only accepts numbers)");
		chckCopy.setBounds(6, 118, 97, 23);
		this.add(chckCopy);
		
		JLabel lblCopy = new JLabel("Copy Field");
		lblCopy.setEnabled(false);
		lblCopy.setBounds(16, 148, 123, 14);
		this.add(lblCopy);
		
		JLabel lblReplace = new JLabel("Replacement Field");
		lblReplace.setEnabled(false);
		lblReplace.setBounds(178, 148, 123, 14);
		this.add(lblReplace);
		
		comboCopy = new JComboBox(FIELDS);
		comboCopy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int index = comboCopy.getSelectedIndex();
				if(index > 0)
				{
					lblReplace.setEnabled(true);
					comboReplace.setEnabled(true);
					if(index < 8) // TODO
					{
						comboReplace.setModel(new DefaultComboBoxModel(Utils.FIELDS_STRINGS));
					}
					else
					{
						comboReplace.setModel(new DefaultComboBoxModel(Utils.FIELDS_INTS));
					}
				}
				else
				{
					comboReplace.setEnabled(false);
				}	
			}
		});
		comboCopy.setEnabled(false);
		comboCopy.setBounds(16, 173, 123, 20);
		this.add(comboCopy);
		
		comboReplace = new JComboBox(FIELDS);
		comboReplace.setEnabled(false);
		comboReplace.setBounds(178, 173, 123, 20);
		this.add(comboReplace);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(10, 204, 291, 125));
		this.add(infoTextArea);
		
		chckSwap.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				chckCopy.setEnabled(!chckSwap.isSelected());
				lblFieldOne.setEnabled(chckSwap.isSelected());
				comboFieldOne.setEnabled(chckSwap.isSelected());
				if(!chckSwap.isSelected())
				{
					lblFieldTwo.setEnabled(false);
					comboFieldTwo.setEnabled(false);
				}
			}
		});
		
		chckCopy.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				chckSwap.setEnabled(!chckCopy.isSelected());
				lblCopy.setEnabled(chckCopy.isSelected());
				comboCopy.setEnabled(chckCopy.isSelected());
				if(!chckCopy.isSelected())
				{
					lblReplace.setEnabled(false);
					comboReplace.setEnabled(false);
				}
			}
		});
	}
	
	private boolean isCopyFields()
	{
		return chckCopy.isSelected();
	}
	
	private FieldKey getFieldOneField()
	{
		return Utils.getFieldKeyFromString((String) comboFieldOne.getSelectedItem());
	}
	
	private FieldKey getFieldTwoField()
	{
		return Utils.getFieldKeyFromString((String) comboFieldTwo.getSelectedItem());
	}
	
	private boolean isSwapFields()
	{
		return chckSwap.isSelected();
	}
	
	private FieldKey getCopyField()
	{
		return Utils.getFieldKeyFromString((String) comboCopy.getSelectedItem());
	}
	
	private FieldKey getReplacementField()
	{
		return Utils.getFieldKeyFromString((String) comboReplace.getSelectedItem());
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		if(isCopyFields())
		{
			Functions.copyTags(entry, getCopyField(), getReplacementField());
		}
		else if(isSwapFields())
		{
			Functions.swapTags(entry, getFieldOneField(), getFieldTwoField());
		}
	}

	@Override
	public boolean checkForErrors()
	{
		if(!isCopyFields() && !isSwapFields())
		{
			return true;
		}
		else if(isCopyFields())
		{
			if(getCopyField() == null || getReplacementField() == null)
			{
				return true;
			}
		}
		else if(isSwapFields())
		{
			if(getFieldOneField() == null || getFieldTwoField() == null)
			{
				return true;
			}
		}
		return false;
	}
	
}
