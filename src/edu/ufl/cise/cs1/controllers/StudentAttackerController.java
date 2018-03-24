package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue)
	{
		int action;

		//Chooses a random LEGAL action if required.
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
		if (possibleDirs.size() != 0)
			action = possibleDirs.get(Game.rng.nextInt(possibleDirs.size()));
		else
			action = -1;

		return action;
	}
}