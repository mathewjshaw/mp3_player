package project4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;




import java.util.Iterator;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;



public class Library {
	private  ArrayList <Song> songs = new ArrayList <Song>();



	public  void traverse(String directory) {
		File file = new File (directory);
		File[] files = file.listFiles();

		for (int i = 0; i < files.length; i ++){
			if (files[i].isFile()){
				if (files[i].getPath().endsWith(".mp3")){
					File curr = files[i];
					String path =curr.getAbsolutePath();
					AudioFile f = new AudioFile();
					try {
						f = AudioFileIO.read(new File(path));
					} catch (CannotReadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TagException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ReadOnlyFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidAudioFrameException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Tag tag = (Tag) f.getTag();
					String title = tag.getFirst(FieldKey.TITLE);
					String artist = tag.getFirst(FieldKey.ARTIST);
					String album = tag.getFirst(FieldKey.ALBUM);
					Song mini = new Song(title, artist, album, path);
					songs.add(mini);
				}
			}
			if (files[i].isDirectory()){
				traverse(files[i].getPath());
			}
		}
	}
	public Iterator<Song> getSongs() {	
		return songs.iterator();
	}
	public int getnSongs(){
		return songs.size();
	}
	public void add(Song song){
		songs.add(song);
	}

	public void BubbleSort(){
		int n = songs.size();
		for (int i = 0; i < n-1; i++){
			for (int j = 0; j < n - (i+1); j++){
				if (songs.get(j).getTitle().compareToIgnoreCase(songs.get(j+1).getTitle()) > 0){
					Song swapper = songs.get(j);

					songs.set(j, songs.get(j+1));
					songs.set(j+1, swapper);
				}
			}
		}
	

	}
	public void BubbleSortArtist(){
		int n = songs.size();
		for (int i = 0; i < n-1; i++){
			for (int j = 0; j < n - (i+1); j++){
				if (songs.get(j).getArtist().compareToIgnoreCase(songs.get(j+1).getArtist()) > 0){
					Song swapper = songs.get(j);
					songs.set(j, songs.get(j+1));
					songs.set(j+1, swapper);
				}
			}
	}
}
	public void BubbleSortAlbum(){
		int n = songs.size();
		for (int i = 0; i < n-1; i++){
			for (int j = 0; j < n - (i+1); j++){
				if (songs.get(j).getAlbum().compareToIgnoreCase(songs.get(j+1).getAlbum()) > 0){
					Song swapper = songs.get(j);
					songs.set(j, songs.get(j+1));
					songs.set(j+1, swapper);
				}
			}
	}
}
	
	
	
	public Song getSong(int i){
		return songs.get(i);
	}
}



