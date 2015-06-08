package id3.tables.abstractid3model.models;

import id3.tables.TableEntry;
import id3.tables.abstractid3model.AbstractID3Model;

import java.util.ArrayList;

/** Used to back {@link ArtistInNamePanel}'s table.
 * @see AbstractID3Model
 */
public class ArtistInNameModel extends AbstractID3Model
{
	protected static final String[] HEADER_TITLES = {"Current Song Title", "New Song Title", "Artist"};
	
	/** Enum for {@code ArtistInNameModel} column names */
	public enum NameColumns
	{
		CurrentTitle(0), NewTitle(1), Artist(2);
		
		private final int value;
		
		private NameColumns(final int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static NameColumns getEnum(int i)
		{
			for(NameColumns clmn : NameColumns.values())
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
	public ArtistInNameModel()
	{ 
		this(new ArrayList<TableEntry>());
	}

	public ArtistInNameModel(ArrayList<TableEntry> entries)
	{
		super(HEADER_TITLES, entries);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		TableEntry tableEntry = tableEntries.get(rowIndex);
		switch(NameColumns.getEnum(columnIndex))
		{
			case CurrentTitle :
				return tableEntry.SongTitle;
			case NewTitle :
				return tableEntry.NewTitle;
			case Artist :
				return tableEntry.Artist;
		}
		return null;
	}

}
