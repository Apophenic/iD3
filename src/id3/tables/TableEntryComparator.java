package id3.tables;

import java.util.Comparator;

/** Sorts {@link TableEntry}s using the selected enum */
public class TableEntryComparator implements Comparator<TableEntry>
{
	public enum CompareType
	{
		SongTitle, NewTitle, Artist, Album, FileName,
		FilePath, NewFilePath, Rating, TrackNumber, Status;
	}
	
	private CompareType mode;
	
	public TableEntryComparator(CompareType mode)
	{
		this.mode = mode;
	}

	@Override
	public int compare(TableEntry o1, TableEntry o2)
	{
		switch(mode)
		{
			default :
			case SongTitle :
			{
				return o1.SongTitle.compareTo(o2.SongTitle);
			}
			case NewTitle :
			{
				return o1.NewTitle.compareTo(o2.NewTitle);
			}
			case Artist :
			{
				return o1.Artist.compareTo(o2.Artist);
			}
			case Album :
			{
				return o1.Album.compareTo(o2.Album);
			}
			case FileName :
			{
				return o1.FileName.compareTo(o2.FileName);
			}
			case FilePath :
			{
				return o1.FilePath.compareTo(o2.FilePath);
			}
			case NewFilePath :
			{
				return o1.NewFilePath.compareTo(o2.NewFilePath);
			}
			case Rating :
			{
				return Integer.valueOf(o1.Rating).compareTo(Integer.valueOf(o2.Rating));
			}
			case TrackNumber :
			{
				return Integer.valueOf(o1.TrackNumber).compareTo(Integer.valueOf(o2.TrackNumber));
			}
			case Status :
			{
				return o1.Status.compareTo(o2.Status);
			}
		}
	}

}
