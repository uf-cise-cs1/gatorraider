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

	//Several options are listed - simply remove comments to use the option you want
	public static void main(String[] args)
	{
		AttackerHumanController humanAttacker = new AttackerHumanController();
		AttackerController studentAttacker = new StudentAttackerController();
		AttackerController exampleAttacker = new Devastator();
		DefenderController defender = new OriginalDefenders();
		GameLoop loop = null;
		Runnable command = null;

		if (args.length > 0)
		{
			switch (args[0].toLowerCase())
			{
				case "-debugexample":
					command = () -> runExperiment(exampleAttacker, defender, 5, true);
					break;
				case "-debugstudent":
					command = () -> runExperiment(studentAttacker, defender, 5, true);
					break;
				case "-testexample":
					command = () -> runExperiment(exampleAttacker, defender, 100, false);
					break;
				case "-teststudent":
					command = () -> runExperiment(studentAttacker, defender, 100, false);
					break;
				case "-visualhuman":
					command = () -> runGame(humanAttacker, defender, true, false, _Game.DELAY, null);
					break;
				case "-visualexample":
					command = () -> runGame(exampleAttacker, defender, true, false, _Game.DELAY, null);
					break;
				case "-visualstudent":
					command = () -> runGame(studentAttacker, defender, true, false, _Game.DELAY, null);
					break;
				default:
					System.err.println("Invalid command line option");
			}
		} else System.err.println("No command line option specified");

		if (command != null) command.run();
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

	protected boolean pacmanPlayed, ghostsPlayed;

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

		for (int i = 0; i < trials; i++)
		{
			game.newGame();
			attackerController.init(game.copy());
			defenderController.init(game.copy());

			while (!game.gameOver())
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

			avgScore += game.getScore();
			attackerController.shutdown(game.copy());
			defenderController.shutdown(game.copy());
			System.out.println("Trial #" + i + " complete. Score: " + game.getScore());
		}

		System.out.println(avgScore / trials);
		if (writer != null)
		{
			writer.flush();
			writer.close();
		}
	}

	public static void runGame(AttackerController attacker, DefenderController defender, boolean visual, boolean timed, int delay, String filename)
	{
		GameLoop loop = new GameLoop(attacker, defender, visual, timed, delay, filename);
		loop.init();
		while (!loop.isDone())
			loop.update();

		loop.shutdown();
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
		_ReplayGame_ game = new _ReplayGame_();
		game.newGame();

		Replay replay = new Replay(filename);

		AttackerController attacker = replay.getPacMan();
		DefenderController defender = replay.getGhosts();
		attacker.init(game.copy());
		defender.init(game.copy());

//		PlatformCanvasSurface surface = acquireSurface(game, attacker, defender);

		while (!game.gameOver())
		{
			game.advanceGame(attacker.update(game.copy(), 0), defender.update(game.copy(), 0));
//			surface.repaint();

			try {Thread.sleep(_Game.DELAY);}catch (Exception e) {}
		}

		attacker.shutdown(game.copy());
		defender.shutdown(game.copy());
	}
}