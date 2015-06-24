package id3.objects;

import java.util.ArrayList;

/** Artist objects store artist specific fields found on songs sharing
 * the same artist field. Also contains a list of all {@link Album}s
 * belonging to this Artist.
 */
public class Artist 
{
	/** Literal artist name*/
	private String					name;
	
	/** Artist rating used by {@link id3.functions.Functions#calculateArtistRating} */
	private int rating = -1;
	
	/** All {@link Album}s that belong to this Artist */
	private ArrayList<Album> albums 					= new ArrayList<>();
	
	/** Creates a new Artist by using
	 * the artist's literal name.
	 * @param name
	 */
	public Artist(String name)
	{
		this.name = name;
	}
	
	/** Gets an Artist's {@link Album} by
	 * literal name
	 * @param name  Literal album name.
	 * @returns  Album that matches literal name, else null.
	 */
	public Album getAlbum(String name)
	{
		for(Album album : albums)
		{
			if(album.getName().equalsIgnoreCase(name))
			{
				return album;
			}
		}
		return null;
	}
	
	public void addAlbum(Album album)
	{
		albums.add(album);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public int getRating()
	{
		return rating;
	}
	
	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public ArrayList<Album> getAlbums()
	{
		return albums;
	}

}
