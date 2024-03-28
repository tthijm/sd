package archiver.encryption;

import java.io.File;
import java.nio.file.Files;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.javatuples.Pair;

public class Encryption {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int SALT_LENGTH = 8;
  private static final int SALT_ROUNDS = 10;
  private static final String MAGIC = "magic bytes";
  private static final String PREFIX = "E!";
  private static final int PASSWORD_KEY_LENGTH = 128;
  private static final String PASSWORD_ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final String ENCRYPTION_ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
  private static final int IV_LENGTH = 16;

  protected static byte[] merge(final byte[] first, final byte[] second, final byte[]... rest) {
    if (rest.length > 0) {
      return merge(first, merge(second, rest[0]), Arrays.copyOfRange(rest, 1, rest.length));
    }

    final byte[] merged = Arrays.copyOf(first, first.length + second.length);

    System.arraycopy(second, 0, merged, first.length, second.length);

    return merged;
  }

  protected static byte[] getRandomSalt() {
    final byte[] salt = new byte[SALT_LENGTH];

    RANDOM.nextBytes(salt);

    return salt;
  }

  protected static Key getKey(final String password, final byte[] salt) {
    try {
      final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PASSWORD_ALGORITHM);
      final PBEKeySpec passwordKey = new PBEKeySpec(password.toCharArray(), salt, SALT_ROUNDS, PASSWORD_KEY_LENGTH);
      final SecretKey otherKey = secretKeyFactory.generateSecret(passwordKey);

      return new SecretKeySpec(otherKey.getEncoded(), ENCRYPTION_ALGORITHM);
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  public static boolean isEncrypted(final File file) {
    try {
      final byte[] content = Files.readAllBytes(file.toPath());

      return (
        content.length >= PREFIX.length() &&
        Arrays.equals(PREFIX.getBytes(), Arrays.copyOfRange(content, 0, PREFIX.length()))
      );
    } catch (final Exception e) {
      e.printStackTrace();

      return false;
    }
  }

  public static boolean isPassword(final File file, final String password) {
    try {
      final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      final byte[] content = Files.readAllBytes(file.toPath());
      final byte[] salt = Arrays.copyOfRange(content, PREFIX.length(), PREFIX.length() + SALT_LENGTH);
      final byte[] iv = Arrays.copyOfRange(
        content,
        PREFIX.length() + SALT_LENGTH,
        PREFIX.length() + SALT_LENGTH + IV_LENGTH
      );
      final byte[] encrypted = Arrays.copyOfRange(content, PREFIX.length() + SALT_LENGTH + IV_LENGTH, content.length);
      final Key key = getKey(password, salt);

      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

      final byte[] decrypted = cipher.doFinal(encrypted);

      return (
        decrypted.length >= MAGIC.length() &&
        Arrays.equals(MAGIC.getBytes(), Arrays.copyOfRange(decrypted, 0, MAGIC.length()))
      );
    } catch (final Exception e) {
      return false;
    }
  }

  protected static Pair<byte[], byte[]> encrypt(final byte[] bytes, final String password, final byte[] salt) {
    try {
      final Key key = getKey(password, salt);
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

  public static void encrypt(final File file, final String password) {
    try {
      final byte[] content = Files.readAllBytes(file.toPath());
      final byte[] salt = getRandomSalt();
      final Pair<byte[], byte[]> pair = encrypt(merge(MAGIC.getBytes(), content), password, salt);
      final byte[] iv = pair.getValue0();
      final byte[] encrypted = pair.getValue1();

      Files.write(file.toPath(), merge(PREFIX.getBytes(), salt, iv, encrypted));
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  protected static byte[] decrypt(final byte[] bytes, final String password, final byte[] salt, final byte[] iv) {
    try {
      final Key key = getKey(password, salt);
      final Cipher cipher = Cipher.getInstance(TRANSFORMATION);

      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

      return cipher.doFinal(bytes);
    } catch (final Exception e) {
      e.printStackTrace();

      return null;
    }
  }

  public static void decrypt(final File file, final String password) {
    try {
      final byte[] content = Files.readAllBytes(file.toPath());
      final byte[] salt = Arrays.copyOfRange(content, PREFIX.length(), PREFIX.length() + SALT_LENGTH);
      final byte[] iv = Arrays.copyOfRange(
        content,
        PREFIX.length() + SALT_LENGTH,
        PREFIX.length() + SALT_LENGTH + IV_LENGTH
      );
      final byte[] encrypted = Arrays.copyOfRange(content, PREFIX.length() + SALT_LENGTH + IV_LENGTH, content.length);
      final byte[] decrypted = decrypt(encrypted, password, salt, iv);

      Files.write(file.toPath(), Arrays.copyOfRange(decrypted, MAGIC.length(), decrypted.length));
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
