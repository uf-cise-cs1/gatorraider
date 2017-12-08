package game.system;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import game.models.Defender;
import game.models.Node;
import game.models.Maze;

public final class _Maze implements Maze
{
    public int getNumberPills() { return pillNodes.length; }
    public int getNumberPowerPills() { return powerPillNodes.length; }
    public String getName() { return name; }
    public Node getInitialAttackerPosition() { return initialHeroPosition; }
    public Node getInitialDefendersPosition() { return initialEnemiesPosition; }
    public int getNumberOfNodes() { return graph.length; }
    public List<Node> getPillNodes() { return Arrays.asList(Arrays.copyOf(pillNodes, pillNodes.length)); }
    public List<Node> getPowerPillNodes() { return Arrays.asList(Arrays.copyOf(powerPillNodes, powerPillNodes.length)); }
    public List<Node> getJunctionNodes() { return Arrays.asList(Arrays.copyOf(junctionNodes, junctionNodes.length)); }
    public Node getNode(int x, int y) { return nodes.get(x, y); }

    public void fillJunctionNodes(){
        int junctionIndex = 0;
        for (_Node a : graph)
            if (a.getNumNeighbors() > 2)
                junctionNodes[junctionIndex++] = a;
    }
    protected DuoMap<Node, Node, Integer> distances;
    protected DuoMap<Integer, Integer, Node> nodes;
    protected _Node[] pillNodes, powerPillNodes, junctionNodes;
    protected _Node[] graph;

    //The actual maze, stored as a graph (set of nodes)
    protected _Node initialHeroPosition, lairPosition, initialEnemiesPosition;
    protected int width, height;	//_Maze-specific information
    protected String name;																//Name of the _Maze

    /*
     * Each maze is stored as a (connected) graph: all nodes have neighbors, stored in an array of length 4. The
     * index of the array associates the direction the neighbour is located at: '[up,right,down,left]'.
     * For instance, if node '9' has neighbours '[-1,12,-1,6]', you can reach node '12' by going right, and node
     * 6 by going left. The directions returned by the controllers should thus be in {0,1,2,3} and can be used
     * directly to determine the next node to go to.
     */
    protected _Maze(int index)
    {
        loadNodes(_Game.nodeNames[index]);
        loadDistances(_Game.distNames[index]);
    }

    //Loads all the nodes from files and initialises all maze-specific information.
    private void loadNodes(String fileName)
    {
        nodes = new DuoMap<Integer, Integer, Node>();

        final int NODE_LENGTH = 9;
        try
        {
            // Prepare a stream to read data from the file.
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(_Game.pathMazes + System.getProperty("file.separator") + fileName)));
            LinkedList<int[]> nodeData = new LinkedList<int[]>();

            // First, read in the name of this map.
            String input = reader.readLine();
            this.name = input.substring(0, input.indexOf("\t"));
            input = input.substring(input.indexOf("\t") + 1);

            // Then, read in the rest of the information (all integers.)
            do
            {
                String[] tokens = input.split("\t");
                int[] entry = new int[tokens.length];

                for (int index = 0; index < tokens.length; index++)
                    entry[index] = Integer.parseInt(tokens[index]);

                nodeData.add(entry);
                input = reader.readLine();
            }
            while (input != null);

            // Load general map information
            int[] preamble = nodeData.remove();
            int initialHeroIndex = preamble[0];
            int lairIndex = preamble[1];
            int initialEnemiesIndex = preamble[2];

            _Node[] nodeList = new _Node[preamble[3]];
            this.pillNodes = new _Node[preamble[4]];
            this.powerPillNodes = new _Node[preamble[5]];
            this.junctionNodes = new _Node[preamble[6]];
            this.width = preamble[7];
            this.height = preamble[8];

            this.graph = nodeList;
            int nodeIndex=0;
            int pillIndex=0;
            int powerPillIndex=0;
            int junctionIndex=0;

            // Create the nodes.
            for (int[] entry : nodeData)
            {
                _Node node = new _Node(entry[1], entry[2], entry[7], entry[8], this);

                nodeList[nodeIndex++] = node;

                if (node.getPillIndex() >= 0)
                    pillNodes[pillIndex++] = node;
                else if (node.getPowerPillIndex() >= 0)
                    powerPillNodes[powerPillIndex++] = node;

                /*if (node.getNumNeighbors() > 2)
                    junctionNodes[junctionIndex++] = node;*/
            }

            // Connect the nodes.
            for (int index = 0; index < nodeList.length; index++)
            {
                _Node thisNode = nodeList[index];
                _Node[] newNeighbors = new _Node[4];
                for (int neighborNo = 0; neighborNo < 4; neighborNo++)
                {
                    int neighborIndex = nodeData.get(index)[neighborNo+3];
                    newNeighbors[neighborNo] = (neighborIndex == -1 ? null : nodeList[neighborIndex]);
                }
                thisNode.setNeighbors(newNeighbors);
                nodes.put(thisNode.getX(), thisNode.getY(), thisNode);
            }
            fillJunctionNodes();
            // Set up the starting positions.
            this.initialHeroPosition = graph[initialHeroIndex];
            this.lairPosition = graph[lairIndex];
            this.initialEnemiesPosition = graph[initialEnemiesIndex];
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /*
     * Loads the shortest path distances which have been pre-computed. The data contains the shortest distance from
     * any node in the maze to any other node. Since the graph is symmetric, the symmetries have been removed to preserve
     * memory and all distances are stored in a 1D array; they are looked-up using getDistance(-).
     */
    private void loadDistances(String fileName)
    {
        distances = new DuoMap<Node, Node, Integer>();

        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(_Game.pathMazes+System.getProperty("file.separator")+fileName)));

            for (int end = 0; end < graph.length; end++)
            {
                for (int start = 0; start <= end; start++)
                {
                    String input=br.readLine();
                    if (input == null)
                        break;

                    distances.put(graph[start], graph[end], Integer.parseInt(input));
                    distances.put(graph[end], graph[start], Integer.parseInt(input));
                }
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
