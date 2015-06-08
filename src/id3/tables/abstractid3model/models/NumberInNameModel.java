package id3.tables.abstractid3model.models;

import id3.tables.TableEntry;
import id3.tables.abstractid3model.AbstractID3Model;

import java.util.ArrayList;


/** Used to back {@link NumberInNamePanel}'s table.
 * @see AbstractID3Model
 */
public class NumberInNameModel extends AbstractID3Model
{
	protected static final String[] HEADER_TITLES = {"Current Song Title", "New Song Title", "Track #"};
	
	/** Enum for {@code NumberInNameModel}'s column names */
	public enum NumberColumns
	{
		CurrentTitle(0), NewTitle(1), TrackNumber(2);
		
		private final int value;
		
		private NumberColumns(final int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static NumberColumns getEnum(int i)
		{
			for(NumberColumns clmn : NumberColumns.values())
			{
				if(clmn.getValue() == i)
				{
					return clmn;
				}
			}
			return null;
		}
	}
	
	/** Creates empty model */
	public NumberInNameModel()
	{ 
		this(new ArrayList<TableEntry>());
	}
	
	public NumberInNameModel(ArrayList<TableEntry> entries)
	{
		super(HEADER_TITLES, entries);
	}
	
	@Override
	public Class getColumnClass(int column)
	{
		switch(NumberColumns.getEnum(column))
		{
			default :
				return String.class;
			case TrackNumber :
				return Integer.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		TableEntry tableEntry = tableEntries.get(rowIndex);
		switch(NumberColumns.getEnum(columnIndex))
		{
			case CurrentTitle :
				return tableEntry.SongTitle;
			case NewTitle:
				return tableEntry.NewTitle;
			case TrackNumber :
				return Integer.parseInt(tableEntry.TrackNumber);
		}
		return null;
	}

}
