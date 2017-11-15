package game.system;
import game.models.Actor;
import game.models.Node;
import game.models.Defender;
import java.util.List;

public class _Defender extends _Actor implements Defender
{
    public int getVulnerableTime()
    {
        return vulnerableTime;
    }
    public int getLairTime()
    {
        return lairTime;
    }
    public boolean isVulnerable()
    {
        return vulnerableTime > 0;
    }

    public List<Integer> getPossibleDirs() { return getPossibleDirs(false); }
    public int getNextDir(Node to, boolean approach) { return location.getNextDir(to, approach, direction); }
    public List<Node> getPathTo(Node to) { return getPathTo(to, false); }
    public List<Node> getPossibleLocations() { return getPossibleLocations(false); }
    public Node getTargetNode(List<Node> targets, boolean nearest) { return getTargetNode(targets, nearest, false); }
    public Actor getTargetActor(List<? extends Actor> targets, boolean nearest) { return getTargetActor(targets, nearest, false); }

/*    public boolean requiresAction()
    {
        return (location.isJunction() && vulnerableTime == 0 || vulnerableTime % _Game.DEFENDER_SPEED_REDUCTION != 0);
    }*/

    protected int vulnerableTime, lairTime;

    protected _Defender(_Node location, int direction, int _lairTime)
    {
        super(location, direction);
        vulnerableTime = 0;
        lairTime = _lairTime;
    }
    protected _Defender clone()
    {
        return (_Defender)super.clone();
    }
}
