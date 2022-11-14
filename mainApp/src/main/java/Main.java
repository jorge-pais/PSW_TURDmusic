import java.awt.*;
import java.util.Scanner;
import java.io.File;

public class Main {
    // CLI interface loop
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
                    "listPath : lists all library file paths\n"+
                    "listSongs: lists all available music files\n" +
                    "open <index-of-song-in-list> : open a specified music file in OS default media player\n" +
                    "exit : exits the application\n" +
                    "readData <music-index> : print a song's metadata\n" +
                    "listAlbums: lists all added albums\n" +
                    "listArtists: lists all artists\n");
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
                    for (String i: library.getPaths())
                        System.out.println(i);
                    break;

                case "listSongs":
                    for(int i = 0; i < library.getAllSongs().toArray().length; i++)
                        System.out.println(i + " -> " + library.getAllSongs().get(i).getTitle());
                    break;

                case "listAlbums":
                    for (int i = 0; i < library.getAlbums().toArray().length; i++)
                        System.out.println(i + " -> " + library.getAlbums().get(i).getTitle());
                    break;

                case "listArtists":
                    for (int i = 0; i < library.getArtists().toArray().length; i++)
                        System.out.println(i + " -> " + library.getArtists().get(i).getName());
                    break;

                case "readData":
                    if(argument[1] == null) break;

                    int i = Integer.parseInt(argument[1]);

                    library.getAllSongs().get(i).getSongAttribute();
                    break;

                case "open":
                    if(argument[1] == null) break;

                    try{
                        int index = Integer.parseInt(argument[1]);

                        // This approach can only open one file at a time
                        File file = library.getAllSongs().get(index).getFile();
                        desktop.open(file);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                    break;

                case "exit":
                    System.out.println("Exiting");
                    break;

                default:
                    break;
            }
        } while (!argument[0].equals("exit"));
    }
}
