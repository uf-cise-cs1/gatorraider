package game.system;

public class _Random
{
    long next;

    public _Random(int seed)
    {
        next = seed;
    }

    public short nextShort()
    {
        return nextShort(Short.MAX_VALUE);
    }
    public short nextShort(short limit)
    {
        next = next * 1103515245 + 12345;
        return (short) Math.abs(((next / 65536) % limit));
    }

    public int nextInt()
    {
        return nextInt(Integer.MAX_VALUE);
    }

    public int nextInt(int limit)
    {
        return ((((int) nextShort()) << 16) | ((int) nextShort())) % limit;
    }

    public double nextDouble()
    {
        return (double) nextInt() / (double) Integer.MAX_VALUE;
    }
}