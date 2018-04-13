package edu.ufl.cise.cs1.controllers;

import java.awt.event.*;
import game.controllers.AttackerController;
import game.models.*;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class AttackerHumanController implements AttackerController, KeyListener
{
    private int lastDir = Game.Direction.EMPTY;

    private int directionFromKey(KeyEvent keyEvent)
    {
        switch (keyEvent.getKeyCode())
        {
            case KeyEvent.VK_UP:
                return Game.Direction.UP;
            case KeyEvent.VK_RIGHT:
                return Game.Direction.RIGHT;
            case KeyEvent.VK_DOWN:
                return Game.Direction.DOWN;
            case KeyEvent.VK_LEFT:
                return Game.Direction.LEFT;
            default:
                return Game.Direction.EMPTY;
        }
    }

    public void keyPressed(KeyEvent keyEvent)
    {
        lastDir = directionFromKey(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent)
    {
        if (directionFromKey(keyEvent) == lastDir)
            lastDir = Game.Direction.EMPTY;
    }

    public void keyTyped(KeyEvent e) { }
    public void init(Game game) { }
    public void shutdown(Game game) { }

    public int update(Game game,long dueTime)
    {
        return lastDir;
    }
}