package server;

import dao.ClientDao;
import dao.RequestDao;
import entity.Client;
import entity.Request;
import entity.RequestStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable{

    private static Socket clientDialog;

    public ClientHandler(Socket client) {
        clientDialog = client;
    }

    @Override
    public void run() {

        try {
            // инициируем каналы общения в сокете, для сервера
            // канал записи в сокет следует инициализировать сначала канал чтения для избежания блокировки выполнения программы на ожидании заголовка в сокете
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());

            // канал чтения из сокета
            DataInputStream in = new DataInputStream(clientDialog.getInputStream());

            System.out.println("DataInputStream created");
            System.out.println("DataOutputStream  created");
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");

                // серверная нить ждёт в канале чтения (inputstream) получения
                // данных клиента после получения данных считывает их
                String entry = in.readUTF();

                // и выводит в консоль
                System.out.println("READ from clientDialog message - " + entry);

                // инициализация проверки условия продолжения работы с клиентом
                // по этому сокету по кодовому слову - quit в любом регистре
                if (entry.equalsIgnoreCase("quit")) {

                    // если кодовое слово получено то инициализируется закрытие
                    // серверной нити
                    System.out.println("Client initialize connections suicide ...");
                    out.writeUTF("Server reply - " + entry + " - OK");
                    Thread.sleep(3000);
                    break;
                }
                // создаем нового клиента
                String[] clientData = ClientHandler.parse(entry);
                Client client = new Client();
                client.setLogin(clientData[0]);
                client.setPassword(clientData[1]);

                out.writeUTF("Уважаемый, " + client.getLogin() + "! Регистрация прошла успешно");

                // добавляем его в базу
                ClientDao clientDao = ClientDao.getInstance();
                clientDao.createTable();
                clientDao.add(client);

                // создаем новую заявку
                Request request = new Request();
                request.setClientId(client.getId());
                request.setCreationTime(new Date());
                request.setRequestStatus(RequestStatus.CREATED);

                // добавляем ее в базу
                RequestDao requestDao = RequestDao.getInstance();
                requestDao.createTable();
                requestDao.add(request);

                // отвечаем клиенту
                out.writeUTF("Ваша заявка принята. ");
                System.out.println("Server Wrote message to clientDialog.");

                // освобождаем буфер сетевых сообщений
                out.flush();

                // возвращаемся в началло для считывания нового сообщения
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // если условие выхода - верно выключаем соединения
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            in.close();
            out.close();

            // потом закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static String[] parse(String string){
        String[] clientData = string.split(" ");
        return clientData;
    }
}