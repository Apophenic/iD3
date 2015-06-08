package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.InfoTextArea;
import id3.gui.functionpanel.FunctionPanel;
import id3.utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import org.jaudiotagger.tag.FieldKey;

import java.awt.Rectangle;

public class AppendPrependPanel extends FunctionPanel 
{
	public enum TextFunction
	{
		Append, Prepend;
	}
	
	public enum TextType
	{
		Field, UserText;
	}
	
	private static final String INFO_TEXT = "This operation is applied to all tracks\r\n"
			+ "for the selected field.\r\n\r\nYou may append/prepend one field to\r\n"
			+ "another field, or use custom text.";
	
	public static String[] FIELDS;
	
	private JCheckBox chckAppPre;
	private JRadioButton rdbtnAppend;
	private JRadioButton rdbtnPrepend;
	private JComboBox comboEditField;
	private JRadioButton rdbtnUseField;
	private JRadioButton rdbtnUseText;
	private JComboBox comboCopyField;
	private JTextField txtUserText;
	
	/** Creates a new {@code AppendPrependPanel}
	 * @see FunctionPanel
	 */
	public AppendPrependPanel()
	{
		FIELDS = Utils.getFieldDisplay();
		
		chckAppPre = new JCheckBox("Append / Prepend");
		chckAppPre.setBounds(6, 7, 144, 23);
		this.add(chckAppPre);
		
		rdbtnAppend = new JRadioButton("Append");
		rdbtnAppend.setEnabled(false);
		rdbtnAppend.setBounds(26, 61, 90, 23);
		this.add(rdbtnAppend);
		
		rdbtnPrepend = new JRadioButton("Prepend");
		rdbtnPrepend.setEnabled(false);
		rdbtnPrepend.setBounds(118, 61, 90, 23);
		this.add(rdbtnPrepend);
		
		JLabel lblField = new JLabel("Field:");
		lblField.setEnabled(false);
		lblField.setBounds(16, 40, 46, 14);
		this.add(lblField);
		
		PlainDocument doc = new PlainDocument();
		Utils.setDocFormatNumbersOnly(doc);
		
		comboEditField = new JComboBox(FIELDS);
		comboEditField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int index = comboEditField.getSelectedIndex();
				if(index > 0)
				{
					if(index < 8) // TODO
					{
						comboCopyField.setModel(new DefaultComboBoxModel(Utils.FIELDS_STRINGS));
						txtUserText.setDocument(new PlainDocument());
					}
					else
					{
						comboCopyField.setModel(new DefaultComboBoxModel(Utils.FIELDS_INTS));
						txtUserText.setDocument(doc);
					}
				}
			}
		});
		comboEditField.setEnabled(false);
		comboEditField.setBounds(50, 37, 130, 20);
		this.add(comboEditField);
		
		rdbtnUseField = new JRadioButton("Use this field:");
		rdbtnUseField.setEnabled(false);
		rdbtnUseField.setBounds(41, 87, 109, 23);
		this.add(rdbtnUseField);
		
		rdbtnUseText = new JRadioButton("Use this text:");
		rdbtnUseText.setEnabled(false);
		rdbtnUseText.setBounds(41, 113, 109, 23);
		this.add(rdbtnUseText);
		
		comboCopyField = new JComboBox();
		comboCopyField.setEnabled(false);
		comboCopyField.setBounds(156, 91, 130, 20);
		this.add(comboCopyField);
		
		txtUserText = new JTextField();
		txtUserText.setEnabled(false);
		txtUserText.setBounds(156, 117, 90, 20);
		this.add(txtUserText);
		txtUserText.setColumns(10);
		
		ButtonGroup bg = new ButtonGroup();
		ButtonGroup bg2 = new ButtonGroup();
		bg.add(rdbtnAppend);
		bg.add(rdbtnPrepend);
		bg2.add(rdbtnUseField);
		bg2.add(rdbtnUseText);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(6, 147, 280, 106));
		this.add(infoTextArea);
		
		chckAppPre.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!chckAppPre.isSelected())
				{
					comboCopyField.setEnabled(false);
					txtUserText.setEnabled(false);
				}
				
				lblField.setEnabled(chckAppPre.isSelected());
				comboEditField.setEnabled(chckAppPre.isSelected());
				rdbtnAppend.setEnabled(chckAppPre.isSelected());
				rdbtnPrepend.setEnabled(chckAppPre.isSelected());
				rdbtnUseField.setEnabled(chckAppPre.isSelected());
				rdbtnUseText.setEnabled(chckAppPre.isSelected());
			}
		});
		
		rdbtnUseField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				comboCopyField.setEnabled(rdbtnUseField.isSelected());
				txtUserText.setEnabled(!rdbtnUseField.isSelected());
			}
		});
		
		rdbtnUseText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				txtUserText.setEnabled(rdbtnUseText.isSelected());
				comboCopyField.setEnabled(!rdbtnUseText.isSelected());
			}
		});
	}
	
	private boolean isAppendPrepend()
	{
		if(chckAppPre.isSelected())
		{
			return true;
		}
		return false;
	}
	
	private TextFunction getTextFunctionEnum()
	{
		if(rdbtnAppend.isSelected())
		{
			return TextFunction.Append;
		}
		else if(rdbtnPrepend.isSelected())
		{
			return TextFunction.Prepend;
		}
		return null;
	}
	
	private TextType getTextTypeEnum()
	{
		if(rdbtnUseField.isSelected())
		{
			return TextType.Field;
		}
		else if(rdbtnUseText.isSelected())
		{
			return TextType.UserText;
		}
		return null;
	}
	
	private FieldKey getEditField()
	{
		return Utils.getFieldKeyFromString((String) comboEditField.getSelectedItem());
	}
	
	private FieldKey getCopyField()
	{
		return Utils.getFieldKeyFromString((String) comboCopyField.getSelectedItem());
	}
	
	private String getUserText()
	{
		return txtUserText.getText();
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		switch(getTextTypeEnum())
		{
			case Field :
			{
				Functions.appendOrPrependField(entry, getTextFunctionEnum(), getEditField(), getCopyField());
				break;
			}
			case UserText :
			{
				Functions.appendOrPrependText(entry, getTextFunctionEnum(), getEditField(), getUserText());
				break;
			}
		}
	}

	@Override
	public boolean checkForErrors()
	{
		if(isAppendPrepend())
		{
			if(getTextFunctionEnum() == null)
			{
				return true;
			}
			
			TextType type = getTextTypeEnum();
			if(type != null)
			{
				switch(type)
				{
					case Field :
					{
						if(getEditField() != null && getCopyField() != null)
						{
							return false;
						}
					}
					case UserText :
					{
						if(getEditField() != null && !getUserText().isEmpty())
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
}
