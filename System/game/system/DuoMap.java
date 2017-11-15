package game.system;

import java.util.HashMap;

public class DuoMap<K1, K2, V> extends HashMap<Pair<K1,K2>, V>
{
    public DuoMap()
    {
        super();
    }

    public V get(K1 key1, K2 key2)
    {
        return super.get(new Pair<K1,K2>(key1, key2));
    }

    public void put(K1 key1, K2 key2, V value)
    {
        super.put(new Pair<K1,K2>(key1, key2), value);
    }
}
