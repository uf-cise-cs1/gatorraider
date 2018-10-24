package platform;

import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

public class Image
{
	protected final int width, height;
	protected int[] data;

	private java.awt.Image systemImage;
	private MemoryImageSource imageSource;

	protected Image(int _width, int _height)
	{
		width = _width;
		height = _height;
		systemImage = null;
		imageSource = null;

		data = new int[width * height];
		java.util.Arrays.fill(data, 0);
	}

	// Get the Buffered Image from within. (Used inside this package only.)
	protected java.awt.Image getImage()
	{
		if (systemImage == null)
		{
			imageSource = new MemoryImageSource(width, height, ColorModel.getRGBdefault(), data, 0, width);
			imageSource.setAnimated(true);
			systemImage = Toolkit.getDefaultToolkit().createImage(imageSource);
		}
		else
		{
			imageSource.newPixels();
		}

		return systemImage;
	}
}
