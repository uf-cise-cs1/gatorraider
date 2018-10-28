package edu.ufl.cise.gatorraider.platform;

import edu.ufl.cise.gatorraider.controllers.benchmark.OriginalDefenders;
import edu.ufl.cise.gatorraider.system.*;

import edu.ufl.cise.gatorraider.controllers.benchmark.*;
import edu.ufl.cise.gatorraider.controllers.*;
import edu.ufl.cise.lib.system.AppLoop;

/*
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and 
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class JreExec
{
	//Several options are listed - simply remove comments to use the option you want
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
				    loop = new ExperimentLoop(exampleAttacker, defender, 5, true);
					break;
				case "-debugstudent":
                    loop = new ExperimentLoop(studentAttacker, defender, 5, true);
					break;
				case "-testexample":
                    loop = new ExperimentLoop(exampleAttacker, defender, 100, false);
					break;
				case "-teststudent":
                    loop = new ExperimentLoop(studentAttacker, defender, 100, false);
					break;
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
            loop.init();

            while (!loop.isDone()) loop.update();

            loop.shutdown();
        }
	}
}