package game.controllers;

import game.models.Game;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public final class Human extends KeyAdapter implements AttackerController
{
    private int key;

    public void init(Game game) { }

    public int update(Game game,long dueTime)
    {
    	if (key == KeyEvent.VK_UP)
            return 0;
    	else if (key == KeyEvent.VK_RIGHT)
            return 1;
        else if (key == KeyEvent.VK_DOWN)
            return 2;
        else if (key == KeyEvent.VK_LEFT)
            return 3;

        return -1;
    }

    public void shutdown(Game game) { }

    public void keyPressed(KeyEvent e)
    {
        key=e.getKeyCode();
    }
}