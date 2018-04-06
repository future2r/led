package name.ulbricht.led;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Font5x8CharacterTest {

	@Parameters(name = "{0}")
	public static Collection<Object[]> initParams() {
		String s = "!\"#$%&'()*+,-./" // special characters
				+ "0123456789" // digits
				+ ":;<=>?" // special characters
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" // upper case letters
				+ "[\\]_" // special characters
				+ "abcdefghijklmnopqrstuvwxyz" // lower case letters
				+ "{|}~€£§°" // special characters
				+ "ÄÖÜäöü"; // German umlauts

		return s.chars().mapToObj(i -> new Object[] { Character.valueOf((char) i) })
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private char c;

	public Font5x8CharacterTest(Character c) {
		this.c = c.charValue();
	}

	@SuppressWarnings("boxing")
	@Test
	public void testCharacter() {

		byte[] data = MatrixFont.FONT_5x8.getCharacter(this.c);

		assertNotNull(String.format("Unsupported character %s (%sh, %s)", this.c, Integer.toHexString(this.c),
				Integer.toString(this.c)), data);
		assertEquals(MatrixFont.FONT_5x8.width * MatrixFont.FONT_5x8.height, data.length);
	}
}
