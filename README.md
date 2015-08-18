# iD3 for Windows
-----------------
![Sample Img](https://github.com/Apophenic/iD3/blob/master/res/sample.png)

_iD3_ provides a number of ID3 metadata editing tools that are meant to be used with the Windows version of iTunes.

### Supported Operations
------------------------
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
* Remove track number from song title (in the ID3 tag, _not_ the file name)
* Remove duplicate files
* Save iTunes song ratings to ID3 tags
* Swap tag fields
* Find songs in the music directory that aren't in your Library

### How to Use It
-----------------
If you don't need the source, you'll find the precompiled version [here](https://github.com/Apophenic/iD3/blob/master/jar/iD3)

Otherwise, create a new project in your IDE of chocie and include the jars in the lib folder as project dependencies.

### How It Works
----------------
When you add a track to iTunes, any information on that track's ID3 tags is read and stored as a part of that track's
iTunes entry in two seperate files. The first file is actually used to read and write track entry information from,
but it's in a binary format and is difficult to work with. The second file is a readable _xml_ file that mirrors the
binary file, but has no bearing over what information is actually displayed in iTunes.

So, there are four ways to change information in iTunes:
* The obvious, edit track info in iTunes. Obviously this won't work for batch operations.
* Edit the binary file. There is a COM SDK for doing this, but it lacks documentation and features.
* Edit the xml file and _import_ it into iTunes. This would be the best solution, but a lot of information is lost when
 you import.
* Edit individual track ID3 metadata tags and force the library to update. This is the approach _iD3_ uses.

iD3 reads track information from the _xml_ file, not the ID3 tags. It's assumed this contains the most relevant
information since it is user supplied. When it's finished, track information is written to the ID3 tags.

After any operation is performed, you're going to have to force iTunes to "update" based on the new ID3 tag information that
was written. iTunes provides no significant way to do this sadly, but there are three workarounds:

* Open iTunes, select all songs in My Music, CHANGE any empty field you aren't using (such as bpm maybe), click ok.
* same as option one, but instead of changing an empty field, go to options and change the equalizer setting. The
downside to this is you'll be forced to then change it back, meaning iTunes will be forced to read your library twice.
* Convert ID3 tags or Reverse Unicode (__Not Recommended__)

##### For the Best Results...
-----------------------------
* Your library should be consolidated (edit -> preferences -> advanced -> Keep iTunes Media folder organized)
* Your music should be stored in the following directory structure: yourlibraryfolder/music/
* Your composer, grouping, and comments field should be Artist or Album specific fields (i.e. artist location,
artist/album rating, number of albums, artist status, other artist specific info like bandcamp url, etc.).
Otherwise, don't use the _Missing Fields_ functions.

__Make a backup of your library files and songs before using iD3!__
