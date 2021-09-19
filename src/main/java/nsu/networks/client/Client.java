package nsu.networks.client;

import java.io.*;
import java.net.Socket;


public class Client {
    public final static int BUFFER_SIZE = 1048576;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private BufferedInputStream fileInputStream;


    public Client(String filePath, String ipAddress, int port) {
        File file = new File(filePath);
        long fileLength = file.length();
        String fileName = file.getName();
        try {
            this.socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            System.err.println("ERROR: Socket failed");
        }
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            closeSocket();
        }
        try {
            this.fileInputStream = new BufferedInputStream(new FileInputStream(filePath));
            sendMessage(fileName + ";" + fileLength + ";");
            System.out.println("Starting sending of " + fileName + " size = " + getSizeString(fileLength));
            byte[] byteArray = new byte[BUFFER_SIZE];
            long sentBytes = 0;
            while (sentBytes < fileLength) {
                int bytesRead = fileInputStream.read(byteArray, 0, byteArray.length);
                if (bytesRead >= 0) sentBytes += bytesRead;
                else {
                    System.out.println("File did not loaded correctly!");
                    break;
                }
                out.write(byteArray, 0, bytesRead);
                out.flush();
            }
            System.out.println("Done!");
            readMessage();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (in != null) in.close();
                if (out != null) out.close();
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private String getSizeString(long fileLength) {
        long size = fileLength;
        int count = 0;
        long fraction = 0;
        while (size / 1024 > 0) {
            fraction = size % 1024;
            size /= 1024;
            count++;
        }
        String strSize = Integer.toString((int) size);
        fraction = fraction * 1000 / 1024;
        if (fraction != 0) {
            strSize = strSize + "," + (int) fraction;
        }
        switch (count) {
            case 1 -> strSize += " KBytes";
            case 2 -> strSize += " MBytes";
            case 3 -> strSize += " GBytes";
            case 4 -> strSize += " TBytes";
        }
        return strSize;
    }

    private void readMessage() throws IOException {
        try {
            String inputMessage = in.readUTF();
            System.out.println("Server: " + inputMessage);
        } catch (IOException exception) {
            closeSocket();
            throw new IOException("ERROR with reading from server");
        }
    }

    public void sendMessage(String message) throws IOException {
        if (message.isEmpty()) return;
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            closeSocket();
            throw new IOException("ERROR with sending to server");
        }
    }

    public void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
