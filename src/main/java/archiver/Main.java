package archiver;

import archiver.handler.Handler;

public class Main {

  public static void main(String[] args) {
    Handler start = new Handler();
    start.loop();
  }
}
