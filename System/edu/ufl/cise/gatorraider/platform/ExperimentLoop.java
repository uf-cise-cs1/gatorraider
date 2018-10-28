package edu.ufl.cise.gatorraider.platform;

import edu.ufl.cise.gatorraider.controllers.AttackerController;
import edu.ufl.cise.gatorraider.controllers.DefenderController;
import edu.ufl.cise.gatorraider.system._Game_;
import edu.ufl.cise.lib.system.AppLoop;

import java.io.PrintWriter;

/*
 * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
 * against a chosen opponent: the random nature of the game means that performance can vary from game to game.
 * Running many games and looking at the average score (and standard deviation/error) helps to get a better
 * idea of how well the controller is likely to do in the competition.
 */
public class ExperimentLoop implements AppLoop
{
    private static PrintWriter writer = null;

    private AttackerController attacker;
    private DefenderController defender;
    private boolean debug;
    private int trials, trialNo;

    private double scoreSum;
    private int tick;
    private _Game_ game;

    public ExperimentLoop(AttackerController _attacker, DefenderController _defender, int _trials, boolean _debug)
    {
        attacker = _attacker;
        defender = _defender;
        trials = _trials;
        debug = _debug;
    }

    public void init()
    {
        scoreSum = 0;
        tick = 0;
        game = new _Game_();
        trialNo = 0;

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

        game.newGame();
        attacker.init(game.copy());
        defender.init(game.copy());
    }

    public void update()
    {
        long due = _Game_.DELAY;
        int attackerDirection = attacker.update(game.copy(), due);
        int[] defenderDirections = defender.update(game.copy(), due);

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

        if (game.gameOver())
        {
            scoreSum += game.getScore();
            attacker.shutdown(game.copy());
            defender.shutdown(game.copy());
            System.out.println("Trial #" + trialNo + " complete. Score: " + game.getScore());
            trialNo++;

            if (trialNo < trials)
            {
                game.newGame();
                attacker.init(game.copy());
                defender.init(game.copy());
            }
        }
    }

    public void shutdown()
    {
        System.out.println(scoreSum / trials);
        if (writer != null)
        {
            writer.flush();
            writer.close();
        }
    }

    public boolean isDone()
    {
        return trialNo >= trials;
    }
}
