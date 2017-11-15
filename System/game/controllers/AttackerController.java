package game.controllers;

import game.models.Game;

/*
 * Interface that the hero controllers must implement. The only method that is
 * required is getAction(-), which returns the direction to be taken: 
 * Up - Right - Down - Left -> 0 - 1 - 2 - 3
 * Any other number is considered to be a lack of action (Neutral). 
 */
public interface AttackerController
{
	public void init(Game game);
	public int update(Game game, long timeDue);
	public void shutdown(Game game);
}
