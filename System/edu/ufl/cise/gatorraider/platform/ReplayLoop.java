package edu.ufl.cise.gatorraider.platform;

import edu.ufl.cise.gatorraider.controllers.AttackerController;
import edu.ufl.cise.gatorraider.controllers.AttackerHumanController;
import edu.ufl.cise.gatorraider.controllers.DefenderController;
import edu.ufl.cise.gatorraider.system._Game;
import edu.ufl.cise.gatorraider.system._Game_;
import edu.ufl.cise.gatorraider.system._ReplayGame_;
import edu.ufl.cise.gatorraider.view.GameView;
import edu.ufl.cise.gatorraider.view.Replay;
import edu.ufl.cise.lib.platform.PlatformCanvasSurface;
import edu.ufl.cise.lib.system.AppLoop;

/*
 * This is used to replay a recorded game. The controllers are given by the class Replay which may
 * also be used to load the actions from file.
 */
public class ReplayLoop implements AppLoop
{
    private static int MAG = 2;
    private static final int GRAPHICAL_DELAY_MS = 2000;

    private String filename;

    private _Game_ game;
    private AttackerController attacker;
    private DefenderController defender;
    private PlatformCanvasSurface surface;

    public ReplayLoop(String _filename)
    {
        filename = _filename;
    }

    @Override
    public void init()
    {
        game = new _ReplayGame_();
        game.newGame();
        Replay replay = new Replay(filename);

        attacker = replay.getPacMan();
        defender = replay.getGhosts();
        attacker.init(game.copy());
        defender.init(game.copy());
        surface = acquireSurface(game, attacker, defender);
    }

    @Override
    public void update()
    {
        game.advanceGame(attacker.update(game.copy(), 0), defender.update(game.copy(), 0));
        surface.repaint();

        try {Thread.sleep(_Game.DELAY);}catch (Exception e) {}
    }

    @Override
    public void shutdown()
    {
        attacker.shutdown(game.copy());
        defender.shutdown(game.copy());
    }

    @Override
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

}
