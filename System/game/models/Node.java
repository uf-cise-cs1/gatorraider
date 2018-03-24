package game.models;
import java.util.List;

public interface Node extends Comparable<Node>
{
    int getX();
    int getY();

    boolean isPill();
    boolean isPowerPill();
    boolean isJunction();

    int getNumNeighbors();
    Node getNeighbor(int inDirection);
    List<Node> getNeighbors();
    int getPathDistance(Node to);

    static int getReverse(int direction)
    {
        switch(direction)
        {
            case 0: return 2;
            case 1: return 3;
            case 2: return 0;
            case 3: return 1;
        }
        return 4;
    }
}
