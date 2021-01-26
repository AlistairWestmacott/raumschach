package uk.ac.cam.amw223.raumschach.online;

import uk.ac.cam.amw223.raumschach.core.*;
import uk.ac.cam.amw223.raumschach.online.messages.*;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OnlineGameClient extends game {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  Thread in;
  Thread out;


  boolean localPlayerColour;
  player localPlayer = null;
  final Socket s;
  final InputStream ins;
  final OutputStream outs;

  boolean finished = false;

  public OnlineGameClient(String server, int port) throws ExceptionInInitializerError {
    super(null, null);

    ObjectOutputStream oos;
    ObjectInputStream ois;

    try {
      s = new Socket(server, port);

      System.out.println(getCurrentTime() + " [Client] Connected to " + server + " on port " + port + ".");

      outs = s.getOutputStream();
      oos = new ObjectOutputStream(outs);

      ins = s.getInputStream();
      ois = new ObjectInputStream(ins);
    } catch (IOException e) {
      System.err.println("Cannot connect to " + server + " on port " + port);
      throw new ExceptionInInitializerError();
    }


    boolean gameRunning = false;

    Message message = null;
    boolean connected = true;
    // todo: Server discards io streams between games which is giving the client issues because the client doesn't
    while (connected) {
      try {
        message = (Message) ois.readObject();
      } catch (IOException e) {
        //System.err.println("Unable to get message: " + e.getMessage());
        System.err.println("Lost connection to server: input stream closed");
        connected = false;
      } catch (ClassNotFoundException e) {
        System.err.println("Malformed message: " + e.getMessage());
      }

      if (message != null) {
        if (message instanceof GameStartMessage && !gameRunning) {
          localPlayerColour = ((GameStartMessage) message).isWhite();
          localPlayer = new OnlinePlayerClient(((GameStartMessage) message).isWhite());
          gameRunning = true;
          b = new board();
          System.out.println(b);
          // white makes first move
          if (localPlayerColour) {
            message = new MoveMessage(getMove());
            try {
              oos.writeObject(message);
            } catch (IOException e) {
              System.err.println("Lost connection to server: output stream closed");
              connected = false;
            }
          }
          //setUpGame(((GameStartMessage) message).isWhite());
          //System.out.println(b);
        } else if (message instanceof MoveResultMessage && gameRunning) {
          try {
            // todo maybe this needs to be replaced by a method in board
            //  that updates the internal state with that of the passed board
            b.makeMove(
                    ((MoveResultMessage) message).getMove(),
                    ((MoveResultMessage) message).getLastMoveColour()
            );
            System.out.println(b);
            if (((MoveResultMessage) message).getLastMoveColour() != localPlayerColour) {
              message = new MoveMessage(getMove());
              try {
                oos.writeObject(message);
              } catch (IOException e) {
                System.err.println("Lost connection to server: output stream closed.");
                connected = false;
              }
            }
          } catch (InvalidMoveException e) {
            if (((MoveResultMessage) message).getLastMoveColour() == localPlayerColour) {
              System.out.println("Your move was invalid: " + e.reason());
            }
            // else: opponent made a bad move, can safely ignore
          }
        } else if (message instanceof GameEndMessage && gameRunning) {
          System.out.println("You are the "
                  + (((GameEndMessage) message).getWinner() == localPlayerColour? "winner!" : "loser."));
          gameRunning = false;
        } else {
          System.err.println(message.getClass().toString() + " message received out of order");
        }
      }
    }

    try {
      ois.close();
      oos.close();
    } catch (IOException ignored) {}
  }

  @Override
  public void playGame() {
    /*Thread out = new Thread(() -> {
      Message message;
      String text;

      while (!isFinished()) {
        try {
          // message handling here
          Move moveToMake = null;
          boolean valid = false;
          while (!valid) {
            try {
              moveToMake = getMove();
              b.validateMove(moveToMake, localPlayerColour);
              valid = true;
            } catch (InvalidMoveException e) {
              System.out.println("Your move was invalid: " + e.reason());
            }
          }
          message = new MoveMessage(moveToMake);
          oos.writeObject(message);
        } catch (IOException e) {
          System.err.println("Error writing message to server");
          finished = true;
        }
      }
      try {
        oos.close();
      } catch (IOException e) {
        System.err.println("Error closing object output stream: " + e.getMessage());
      }

    });
    out.setDaemon(true);
    out.start();
     */
  }

  /*private synchronized boolean finish() {
    return finished = true;
  }*/

  private synchronized boolean isFinished() {
    return finished;
  }

  private synchronized Move getMove() {
    Move moveToMake = null;
    boolean valid = false;
    while (!valid) {
      try {
        // todo: add support for generating resignation moves (i.e. resigning)
        moveToMake = localPlayer.getMove();
        b.validateMove(moveToMake, localPlayerColour);
        valid = true;
      } catch (InvalidMoveException e) {
        System.out.println("Your move was invalid: " + e.reason());
      }
    }
    return moveToMake;
  }

  /*private synchronized void setUpGame(boolean isWhite) {
    localPlayerColour = isWhite;
    localPlayer = new OnlinePlayerClient(isWhite);
    playGame();
  }*/

  static String getCurrentTime() {
    return dateFormat.format(new Date());
  }
}
