package uk.ac.cam.amw223.raumschach.online;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static java.lang.Thread.yield;

public class Server {

  private static class counter {
    static int count = 0;
    public static int next() {
      return count++;
    }
  }

  public static void main(String[] args) {
    int port;
    if (args.length == 1) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("[Server] Usage: java RaumschachServer <port>");
        return;
      }
    } else {
      System.err.println("[Server] Usage: java  RaumschachServer <port>");
      return;
    }

    ServerSocket ss;
    SafeQueue<Integer> lobby = new SafeQueue<>();
    List<OnlineGameServer> ongoing = new ArrayList<>();

    Map<Integer, ClientHandler> clientHandlers = new HashMap<>();

    try {
      ss = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println("[Server] Cannot use port number " + port);
      return;
    }

    System.out.println("[Server] Server ready on port " + port);

    Thread cleanupFinishedGames = new Thread(() -> {
      while (true) {
        synchronized (ongoing) {
          //ongoing.wait();
          int i = 0;
          while (i < ongoing.size()) {
            OnlineGameServer g = ongoing.get(i);
            if (g.isFinished()) {
              System.out.println("[Server] Game ended, remaining players added to lobby");
              g.getRemainingConnectedPlayers().forEach(lobby::add);
              ongoing.remove(g);
            } else {
              i++;
            }
          }
        }
        yield();
      }
    });
    cleanupFinishedGames.setDaemon(true);
    //cleanupFinishedGames.start();

    Thread createGames = new Thread(() -> {
      while (true) {
        synchronized (lobby) {
          try {
            lobby.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          while (lobby.size() > 1) {
            int ID1 = lobby.get();
            int ID2 = lobby.get();

            ClientHandler p1 = clientHandlers.get(ID1);
            ClientHandler p2 = clientHandlers.get(ID2);

            if (p1.isConnected() && p2.isConnected()) {
              p1.startGame(true);
              p2.startGame(false);
              OnlineGameServer newGame = new OnlineGameServer(p1, p2, lobby);
              ongoing.add(newGame);
              Thread t = new Thread(newGame::playGame);
              t.start();
            } else {
              if (p1.isConnected()) lobby.add(p1.getID());
              else System.out.println("[Client" + p1.getID() + "] Disconnected");

              if (p2.isConnected()) lobby.add(p2.getID());
              else System.out.println("[Client" + p2.getID() + "] Disconnected");
            }
          }
        }
      }
    });
    createGames.setDaemon(true);
    createGames.start();

    while (true) {
      Socket client;
      try {
        client = ss.accept();
        int ID = counter.next();
        System.out.println("[Server] New connection: Client" + ID);
        ClientHandler ch = new ClientHandler(ID, client.getInputStream(), client.getOutputStream());
        clientHandlers.put(ID, ch);
        lobby.add(ID);
        synchronized (lobby) {lobby.notify();}
      } catch (IOException e) {
        System.err.println("IOException when accepting on ServerSocket. Is this the client disconnecting?");
        e.printStackTrace();
      }
    }
  }

}
