package archiver.command;

import archiver.encryption.Encryption;
import archiver.format.Format;
import java.io.File;
import java.util.*;

public class Extract extends Command {

  private static final String NAME = "extract";

  protected Extract() {
    super(NAME);
  }

  public void run(File[] args, HashMap<String, String> options) {
    final boolean hasPassword = Encryption.isEncrypted(args[0]);
    Format fmt = Format.getInstance(args[0].getName());

    if (hasPassword) {
      final String password = promptPassword(args[0]);

      if (password == null) {
        System.out.println(ABORT_MESSAGE);
        return;
      }

      Encryption.decrypt(args[0], password);
      fmt.decompress(args[0], args[1]);
      Encryption.encrypt(args[0], password);
    } else {
      fmt.decompress(args[0], args[1]);
    }
  }
}
