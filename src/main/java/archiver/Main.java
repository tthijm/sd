package archiver;

import archiver.handler.Handler;

public class Main {

  public static void main(String[] args) {
    //System.out.println("Welcome to Software Design!");
    Handler start = new Handler();
    start.loop();
  }
}
