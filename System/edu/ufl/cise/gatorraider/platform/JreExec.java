package edu.ufl.cise.gatorraider.platform;

import java.io.PrintWriter;

import edu.ufl.cise.gatorraider.controllers.benchmark.OriginalDefenders;
import edu.ufl.cise.gatorraider.system.*;
import edu.ufl.cise.gatorraider.view.*;

import edu.ufl.cise.gatorraider.controllers.benchmark.*;
import edu.ufl.cise.gatorraider.controllers.*;
import edu.ufl.cise.lib.platform.PlatformCanvasSurface;

/*
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and 
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class JreExec
{
	public static boolean logging = false;
	public static PrintWriter writer = null;
	private static int MAG = 2;
	private static final int GRAPHICAL_DELAY_MS = 2000;

	//Several options are listed - simply remove comments to use the option you want
	public static void main(String[] args)
	{
		AttackerHumanController humanAttacker = new AttackerHumanController();
		AttackerController studentAttacker = new StudentAttackerController();
		AttackerController exampleAttacker = new Devastator();
		DefenderController defender = new OriginalDefenders();

		if (args.length > 0)
		{
			switch (args[0].toLowerCase())
			{
				case "-debugexample":
					runExperiment(exampleAttacker, defender, 5, true);
					break;
				case "-debugstudent":
					runExperiment(studentAttacker, defender, 5, true);
					break;
				case "-testexample":
					runExperiment(exampleAttacker, defender, 100, false);
					break;
				case "-teststudent":
					runExperiment(studentAttacker, defender, 100, false);
					break;
				case "-visualhuman":
					runGame(humanAttacker, defender, true, false, _Game.DELAY, null);
					break;
				case "-visualexample":
					runGame(exampleAttacker, defender, true, false, _Game.DELAY, null);
					break;
				case "-visualstudent":
					runGame(studentAttacker, defender, true, false, _Game.DELAY, null);
					break;
				default:
					System.err.println("Invalid command line option");
			}
		}
		else
			System.err.println("No command line option specified");

		//run game without time limits (un-comment if required)
//		exec.runGameUntimed(new RandomAttacker(),new RandomDefenders(),true,_Game.DELAY);

		//run game with time limits. Here NearestPillAttackerVS is chosen to illustrate how to use graphics for debugging/information purposes
//		exec.runGameTimed(new NearestPillAttackerVS(),new AttractRepelGhosts(false),true);

		//this allows you to record a game and replay it later. This could be very useful when
		//running many games in non-visual mode - one can then pick out those that appear irregular
		//and replay them in visual mode to see what is happening.
//		exec.runGameTimedAndRecorded(new RandomAttacker(),new Legacy2TheReckoning(),true,"human-v-Legacy2.txt");
//		exec.replayGame("human-v-Legacy2.txt");
	}

    protected boolean pacmanPlayed,ghostsPlayed;

    /*
     * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game.
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     */
    public static void runExperiment(AttackerController attackerController, DefenderController defenderController, int trials, boolean debug)
    {
		double avgScore = 0;
    	int tick = 0;
		_Game_ game = new _Game_();

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

    private static PlatformCanvasSurface acquireSurface(_Game_ game, AttackerController attacker, DefenderController defender)
	{
		GameView gv = new GameView(game, MAG);
		PlatformCanvasSurface surface = new PlatformCanvasSurface(gv, game.getWidth() * MAG, game.getHeight() * MAG + 20);
		gv.setPlatformSurface(surface);

		try
		{
			Thread.sleep(GRAPHICAL_DELAY_MS);
		}
		catch (Exception ignored) { }

		if(attacker instanceof AttackerHumanController)
			surface.getFrame().addKeyListener(((AttackerHumanController) attacker));

		return surface;
	}

	public static void runGame(AttackerController attacker, DefenderController defender, boolean visual, boolean timed, int delay, String filename)
	{
		int[] defenderDirs = new int[4]; // TODO: Un-hardcode this length
		int[] attackerDir = new int[1];
		_Game_ game = new _Game_();
		game.newGame();
		PacMan pacMan = null;
		Ghosts ghosts = null;

		// Tracking - for use in recording for replay.
		String history="";
		int lastLevel = 0;
		boolean firstWrite = false;	//this makes sure the content of any existing files is overwritten

		if (timed)
		{
			pacMan = new PacMan(attacker, attackerDir);
			ghosts = new Ghosts(defender, defenderDirs);
		}

		attacker.init(game.copy());
		defender.init(game.copy());
		PlatformCanvasSurface surface = visual ? acquireSurface(game, attacker, defender) : null;

		while(!game.gameOver())
		{
			// Get the updated directions.
			if (timed)
			{
				pacMan.setGame(game);
				ghosts.setGame(game);
				pacMan.alert();
				ghosts.alert();
			}
			else
			{
				attackerDir[0] = attacker.update(game.copy(), delay);
				System.arraycopy(defender.update(game.copy(), delay), 0, defenderDirs, 0, defenderDirs.length);
			}

			// Sleep to sync game / wait for threads to process.
			try
			{
				Thread.sleep(delay);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			// Advance the game.
			int[] actionsTaken = game.advanceGame(attackerDir[0], defenderDirs);

			// Update the visuals, if applicable.
			if(surface != null)
				surface.repaint();

			// If we're recording, store the actions.
			if (filename != null)
			{
				history=addActionsToString(history, actionsTaken, game);

				//saves actions after every level
				if(game.getLevel() != lastLevel)
				{
					Replay.saveActions(history, filename, firstWrite);
					lastLevel = game.getLevel();
					firstWrite = true;
				}
			}
		}

		attacker.shutdown(game.copy());
		defender.shutdown(game.copy());

		//save the final actions
		if (filename != null)
			Replay.saveActions(history, filename, firstWrite);

		if (timed)
		{
			pacMan.kill();
			ghosts.kill();
		}
	}

    /*
     * Run game without time limit. Very good for testing as game progresses as soon as the controllers
     * return their action(s). Can be played with and without visual display of game states. The delay
     * is purely for visual purposes (as otherwise the game could be too fast if controllers compute quickly.
     * For testing, this can be set to 0 for fasted game play.
     */
	public void runGameUntimed(AttackerController attackerController, DefenderController defenderController, boolean visual, int delay)
	{
		runGame(attackerController, defenderController, visual, false, delay, null);
	}
	
    /*
     * Run game with time limit. This is how it will be done in the competition. 
     * Can be played with and without visual display of game states.
     */
	public void runGameTimed(AttackerController attacker, DefenderController defender, boolean visual)
	{
		runGame(attacker, defender, visual, true, _Game_.DELAY, null);
	}
	
	/*
	 * Runs a game and records all directions taken by all controllers - the data may then be used to replay any game saved using
	 * replayGame(-).
	 */
	public void runGameTimedAndRecorded(AttackerController attacker, DefenderController defender, boolean visual, String filename)
	{
		runGame(attacker, defender, visual, true, _Game_.DELAY, filename);
	}
	
	/*
	 * This is used to replay a recorded game. The controllers are given by the class Replay which may
	 * also be used to load the actions from file.
	 */
	public void replayGame(String filename)
	{
		_ReplayGame_ game=new _ReplayGame_();
		game.newGame();

		Replay replay = new Replay(filename);

		AttackerController attacker = replay.getPacMan();
		DefenderController defender = replay.getGhosts();
		attacker.init(game.copy());
		defender.init(game.copy());

		PlatformCanvasSurface surface = acquireSurface(game, attacker, defender);

		while(!game.gameOver())
		{
	        game.advanceGame(attacker.update(game.copy(), 0), defender.update(game.copy(), 0));
	        surface.repaint();
	        
	        try{Thread.sleep(_Game.DELAY);}catch(Exception e){}
		}

		attacker.shutdown(game.copy());
		defender.shutdown(game.copy());
	}
	
    private static String addActionsToString(String history,int[] actionsTaken, _Game_ game)
    {
    	history+=(game.getTotalTime()-1)+"\t"+actionsTaken[0]+"\t";

        for (int i = 0; i< _Game.NUM_DEFENDER; i++)
        	history+=actionsTaken[i+1]+"\t";

        history+="\n";
        
        return history;
    }

	protected static class Character extends Thread
	{
		protected boolean alive = true;
		_Game_ game = null;

		public synchronized void kill()
		{
			alive=false;
			notify();
		}

		public synchronized void alert()
		{
			notify();
		}

		public synchronized void setGame(_Game_ _game)
		{
			game = _game;
		}
	}
	/*
	 * Wraps the controller in a thread for the timed execution. This class then updates the
	 * directions for JreExec to parse to the game.
	 */
	protected static class PacMan extends Character
	{
	    private AttackerController attacker;
	    private final int[] attackerDir;


	    public PacMan(AttackerController _attacker, int[] _attackerDir)
	    {
	    	attackerDir = _attackerDir;
	        attacker = _attacker;
	        start();
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

	        		int newDir = attacker.update(game.copy(), /*System.currentTimeMillis() +*/ _Game.DELAY);

	        		synchronized (attackerDir)
					{
						attackerDir[0] = newDir;
					}
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
	 * directions for JreExec to parse to the game.
	 */
	protected static class Ghosts extends Character
	{
		private DefenderController ghosts;
		private final int[] ghostDirs;

	    public Ghosts(DefenderController _ghosts, int[] _ghostDirs)
	    {	    	
	    	ghosts = _ghosts;
	    	ghostDirs = _ghostDirs;
	        start();
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

	                // Update ghost directories
	        		int[] newGhostDirs = ghosts.update(game.copy(), /*System.currentTimeMillis()+*/ _Game.DELAY);

	        		synchronized(ghostDirs)
					{
						System.arraycopy(newGhostDirs, 0, ghostDirs, 0, ghostDirs.length);
					}
	            }
	        	catch(InterruptedException e) 
	        	{
	                e.printStackTrace();
	            }
	        }
	    }
	}
}