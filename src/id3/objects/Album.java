package id3.objects;

import java.util.HashMap;

import org.jaudiotagger.tag.images.Artwork;

/** Album objects store album specific fields found on song's sharing
 * the same album AND artist. Albums cannot be accessed without an Artist object.
 */
public class Album
{	
	/** Literal album name */
	private String 				name;
	
	/** The Artist object this album belongs to */
	private Artist 				artist;
	
	/** A map containing this album's tracks' Library key IDs and file paths */
	private HashMap<String, String> 	tracks 				= new HashMap<>();
	
	private String 				genre;
	private String				composer;
	private String 				grouping;
	private String				comments;
	private int 				disccount			= -1;
	private int 				trackcount  		= -1;
	private int 				year				= -1;
	private int 				rating				= -1;
	private Artwork 			artwork 			= null;
	
	/** Creates a new Album object.
	 * <p>
	 * Be warned that composer, grouping, and comments are
	 * being considered as album specific fields. If you use
	 * these fields as album or artist specific, you'll be fine.
	 * However, if you have, for example, comments that vary from
	 * track to track on an album, you're going to get bad results
	 * if you use any function that requires Artist object creation.
	 * @param name  Literal album name.
	 * @param artist  {@link Artist} object this album belongs to.
	 * @param genre    Album's genre.
	 * @param disccount  Number of discs on the album.
	 * @param trackcount  Number of tracks on the album. By default,
	 * this field is randomly chosen from a track on this album.
	 * @param year  Year album was released.
	 * @param composer  Literal composer or used as a custom field.
	 * @param grouping  Literal grouping or used as a custom field.
	 * @param comments  Literal comments or used as a custom field.
	 * @param artwork  {@link Artwork} for this album.
	 * @see Library#createArtistObjects()
	 * @see Functions#findMissing()
	 */
	public Album(String name, Artist artist, String genre, int disccount, int trackcount,
			int year, String composer, String grouping, String comments, Artwork artwork)
	{
		this.name = name;
		this.artist = artist;
		this.genre = genre;
		this.disccount = disccount;
		this.trackcount = trackcount;
		this.year = year;
		this.composer = composer;
		this.grouping = grouping;
		this.comments = comments;
		this.artwork = artwork;
	}
	
	public Artist getArtist()
	{
		return artist;
	}

	public void setArtist(Artist artist)
	{
		this.artist = artist;
	}

	public String getGenre()
	{
		return genre;
	}

	public void setGenre(String genre)
	{
		this.genre = genre;
	}

	public int getDiscCount()
	{
		return disccount;
	}

	public void setDiscCount(int disccount)
	{
		this.disccount = disccount;
	}
	
	public int getCalculatedTrackCount()
	{
		return tracks.size();
	}

	public int getTrackCount()
	{
		return trackcount;
	}

	public void setTrackCount(int trackcount)
	{
		this.trackcount = trackcount;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getRating()
	{
		return rating;
	}

	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public void addTrack(String id, String filePath)
	{
		tracks.put(id, filePath);
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public HashMap<String, String>getTracks()
	{
		return tracks;
	}
	
	public Artwork getArtwork()
	{
		return artwork;
	}
	
	public void setArtwork(Artwork artwork)
	{
		this.artwork = artwork;
	}
	
	public String getComposer()
	{
		return composer;
	}
	
	public void setComposer(String composer)
	{
		this.composer = composer;
	}
	
	public String getGrouping()
	{
		return grouping;
	}
	
	public void setGrouping(String grouping)
	{
		this.grouping = grouping;
	}
	
	public String getComments()
	{
		return comments;
	}
	
	public void setComments(String comments)
	{
		this.comments = comments;
	}
}
