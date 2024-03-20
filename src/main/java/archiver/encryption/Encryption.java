package archiver.encryption;

import java.io.File;
import java.nio.file.Files;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.javatuples.Pair;

public class Encryption {

  private static final String DEFAULT_SALT = "pepper";
  private static final int DEFAULT_SALT_ROUNDS = 10;
  private static final String DEFAULT_MAGIC = "magic bytes";
  private static final String DEFAULT_PREFIX = "E!";
  private static final int PASSWORD_KEY_LENGTH = 128;
  private static final String PASSWORD_ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final String ENCRYPTION_ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
  private static final int IV_LENGTH = 16;

  private final String salt;
  private final int saltRounds;
  private final String magic;
  private final String prefix;

  public Encryption() {
    this.salt = DEFAULT_SALT;
    this.saltRounds = DEFAULT_SALT_ROUNDS;
    this.magic = DEFAULT_MAGIC;
    this.prefix = DEFAULT_PREFIX;
  }

  public Encryption(final String salt, final int saltRounds, final String magic, final String prefix) {
    this.salt = salt;
    this.saltRounds = saltRounds;
    this.magic = magic;
    this.prefix = prefix;
  }

  protected static byte[] merge(final byte[] first, final byte[] second, final byte[]... rest) {
    if (rest.length > 0) {
      return merge(first, merge(second, rest[0]), Arrays.copyOfRange(rest, 1, rest.length));
    }

    final byte[] merged = Arrays.copyOf(first, first.length + second.length);

    System.arraycopy(second, 0, merged, first.length, second.length);

    return merged;
  }

  protected Key getKey(final String password) {
    try {
      final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PASSWORD_ALGORITHM);
      final PBEKeySpec passwordKey = new PBEKeySpec(
        password.toCharArray(),
        salt.getBytes(),
        saltRounds,
        PASSWORD_KEY_LENGTH
      );
      final SecretKey otherKey = secretKeyFactory.generateSecret(passwordKey);

      return new SecretKeySpec(otherKey.getEncoded(), ENCRYPTION_ALGORITHM);
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  public boolean isEncrypted(final File file) {
    try {
      final byte[] content = Files.readAllBytes(file.toPath());

      return (
        content.length >= prefix.length() &&
        Arrays.equals(prefix.getBytes(), Arrays.copyOfRange(content, 0, prefix.length()))
      );
    } catch (final Exception e) {
      e.printStackTrace();

      return false;
    }
  }

  public boolean isPassword(final File file, final String password) {
    try {
      final Key key = getKey(password);
      final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      final byte[] content = Files.readAllBytes(file.toPath());
      final byte[] iv = Arrays.copyOfRange(content, prefix.length(), prefix.length() + IV_LENGTH);
      final byte[] encrypted = Arrays.copyOfRange(content, prefix.length() + IV_LENGTH, content.length);

      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

      final byte[] decrypted = cipher.doFinal(encrypted);

      return (
        decrypted.length >= magic.length() &&
        Arrays.equals(magic.getBytes(), Arrays.copyOfRange(decrypted, 0, magic.length()))
      );
    } catch (final Exception e) {
      return false;
    }
  }

  protected Pair<byte[], byte[]> encrypt(final byte[] bytes, final String password) {
    try {
      final Key key = getKey(password);
      final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

      cipher.init(Cipher.ENCRYPT_MODE, key);

      final AlgorithmParameters parameters = cipher.getParameters();
      final byte[] iv = parameters.getParameterSpec(IvParameterSpec.class).getIV();
      final byte[] encrypted = cipher.doFinal(bytes);

      return new Pair<byte[], byte[]>(iv, encrypted);
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  public void encrypt(final File file, final String password) {
    try {
      final byte[] content = Files.readAllBytes(file.toPath());
      final Pair<byte[], byte[]> pair = encrypt(merge(magic.getBytes(), content), password);
      final byte[] iv = pair.getValue0();
      final byte[] encrypted = pair.getValue1();

      Files.write(file.toPath(), merge(prefix.getBytes(), iv, encrypted));
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  protected byte[] decrypt(final byte[] bytes, final String password, final byte[] iv) {
    try {
      final Key key = getKey(password);
      final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

      return cipher.doFinal(bytes);
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  public void decrypt(final File file, final String password) {
    try {
      final byte[] content = Files.readAllBytes(file.toPath());
      final byte[] iv = Arrays.copyOfRange(content, prefix.length(), prefix.length() + IV_LENGTH);
      final byte[] encrypted = Arrays.copyOfRange(content, prefix.length() + IV_LENGTH, content.length);
      final byte[] decrypted = decrypt(encrypted, password, iv);

      Files.write(file.toPath(), Arrays.copyOfRange(decrypted, magic.length(), decrypted.length));
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
