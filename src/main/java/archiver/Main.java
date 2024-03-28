package archiver;

import archiver.handler.Handler;

public class Main {

  public static void main(String[] args) {
    final Handler handler = Handler.getInstance();

    handler.loop();
  }
}
