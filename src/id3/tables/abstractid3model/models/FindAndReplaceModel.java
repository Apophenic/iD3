package id3.tables.abstractid3model.models;

import id3.tables.TableEntry;
import id3.tables.abstractid3model.AbstractID3Model;

import java.util.ArrayList;

/** Used to back {@link FindAndReplacePanel}'s table.
 * @see AbstractID3Model
 */
public class FindAndReplaceModel extends AbstractID3Model
{
	protected static final String[] HEADER_TITLES = {"Title", "New Title", "Field"};
	
	/** Enum for {@code FindAndReplaceModel}'s column names */
	public enum FindColumns
	{
		Title(0), NewTitle(1), Field(2);
		
		private final int value;
		
		private FindColumns(final int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static FindColumns getEnum(int i)
		{
			for(FindColumns clmn : FindColumns.values())
			{
				if(clmn.getValue() == i)
				{
					return clmn;
				}
			}
			return null;
		}
	}

	/** Creates an empty model */
	public FindAndReplaceModel()
	{ 
		this(new ArrayList<TableEntry>());
	}

	public FindAndReplaceModel(ArrayList<TableEntry> entries)
	{
		super(HEADER_TITLES, entries);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		TableEntry entry = tableEntries.get(rowIndex);
		switch(FindColumns.getEnum(columnIndex))
		{
			case Title :
				return entry.SongTitle;
			case NewTitle :
				return entry.NewTitle;
			case Field :
				return entry.Status;
		}
		return null;
	}

}
