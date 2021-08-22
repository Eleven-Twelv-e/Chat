import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class RunnableClient implements Runnable {



    private PrintWriter outMessage;

    private BufferedReader inMessage;

    private static int numberOfClients = 0;

    String name;


    public RunnableClient(Socket socket) {

        numberOfClients++;
        try {
            outMessage = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RunnableClient(){}


    @Override
    public void run() {

        try {
            name = inMessage.readLine();
            sendMessageToAllClients("Пользователь " + name + " вошел в чат." +
                    " Количество участников чата равно: " + numberOfClients, null);

            String line;
            while (true) {
                while ((line = inMessage.readLine()) != null) {
                    if ("/exit".equals(line)) {
                        return;
                    }
                    sendMessageToAllClients(line, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    private synchronized void sendMessageToAllClients(String msg, RunnableClient thisClient) {
        log(msg);
        for (RunnableClient client : Server.CLIENTS) {
            if (client != thisClient) {
                client.sendMsg(msg);
            }
        }
    }

    static void log(String msg) {
        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        System.out.printf("[%s] %s\n", dateNow, msg);

        File log = new File("src/main/resources/serverLog.txt");
        try (FileWriter fw = new FileWriter(log, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.format("[%s] %s\n", dateNow, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void close() {

        Server.removeClient(this, Server.CLIENTS);
        numberOfClients--;
        sendMessageToAllClients("Пользователь " + name + " покинул чат." +
                " Количество участников чата равно: " + numberOfClients, this);
    }
}