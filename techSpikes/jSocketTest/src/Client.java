import java.net.*;
import java.io.*;

public class Client {

    // initialize socket and input output streams
    private Socket socket           = null;
    private DataOutputStream out    = null;
    private File file               = null;

    // constructor to put ip address and port
    public Client(String address, int port, String filepath) throws Exception{

        // establish a connection
        socket = new Socket(address, port);
        System.out.println("Connected");

        // send output to the socket
        out    = new DataOutputStream(socket.getOutputStream());
        file = new File(filepath);

        // Send the file
        if(file.canRead()){
            String name = file.getName();
            long fileSize = file.length();

            out.writeUTF(name);
            out.writeLong(fileSize);

            int count = 0;
            byte[] buffer = new byte[64*1024]; // 64kB chunks

            FileInputStream fileStream = new FileInputStream(file);

            out.flush();
            //Read the file and send to socket
            while((count = fileStream.read(buffer)) != -1){
                out.write(buffer, 0, count);
                out.flush();
            }
            fileStream.close();
        }

        // close the connection

        out.close();
        socket.close();

    }

    public static void main(String args[]){
        if (args.length != 3) {
            System.out.println("USAGE: java Client <address> <port>");
            return;
        }
        try {
            Client client = new Client(args[0], Integer.parseInt(args[1]), args[2]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}