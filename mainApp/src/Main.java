public class Main {

    public static void main(String args[]){
        if(args.length != 1)
            System.out.println("USAGE: java Main <path-to-music-files>");

        User user = new User();

        user.addPath(args[0]);
    }

}
