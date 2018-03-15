package project4;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument.Iterator;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MPlayerPanel extends JPanel {

	class PlayerThread extends Thread{
		Player pl;
		PlayerThread(String filename){
			FileInputStream file;
			try{
				file = new FileInputStream(filename);
				try {
					Player pl = new Player(file);
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.getMessage();
				}
			}
			catch (FileNotFoundException e){
				e.getMessage();
			}
		}
		public void run(){
			try{
				pl.play();
			}
			catch(Exception e){
				e.getMessage();
			}
		}
		public void close(){
			try{
				pl.close();
			}
			catch(Exception e){
				e.getMessage();
			}
		}
	}
	PlayerThread currThread = new PlayerThread("");
	Library library = new Library();
	Library miniLibrary = new Library();
	int sortCounter = 0;
	int sort2Counter = 0;
	int searchCount = 0;

	// three subpanels
	JPanel topPanel, bottomPanel; 
	JScrollPane centerPanel;

	// boxes, textfield, check box
	JButton playButton, stopButton, exitButton, loadMp3Button, saveButton, openButton;

	// the checkbox that specifies whether the songs should be sorted by Artist (or by Title)
	JCheckBox sortBox = new JCheckBox("Sort by Artist");
	JCheckBox sortBox2 = new JCheckBox("Sort by Album");

	// the text field used to search for a song
	JTextField searchBox;

	int selectedSong = -1; // the index of the row that corresponds to the selected song
	private JTable table = null;
	private final JFileChooser fc = new JFileChooser(); // for opening a window to select a file

	MPlayerPanel() {

		this.setLayout(new BorderLayout());
		// Create panels: top, center, bottom
		// Create the top panel that has buttons: Load mp3, Save Library, Load Library 
		// It also has a textfield to search for a song and "sort by artist" checkbox
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,4));

		// create buttons
		loadMp3Button = new JButton("Load mp3");
		saveButton = new JButton("Save Library");
		openButton = new JButton("Load Library");
		searchBox = new JTextField(5);
		exitButton = new JButton("Exit");
		playButton = new JButton("Play");
		stopButton = new JButton("Stop");

		// add a listener for each button
		loadMp3Button.addActionListener(new MyButtonListener());
		saveButton.addActionListener(new MyButtonListener());
		openButton.addActionListener(new MyButtonListener());
		sortBox.addActionListener(new MyButtonListener());
		sortBox2.addActionListener(new MyButtonListener());
		exitButton.addActionListener(new MyButtonListener());
		playButton.addActionListener(new MyButtonListener());
		stopButton.addActionListener(new MyButtonListener());
		searchBox.addActionListener(new MyButtonListener());


		// add buttons, textfield and a checkbox to the top panel
		topPanel.add(loadMp3Button);
		topPanel.add(saveButton);
		topPanel.add(openButton);
		topPanel.add(searchBox);
		topPanel.add(sortBox);
		topPanel.add(sortBox2);

		this.add(topPanel, BorderLayout.NORTH);

		// create the bottom panel and add three buttons to it
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1,3));
		bottomPanel.add(playButton);
		bottomPanel.add(stopButton);
		bottomPanel.add(exitButton);

		this.add(bottomPanel, BorderLayout.SOUTH);

		// the panel in the center that shows mp3 songs
		centerPanel = new JScrollPane();
		this.add(centerPanel, BorderLayout.CENTER );

		// file chooser: set the default directory to the current directory 
		fc.setCurrentDirectory(new File("."));
	}


	/** A inner listener class for buttons, textfield and checkbox **/
	class MyButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == loadMp3Button) {
				System.out.println("Load mp3 button");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Select a directory with mp3 songs");
				// open a window to select a directory
				int returnVal = fc.showOpenDialog(MPlayerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File dir = fc.getSelectedFile();

					String dirPath = dir.getAbsolutePath();
					library.traverse(dirPath);

					library.BubbleSort();
					java.util.Iterator<Song> it = library.getSongs();
					int numsongs = library.getnSongs();

					String[][] tableElems = new String[numsongs][3];
					String[] columnNames = {"Title", "Album", "Artist"};
					int t = 0;
					while (it.hasNext()){
						Song curr = it.next();
						tableElems[t][0] = curr.getTitle();
						tableElems[t][1] = curr.getAlbum();
						tableElems[t][2] = curr.getArtist();
						t += 1;
					}
					// creating a table and adding it to the centerPanel
					table = new JTable(tableElems, columnNames );
					centerPanel.getViewport().add (table);
					updateUI();					
				}
			}
			else if (e.getSource() == saveButton) {
				// save the song catalog into a file
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setDialogTitle("Select a file to save the library file");
				int returnVal = fc.showSaveDialog(MPlayerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					PrintWriter printWriter;
					try {
						printWriter = new PrintWriter (file);
						java.util.Iterator<Song> it = library.getSongs();
						printWriter.println (library.getnSongs());
						while (it.hasNext()){
							Song curr = it.next();

							printWriter.println (curr.getTitle());
							printWriter.println (curr.getArtist());
							printWriter.println (curr.getAlbum());
							printWriter.println (curr.getPath());
						}
						if (printWriter != null)
							printWriter.close ();  
					}
					catch (FileNotFoundException e1) {
						System.out.println("Could not find the file.");
					}
				}
			}

			else if (e.getSource() == openButton) {
				System.out.println("Load Library button");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setDialogTitle("Select a saved .txt library");
				int returnVal = fc.showOpenDialog(MPlayerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File dir = fc.getSelectedFile();

					try {
						BufferedReader br = new BufferedReader(new FileReader(dir.getAbsolutePath()));  

						String line;
						int counter = 0;
						while ((line = br.readLine()) != null) {
							if (counter == 0){
								String title = br.readLine();
								String artist = br.readLine();
								String album = br.readLine();
								String path = br.readLine();
								Song mini = new Song(title,artist,album,path);
								library.add(mini);
								counter += 1;
							}
							else{
								int checker = 1;
								String title = line;
								String artist = br.readLine();
								String album = br.readLine();
								String path = br.readLine();
								Song mini = new Song(title,artist,album,path);
								for (int i = 0; i < library.getnSongs(); i++){
									if (mini.getTitle().equals(library.getSong(i).getTitle()) )
										  checker = 0;
								}
								if (checker == 1){
								library.add(mini);
								}
							}
						}
					}
					catch(IOException e1) {
						System.out.println("Can't read from the file");
					}
				}
				library.BubbleSort();
				java.util.Iterator<Song> it = library.getSongs();
				int numsongs = library.getnSongs();

				String[][] tableElems = new String[numsongs][3];
				String[] columnNames = {"Title", "Album", "Artist"};
				int t = 0;
				while (it.hasNext()){
					Song curr = it.next();
					tableElems[t][0] = curr.getTitle();
					tableElems[t][1] = curr.getAlbum();
					tableElems[t][2] = curr.getArtist();
					t += 1;
				}
				
				// creating a table and adding it to the centerPanel
				table = new JTable(tableElems, columnNames );
				centerPanel.getViewport().add (table);
				updateUI();	
				
			}
			
			else if (e.getSource() == playButton) {
				selectedSong = table.getSelectedRow();

				if((currThread.equals(""))){
					if (searchCount == 0){
					currThread = new PlayerThread(library.getSong(selectedSong).getPath());
					currThread.start();
					}
					else{
						currThread = new PlayerThread(miniLibrary.getSong(selectedSong).getPath());
						currThread.start();
					}
				}
				else{
					if (searchCount == 0){
					currThread.close();
					currThread = new PlayerThread(library.getSong(selectedSong).getPath());
					currThread.start();
					}
					else{
						currThread.close();
						currThread = new PlayerThread(miniLibrary.getSong(selectedSong).getPath());
						currThread.start();
					}
				}
			}
			
			else if (e.getSource() == stopButton) {
				currThread.close();
			}
			
			else if (e.getSource() == exitButton) {
				System.exit(0);
			}

			else if (e.getSource() == sortBox) {
				if (sortCounter % 2 == 0)
					library.BubbleSortArtist();
				else
					library.BubbleSort();
				java.util.Iterator<Song> it = library.getSongs();
				int numsongs = library.getnSongs();

				String[][] tableElems = new String[numsongs][3];
				String[] columnNames = {"Title", "Album", "Artist"};
				int t = 0;
				while (it.hasNext()){
					Song curr = it.next();
					tableElems[t][0] = curr.getTitle();
					tableElems[t][1] = curr.getAlbum();
					tableElems[t][2] = curr.getArtist();
					t += 1;
				}
				table.removeAll();
				table = new JTable(tableElems, columnNames );
				centerPanel.getViewport().add (table);
				updateUI();
				sortCounter += 1;
			}
			
			else if (e.getSource() == sortBox2) {
				if (sort2Counter % 2 == 0)
					library.BubbleSortAlbum();
				else
					library.BubbleSort();
				java.util.Iterator<Song> it = library.getSongs();
				int numsongs = library.getnSongs();

				String[][] tableElems = new String[numsongs][3];
				String[] columnNames = {"Title", "Album", "Artist"};
				int t = 0;
				while (it.hasNext()){
					Song curr = it.next();
					tableElems[t][0] = curr.getTitle();
					tableElems[t][1] = curr.getAlbum();
					tableElems[t][2] = curr.getArtist();
					t += 1;
				}
				table.removeAll();
				table = new JTable(tableElems, columnNames );
				centerPanel.getViewport().add (table);
				updateUI();
				sort2Counter += 1;
			}

			else if (e.getSource() == searchBox) {
				miniLibrary = new Library();
				searchCount += 1;
				String searchT = searchBox.getText().toLowerCase();;
				java.util.Iterator<Song> it = library.getSongs();
				while(it.hasNext()){
					Song curr = it.next();
					if(curr.getTitle().toLowerCase().startsWith(searchT)){
						miniLibrary.add(curr);
					}
				}
				java.util.Iterator<Song> miniIt = miniLibrary.getSongs();
				int numsongs = miniLibrary.getnSongs();

				String[][] tableElems = new String[numsongs][3];
				String[] columnNames = {"Title","Album",  "Artist"};
				int t = 0;
				while (miniIt.hasNext()){
					Song curr = miniIt.next();
					tableElems[t][0] = curr.getTitle();
					tableElems[t][1] = curr.getAlbum();
					tableElems[t][2] = curr.getArtist();
					t += 1;
				}
				table.removeAll();
				table = new JTable(tableElems, columnNames );
				centerPanel.getViewport().add (table);
				updateUI();
			}
			updateUI();
		} // actionPerformed
	} // ButtonListener
}




