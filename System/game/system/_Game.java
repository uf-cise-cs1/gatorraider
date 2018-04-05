/*
 *
 * Code written by Philipp Rohlfshagen, based on earlier implementations of the game by
 * Simon Lucas and David Robles. 
 *
 * Code refactored and updated by Jeremiah Blanchard at the University of Florida (2017).
 *
 * You may use and distribute this code freely for non-commercial purposes. This notice 
 * needs to be included in all distributions. Deviations from the original should be 
 * clearly documented. We welcome any comments and suggestions regarding the code.
 */
package game.system;

import game.models.*;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

/*
 * Simple implementation of the game. The class Game contains all code relating to the
 * game; the class GameView displays the game. Controllers must implement AttackerController
 * and DefenderController respectively. The game may be executed using Exec.
 */
public class _Game implements Game
{	
	//File names for data
	public static String[] nodeNames = {"a","b","c","d"};
	public static String[] distNames = {"da","db","dc","dd"};
	public static String pathMazes = "data";
	
	//Static stuff (mazes are immutable - hence static)
	protected static _Maze[] mazes = new _Maze[NUM_MAZES];
	
	//Variables (game state):
	TreeSet<Node> pills, powerPills;
//	protected BitSet pills,powerPills;

	//level-specific
	protected int curMaze,totLevel,levelTime,totalTime,score, defenderKillMultiplier;
	protected boolean gameOver;

	// Actors
	protected _Attacker attacker;
	protected _Defender[] defenders;

	protected int livesRemaining;
	protected boolean extraLife;

	/////////////////////////////////////////////////////////////////////////////
	/////////////////  Constructors and Initializers   //////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
	//Constructor
	protected _Game(){}

	//loads the mazes and store them
	protected void init()
	{		
		for(int i=0;i<mazes.length;i++)
			if(mazes[i]==null)
				mazes[i]=new _Maze(i);
	}
	
	//Creates an exact copy of the game
	public Game copy()
	{
		_Game copy=new _Game();
		copy.pills= (TreeSet<Node>) pills.clone();
		copy.powerPills = (TreeSet<Node>) powerPills.clone();
		copy.curMaze=curMaze;
		copy.totLevel=totLevel;
		copy.levelTime=levelTime;
		copy.totalTime=totalTime;
		copy.defenderKillMultiplier = defenderKillMultiplier;
		copy.score = score;
		copy.gameOver=gameOver;
		copy.attacker = attacker.clone();
		copy.livesRemaining=livesRemaining;
		copy.extraLife=extraLife;
		copy.defenders = new _Defender[defenders.length];

		for (int index = 0; index < defenders.length; index++)
			copy.defenders[index] = defenders[index].clone();

		return copy;
	}
	
	//If the hero has been eaten or a new level has been reached
	protected void reset(boolean newLevel)
	{
		if(newLevel)
		{
			curMaze=(curMaze+1)% _Game.NUM_MAZES;
			totLevel++;
			levelTime=0;
			pills.clear();
			powerPills.clear();
			pills.addAll(mazes[curMaze].getPillNodes());
			powerPills.addAll(mazes[curMaze].getPowerPillNodes());
		}

		attacker = new _Attacker(mazes[curMaze].initialHeroPosition, _Game.INITIAL_ATTACKER_DIR);

		for (int index = 0; index < defenders.length; index++)
		{
			defenders[index] = new _Defender(mazes[curMaze].lairPosition, _Game.INITIAL_DEFENDER_DIRS[index], (int)(_Game.LAIR_TIMES[index]*(Math.pow(LAIR_REDUCTION,totLevel))));
		}

		defenderKillMultiplier = 1;
	}
		
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////  Game Play   //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
			
	//Central method that advances the game state
	public int[] advanceGame(int heroDir, int[] defenderDirs)
	{			
		updateHero(heroDir);			//move the hero
		eatPill();							//eat a pill
		boolean reverse=eatPowerPill();		//eat a power pill
		updateEnemies(defenderDirs,reverse);	//move defenders
		
		//This is primarily done for the replays as reset (as possibly called by feast()) sets the 
		//last directions to the initial ones, not the ones taken
		int[] actionsTakens = { attacker.direction, defenders[0].direction, defenders[1].direction, defenders[2].direction, defenders[3].direction };
		
		feast();							//defenders eat the hero or vice versa
		
		for(int i = 0; i < defenders.length; i++)
		{
			if (defenders[i].lairTime > 0)
			{
				defenders[i].lairTime--;

				if (defenders[i].lairTime == 0)
					defenders[i].location = mazes[curMaze].initialEnemiesPosition;
			}
		}

		if(!extraLife && score>=EXTRA_LIFE_SCORE)	//award 1 extra life at 10000 points
		{
			extraLife=true;
			livesRemaining++;
		}
	
		totalTime++;
		levelTime++;
		checkLevelState();	//check if level/game is over
		
		return actionsTakens;
	}

	public Attacker getAttacker() { return attacker.clone(); }
	public Defender getDefender(int whichDefender) { return defenders[whichDefender].clone(); }

	public List<Defender> getDefenders()
	{
		ArrayList<Defender> result = new ArrayList<Defender>();

		for (_Defender enemy : defenders)
			result.add(enemy.clone());

		return result;
	}

	//Updates the location of the hero
	protected void updateHero(int direction)
	{
		direction = checkHeroDir(direction);

		if (direction == 4) // If the attacker can't move, stop.
			return;

		attacker.direction = direction;
		attacker.location = attacker.location.neighbors[direction];
	}
		
	//Checks the direction supplied by the controller and substitutes for a legal one if necessary
	protected int checkHeroDir(int direction)
	{
		List<Node> neighbors = attacker.location.getNeighbors();
		int oldDirection = attacker.direction;
				
		if((direction > 3 || direction < 0 || neighbors.get(direction) == null) && (oldDirection > 3 || oldDirection < 0 || neighbors.get(oldDirection) == null))
			return 4;
		
		if(direction < 0 || direction > 3)
			direction = oldDirection;
		
		if(neighbors.get(direction) == null)
			if(neighbors.get(oldDirection) != null)
				direction = oldDirection;
			else
			{
				List<Integer> options = attacker.getPossibleDirs(true);
				direction = options.get(rng.nextInt(options.size()));
			}

		return direction;		
	}
	
	//Updates the locations of the defenders
	protected void updateEnemies(int[] directions, boolean reverse)
	{
//		if(directions==null)
//			directions=Arrays.copyOf(lastEnemyDirs, lastEnemyDirs.length);
		
		for(int i = 0; i < defenders.length; i++)
		{											
			if(reverse && defenders[i].lairTime == 0)
			{
				defenders[i].direction = defenders[i].getReverse();
				defenders[i].location = defenders[i].location.neighbors[defenders[i].direction];
			}
			else if(defenders[i].lairTime == 0 && (defenders[i].vulnerableTime == 0 || defenders[i].vulnerableTime % DEFENDER_SPEED_REDUCTION !=0))
			{
				directions[i] = checkEnemyDir(i, directions[i]);
				defenders[i].direction = directions[i];
				defenders[i].location = defenders[i].location.neighbors[directions[i]];
			}
		}		
	}
	
	//Checks the directions supplied by the controller and substitutes for a legal ones if necessary
	protected int checkEnemyDir(int whichEnemy, int direction)
	{
		if(direction < 0 || direction > 3)
			direction = defenders[whichEnemy].direction;
			
		List<Node> neighbors = defenders[whichEnemy].getPossibleLocations();
			
		if(neighbors.get(direction) == null)
		{
			if(neighbors.get(defenders[whichEnemy].direction) != null)
				direction = defenders[whichEnemy].direction;
			else
			{
				List<Integer> options = defenders[whichEnemy].getPossibleDirs();
				direction = options.get(rng.nextInt(options.size()));
			}
		}

		return direction;
	}
		
	//Eats a pill
	protected void eatPill()
	{
		if (pills.contains(attacker.location))
		{
			score += Game.PILL_SCORE;
			pills.remove(attacker.location);
		}
	}
	
	//Eats a power pill - turns defenders edible (blue)
	protected boolean eatPowerPill()
	{
		boolean reverse = false;

		if(powerPills.contains(attacker.location))
		{
			score += Game.POWER_PILL_SCORE;
			defenderKillMultiplier =1;
			powerPills.remove(attacker.location);
			
			//This ensures that only defenders outside the lair (i.e., inside the maze) turn edible
			int newEdibleTime=(int)(Game.VULNERABLE_TIME * (Math.pow(Game.VULNERABLE_TIME_REDUCTION, totLevel)));
			
			for(int i = 0; i< NUM_DEFENDER; i++)
				if(defenders[i].lairTime == 0)
					defenders[i].vulnerableTime = newEdibleTime;
				else
					defenders[i].vulnerableTime = 0;
			
			//This turns all defenders edible, independent on whether they are in the lair or not
//			Arrays.fill(edibleTimes,(int)(_Game.VULNERABLE_TIME*(Math.pow(_Game.VULNERABLE_TIME_REDUCTION,totLevel))));
			
			reverse = true;
		}
		else if (levelTime > 1 && rng.nextDouble() < Game.DEFENDER_REVERSAL)	//random enemy reversal
			reverse=true;
		
		return reverse;
	}
	
	//This is where the characters of the game eat one another if possible
	protected void feast()
	{
		for(int i = 0; i < defenders.length; i++)
		{
			int distance=attacker.location.getPathDistance(defenders[i].location);
			
			if(distance <= Game.EAT_DISTANCE && distance != -1)
			{
				if(defenders[i].vulnerableTime > 0)									//hero eats enemy
				{
					score+= Game.DEFENDER_KILL_SCORE * defenderKillMultiplier;
					defenderKillMultiplier *=2;
					defenders[i].vulnerableTime = 0;
					defenders[i].lairTime = (int)(Game.COMMON_LAIR_TIME*(Math.pow(Game.LAIR_REDUCTION,totLevel)));
					defenders[i].location = mazes[curMaze].lairPosition;
					defenders[i].direction = Game.INITIAL_DEFENDER_DIRS[i];
				}
				else													//enemy eats hero
				{
					livesRemaining--;
					
					if(livesRemaining<=0)
					{
						gameOver=true;
						return;
					}
					else
						reset(false);
				}
			}
		}
		
		for(int i = 0; i < defenders.length; i++)
			if(defenders[i].vulnerableTime > 0)
				defenders[i].vulnerableTime--;
	}
	
	//Checks the state of the level/game and advances to the next level or terminates the game
	protected void checkLevelState()
	{
		//if all pills have been eaten or the time is up...
		if((pills.isEmpty() && powerPills.isEmpty()) || levelTime>=LEVEL_LIMIT)
		{
			//award any remaining pills to the hero
			score+= _Game.PILL_SCORE * pills.size() + Game.POWER_PILL_SCORE * powerPills.size();

			//put a cap on the total number of levels played
			if(totLevel+1== _Game.MAX_LEVELS)
			{
				gameOver=true;
				return;
			}
			else
				reset(true);
		}		
	}
	
	/////////////////////////////////////////////////////////////////////////////
	///////////////////////////  Getter Methods  ////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	//Whether the game is over or not
	public boolean gameOver()
	{
		return gameOver;
	}
	
	//Whether the pill specified is still there
	public boolean checkPill(Node location)
	{
		return pills.contains(location);
	}
	
	//Whether the power pill specified is still there
	public boolean checkPowerPill(Node location)
	{
		return powerPills.contains(location);
	}

	public List<Node> getPillList() { return new ArrayList<Node>(pills); }
	public List<Node> getPowerPillList() { return new ArrayList<Node>(powerPills); }

	//The current level
	public int getLevel()
	{
		return totLevel;
	}

	//The current maze # (1-4)
	public int getCurMazeNum()
	{
		return curMaze;
	}

	//The current maze object
	public Maze getCurMaze()
	{
		return mazes[curMaze];
	}

	//Lives that remain for the hero
	public int getLivesRemaining()
	{
		return livesRemaining;
	}

	//Returns the score of the game
	public int getScore()
	{
		return score;
	}
	
	//Returns the time of the current level (important with respect to LEVEL_LIMIT)
	public int getLevelTime()
	{
		return levelTime;
	}
	
	//Total time the game has been played for (at most LEVEL_LIMIT*MAX_LEVELS)
	public int getTotalTime()
	{
		return totalTime;
	}
	
	//Returns the pill index of the node. If it is -1, the node has no pill. Otherwise one can
	//use the bitset to check whether the pill has already been eaten
//	public int getPillIndex(Node node)
//	{
//		return node.getPillIndex();
//	}
	
	//Returns the power pill index of the node. If it is -1, the node has no pill. Otherwise one 
	//can use the bitset to check whether the pill has already been eaten
//	public int getPowerPillIndex(Node node)
//	{
//		return node.getPowerPillIndex();
//	}
}