package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainCLI {
    //
    // Basic CLI interface loop
    // We should re-do
    //
    public static void main(String[] args) {

        System.out.print(
                "Yet nameless music organizer CLI:\n" +
                "Enter \"help\" for a list of available commands\n");
        Scanner scan = new Scanner(System.in);
        String cmd; String[] argument;

        Library library = new Library();

        Desktop desktop = Desktop.getDesktop();

        do {
            System.out.print(">");
            cmd = scan.nextLine();
            argument = cmd.split("\\s+"); // Separate command by whitespace

            switch (argument[0]){
                case "help":
                    System.out.print(
                    "addPath <Path-to-folder> : adds a path to library and scans it for music files\n" +
                    "removePath <Path-to-folder> : removes a path from the library\n" +
                    "listPath : lists all library file paths\n" +
                    "listSongs: lists all available music files\n" +
                    "open <index-of-song-in-list> : open a specified music file in OS default media player\n" +
                    "exit : exits the application\n" +
                    "readData <music-index> : print a song's metadata\n" +
                    "listAlbums: lists all added albums\n" +
                    "listArtists: lists all artists\n" +
                    "newPlaylist <playlistName> <songIndex[]>: creates a new playlist entry\n"+
                    "listPlaylists: lists all playlists\n");
                    break;

                case "addPath":
                    if(argument[1] != null)
                        library.addPath(argument[1]);
                    else
                        System.out.println("Usage: addPath <Path-to-folder>");
                    break;

                case "removePath":
                    if(argument[1] != null)
                        library.removePath(argument[1]);
                    break;

                case "listPath":
                    for (String i: library.getLibraryPaths())
                        System.out.println(i);
                    break;

                case "listSongs":
                    for(Music i : library.getSongs())
                        System.out.println(i.id + "->" + i.getTitle());
                    break;

                case "listAlbums":
                    for (Album i : library.getAlbums())
                        System.out.println(i.id + "->" + i.getTitle());
                    break;

                case "listArtists":
                    for (Artist i : library.getArtists())
                        System.out.println(i.id + " -> " + i.getName());
                    break;

                case "readData":
                    if(argument[1] == null) break;

                    int index = Integer.parseInt(argument[1]);

                    library.getSongs().get(index).getSongAttribute();
                    break;

                case "open":
                    if(argument[1] == null) break;

                    try{
                        // HOW IS THIS THE SAME SCOPE AS THE PREVIOUS INDEX???
                        index = Integer.parseInt(argument[1]);

                        // This approach can only open one file at a time
                        File file = library.getSongs().get(index).getFile();
                        desktop.open(file);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;
                // newPlaylist <playlistName> <songIndex1> ... <songIndexN>
                case "newPlaylist":
                    if(argument.length == 1) break;

                    ArrayList<Music> initialSongs = new ArrayList<>();

                    for (int i = 2; i < argument.length; i++)
                        initialSongs.add(library.getSongs().get(i));

                    Playlist playlist = new Playlist(argument[1], initialSongs);
                    library.getPlaylists().add(playlist);

                    break;

                case "listPlaylists":
                    for (int i = 0; i < library.getPlaylists().toArray().length; i++)
                        System.out.println(i + " -> " + library.getPlaylists().get(i).getTitle());
                    break;

                case "saveLibrary":
                    if(argument.length == 1) break;
                    try {
                        library.saveLibrary(argument[1]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case "loadLibrary":
                    if(argument.length == 1) break;

                    try {
                        library = Library.loadLibrary(argument[1]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(library == null)
                        System.out.println("error");

                    break;

                /*case "getArtwork": // Test to read album cover as bitmap
                    if(argument[1] == null) break;
                    Music music = library.getAllSongs().get(Integer.parseInt(argument[1]));

                    try {
                        File output = new File("albumCoverTest.bmp");
                        FileOutputStream outputStream = new FileOutputStream(output);

                        outputStream.write(music.readSongArtwork());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;*/

                default:
                    break;
            }
        } while (!argument[0].equals("exit"));

        System.out.println("Exiting");
    }
}
