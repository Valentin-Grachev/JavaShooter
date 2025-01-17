package com.example.shooter.server;

import com.example.shooter.client.ClientState;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class ServerDialog extends Thread {

    Server server;
    public ArrayList<Connection> connections = new ArrayList<>();
    Gson gson = new Gson();


    public ServerDialog(Server server) throws IOException {
        this.server = server;
    }


    public void run() {
        try {
            while (true) {
                // От каждого клиента постоянно принимаем его состояние
                for (int i = 0; i < connections.size(); i++) {
                    if (connections.get(i).dataFromClient.available() > 0) {
                        // Получаем данные от клиента
                        var clientState = gson.fromJson(connections.get(i).dataFromClient.readUTF(), ClientState.class);

                        if (server.clientStates.size() == i) server.clientStates.add(clientState);
                        else server.clientStates.set(i, clientState);
                    }
                }

                Thread.sleep(Server.sleepTime);
            }
        }

        catch (IOException e) {} catch (InterruptedException e) {}
    }

    // Отправка состояния сервера всем подключенным клиентам
    public void send() throws IOException {
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).dataToClient.flush();
            connections.get(i).dataToClient.writeUTF(gson.toJson(server.serverState));
        }
    }


}
