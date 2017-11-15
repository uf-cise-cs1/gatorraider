package game.system;
import game.models.Actor;
import game.models.Node;
import java.util.List;
import java.util.ArrayList;

public abstract class _Actor implements Actor
{
    _Node location;
    int direction;

    public Node getLocation()
    {
        return location;
    }
    public int getDirection()
    {
        return direction;
    }

    public int getReverse()
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

    protected List<Node> getPathTo(Node to, boolean canReverse)
    {
        if (canReverse)
            return location.getPathTo(to);
        else
            return location.getPathTo(to, direction);
    }

    protected List<Integer> getPossibleDirs(boolean canReverse)
    {
        ArrayList<Integer> directions = new ArrayList<Integer>();
        int numNeighbors = location.getNumNeighbors();

        if (numNeighbors == 0)
            return directions;

        List<Node> nodes = location.getNeighbors();

        for (int i = 0; i < nodes.size(); i++)
        {
            if (nodes.get(i) != null)
            {
                if (canReverse || (direction < 0 || direction > 3))
                    directions.add(i);
                else if (i != getReverse())
                    directions.add(i);
            }
        }

        return directions;
    }

    protected List<Node> getPossibleLocations(boolean canReverse)
    {
        List<Node> newLocations = location.getNeighbors();
        if (!canReverse)
            newLocations.set(getReverse(), null);

        return newLocations;
    }

    protected Actor getTargetActor(List<? extends Actor> targets, boolean nearest, boolean canReverse)
    {
        Actor result = null;

        double min=Integer.MAX_VALUE;
        double max=-Integer.MAX_VALUE;


        for (Actor target : targets)
        {
            double dist = getPathTo(target.getLocation(), canReverse).size();

            if(nearest && dist<min)
            {
                min = dist;
                result = target;
            }

            if(!nearest && dist > max)
            {
                max = dist;
                result = target;
            }
        }

        return result;
    }

    //Returns the target closest from this actor's position
    protected Node getTargetNode(List<Node> targets, boolean nearest, boolean canReverse)
    {
        Node result = null;

        double min=Integer.MAX_VALUE;
        double max=-Integer.MAX_VALUE;

        for (Node target : targets)
        {
            double dist = getPathTo(target, canReverse).size();

            if(nearest && dist<min)
            {
                min = dist;
                result = target;
            }

            if(!nearest && dist > max)
            {
                max = dist;
                result = target;
            }
        }

        return result;
    }


    protected _Actor(_Node _location, int _direction)
    {
        location = _location;
        direction = _direction;
    }

    protected _Actor clone()
    {
        try
        {
            return (_Actor)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            return null;
        }
    }
}
