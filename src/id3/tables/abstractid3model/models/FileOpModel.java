package id3.tables.abstractid3model.models;

import id3.tables.TableEntry;
import id3.tables.abstractid3model.AbstractID3Model;

import java.util.ArrayList;

/** Used to back {@link id3.gui.functionpanel.panels.RemoveDuplicatePanel}'s and
 * {@link id3.gui.functionpanel.panels.UnlistedSongsPanel}'s tables.
 * @see AbstractID3Model
 */
public class FileOpModel extends AbstractID3Model
{
	protected static final String[] HEADER_TITLES = {"File Path", "Status"};
	
	/** Enum for {@code FileOpModel}'s column names */
	public enum FileOpColumns
	{
		FilePath(0), Status(1);
		
		private final int value;
		
		FileOpColumns(final int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static FileOpColumns getEnum(int i)
		{
			for(FileOpColumns clmn : FileOpColumns.values())
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
	public FileOpModel()
	{ 
		this(new ArrayList<>());
	}
	
	public FileOpModel(ArrayList<TableEntry> entries)
	{
		super(HEADER_TITLES, entries);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		TableEntry tableEntry = tableEntries.get(rowIndex);
		switch(FileOpColumns.getEnum(columnIndex))
		{
			case FilePath :
				if(tableEntry.FilePath.contains("\\")) {	//Make sure file entry is formatted as a file path
					String[] temp = tableEntry.FilePath.split("\\\\");
					if(temp.length < 4)
					{
						return tableEntry.FilePath;
					}
				//	return temp[0] + "\\" + temp[1] + "\\" + "..." + "\\" + temp[temp.length-1];
					return temp[0] + "\\" + "..." + "\\" + temp[temp.length-3] + "\\" + temp[temp.length-2]
							+ "\\" + temp[temp.length-1];
				}
				else
				{
					return tableEntry.FilePath;
				}
			case Status :
				return tableEntry.Status;
		}
		return null;
	}

}
