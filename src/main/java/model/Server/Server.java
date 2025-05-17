//
//package model.Server;
//
//import java.io.*;
//import java.net.*;
//
//import java.util.concurrent.*;
//
//public class Server {
//    private static final int PORT = 5555;
//
//    private static ConcurrentHashMap  <Integer, ConcurrentHashMap<Integer,PrintWriter> > clients = new ConcurrentHashMap<>();
//
//    public static void main(String[] args) throws IOException {
//        System.out.println("Server started...");
//        ServerSocket serverSocket = new ServerSocket(PORT);
//
//        while (true) {
//            Socket socket = serverSocket.accept();
//
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            int code = Integer.parseInt(in.readLine().trim()) ;
//            int playerId = Integer.parseInt(in.readLine().trim()) ;
//
//            int serverIsignedId = 0 ;
//            if (clients.containsKey(code) ){
//                serverIsignedId = 2 ;
//                clients.get(code).put(serverIsignedId, out);
//                out.println(serverIsignedId);
//                broadcast("ROOM:"+code+";NEW_PLAYER_JOIN;",code ,serverIsignedId);
//
//            }else if (!clients.containsKey(code)){
//                serverIsignedId = 1 ;
//                ConcurrentHashMap<Integer,PrintWriter> temp = new ConcurrentHashMap<>();
//                temp.put(serverIsignedId, out);
//                clients.put(code, temp);
//                out.println(serverIsignedId);
//            }else {
//                System.out.println("Erruer can not join the room !!");
//            }
//
//            if (playerId == 0) return;
//
//            System.out.println("Client connected as Player of Id : " + playerId +" and with ID On server : "+serverIsignedId);
//
//            new Thread(() -> handleClientGame(playerId , in)).start();
//        }
//    }
//
//    private static void handleClientGame(int playerId, BufferedReader in) {
//
//        try {
//            // from who send : ROOM: 123 ;PLAYER_SERVER_ID: 1 | 2 ;PLAYER_MOVE: X,Y; PLAYER_SHOT: X,Y ;ANNEMIE_APPEARS: X,Y ;PLAYER_START;
//            // the message send to the PLAYER_SERVER_ID: 1 | 2 ;PLAYER_MOVE: X,Y; PLAYER_SHOT: X,Y ;ANNEMIE_APPEARS: X,Y ;PLAYER_START;
//            int code;
//            int id ;
//            String line;
//
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//                String[] playerInfo = line.split(";");
//                code = Integer.parseInt(playerInfo[0].split(":")[1]);
//                id = Integer.parseInt(playerInfo[1].split(":")[1]);
//
//                String message ="PLAYER_SERVER_ID:"+id+";";
//
//                int i = 0;
//
//                while( i < playerInfo.length){
//                    if (playerInfo[i].startsWith("PLAYER_START")) {
//                        message += "PLAYER_START";
//                    }else if(playerInfo[i].startsWith("PLAYER_MOVE:")){
//                        String[] position = playerInfo[i].split(":")[1].split(",");
//                        message += "PLAYER_MOVE:"+position[0]+","+position[1]+";";
//                    }else if(playerInfo[i].startsWith("PLAYER_SHOOT:")){
//                        String[] position = playerInfo[i].split(":")[1].split(",");
//                        message += "PLAYER_SHOOT:"+position[0]+","+position[1]+";";
//                    }else if(playerInfo[i].startsWith("ENIMIE:")){
//                        String[] position = playerInfo[i].split(":")[1].split(",");
//                        message += "ENIMIE:"+position[0]+","+position[1]+";";
//                    }else if (playerInfo[i].startsWith("GAME_START")) {
//                        message += "GAME_START;";
//                    }else if (playerInfo[i].startsWith("GAME_STOP")){
//                        message += "GAME_STOP;";
//                    }else if (playerInfo[i].startsWith("PLAYER_START")){
//                        message += "PLAYER_START;";
//                    }else if (playerInfo[i].startsWith("GAME_START")){
//                        message += "GAME_START;";
//                    }
//                    i++;
//
//                }
//                broadcast(message,code,id);
//            }
//        } catch (IOException e) {
//            System.out.println("Player " + playerId + " disconnected.");
//        } finally {
////            clients.remove(code);
//        }
//    }
//    private static void broadcast(String message, int code, int id) {
//        ConcurrentHashMap<Integer, PrintWriter> map = clients.get(code);
//        if (map == null) {
//            System.out.println("No client map for code: " + code);
//            return;
//        }
//
//        int targetId = (id == 2) ? 1 : 2;
//        PrintWriter targetOut = map.get(targetId);
//
//        if (targetOut != null) {
//            targetOut.println(message);
//        } else {
//            System.out.println("Target player " + targetId + " is not connected in room " + code);
//        }
//    }
//
//}
package model.Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 5555;
    private static ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<>();

    private static class Room {
        ConcurrentHashMap<Integer, PrintWriter> players = new ConcurrentHashMap<>();
        boolean isGameStarted = false;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server started on port " + PORT);
        System.out.println("Waiting for players to connect...");
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New connection from: " + socket.getInetAddress());
            handleNewConnection(socket);
        }
    }

    private static void handleNewConnection(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int roomId = Integer.parseInt(in.readLine().trim());
            int playerId = Integer.parseInt(in.readLine().trim());

            Room room = rooms.computeIfAbsent(roomId, k -> {
                System.out.println("Created new room: " + k);
                return new Room();
            });

            int playerServerId;

            synchronized (room) {
                if (room.players.size() >= 2) {
                    System.out.println("Room " + roomId + " is full. Connection rejected.");
                    out.println(0);
                    return;
                }

                playerServerId = room.players.size() + 1;
                room.players.put(playerServerId, out);
                out.println(playerServerId);

                System.out.println("Player " + playerId + " joined room " + roomId + " as player " + playerServerId);

                if (room.players.size() == 2) {
                    System.out.println("Room " + roomId + " is now full. Starting game...");
                    broadcast("NEW_PLAYER_JOIN;GAME_START", roomId);
                    room.isGameStarted = true;
                } else {
                    System.out.println("Waiting for another player to join room " + roomId);
                }
            }

            new Thread(() -> handlePlayerMessages(roomId, playerServerId, in)).start();

        } catch (IOException e) {
            System.out.println("Error handling connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handlePlayerMessages(int roomId, int playerServerId, BufferedReader in) {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Room " + roomId + " - Player " + playerServerId + " sent: " + message);
                
                Room room = rooms.get(roomId);
                if (room != null && room.isGameStarted) {
                    if (message.contains(";CHAT:")) {
                        broadcast(message, roomId);
                    } else {
                        int otherPlayerId = (playerServerId == 1) ? 2 : 1;
                        PrintWriter otherPlayer = room.players.get(otherPlayerId);
                        if (otherPlayer != null) {
                            otherPlayer.println(message);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Player " + playerServerId + " disconnected from room " + roomId);
            handlePlayerDisconnect(roomId, playerServerId);
        }
    }

    private static void handlePlayerDisconnect(int roomId, int playerServerId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.players.remove(playerServerId);
            System.out.println("Player " + playerServerId + " removed from room " + roomId);

            if (room.players.isEmpty()) {
                rooms.remove(roomId);
                System.out.println("Room " + roomId + " removed as it is empty");
            } else {
                broadcast("PLAYER_DISCONNECTED;" + playerServerId, roomId);
                System.out.println("Notified remaining players in room " + roomId + " about disconnect");
            }
        }
    }

    private static void broadcast(String message, int roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            System.out.println("Broadcasting to room " + roomId + ": " + message);
            room.players.values().forEach(out -> out.println(message));
        }
    }
}