package chabernac.utils;

import org.junit.Assert;
import org.junit.Test;

import chabernac.utils.Color;

public class ColorTest {
	@Test
	public void channels() {
		Color color = Color.fromARGB(0x11223344);
		Assert.assertEquals(0x11, color.getAlpha());
		Assert.assertEquals(0x22, color.getRed());
		Assert.assertEquals(0x33, color.getGreen());
		Assert.assertEquals(0x44, color.getBlue());
	}
	
	@Test
	public void getRGBA() {
		Assert.assertEquals(0x22334411, Color.fromARGB(0x11223344).getRGBA());
	}
	
	@Test
	public void getARGB() {
		Assert.assertEquals(0x11223344, Color.fromARGB(0x11223344).getARGB());
	}
}
