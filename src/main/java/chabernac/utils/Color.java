package chabernac.utils;

public class Color {
	private final byte alpha;
	private final byte red;
	private final byte green;
	private final byte blue;

	public Color(byte alpha, byte red, byte green, byte blue) {
		super();
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static Color fromARGB(int aColor) {
		byte blue = (byte) (aColor & 0X000000FF);
		byte green = (byte) (aColor >> 8 & 0X000000FF);
		byte red = (byte) (aColor >> 16 & 0X000000FF);
		byte alpha = (byte) (aColor >> 24 & 0X000000FF);
		return new Color(alpha, red, green, blue);
	}

	public int getARGB() {
		return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
	}

	public int getRGBA() {
		return ((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | ((alpha & 0xFF) << 0);
	}

	public byte getAlpha() {
		return alpha;
	}

	public byte getRed() {
		return red;
	}

	public byte getGreen() {
		return green;
	}

	public byte getBlue() {
		return blue;
	}
}
