/*
 * Implementation of "Ms Pac-Man" for the "Ms Pac-Man versus Ghost Team Competition", brought
 * to you by Philipp Rohlfshagen, David Robles and Simon Lucas of the University of Essex.
 * 
 * www.pacman-vs-ghosts.net
 * 
 * Code written by Philipp Rohlfshagen, based on earlier implementations of the game by
 * Simon Lucas and David Robles. 
 * 
 * You may use and distribute this code freely for non-commercial purposes. This notice 
 * needs to be included in all distributions. Deviations from the original should be 
 * clearly documented. We welcome any comments and suggestions regarding the code.
 */
package edu.ufl.cise.gatorraider.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ufl.cise.gatorraider.models.Node;
import edu.ufl.cise.gatorraider.system._Game_;
import edu.ufl.cise.gatorraider.system.Pair;
import edu.ufl.cise.gatorraider.models.Attacker;
import edu.ufl.cise.gatorraider.models.Defender;
import edu.ufl.cise.gatorraider.models.Game;
import edu.ufl.cise.lib.*;
import edu.ufl.cise.lib.io.DataFile;
import edu.ufl.cise.lib.graphics.Image;
import edu.ufl.cise.lib.platform.PlatformCanvasSurface;

@SuppressWarnings("serial")
public final class GameView extends CanvasSurface
{
	public static final String pathImages="images";
    private PlatformCanvasSurface surface = null;

    //for debugging/illustration purposes only: draw colors in the maze to check whether controller is working
    //correctly or not; can draw squares and lines (see NearestPillAttackerVS for demostration).
    public static ArrayList<DebugPointer> debugPointers=new ArrayList<DebugPointer>();
    public static ArrayList<DebugLine> debugLines=new ArrayList<DebugLine>();

    public GameView(_Game_ game, DataFile gameDir, int _magnification)
    {
        this(game, gameDir, _magnification, "gator", "uga", "ut", "fsu", "lsu", "edible");
    }

    public GameView(_Game_ game, int _magnification)
    {
        this(game, new DataFile(null), _magnification, "gator", "uga", "ut", "fsu", "lsu", "edible");
    }

    public GameView(_Game_ game, DataFile gameDir, int _magnification, String attacker, String defender1, String defender2, String defender3, String defender4, String edible)
    {
        this.game=game;
        magnification = _magnification;
        images=loadImages(gameDir);

        pacmanImgs[Game.Direction.UP][0]=getImage(attacker + "_up_normal.png", gameDir);
        pacmanImgs[Game.Direction.UP][1]=getImage(attacker + "_up_open.png", gameDir);
        pacmanImgs[Game.Direction.UP][2]=getImage(attacker + "_up_closed.png", gameDir);
        pacmanImgs[Game.Direction.RIGHT][0]=getImage(attacker + "_right_normal.png", gameDir);
        pacmanImgs[Game.Direction.RIGHT][1]=getImage(attacker + "_right_open.png", gameDir);
        pacmanImgs[Game.Direction.RIGHT][2]=getImage(attacker + "_right_closed.png", gameDir);
        pacmanImgs[Game.Direction.DOWN][0]=getImage(attacker + "_down_normal.png", gameDir);
        pacmanImgs[Game.Direction.DOWN][1]=getImage(attacker + "_down_open.png", gameDir);
        pacmanImgs[Game.Direction.DOWN][2]=getImage(attacker + "_down_closed.png", gameDir);
        pacmanImgs[Game.Direction.LEFT][0]=getImage(attacker + "_left_normal.png", gameDir);
        pacmanImgs[Game.Direction.LEFT][1]=getImage(attacker + "_left_open.png", gameDir);
        pacmanImgs[Game.Direction.LEFT][2]=getImage(attacker + "_left_closed.png", gameDir);
        
        ghostsImgs[0][Game.Direction.UP][0]=getImage(defender1 + "_up_1.png", gameDir);
        ghostsImgs[0][Game.Direction.UP][1]=getImage(defender1 + "_up_2.png", gameDir);
        ghostsImgs[0][Game.Direction.RIGHT][0]=getImage(defender1 + "_right_1.png", gameDir);
        ghostsImgs[0][Game.Direction.RIGHT][1]=getImage(defender1 + "_right_2.png", gameDir);
        ghostsImgs[0][Game.Direction.DOWN][0]=getImage(defender1 + "_down_1.png", gameDir);
        ghostsImgs[0][Game.Direction.DOWN][1]=getImage(defender1 + "_down_2.png", gameDir);
        ghostsImgs[0][Game.Direction.LEFT][0]=getImage(defender1 + "_left_1.png", gameDir);
        ghostsImgs[0][Game.Direction.LEFT][1]=getImage(defender1 + "_left_2.png", gameDir);
        
        ghostsImgs[1][Game.Direction.UP][0]=getImage(defender2 + "_up_1.png", gameDir);
        ghostsImgs[1][Game.Direction.UP][1]=getImage(defender2 + "_up_2.png", gameDir);
        ghostsImgs[1][Game.Direction.RIGHT][0]=getImage(defender2 + "_right_1.png", gameDir);
        ghostsImgs[1][Game.Direction.RIGHT][1]=getImage(defender2 + "_right_2.png", gameDir);
        ghostsImgs[1][Game.Direction.DOWN][0]=getImage(defender2 + "_down_1.png", gameDir);
        ghostsImgs[1][Game.Direction.DOWN][1]=getImage(defender2 + "_down_2.png", gameDir);
        ghostsImgs[1][Game.Direction.LEFT][0]=getImage(defender2 + "_left_1.png", gameDir);
        ghostsImgs[1][Game.Direction.LEFT][1]=getImage(defender2 + "_left_2.png", gameDir);
        
        ghostsImgs[2][Game.Direction.UP][0]=getImage(defender3 + "_up_1.png", gameDir);
        ghostsImgs[2][Game.Direction.UP][1]=getImage(defender3 + "_up_2.png", gameDir);
        ghostsImgs[2][Game.Direction.RIGHT][0]=getImage(defender3 + "_right_1.png", gameDir);
        ghostsImgs[2][Game.Direction.RIGHT][1]=getImage(defender3 + "_right_2.png", gameDir);
        ghostsImgs[2][Game.Direction.DOWN][0]=getImage(defender3 + "_down_1.png", gameDir);
        ghostsImgs[2][Game.Direction.DOWN][1]=getImage(defender3 + "_down_2.png", gameDir);
        ghostsImgs[2][Game.Direction.LEFT][0]=getImage(defender3 + "_left_1.png", gameDir);
        ghostsImgs[2][Game.Direction.LEFT][1]=getImage(defender3 + "_left_2.png", gameDir);
        
        ghostsImgs[3][Game.Direction.UP][0]=getImage(defender4 + "_up_1.png", gameDir);
        ghostsImgs[3][Game.Direction.UP][1]=getImage(defender4 + "_up_2.png", gameDir);
        ghostsImgs[3][Game.Direction.RIGHT][0]=getImage(defender4 + "_right_1.png", gameDir);
        ghostsImgs[3][Game.Direction.RIGHT][1]=getImage(defender4 + "_right_2.png", gameDir);
        ghostsImgs[3][Game.Direction.DOWN][0]=getImage(defender4 + "_down_1.png", gameDir);
        ghostsImgs[3][Game.Direction.DOWN][1]=getImage(defender4 + "_down_2.png", gameDir);
        ghostsImgs[3][Game.Direction.LEFT][0]=getImage(defender4 + "_left_1.png", gameDir);
        ghostsImgs[3][Game.Direction.LEFT][1]=getImage(defender4 + "_left_2.png", gameDir);
        
        ghostsImgs[4][0][0]=getImage(edible + "_1.png", gameDir);
        ghostsImgs[4][0][1]=getImage(edible + "_2.png", gameDir);
        ghostsImgs[5][0][0]=getImage(edible + "_blink_1.png", gameDir);
        ghostsImgs[5][0][1]=getImage(edible + "_blink_2.png", gameDir);
    }


    public synchronized void setPlatformSurface(PlatformCanvasSurface _surface)
    {
        surface = _surface;
    }

    ////////////////////////////////////////
    ////// Visual aids for debugging ///////
    ////////////////////////////////////////
    
    // Adds a node to be highlighted using the color specified
    // NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addPoints(Game game, int color, List<Node> nodes)
    {
        for (Node point : nodes)
    		debugPointers.add(new DebugPointer(point.getX(),point.getY(),color));
    }
    
    // Adds a set of lines to be drawn using the color specified (fromNnodeIndices.length must be equals toNodeIndices.length)
    // NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addLines(Game game, int color, List<Pair<Node, Node>> pairs)
    {
        for (Pair<Node,Node> pair : pairs)
            debugLines.add(new DebugLine(pair.first().getX(), pair.first().getY(), pair.second().getX(), pair.second().getY(), color));
    }

    // Adds a set of lines to be drawn using the color specified
    // NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addLines(Game game, int color, List<Node> fromNodes, List<Node> toNodes)
    {
        int size = Math.min(fromNodes.size(), toNodes.size());
        for (int index = 0; index < size; index++)
        {
            Node from = fromNodes.get(index);
            Node to = toNodes.get(index);
            debugLines.add(new DebugLine(from.getX(), from.getY(), to.getX(), to.getY(), color));
        }
    }

    //Adds a line to be drawn using the color specified
    //NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addLines(Game game, int color, Node fromNode, Node toNode)
    {
    	debugLines.add(new DebugLine(fromNode.getX(), fromNode.getY(), toNode.getX(), toNode.getY(), color));
    }

    ////////////////////////////////////////
    ////// Visual aids for debugging ///////
    ////////////////////////////////////////

    public void paint()
    {
        drawMaze();
        drawDebugInfo();	//this will be used during testing only and will be disabled in the competition itself
        drawPills();
        drawPowerPills();
        drawPacMan();
        drawGhosts();
        drawLives();
        drawGameInfo();

        if(game.gameOver())
            drawGameOver();
    }

    private void drawDebugInfo()
    {
    	for(int i=0;i<debugPointers.size();i++)
    	{
    		DebugPointer dp=debugPointers.get(i);
    		surface.setColor(dp.color);
            surface.fillRect(dp.x* magnification +1,dp.y* magnification +5,10,10);
    	}
    	
    	for(int i=0;i<debugLines.size();i++)
    	{
    		DebugLine dl=debugLines.get(i);
            surface.setColor(dl.color);
            surface.drawLine(dl.x1* magnification +5,dl.y1* magnification +10,dl.x2* magnification +5,dl.y2* magnification +10);
    	}

    	debugPointers.clear();
    	debugLines.clear();
    }

    private void drawMaze()
    {
        surface.setColor(0);
        surface.fillRect(0, 0, _Game_.WIDTH * magnification, _Game_.HEIGHT * magnification +20);
        
        if(images[game.getCurMazeNum()]!=null)
            surface.drawImage(images[game.getCurMazeNum()],2,6);
    }

    private void drawPills()
    {
        List<Node> pillNodes = game.getCurMaze().getPillNodes();

        surface.setColor(0xffffffff);

        for (Node pill : pillNodes)
        	if (game.checkPill(pill))
        		surface.fillOval(pill.getX()* magnification +4,pill.getY()* magnification +8,3,3);
    }
    
    private void drawPowerPills()
    {
          List<Node> powerPillNodes = game.getCurMaze().getPowerPillNodes();
          
          surface.setColor(0xffffffff);

          for (Node pill : powerPillNodes)
          	if(game.checkPowerPill(pill))
                surface.fillOval(pill.getX()* magnification +1,pill.getY()* magnification +5,8,8);
    }
    
    private void drawPacMan()
    {
        Attacker attacker = game.getAttacker();
    	Node attackerLoc = attacker.getLocation();
    	int currentDir = attacker.getDirection();
        
    	if(currentDir>=0 && currentDir<4)
    		attackerDir =currentDir;

        surface.drawImage(pacmanImgs[attackerDir][(game.getTotalTime()%6)/2],attackerLoc.getX()* magnification -1,attackerLoc.getY()* magnification +3);
    }

    private void drawGhosts() 
    {
    	for(int index = 0; index< Game.NUM_DEFENDER; index++)
    	{
    	    Defender defender = game.getDefender(index);
	    	Node loc = defender.getLocation();
	    	int x = loc.getX();
	    	int y = loc.getY();
	    	
	    	if(defender.getVulnerableTime() > 0)
	    	{
	    		if(defender.getVulnerableTime() < _Game_.EDIBLE_ALERT && ((game.getTotalTime() % 6) / 3) ==0)
                    surface.drawImage(ghostsImgs[5][0][(game.getTotalTime()%6)/3],x* magnification -1,y* magnification +3);
	            else
                    surface.drawImage(ghostsImgs[4][0][(game.getTotalTime()%6)/3],x* magnification -1,y* magnification +3);
	    	}
	    	else 
	    	{
	    		if(defender.getLairTime() > 0)
                    surface.drawImage(ghostsImgs[index][Game.Direction.UP][(game.getTotalTime()%6)/3],x* magnification -1+(index*5),y* magnification +3);
	    		else
                    surface.drawImage(ghostsImgs[index][defender.getDirection()][(game.getTotalTime()%6)/3],x* magnification -1,y* magnification +3);
	        }
    	}
    }

    private void drawLives()
    {
    	for(int i=0;i<game.getLivesRemaining()-1;i++) //-1 as lives remaining includes the current life
            surface.drawImage(pacmanImgs[Game.Direction.RIGHT][0],210-(30*i)/2,260);
    }
    
    private void drawGameInfo()
    {
        surface.setColor(0x00ffffff);
        surface.drawString("S: ",4,271);
        surface.drawString(""+game.getScore(),16,271);
        surface.drawString("L: ",78,271);
        surface.drawString(""+(game.getLevel()+1),90,271);
        surface.drawString("T: ",116,271);
        surface.drawString(""+game.getLevelTime(),129,271);
    }
    
    private void drawGameOver()
    {
        surface.setColor(0x00ffffff);
        surface.drawString("Game Over",80,150);
    }
    
    private Image[] loadImages(DataFile gameDir)
    {
        Image[] images=new Image[4];
        
        for(int i=0;i<images.length;i++)
        	images[i]=getImage(mazes[i], gameDir);
        
        return images;
    }
    
    private Image getImage(String filename, DataFile gameDir)
    {
        Image image = null;

        try
        {
            image = new Image(new DataFile(gameDir, pathImages + System.getProperty("file.separator") + filename));
        }
        catch(IOException e) 
        {
            e.printStackTrace();
        }
        
        return image;
    }

    private static final String[] mazes={"maze_a.png","maze_b.png","maze_c.png","maze_d.png"};
    private int magnification = 2;
    private int attackerDir = Game.INITIAL_ATTACKER_DIR;

    private final _Game_ game;
    private final Image[][] pacmanImgs=new Image[4][3];
    private final Image[][][] ghostsImgs=new Image[6][4][2];
    private final Image[] images;

    private static class DebugPointer
    {
    	public int x,y;
    	public int color;
    	
    	public DebugPointer(int x,int y, int color)
    	{
    		this.x=x;
    		this.y=y;
    		this.color=color;
    	}
    }
    
    private static class DebugLine
    {
    	public int x1,y1,x2,y2;
    	public int color;
    	
    	public DebugLine(int x1,int y1,int x2,int y2, int color)
    	{
    		this.x1=x1;
    		this.y1=y1;
    		this.x2=x2;
    		this.y2=y2;
    		this.color=color;
    	}
    }
}