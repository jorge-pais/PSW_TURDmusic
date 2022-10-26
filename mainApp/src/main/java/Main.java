import java.awt.*;
import java.util.Scanner;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        System.out.print(
                "Yet nameless music organizer CLI:\n" +
                "Enter \"help\" for a list of available commands\n");
        Scanner scan = new Scanner(System.in);
        String cmd; String[] argument;

        User user = new User();

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
                    "list: lists all available music files\n" +
                    "open <index-of-song-in-list> : open a specified music file in OS default media player\n" +
                    "exit : exits the application\n" +
                    "readData <music-index> : print a song's metadata\n" );
                    break;

                case "addPath":
                    if(argument[1] != null)
                        user.addPath(argument[1]);
                    else
                        System.out.println("Usage: addPath <Path-to-folder>");
                    break;

                case "removePath":
                    if(argument[1] != null)
                        user.removePath(argument[1]);
                    break;

                case "listPath":
                    for (String i: user.getPaths())
                        System.out.println(i);
                    break;

                case "list":
                    for(int i = 0; i < user.getAllSongs().toArray().length; i++)
                        System.out.println(i + "->" +user.getAllSongs().get(i).getTitle());
                    break;

                case "readData":
                    if(argument[1] == null) return;

                    int i = Integer.parseInt(argument[1]);

                    user.getAllSongs().get(i).getSongAttribute();
                    break;

                case "open":
                    if(argument[1] == null) break;

                    try{
                        int index = Integer.parseInt(argument[1]);

                        // This approach can only open one file at a time
                        File file = user.getAllSongs().get(index).getFile();
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
