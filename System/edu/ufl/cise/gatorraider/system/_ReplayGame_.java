package edu.ufl.cise.gatorraider.system;

/*
 * This class is to replay games that were recorded using Replay. The only differences are:
 * 1. Ghost reversals are removed
 * 2. Directions are not checked (since they are necessarily valid)
 * This class should only be used in conjunction with stored directions, not to play the game itself.
 */
public final class _ReplayGame_ extends _Game_
{
	public _ReplayGame_()
	{
		super(null);
	}

	public _ReplayGame_(String gameDir)
	{
		super(gameDir);
	}
	//Updates the locations of the ghosts without reversals
	protected void updateEnemies(int[] directions, boolean reverse)
	{
		super.updateEnemies(directions,false);
	}
	
	public int checkEnemyDir(int whichEnemy, int direction)
	{
		return direction;
	}
	
	public int checkHeroDir(int direction)
	{
		return direction;		
	}
}