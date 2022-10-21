import java.net.*;
import java.io.*;

public class Server {

    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       = null;
    private File            file     = null;

    // constructor with port
    public Server(int port) throws Exception{
        // starts server and waits for a connection
        server = new ServerSocket(port);
        System.out.println("Server started");

        System.out.println("Waiting for a client ...");

        socket = server.accept();
        System.out.println("Client accepted");

        // takes input from the client socket
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        // get file info
        String fileName = null;
        long fileSize;
        fileName = in.readUTF();
        System.out.println("filename: " + fileName);
        fileSize = in.readLong();
        System.out.println("size: " + fileSize);

        file = new File(fileName);
        FileOutputStream fileOutStream = new FileOutputStream(file);

        byte[] buffer = new byte[64*1024];
        int bytesRead;
        while(fileSize > 0 && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1){
            fileOutStream.write(buffer, 0, bytesRead);
            fileSize -= bytesRead;
        }

        System.out.println("Closing connection");

        // close the file
        fileOutStream.close();
        // close connection
        socket.close();
        in.close();

    }

    public static void main(String args[]){
        if (args.length != 1) {
            System.out.println("USAGE: java Server <port>");
            return;
        }
        try {
            Server server = new Server(Integer.parseInt(args[0]));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}