package archiver.encryption;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Arrays;
import org.javatuples.Pair;
import org.junit.Test;

public class EncryptionTest {

  @Test
  public void mergeTwoArrays() {
    final byte[] a = { 1, 2 };
    final byte[] b = { 3, 4, 5 };
    final byte[] expected = { 1, 2, 3, 4, 5 };

    final byte[] merged = Encryption.merge(a, b);

    assertArrayEquals(expected, merged);
  }

  @Test
  public void mergeThreeArrays() {
    final byte[] a = { 10, 20, 30 };
    final byte[] b = { 40, 50, 60 };
    final byte[] c = { 70, 80 };
    final byte[] expected = { 10, 20, 30, 40, 50, 60, 70, 80 };

    final byte[] merged = Encryption.merge(a, b, c);

    assertArrayEquals(expected, merged);
  }

  @Test
  public void getSameKey() {
    final Encryption encryption = new Encryption();
    final String password = "qwerty";

    final Key keyOne = encryption.getKey(password);
    final Key keyTwo = encryption.getKey(password);

    assertEquals(keyOne, keyTwo);
  }

  @Test
  public void getDifferentKeys() {
    final Encryption encryption = new Encryption();
    final String passwordOne = "password";
    final String passwordTwo = "qwerty123";

    final Key keyOne = encryption.getKey(passwordOne);
    final Key keyTwo = encryption.getKey(passwordTwo);

    assertNotEquals(keyOne, keyTwo);
  }

  @Test
  public void isNotEncrypted() throws IOException {
    final Encryption encryption = new Encryption();
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final byte[] content = "Et odio earum dolores vitae.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    assertFalse(encryption.isEncrypted(file));
  }

  @Test
  public void isEncrypted() throws IOException {
    final Encryption encryption = new Encryption();
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "1q2w3e";
    final byte[] content = "Vel qui iusto illo quod rerum.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    encryption.encrypt(file, password);

    assertTrue(encryption.isEncrypted(file));
  }

  @Test
  public void isNotPassword() throws IOException {
    final Encryption encryption = new Encryption();
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "abc123";
    final String notPassword = "aa12345678";
    final byte[] content = "Repudiandae vitae eum voluptate magni at.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    encryption.encrypt(file, password);

    assertFalse(encryption.isPassword(file, notPassword));
  }

  @Test
  public void isPassword() throws IOException {
    final Encryption encryption = new Encryption();
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "password1";
    final byte[] content = "Possimus mollitia odio dicta numquam rem aliquid id architecto.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    encryption.encrypt(file, password);

    assertTrue(encryption.isPassword(file, password));
  }

  @Test
  public void encryptAndDecryptText() {
    final Encryption encryption = new Encryption();
    final String password = "qwertyuiop";
    final String text = "Aut laboriosam maiores repellendus minima perspiciatis delectus.";

    final Pair<byte[], byte[]> pair = encryption.encrypt(text.getBytes(), password);
    final byte[] iv = pair.getValue0();
    final byte[] encrypted = pair.getValue1();
    final byte[] decrypted = encryption.decrypt(encrypted, password, iv);

    assertFalse(Arrays.equals(text.getBytes(), encrypted));
    assertArrayEquals(text.getBytes(), decrypted);
  }

  @Test
  public void encryptAndDecryptFile() throws IOException {
    final Encryption encryption = new Encryption();
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "password123";
    final byte[] content = "Recusandae nesciunt eos illo vero.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    encryption.encrypt(file, password);

    assertFalse(Arrays.equals(content, Files.readAllBytes(file.toPath())));

    encryption.decrypt(file, password);

    assertArrayEquals(content, Files.readAllBytes(file.toPath()));
  }
}
