package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.customui.InfoTextArea;
import id3.gui.functionpanel.FunctionPanel;
import id3.utils.Utils;
import org.jaudiotagger.tag.FieldKey;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;


public class FormattingPanel extends FunctionPanel
{
	public enum TextRemovalType
	{
		Leading, Trailing
	}
	
	private static final String INFO_TEXT = "Operations performed on all tracks, all fields (except composer,\r\n"
			+ "grouping, comments by default):\r\nCapitalize Fields: first letter of every word is capitalized\r\n"
			+ "-Proper Caps: skips articles (a, of, the, etc.)\r\n\r\nRemove leading/trailing spaces: applies to" +
			" all fields";

	private JCheckBox chckCapitalizeFields;
	private JCheckBox chckProperCaps;
	private JCheckBox chckComposer;
	private JCheckBox chckGrouping;
	private JCheckBox chckComments;
	
	private JCheckBox chckRemoveSpaces;
	private JCheckBox chckRemoveN;

	private JRadioButton rdbtnLeading;
	private JRadioButton rdbtnTrailing;
	
	private JComboBox comboFields;
	private JTextField txtChars;

	/** Creates a new {@code FormattingPanel}
	 * @see FunctionPanel
	 */
	public FormattingPanel()
	{
		chckCapitalizeFields = new JCheckBox("Capitalize Fields");
		chckCapitalizeFields.setToolTipText("Will capitalize the first letter of EVERY word for all string-type fields" +
				" (excluding grouping, comments, and composer)");
		chckCapitalizeFields.setBounds(6, 17, 175, 23);
		this.add(chckCapitalizeFields);
		
		chckProperCaps = new JCheckBox("Use Proper Capitalization");
		chckProperCaps.setToolTipText("Attempts to follow proper English capitalization by skipping words such as" +
				" a, of, the, and, an, etc. unless they're the first word in a title");
		chckProperCaps.setEnabled(false);
		chckProperCaps.setBounds(16, 43, 192, 23);
		this.add(chckProperCaps);
		
		chckComposer = new JCheckBox("Include Composer Field");
		chckComposer.setEnabled(false);
		chckComposer.setBounds(16, 66, 165, 23);
		this.add(chckComposer);
		
		chckGrouping = new JCheckBox("Include Grouping Field");
		chckGrouping.setEnabled(false);
		chckGrouping.setBounds(16, 92, 165, 23);
		this.add(chckGrouping);
		
		chckComments = new JCheckBox("Include Comments Field");
		chckComments.setEnabled(false);
		chckComments.setBounds(16, 118, 165, 23);
		this.add(chckComments);
		
		chckRemoveSpaces = new JCheckBox("Remove leading/trailing spaces");
		chckRemoveSpaces.setToolTipText("Removes all spaces that occur before and after text in a field." +
				" This works on all String-type fields.");
		chckRemoveSpaces.setBounds(6, 148, 230, 23);
		this.add(chckRemoveSpaces);
		
		chckRemoveN = new JCheckBox("Remove 'n' leading/trailing characters");
		chckRemoveN.setToolTipText("Remove n characters either from the beginning or end of the selected field.");
		chckRemoveN.setBounds(6, 178, 244, 23);
		this.add(chckRemoveN);
		
		rdbtnLeading = new JRadioButton("Leading");
		rdbtnLeading.setSelected(true);
		rdbtnLeading.setEnabled(false);
		rdbtnLeading.setBounds(26, 230, 90, 23);
		this.add(rdbtnLeading);
		
		rdbtnTrailing = new JRadioButton("Trailing");
		rdbtnTrailing.setEnabled(false);
		rdbtnTrailing.setBounds(118, 230, 90, 23);
		this.add(rdbtnTrailing);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnLeading);
		bg.add(rdbtnTrailing);
		
		JLabel lblChars = new JLabel("# of Characters:");
		lblChars.setEnabled(false);
		lblChars.setBounds(36, 260, 96, 14);
		this.add(lblChars);
		
		PlainDocument doc = new PlainDocument();
		Utils.setDocFormatNumbersOnly(doc);
		
		txtChars = new JTextField();
		txtChars.setDocument(doc);
		txtChars.setEnabled(false);
		txtChars.setBounds(141, 257, 37, 20);
		this.add(txtChars);
		txtChars.setColumns(10);
		
		comboFields = new JComboBox(Utils.FIELDS_STRINGS);
		comboFields.setEnabled(false);
		comboFields.setBounds(67, 205, 101, 20);
		this.add(comboFields);
		
		JLabel lblField = new JLabel("Field:");
		lblField.setEnabled(false);
		lblField.setBounds(16, 208, 46, 14);
		this.add(lblField);
		
		InfoTextArea infoTextArea = new InfoTextArea(INFO_TEXT, new Rectangle(280, 18, 175, 265));
		this.add(infoTextArea);
		
		chckCapitalizeFields.addActionListener(e ->
		{
            chckProperCaps.setEnabled(chckCapitalizeFields.isSelected());
            chckComposer.setEnabled(chckCapitalizeFields.isSelected());
            chckGrouping.setEnabled(chckCapitalizeFields.isSelected());
            chckComments.setEnabled(chckCapitalizeFields.isSelected());
        });
		
		chckRemoveN.addActionListener(e ->
		{
            lblField.setEnabled(chckRemoveN.isSelected());
            lblChars.setEnabled(chckRemoveN.isSelected());
            comboFields.setEnabled(chckRemoveN.isSelected());
            txtChars.setEnabled(chckRemoveN.isSelected());
            rdbtnTrailing.setEnabled(chckRemoveN.isSelected());
            rdbtnLeading.setEnabled(chckRemoveN.isSelected());
        });
	}
	
	private boolean isCapitalizeFields()
	{
		return chckCapitalizeFields.isSelected();
	}
	
	private boolean isUseProperCapitalization()
	{
		return chckProperCaps.isSelected();
	}
	
	private boolean isIncludeComposerField()
	{
		return chckComposer.isSelected();
	}
	
	private boolean isIncludeCommentsField()
	{
		return chckComments.isSelected();
	}
	
	private boolean isIncludeGroupingField()
	{
		return chckGrouping.isSelected();
	}
	
	private boolean isRemoveLeadingTrailingWhiteSpace()
	{
		return chckRemoveSpaces.isSelected();
	}
	
	private boolean isRemoveNSpaces()
	{
		return chckRemoveN.isSelected();
	}
	
	private FieldKey getEditField()
	{
		return Utils.getFieldKeyFromString((String) comboFields.getSelectedItem());
	}
	
	private int getNCharCount()
	{
		try
		{
			return Integer.parseInt(txtChars.getText());
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	private TextRemovalType getTextRemovalType()
	{
		if(rdbtnLeading.isSelected())
		{
			return TextRemovalType.Leading;
		}
		else if(rdbtnTrailing.isSelected())
		{
			return TextRemovalType.Trailing;
		}
		return null;
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		if(isCapitalizeFields())
		{
			Functions.formatFields(entry, isUseProperCapitalization(), isIncludeCommentsField(),
					isIncludeComposerField(), isIncludeGroupingField());
		}
		if(isRemoveLeadingTrailingWhiteSpace())
		{
			Functions.removeLeadingTrailingSpaces(entry);
		}
		if(isRemoveNSpaces())
		{
			Functions.removeLeadingTrailingChars(entry, getEditField(), getTextRemovalType(), getNCharCount());
		}
	}

	@Override
	public boolean checkForErrors()
	{
		if(!isCapitalizeFields() && !isRemoveLeadingTrailingWhiteSpace() &&
				!isRemoveNSpaces())
		{
			return true;
		}
		if(isRemoveNSpaces())
		{
			if(getNCharCount() == 0)
			{
				return true;
			}
		}
		return false;
	}
	
}
