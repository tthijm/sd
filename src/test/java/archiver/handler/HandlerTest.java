package archiver.handler;

import static org.junit.Assert.*;

import java.util.Map;
import org.junit.Test;

public class HandlerTest {

  @Test
  public void getEmptyArguments() {
    final String line = "-p qwerty -f tar";
    final String[] expected = { "" };

    final String[] result = Handler.getArguments(line);

    assertArrayEquals(expected, result);
  }

  @Test
  public void getArguments() {
    final String line = "create animals.zip -p qwerty -f tar panda.png";
    final String[] expected = { "create", "animals.zip", "panda.png" };

    final String[] result = Handler.getArguments(line);

    assertArrayEquals(expected, result);
  }

  @Test
  public void getEmptyOptions() {
    final String line = "create animals.zip panda.png";
    final Map<String, String> expected = Map.of();

    final Map<String, String> result = Handler.getOptions(line);

    assertEquals(expected, result);
  }

  @Test
  public void getOptions() {
    final String line = "create animals.zip -p qwerty -f tar panda.png";
    final Map<String, String> expected = Map.of("p", "qwerty", "f", "tar");

    final Map<String, String> result = Handler.getOptions(line);

    assertEquals(expected, result);
  }
}
