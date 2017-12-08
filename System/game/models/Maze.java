package game.models;
import java.util.List;

/*
	 * Stores the mazes, each of which is a connected graph. The differences between the mazes are the connectivity
	 * and the coordinates (used for drawing or to compute the Euclidean distance). There are 3 built-in distance
	 * functions in total: Euclidean, Manhattan and Dijkstra's shortest path distance. The latter is pre-computed and
	 * loaded, the others are computed on the fly whenever getNextDir(-) is called.
	 */

public interface Maze
{
    String getName();                    // Returns name of maze

    Node getInitialAttackerPosition();   // Returns the starting position of the hero
    Node getInitialDefendersPosition();  // Returns the starting position of the defenders (i.e., first node AFTER leaving the lair)
    Node getNode(int x, int y);          // Get Node from X,Y position

    int getNumberPills();                // Total number of pills in the maze
    int getNumberPowerPills();           // Total number of power pills in the maze
    int getNumberOfNodes();              // Total number of nodes in the graph (i.e., those with pills, power pills and those that are empty)

    List<Node> getPillNodes();           // Returns the indices to all the nodes that have pills
    List<Node> getPowerPillNodes();      // Returns all the nodes that have power pills
    List<Node> getJunctionNodes();       // Returns the indices to all the nodes that are junctions
}
