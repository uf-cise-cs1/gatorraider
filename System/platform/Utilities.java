package platform;

import java.awt.Color;

public class Utilities
{
    public static Color colorFromInt(int value)
    {
        return new Color(((value & 0x00ffffff) << 8) | ((value & 0xff000000) >> 24), true);
    }
}
