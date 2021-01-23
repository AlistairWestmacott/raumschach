package uk.ac.cam.amw223.raumschach.online;

import uk.ac.cam.amw223.raumschach.core.Move;
import uk.ac.cam.amw223.raumschach.core.player;
import uk.ac.cam.amw223.raumschach.online.messages.*;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientHandler extends player {

  private final int ID;
  private String nickname;
  private final SafeQueue<Message> clientMessages = new SafeQueue<>();

  private boolean connected = true;

  private final Queue<Move> moveQueue = new LinkedList<>();

  public ClientHandler(boolean isWhite, int ID, final InputStream ins, final OutputStream outs) {
    super(isWhite);
    this.ID = ID;

    setConnected(true);
    // communication from client
    Thread in = new Thread(() -> {
              /*InputStream ins;
              try {
                ins = s.getInputStream();
              } catch (IOException e) {
                System.err.println("[Client@" + s.getPort() + "] Cannot retrieve input stream.");
                return;
              }*/

              ObjectInputStream ois;

              try {
                ois = new ObjectInputStream(ins);
              } catch (IOException e) {
                System.err.println("[Client" + ID + "] Unable to get object input stream.");
                // todo: better error handling, this is fatal!
                setConnected(false);
                return;
              }

              Message message;
              while (true) {
                message = null;
                try {
                  message = (Message) ois.readObject();
                } catch (IOException e) {
                  System.out.println("[Client" + ID + "] disconnected");
                  setConnected(false);

                  // todo: rather than returning here, send message to online game server that game ended?
                  //  allow server to check whether client is still connected
                  //  maybe in getMove
                  return;
                } catch (ClassNotFoundException e) {
                  System.err.println("Malformed message: " + e.getMessage());
                }

                if (message != null) {
                  System.out.println("[Client" + ID + "] " + message);
                  if (message instanceof MoveMessage) {
                    synchronized (moveQueue) {
                      moveQueue.add(((MoveMessage) message).getMove());
                      moveQueue.notify();
                    }
                  }
                  // other message types ignored
                }
              }
            });
    in.setDaemon(true);
    in.start();

    // communication to client
    Thread out = new Thread(() -> {
      //OutputStream outs;
      ObjectOutputStream oos;
      try {
        //outs = s.getOutputStream();
        oos = new ObjectOutputStream(outs);
      } catch (IOException e) {
        System.err.println("[Client" + ID + "] Unable to get object output stream.");
        setConnected(false);
        return;
      }

      Message message;
      while (true) {
        message = clientMessages.get();
        try {
          oos.writeObject(message);
          oos.flush();
        } catch (IOException e) {
          System.err.println(e.getMessage());
          return;
        }
      }
    });
    out.setDaemon(true);
    // start reading later
    out.start();

    sendMessage(new GameStartMessage(isWhite));
  }

  @Override
  public Move getMove() {
    boolean valid = false;
    MoveResultMessage message;
    synchronized (moveQueue) {
      while (moveQueue.isEmpty()) {
        try {
          moveQueue.wait();
        } catch (InterruptedException e) {
          System.err.println(e.getMessage());
        }
      }
      return moveQueue.remove();
    }
  }

  public synchronized void sendMessage(Message msg) {
    System.out.println("[Server -> Client" + ID + "] " + msg);
    clientMessages.add(msg);
  }

  public synchronized boolean isConnected() {
    return connected;
  }

  private synchronized void setConnected(boolean connected) {
    this.connected = connected;
  }

  public int getID() {
    return ID;
  }

}
