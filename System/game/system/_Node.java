package game.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import game.models.Node;

public class _Node implements Node
{
    protected _Node[] neighbors = null;
    private int x, y;
    private int pillIndex, powerPillIndex;
    private int numNeighbors = 0;
    private _Maze maze;

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isJunction() { return numNeighbors > 2; }
    public boolean isPill() { return pillIndex <= 0; }
    public boolean isPowerPill() { return powerPillIndex <= 0; }

    public int getNumNeighbors() { return numNeighbors; }
    public List<Node> getNeighbors() { return Arrays.asList(Arrays.copyOf(neighbors, neighbors.length)); }

    public String toString() { return "_Node(" + x + ", " + y + ")"; }
    //Returns the neighbor of node index that corresponds to direction. In the case of neutral, the
    //same node index is returned
    public Node getNeighbor(int inDirection)
    {
        if(inDirection < 0 || inDirection > 3) //this takes care of "neutral"
            return this;
        else
            return neighbors[inDirection];
    }

    public int getNextDir(Node to, boolean approach) { return getNextDir(to, approach, true, 0); }
    public int getNextDir(Node to, boolean approach, int direction) { return getNextDir(to, approach, false, direction); }

    public int compareTo(Node other)
    {
        if (this.y < other.getY())
            return -1;
        else if (this.y > other.getY())
            return 1;
        else if (this.x < other.getX())
            return -1;
        else if (this.x > other.getX())
            return 1;

        return 0;
    }

    private int getNextDir(Node to, boolean approach, boolean canReverse, int direction)
    {
        _Node[] options = Arrays.copyOf(neighbors, neighbors.length);

        if (!canReverse)
            options[Node.getReverse(direction)] = null;

        int dir = -1;

        double min = Integer.MAX_VALUE;
        double max = -Integer.MAX_VALUE;

        for(int i = 0; i < options.length; i++)
        {
            if(options[i] != null)
            {
                double dist = 0;
                dist = options[i].getPathDistance(to);

                if (dist < 0)
                    continue;

                if(approach && dist < min)
                {
                    min = dist;
                    dir = i;
                }

                if(!approach && dist > max)
                {
                    max = dist;
                    dir = i;
                }
            }
        }

        return dir;
    }

    public List<Node> getPathTo(Node to) { return getPathTo(to, true, 0); }
    public List<Node> getPathTo(Node to, int direction) { return getPathTo(to, false, direction); }

    //Returns the path of adjacent nodes from one node to another, including these nodes
    //E.g., path from a to c might be [a,f,r,t,c]

    private List<Node> getPathTo(Node to, boolean canReverse, int direction)
    {
        List<Node> path = new ArrayList<Node>();

        if(getNumNeighbors()==0)
            return path;

        for (_Node currentNode = this; currentNode != to; currentNode = currentNode.neighbors[direction])
        {
            path.add(currentNode);
            direction = currentNode.getNextDir(to, true, canReverse, direction);

            if (direction == -1)
                break;
        }

        return path;
    }

    public int getPathDistance(Node to)
    {
        Integer result = maze.distances.get(this, to);
        if (result == null)
        {
            System.out.println("Warning: distance not found in precomputed data. Returning -1.");
            return -1;
        }

        return result;
    }

    protected _Node(int _x, int _y, int _pillIndex, int _powerPillIndex, _Maze _maze)
    {
        x = _x;
        y = _y;
        pillIndex = _pillIndex;
        powerPillIndex = _powerPillIndex;
        maze = _maze;
    }

    protected void setNeighbors(_Node[] _neighbors)
    {
        neighbors = _neighbors;
        numNeighbors = 0;

        for (int index = 0; index < _neighbors.length; index++)
            if (_neighbors[index] != null)
                numNeighbors++;
    }

    protected int getPillIndex()
    {
        return pillIndex;
    }
    protected int getPowerPillIndex()
    {
        return powerPillIndex;
    }
}
