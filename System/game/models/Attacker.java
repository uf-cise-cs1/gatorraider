package game.models;
import java.util.List;

public interface Attacker extends Actor
{
    List<Integer> getPossibleDirs(boolean canReverse);
    List<Node> getPossibleLocations(boolean canReverse);
}