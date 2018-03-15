package project4;

public class Song {
	private String title;
	private String artist;
	private String album;
	private String path;

	Song(String title, String artist, String album, String path){
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.path = path;
	}
	public String getTitle(){
		return title;
	}
	public String getArtist(){
		return artist;
	}
	public String getPath(){
		return path;
	}
	public String getAlbum(){
		return album;
	}
	
}
