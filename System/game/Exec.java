package game;

import java.io.PrintWriter;

import edu.ufl.cise.cs1.controllers.AttackerHumanController;
import game.models.Game;
import game.system.*;
import game.view.*;

import game.controllers.*;
import game.controllers.example.*;
import game.controllers.benchmark.*;
import edu.ufl.cise.cs1.controllers.StudentAttackerController;

/*
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and 
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class Exec
{
	public static boolean logging = false;
	public static PrintWriter writer = null;

	//Several options are listed - simply remove comments to use the option you want
	public static void main(String[] args)
	{
		Exec exec=new Exec();

		AttackerHumanController humanAttacker = new AttackerHumanController();
		AttackerController studentAttacker = new StudentAttackerController();
		AttackerController exampleAttacker = new Devastator();
		DefenderController defender = new OriginalDefenders();

		if (args.length > 0)
		{
			switch (args[0].toLowerCase())
			{
				case "-debugexample":
					exec.runExperiment(exampleAttacker, defender, 5, true);
					break;
				case "-debugstudent":
					exec.runExperiment(studentAttacker, defender, 5, true);
					break;
				case "-testexample":
					exec.runExperiment(exampleAttacker, defender, 100, false);
					break;
				case "-teststudent":
					exec.runExperiment(studentAttacker, defender, 100, false);
					break;
				case "-visualhuman":

					exec.runGame(humanAttacker, defender, true, _Game.DELAY);
					break;
				case "-visualexample":
					exec.runGame(exampleAttacker, defender, true, _Game.DELAY);
					break;
				case "-visualstudent":
					exec.runGame(studentAttacker, defender, true, _Game.DELAY);
					break;
				default:
					System.err.println("Invalid command line option");
			}
		}
		else
			System.err.println("No command line option specified");

		//run game without time limits (un-comment if required)
//		exec.runGame(new RandomAttacker(),new RandomDefenders(),true,_Game.DELAY);

		//run game with time limits. Here NearestPillAttackerVS is chosen to illustrate how to use graphics for debugging/information purposes
//		exec.runGameTimed(new NearestPillAttackerVS(),new AttractRepelGhosts(false),true);
		
		//this allows you to record a game and replay it later. This could be very useful when
		//running many games in non-visual mode - one can then pick out those that appear irregular
		//and replay them in visual mode to see what is happening.
//		exec.runGameTimedAndRecorded(new RandomAttacker(),new Legacy2TheReckoning(),true,"human-v-Legacy2.txt");
//		exec.replayGame("human-v-Legacy2.txt");
	}
	
    protected int pacDir;
    protected int[] ghostDirs;
    protected _Game_ game;
    protected PacMan pacMan;
    protected Ghosts ghosts;
    protected boolean pacmanPlayed,ghostsPlayed;
   
    /*
     * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game. 
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     */
    public void runExperiment(AttackerController attackerController, DefenderController defenderController, int trials, boolean debug)
    {
		double avgScore = 0;
    	int tick = 0;
		game=new _Game_();

		if (debug)
		{
			try
			{
				writer = new PrintWriter("experiment.txt", "UTF-8");
				System.out.println("Logging data to experiment.txt.");
			}
			catch (Exception e)
			{
				System.out.println("Couldn't open log file; disabling debugging data.");
				debug = false;
			}
		}

		for(int i=0;i<trials;i++)
		{
			game.newGame();
			attackerController.init(game.copy());
			defenderController.init(game.copy());

			while(!game.gameOver())
			{
				long due = _Game.DELAY;
				int attackerDirection = attackerController.update(game.copy(), due);
				int[] defenderDirections = defenderController.update(game.copy(), due);

				if (debug)
				{
					writer.print("[Tick #" + tick + "] Attacker: [DIR: " + attackerDirection + "; POS: " + game.getAttacker().getLocation().getX() + "," + game.getAttacker().getLocation().getY() + "]; ");
					for (int index = 0; index < 4; index++)
					{
						writer.print("Defender #" + index + ": [DIR: " + defenderDirections[index] + "; POS: " + game.getDefender(index).getLocation().getX() + "," + game.getDefender(index).getLocation().getY() + "]; ");
					}
					writer.println("");
				}
		        game.advanceGame(attackerDirection, defenderDirections);
				tick++;
			}
			
			avgScore+=game.getScore();
			attackerController.shutdown(game.copy());
			defenderController.shutdown(game.copy());
			System.out.println("Trial #" + i + " complete. Score: " + game.getScore());
		}
		
		System.out.println(avgScore/trials);
		if (writer != null)
		{
			writer.flush();
			writer.close();
		}
    }
    
    /*
     * Run game without time limit. Very good for testing as game progresses as soon as the controllers
     * return their action(s). Can be played with and without visual display of game states. The delay
     * is purely for visual purposes (as otherwise the game could be too fast if controllers compute quickly. 
     * For testing, this can be set to 0 for fasted game play.
     */
	public void runGame(AttackerController attackerController, DefenderController defenderController, boolean visual, int delay)
	{
//		Game.rng = new java.util._Random();
		
		game=new _Game_();
		game.newGame();

		GameView gv=null;
		
		if(visual)
		{
			gv = new GameView(game).showGame();

			if(attackerController instanceof AttackerHumanController)
				gv.getFrame().addKeyListener(((AttackerHumanController) attackerController));
		}

		attackerController.init(game.copy());
		defenderController.init(game.copy());

		while(!game.gameOver())
		{
//			long due=System.currentTimeMillis()+ Game.DELAY;
			long due = Game.DELAY;
			game.advanceGame(attackerController.update(game.copy(), due), defenderController.update(game.copy(), due));

	        try{Thread.sleep(delay);}catch(Exception e){}
	        
	        if(visual)
	        	gv.repaint();
		}

		attackerController.shutdown(game.copy());
		defenderController.shutdown(game.copy());
	}
	
    /*
     * Run game with time limit. This is how it will be done in the competition. 
     * Can be played with and without visual display of game states.
     */
	public void runGameTimed(AttackerController attackerController, DefenderController defenderController, boolean visual)
	{
		game=new _Game_();
		game.newGame();
		pacMan=new PacMan(attackerController);
		ghosts=new Ghosts(defenderController);
		
		GameView gv=null;
		
		if(visual)
		{
			gv=new GameView(game).showGame();
			
			if(attackerController instanceof AttackerHumanController)
				gv.getFrame().addKeyListener((AttackerHumanController) attackerController);
		}

		attackerController.init(game.copy());
		defenderController.init(game.copy());

		while(!game.gameOver())
		{
			pacMan.alert();
			ghosts.alert();

			try
			{
				Thread.sleep(_Game.DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

	        game.advanceGame(pacDir,ghostDirs);	        
	        
	        if(visual)
	        	gv.repaint();
		}

		pacMan.kill();
		ghosts.kill();
	}
	
	/*
	 * Runs a game and records all directions taken by all controllers - the data may then be used to replay any game saved using
	 * replayGame(-).
	 */
	public void runGameTimedAndRecorded(AttackerController attackerController, DefenderController defenderController, boolean visual, String fileName)
	{
		String history="";
		int lastLevel=0;
		boolean firstWrite=false;	//this makes sure the content of any existing files is overwritten
		
		game=new _Game_();
		game.newGame();
		pacMan=new PacMan(attackerController);
		ghosts=new Ghosts(defenderController);
		
		GameView gv=null;
		
		if(visual)
		{
			gv=new GameView(game).showGame();
			
			if(attackerController instanceof AttackerHumanController)
				gv.getFrame().addKeyListener((AttackerHumanController) attackerController);
		}

		attackerController.init(game.copy());
		defenderController.init(game.copy());

		while(!game.gameOver())
		{
			pacMan.alert();
			ghosts.alert();

			try
			{
				Thread.sleep(_Game.DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

	        int[] actionsTaken=game.advanceGame(pacDir,ghostDirs);	        
	        
	        if(visual)
	        	gv.repaint();
	        
	        history=addActionsToString(history,actionsTaken);
        	
	        //saves actions after every level
        	if(game.getLevel()!=lastLevel)
        	{
        		Replay.saveActions(history,fileName,firstWrite);
        		lastLevel=game.getLevel();
        		firstWrite=true;
        	}	   
		}

		attackerController.shutdown(game.copy());
		defenderController.shutdown(game.copy());

		//save the final actions
		Replay.saveActions(history,fileName,firstWrite);
		
		pacMan.kill();
		ghosts.kill();
	}
	
	/*
	 * This is used to replay a recorded game. The controllers are given by the class Replay which may
	 * also be used to load the actions from file.
	 */
	public void replayGame(String fileName)
	{
		_ReplayGame_ game=new _ReplayGame_();
		game.newGame();

		Replay replay=new Replay(fileName);
		AttackerController attackerController = replay.getPacMan();
		DefenderController defenderController = replay.getGhosts();

		attackerController.init(game.copy());
		defenderController.init(game.copy());

		GameView gv=new GameView(game).showGame();
		
		while(!game.gameOver())
		{
	        game.advanceGame(attackerController.update(game.copy(), 0), defenderController.update(game.copy(), 0));
	        gv.repaint();
	        
	        try{Thread.sleep(_Game.DELAY);}catch(Exception e){}
		}

		attackerController.shutdown(game.copy());
		defenderController.shutdown(game.copy());
	}
	
    private String addActionsToString(String history,int[] actionsTaken)
    {
    	history+=(game.getTotalTime()-1)+"\t"+actionsTaken[0]+"\t";

        for (int i = 0; i< _Game.NUM_DEFENDER; i++)
        	history+=actionsTaken[i+1]+"\t";

        history+="\n";
        
        return history;
    }
    	
	//sets the latest direction to take for each game step (if controller replies in time)
	public void setGhostDirs(int[] ghostDirs)
	{
		this.ghostDirs=ghostDirs;
		this.ghostsPlayed=true;
	}
	
	//sets the latest direction to take for each game step (if controller replies in time)
	public void setPacDir(int pacDir)
	{
		this.pacDir=pacDir;
		this.pacmanPlayed=true;
	}
	
	/*
	 * Wraps the controller in a thread for the timed execution. This class then updates the
	 * directions for Exec to parse to the game.
	 */
	public class PacMan extends Thread 
	{
	    private AttackerController pacMan;
	    private boolean alive;

	    public PacMan(AttackerController pacMan)
	    {
	        this.pacMan=pacMan;
	        alive=true;
	        start();
	    }

	    public synchronized void kill() 
	    {
	        alive=false;
	        notify();
	    }
	    
	    public synchronized void alert()
	    {
	        notify();
	    }

	    public void run() 
	    {
	        while(alive) 
	        {
	        	try 
	        	{
	        		synchronized(this)
	        		{
	        			wait();
	                }

	        		setPacDir(pacMan.update(game.copy(), /*System.currentTimeMillis() +*/ _Game.DELAY));
	            } 
	        	catch(InterruptedException e) 
	        	{
	                e.printStackTrace();
	            }
	        }
	    }
	}
	
	/*
	 * Wraps the controller in a thread for the timed execution. This class then updates the
	 * directions for Exec to parse to the game.
	 */
	public class Ghosts extends Thread 
	{
		private DefenderController ghosts;
	    private boolean alive;

	    public Ghosts(DefenderController ghosts)
	    {	    	
	    	this.ghosts=ghosts;
	        alive=true;
	        start();
	    }

	    public synchronized void kill() 
	    {
	        alive=false;
	        notify();
	    }

	    public synchronized void alert() 
	    {
	        notify();
	    }
	    
	    public void run() 
	    {
	        while(alive) 
	        {
	        	try 
	        	{
	        		synchronized(this)
	        		{
	        			wait();
	                }

	        		setGhostDirs(ghosts.update(game.copy(), /*System.currentTimeMillis()+*/ _Game.DELAY));
	            } 
	        	catch(InterruptedException e) 
	        	{
	                e.printStackTrace();
	            }
	        }
	    }
	}
}