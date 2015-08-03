iD3 provides a number of ID3 metadata editing tools that are meant to be used with the Windows version of iTunes.

Here's how iTunes' files work in a nutshell:
When you add a track to iTunes, any information on that track's ID3 tags is read and stored as a part of that track's iTunes entry in two seperate files. The first file is actually used to read and write track entry information from, but it's in a binary format and is difficult to work with. The second file is a readable XML file that mirrors the binary file, but has no bearing over what information is actually displayed in iTunes; That is, it is merely a readable copy of the binary file, meant only for displaying iTunes Library information.

So there are four ways to change information in iTunes:
* 1) The obvious, edit track info in iTunes. Obviously this won't work for batch operations.
* 2) Edit the binary file. I don't think anyone has even done this (other than with using the Apple COM SDK, but meh that's a pain and lacks a lot of functionality I wanted).
* 3) Edit the xml file and IMPORT it into iTunes. This would be the best solution, but a lot of information is lost when you import.
* 4) Edit individual track ID3 tags and force the library to update. This is the approach iD3 uses.

iD3 reads track information from the XML file, not the ID3 tags. It's assumed this contains the most relevant information since it is user supplied (technically the ID3 tags SHOULD mirror the information in the XML, but this isn't always the case). When it's finished, track information is written to the ID3 tags (not the xml file). After any operation is performed, you're going to have to force iTunes to "update" based on the new ID3 tag information that was written. iTunes provides no significant way to do this sadly, but there are workarounds:

* Option one: open iTunes, select all songs in My Music, CHANGE any empty field you aren't using (such as bpm maybe), click ok. Let iTunes work *  this will update your library.
* Option two: same as option one, but instead of changing an empty field, go to options and change the equalizer setting. The downside to this is you'll be forced to then change it back, meaning iTunes will be forced to read your library twice.
* Option three: NOT RECOMMENDED/ADVANCED USERS ONLY:  Convert ID3 tags or Reverse Unicode

*iD3 makes some assumptions about your library:*
* Your library is consolidated (edit -> preferences -> advanced -> Keep iTunes Media folder organized)
* Your music is stored in the following directory structure: yourlibraryfolder/music/
* Your composer, grouping, and comments field are Artist or Album specific fields (i.e. artist location, artist/album rating, number of albums, artist status, other artist specific info like bandcamp url, etc.). If not, DO NOT USE MISSING FIELDS FUNCTIONS.

Nothing should break completely if you don't follow these rules, but you may get mixed results if you don't. Regardless, IT IS HIGHLY HIGHLY HIGHLY RECOMMENDED TO MAKE A BACKUP OF YOUR LIBRAY FILES AND SONGS IN CASE THERE ARE ANY ISSUES.

**Available Operations**
* Append / Prepend text or an ID3 field to another field
* Remove Artist name from Track title
* Calculate BPM
* Rebuild iTunes Library.xml file from a directory of music
* Copy all songs from an iPod (Tested on iPod Classic only)
* Custom Field Input
* Delete Fields that can't readily be deleted (artwork / lyrics)
* Embed linked artwork into ID3 tags
* Save a copy of all available artwork in a Library
* Find and Replace text in tags
* Multiple ID3 tag formatting options
* Fill missing tag fields from other songs on the same album, if possible
* Find and Attempt discovery of missing songs
* Remove track number from song title (in the ID3 tag, NOT the file name)
* Remove duplicate files
* Save iTunes song ratings to ID3 tags
* Swap tag fields
* Find songs in the music directory that aren't in your Library