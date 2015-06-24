package id3.gui.functionpanel.panels;

import id3.functions.Functions;
import id3.gui.functionpanel.TableFunctionPanel;
import id3.tables.ID3Table;
import id3.tables.TableEntry;
import id3.tables.abstractid3model.models.GetLyricsModel;

import javax.swing.*;
import java.util.Map;
import java.util.Map.Entry;

public class GetLyricsPanel extends TableFunctionPanel // TODO
{
	private static final String INFO_TEXT = "Search for all songs with the selected minimum rating."
			+ " After reviewing discovered songs, click \"Commit Changes\" to find lyrics for all songs in the table." +
			"\r\n\r\n"
			+ "Not yet implemented";
	
	private JSlider slideRating;
	
	/** Creates a new {@code GetLyricsPanel}
	 * @see TableFunctionPanel
	 * @see id3.gui.functionpanel.FunctionPanel
	 */
	public GetLyricsPanel()
	{
		super(new ID3Table(new GetLyricsModel()), INFO_TEXT);
		init();
	}
	
	private void init()
	{
		slideRating = new JSlider();
		slideRating.setValue(5);
		slideRating.setSnapToTicks(true);
		slideRating.setPaintTicks(true);
		slideRating.setPaintLabels(true);
		slideRating.setMinimum(0);
		slideRating.setMaximum(5);
		slideRating.setMajorTickSpacing(1);
		slideRating.setBounds(363, 270, 117, 38);
		add(slideRating);
		
		JLabel lblRating = new JLabel("Minimum Rating:");
		lblRating.setBounds(370, 252, 107, 14);
		add(lblRating);
	}
	
	public int getMinimumRating()
	{
		return slideRating.getValue();
	}

	@Override
	public void runFunction(Entry<String, Map> entry)
	{
		TableEntry tableEntry = Functions.getSongsByMinimumRating(entry, getMinimumRating());
		if(tableEntry != null)
		{
			tableEntries.add(tableEntry);
			table.getModel().setTableEntries(tableEntries);
		}
	}

	@Override
	protected void runCommit(TableEntry entry)
	{
	//	Functions.fetchLyrics(tableEntries);
	}

	@Override
	protected void resizeColumns()
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(140);
		table.getColumnModel().getColumn(1).setPreferredWidth(75);
		table.getColumnModel().getColumn(2).setPreferredWidth(75);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);
	}
}
