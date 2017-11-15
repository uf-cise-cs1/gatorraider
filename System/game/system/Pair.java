package game.system;

public class Pair<F, S>
{
    private F first;
    private S second;

    public Pair(F _first, S _second)
    {
        first = _first;
        second = _second;
    }

    public F first()
    {
        return first;
    }

    public S second()
    {
        return second;
    }

    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode();
    }

    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Pair<F, S> rhs = (Pair<F, S>) o;

        return rhs.first == first && rhs.second == second;
    }
}
