import nsu.networks.client.Client;

import static java.lang.Integer.parseInt;

public class Main {

    public static String ipAddr;
    public static int port;
    public static String path;

    public static void main(String[] args) {
        if(args.length != 3){
            System.err.println("Server needs directory of file, ip-address, port in the Program arguments\nExample: java -jar ClientFileSenderTCP.jar /home/kirill/Networks/SendFileTCP/Irtegov.pdf 127.0.1.1 8080");
        }
        else{
            path = args[0];
            ipAddr = args[1];
            port = parseInt(args[2]);
            Client client = new Client(path, ipAddr, port);
        }
    }
}