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
    final String password = "qwerty";
    final byte[] salt = Encryption.getRandomSalt();

    final Key keyOne = Encryption.getKey(password, salt);
    final Key keyTwo = Encryption.getKey(password, salt);

    assertEquals(keyOne, keyTwo);
  }

  @Test
  public void getDifferentKeys() {
    final String passwordOne = "password";
    final String passwordTwo = "qwerty123";
    final byte[] salt = Encryption.getRandomSalt();

    final Key keyOne = Encryption.getKey(passwordOne, salt);
    final Key keyTwo = Encryption.getKey(passwordTwo, salt);

    assertNotEquals(keyOne, keyTwo);
  }

  @Test
  public void isNotEncrypted() throws IOException {
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final byte[] content = "Et odio earum dolores vitae.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    assertFalse(Encryption.isEncrypted(file));
  }

  @Test
  public void isEncrypted() throws IOException {
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "1q2w3e";
    final byte[] content = "Vel qui iusto illo quod rerum.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    Encryption.encrypt(file, password);

    assertTrue(Encryption.isEncrypted(file));
  }

  @Test
  public void isNotPassword() throws IOException {
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "abc123";
    final String notPassword = "aa12345678";
    final byte[] content = "Repudiandae vitae eum voluptate magni at.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    Encryption.encrypt(file, password);

    assertFalse(Encryption.isPassword(file, notPassword));
  }

  @Test
  public void isPassword() throws IOException {
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "password1";
    final byte[] content = "Possimus mollitia odio dicta numquam rem aliquid id architecto.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    Encryption.encrypt(file, password);

    assertTrue(Encryption.isPassword(file, password));
  }

  @Test
  public void encryptAndDecryptText() {
    final String password = "qwertyuiop";
    final String text = "Aut laboriosam maiores repellendus minima perspiciatis delectus.";
    final byte[] salt = Encryption.getRandomSalt();

    final Pair<byte[], byte[]> pair = Encryption.encrypt(text.getBytes(), password, salt);
    final byte[] iv = pair.getValue0();
    final byte[] encrypted = pair.getValue1();
    final byte[] decrypted = Encryption.decrypt(encrypted, password, salt, iv);

    assertFalse(Arrays.equals(text.getBytes(), encrypted));
    assertArrayEquals(text.getBytes(), decrypted);
  }

  @Test
  public void encryptAndDecryptFile() throws IOException {
    final File file = File.createTempFile("test", null, Paths.get("").toAbsolutePath().toFile());
    final String password = "password123";
    final byte[] content = "Recusandae nesciunt eos illo vero.".getBytes();

    file.deleteOnExit();
    Files.write(file.toPath(), content);

    Encryption.encrypt(file, password);

    assertFalse(Arrays.equals(content, Files.readAllBytes(file.toPath())));

    Encryption.decrypt(file, password);

    assertArrayEquals(content, Files.readAllBytes(file.toPath()));
  }
}
