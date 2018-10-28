package edu.ufl.cise.gatorraider.platform;

import edu.ufl.cise.gatorraider.controllers.AttackerController;
import edu.ufl.cise.gatorraider.controllers.AttackerHumanController;
import edu.ufl.cise.gatorraider.controllers.DefenderController;
import edu.ufl.cise.gatorraider.system._Game_;
import edu.ufl.cise.gatorraider.view.GameView;
import edu.ufl.cise.gatorraider.view.Replay;
import edu.ufl.cise.lib.platform.PlatformCanvasSurface;
import edu.ufl.cise.lib.system.AppLoop;

public class GameLoop implements AppLoop
{
    private static int MAG = 2;
    private static final int GRAPHICAL_DELAY_MS = 2000;

    private AttackerController attacker;
    private DefenderController defender;
    private boolean visual;
    private boolean timed;
    private int delay;
    private String file;

    private int[] defenderDirs;
    private int[] attackerDir;
    protected _Game_ game;
    private PacMan pacMan;
    private Ghosts ghosts;
    private String history;
    private int lastLevel;
    private boolean firstWrite;	//this makes sure the content of any existing files is overwritten
    private PlatformCanvasSurface surface;

    public GameLoop(AttackerController _attacker, DefenderController _defender, boolean _visual, boolean _timed, int _delay, String _file)
    {
        attacker = _attacker;
        defender = _defender;
        visual = _visual;
        timed = _timed;
        delay = _delay;
        file = _file;
    }

    public void init()
    {
        defenderDirs = new int[4]; // TODO: Un-hardcode this length
        attackerDir = new int[1];
        game = new _Game_();
        game.newGame();

        // Tracking - for use in recording for replay.
        history = "";
        lastLevel = 0;
        firstWrite = false;	//this makes sure the content of any existing files is overwritten

        if (timed)
        {
            pacMan = new PacMan(attacker, attackerDir);
            ghosts = new Ghosts(defender, defenderDirs);
        }
        else
        {
            pacMan = null;
            ghosts = null;
        }

        attacker.init(game.copy());
        defender.init(game.copy());
        surface = visual ? acquireSurface(game, attacker, defender) : null;
    }

    public void update()
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
        if (file != null)
        {
            history=addActionsToString(history, actionsTaken, game);

            //saves actions after every level
            if(game.getLevel() != lastLevel)
            {
                Replay.saveActions(history, file, firstWrite);
                lastLevel = game.getLevel();
                firstWrite = true;
            }
        }
    }

    public void shutdown()
    {
        attacker.shutdown(game.copy());
        defender.shutdown(game.copy());

        //save the final actions
        if (file != null) Replay.saveActions(history, file, firstWrite);

        if (timed)
        {
            pacMan.kill();
            ghosts.kill();
        }
    }

    public boolean isDone()
    {
        return game.gameOver();
    }

    private static PlatformCanvasSurface acquireSurface(_Game_ game, AttackerController attacker, DefenderController defender)
    {
        GameView gv = new GameView(game, MAG);
        PlatformCanvasSurface surface = new PlatformCanvasSurface(gv, _Game_.WIDTH * MAG, _Game_.HEIGHT * MAG + 20);
        gv.setPlatformSurface(surface);

        try
        {
            Thread.sleep(GRAPHICAL_DELAY_MS);
        }
        catch (Exception ignored) { }

        if(attacker instanceof AttackerHumanController)
            surface.addListener(((AttackerHumanController) attacker));

        return surface;
    }

    private static String addActionsToString(String history,int[] actionsTaken, _Game_ game)
    {
        history+=(game.getTotalTime()-1)+"\t"+actionsTaken[0]+"\t";

        for (int i = 0; i< _Game_.NUM_DEFENDER; i++)
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

                    int newDir = attacker.update(game.copy(), /*System.currentTimeMillis() +*/ _Game_.DELAY);

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
                    int[] newGhostDirs = ghosts.update(game.copy(), /*System.currentTimeMillis()+*/ _Game_.DELAY);

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
