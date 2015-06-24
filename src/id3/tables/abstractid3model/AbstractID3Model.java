package id3.tables.abstractid3model;

import id3.tables.TableEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/** Abstract table model used to back
 * {@link id3.tables.ID3Table}. This is simply a subclass
 * of {@link AbstractTableModel}.
 */
public abstract class AbstractID3Model extends AbstractTableModel
{
	/** Column titles. This MUST be set by the subclass. */
	protected String[] HEADER_TITLES;
	
	/** List that backs this model.*/
	protected ArrayList<TableEntry> tableEntries;
	
	public AbstractID3Model(String[] headerTitles, ArrayList<TableEntry> tableEntries)
	{
		this.HEADER_TITLES = headerTitles;
		this.tableEntries = tableEntries;
	}
	
	@Override
	public int getRowCount()
	{
		return tableEntries.size();
	}

	@Override
	public int getColumnCount()
	{
		return HEADER_TITLES.length;
	}
	
	@Override
	public String getColumnName(int column)
	{
		return HEADER_TITLES[column];
	}
	
	@Override
	public Class getColumnClass(int column)
	{
		return String.class;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
	
	public ArrayList<TableEntry> getTableEntries()
	{
		return tableEntries;
	}
	
	public void setTableEntries(ArrayList<TableEntry> tableEntries)
	{
		this.tableEntries = tableEntries;
		if(tableEntries.size() == 1)
		{
			this.fireTableStructureChanged();
		}
		else
		{
			this.fireTableRowsInserted(0, tableEntries.size());
		}
	}

	/** Must be overriden by the subclass. ID3Tables
	 * can hold different amounts of data.
	 */
	@Override
	public abstract Object getValueAt(int rowIndex, int columnIndex);
}
