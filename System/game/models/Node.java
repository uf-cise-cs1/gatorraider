package game.models;
import java.util.List;

public interface Node
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
}
