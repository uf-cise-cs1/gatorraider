package edu.ufl.cise.gatorraider.platform;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import edu.ufl.cise.gatorraider.controllers.benchmark.OriginalDefenders;
import edu.ufl.cise.gatorraider.system.*;

import edu.ufl.cise.gatorraider.controllers.benchmark.*;
import edu.ufl.cise.gatorraider.controllers.*;
import edu.ufl.cise.lib.system.AppLoop;

/*
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their own controllers.
 */

public class JreExec
{
	public static void main(String[] args)
	{
		AttackerHumanController humanAttacker = new AttackerHumanController();
		AttackerController studentAttacker = new StudentAttackerController();
		AttackerController exampleAttacker = new Devastator();
		DefenderController defender = new OriginalDefenders();
		AppLoop loop = null;

		if (args.length > 0)
		{
			switch (args[0].toLowerCase())
			{
				case "-debugexample":
				    runExperiment(exampleAttacker, defender, 5, true);
					return;
				case "-debugstudent":
                    runExperiment(studentAttacker, defender, 5, true);
                    return;
				case "-testexample":
                    runExperiment(exampleAttacker, defender, 100, false);
                    return;
				case "-teststudent":
                    runExperiment(studentAttacker, defender, 100, false);
                    return;
				case "-visualhuman":
                    loop = new GameLoop(humanAttacker, defender, true, false, _Game.DELAY, null);
					break;
				case "-visualexample":
                    loop = new GameLoop(exampleAttacker, defender, true, false, _Game.DELAY, null);
					break;
				case "-visualstudent":
                    loop = new GameLoop(studentAttacker, defender, true, false, _Game.DELAY, null);
					break;
				default:
					System.err.println("Invalid command line option");
			}
		}
		else
		    System.err.println("No command line option specified");

		if (loop != null)
        {
            // Set up surface
            // Set up loop
            loop.init();

            while (!loop.isDone()) loop.update();

            loop.shutdown();
        }
	}

     /* For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game.
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     */
    private static void runExperiment(AttackerController attackerController, DefenderController defenderController, int trials, boolean debug)
    {
        PrintWriter writer = null;

        double avgScore = 0;
        int tick = 0;
        _Game_ game = new _Game_();

        if (debug)
        {
            try
            {
                writer = new PrintWriter("experiment.txt", StandardCharsets.UTF_8);
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
}