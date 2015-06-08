package id3.tables.abstractid3model.models;

import id3.tables.TableEntry;
import id3.tables.abstractid3model.AbstractID3Model;

import java.util.ArrayList;

/** Used to back {@link GetLyricsPanel}'s table.
 * @see AbstractID3Model
 */
public class GetLyricsModel extends AbstractID3Model
{
	protected static final String[] HEADER_TITLES = {"Song", "Artist", "Album", "Rating"};
	
	/** Enum for {@code GetLyricsModel}'s column names */
	public enum LyricsColumns
	{
		Title(0), Artist(1), Album(2), Rating(3);
		
		private final int value;
		
		private LyricsColumns(final int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static LyricsColumns getEnum(int i)
		{
			for(LyricsColumns clmn : LyricsColumns.values())
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
	public GetLyricsModel()
	{
		this(new ArrayList<TableEntry>());
	}
	
	public GetLyricsModel(ArrayList<TableEntry> entries)
	{
		super(HEADER_TITLES, entries);
	}
	
	@Override
	public Class getColumnClass(int column)
	{
		switch(LyricsColumns.getEnum(column))
		{
			case Rating :
				return Integer.class;
			default :
				return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		TableEntry tableEntry = tableEntries.get(rowIndex);
		switch(LyricsColumns.getEnum(columnIndex))
		{
			case Title :
				return tableEntry.SongTitle;
			case Artist :
				return tableEntry.Artist;
			case Album :
				return tableEntry.Album;
			case Rating :
				return Integer.parseInt(tableEntry.Rating);
		}
		return null;
	}

}
