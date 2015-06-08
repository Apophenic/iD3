package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.TableFunctionPanel;
import id3.objects.Library;
import id3.tables.ID3Table;
import id3.tables.TableEntry;
import id3.tables.abstractid3model.models.FindAndReplaceModel;
import id3.utils.Utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;

public class FindAndReplacePanel extends TableFunctionPanel
{
	private static final String INFO_TEXT = "Searches all tag fields for all songs for\r\n"
			+ "matching text. \r\nText will be replaced after\r\nclicking commit changes. Case Sensitive.";
	
	private JTextField txtFind;
	private JTextField txtReplace;

	/** Creates a new {@code FindAndReplacePanel}
	 * @see TableFunctionPanel
	 * @see FunctionPanel
	 */
	public FindAndReplacePanel()
	{
		super(new ID3Table(new FindAndReplaceModel()), INFO_TEXT);
		init();
	}
	
	private void init()
	{
		JLabel lblFind = new JLabel("Find this text:");
		lblFind.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblFind.setBounds(370, 252, 110, 14);
		this.add(lblFind);
		
		txtFind = new JTextField();
		txtFind.setBounds(370, 275, 110, 20);
		this.add(txtFind);
		txtFind.setColumns(10);
		
		JLabel lblReplace = new JLabel("Replacement text:");
		lblReplace.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblReplace.setBounds(370, 306, 147, 14);
		this.add(lblReplace);
		
		txtReplace = new JTextField();
		txtReplace.setBounds(370, 331, 110, 20);
		this.add(txtReplace);
		txtReplace.setColumns(10);
	}
	
	public String getFindText()
	{
		return txtFind.getText();
	}
	
	public String getReplaceText()
	{
		return txtReplace.getText();
	}
	
	@Override
	public boolean checkForErrors()
	{
		if(txtFind.getText().isEmpty())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void initFunction(Library lib)
	{
		ArrayList<TableEntry> tableEntries = new ArrayList<TableEntry>();
		if(!checkForErrors())
		{
			tableEntries = Functions.findMatchingTextInTagFields(lib, getFindText(), getReplaceText());
		}
		table.getModel().setTableEntries(tableEntries);
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		// Do Nothing
		
	}

	@Override
	protected void runCommit(TableEntry entry)
	{
		Tag tag = Utils.getTagFromAudioFile(entry.FilePath);
		FieldKey field = Utils.getFieldKeyFromString(entry.Status);
		
		try
		{
			tag.setField(field, entry.NewTitle);
			Utils.saveTagToFile(entry.FilePath, tag);
			LOG.log(Level.FINER, "Successfully replaced text: " + tag.toString());
		}
		catch (KeyNotFoundException | FieldDataInvalidException e)
		{
			LOG.log(Level.WARNING, "Failed to replace text in tag: " + entry.FilePath);
		}
	}

	@Override
	protected void resizeColumns()
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(140);
		table.getColumnModel().getColumn(1).setPreferredWidth(125);
		table.getColumnModel().getColumn(2).setPreferredWidth(55);
	}
}
