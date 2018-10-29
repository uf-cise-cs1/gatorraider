package edu.ufl.cise.gatorraider.controllers;

import edu.ufl.cise.gatorraider.models.Game;
import edu.ufl.cise.lib.platform.InputListener;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class AttackerHumanController extends InputListener implements AttackerController
{
    private int lastDir = Game.Direction.EMPTY;

    private int directionFromKey(int keyCode)
    {
        switch (keyCode)
        {
            case InputListener.KEYCODE_UP:
                return Game.Direction.UP;
            case InputListener.KEYCODE_RIGHT:
                return Game.Direction.RIGHT;
            case InputListener.KEYCODE_DOWN:
                return Game.Direction.DOWN;
            case InputListener.KEYCODE_LEFT:
                return Game.Direction.LEFT;
            default:
                return Game.Direction.EMPTY;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode)
    {
        lastDir = directionFromKey(keyCode);
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode)
    {
        if (directionFromKey(keyCode) == lastDir)
            lastDir = Game.Direction.EMPTY;

        return true;
    }

    public void init(Game game) { }
    public void shutdown(Game game) { }
    public int update(Game game,long dueTime)
    {
        return lastDir;
    }
}