package uk.ac.cam.amw223.raumschach.core;

import uk.ac.cam.amw223.raumschach.online.OnlineGameClient;
import uk.ac.cam.amw223.tinyPlanet.graphicsApplication;

public class Main {

  public static void main(String[] args) {

    game g;
    boolean online = true;

    if (online) {
      if (args.length != 2) {
        System.err.println("This application requires two arguments: <machine> <port>");
        return;
      }
      String server = args[0];
      int port;
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        System.err.println("This application requires two arguments: <machine> <port>");
        return;
      }
      try {
        // todo: maybe receive messages here then create online games whenever the new game message received
        //  will need to be in a second thread (but this can be done later, all game rules will need to be in
        //  another thread as graphics need first thread)
        g = new OnlineGameClient(server, port);
      } catch (ExceptionInInitializerError e) {
        System.err.println(e.getMessage());
        //return; // only needed when graphics is used which are disabled for server development
      }
    } else {
      player p1 = new player(true);
      player p2 = new player(false);
      g = new LocalGame(p1, p2);
      Thread t = new Thread(g::playGame);
      t.setDaemon(true);
      t.start();
    }

    //graphicsApplication ga = new graphicsApplication();
    //ga.linkBoard(g.b);
    //ga.run();
  }
}
