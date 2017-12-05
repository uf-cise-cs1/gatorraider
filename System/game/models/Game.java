/*
 * Generalized maze game version adapted and object-oriented model written by
 * Jeremiah Blanchard at the University of Florida (2017).
 *
 * Generalized update based on "Ms Pac-Man versus Ghost Team Competition" by
 * Philipp Rohlfshagen, David Robles and Simon Lucas of the University of Essex.
 * Original code written by Philipp Rohlfshagen, based on earlier implementations of
 * the game by Simon Lucas and David Robles.
 *
 * You may use and distribute this code freely for non-commercial purposes. This notice 
 * needs to be included in all distributions. Deviations from the original should be 
 * clearly documented. We welcome any comments and suggestions regarding the code.
 */
package game.models;
import java.util.List;
//import java.util.Random;
import game.system._Random;

/*
 * This interface defines the contract between the game engine and the controllers. It provides all
 * the methods a controller may use to (a) query the game state, (b) compute game-related attributes
 * and (c) test moves by using a forward model (i.e., copy() followed by advanceGame()).
 */
public interface Game
{
	int getScore();											// Returns the score of the game
	int getLevel();											// Returns the current level
	int getLevelTime();										// Returns the time for which the CURRENT level has been played
	int getTotalTime();										// Returns the time for which the game has been played (across all levels)
	int getLivesRemaining();								// Returns the number of lives remaining for the hero

	List<Node> getPillList();								// Get a list of all available pills in the current level
	List<Node> getPowerPillList();							// Get a list of all available power pills in the current level

	boolean checkPill(Node location);						// Checks if the location specified is a pill / is still available
	boolean checkPowerPill(Node location);					// Checks if the location specified is a power pill / is still available

	Attacker getAttacker();									// Returns a copy of the attacker object
	Defender getDefender(int whichDefender);				// Returns a copy of a specific enemy number
	List<Defender> getDefenders();							// Returns a copy of the enemy array

	Game copy();											// Returns an exact copy of the game (forward model)
	Maze getCurMaze();										// Returns the current maze information
	_Random rng = new _Random(0);					// _Random number generator with fixed seed

	int[] advanceGame(int attackerDir, int[] defenderDirs);	// Advances the game using the actions (directions) supplied; returns all directions played [Attacker, Enemy1, Enemy2, Enemy3, Enemy4]
	boolean gameOver();										// Returns true if the hero has lost all her lives or if MAX_LEVELS has been reached

	//These constants specify the exact nature of the game
	class Direction { public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3, EMPTY = -1; }	//directions

	// Points
	int PILL_SCORE = 10;
	int POWER_PILL_SCORE = 50;
	int DEFENDER_KILL_SCORE = 200;

	// Timing
	int VULNERABLE_TIME = 200;						//initial time an enemy is edible for (decreases as level number increases)
	float VULNERABLE_TIME_REDUCTION = 0.9f;			//reduction factor by which edible time decreases as level number increases
	int[] LAIR_TIMES = { 40, 60, 80, 100 };			//time spend in the lair by each enemy at the start of a level
	int COMMON_LAIR_TIME = 40;						//time spend in lair after being eaten
	float LAIR_REDUCTION = 0.9f;					//reduction factor by which lair times decrease as level number increases
	int LEVEL_LIMIT = 3000;							//time limit for a level
	int DELAY = 40;									//delay (in milliseconds) between game advancements

	// Initial Game State
	int NUM_LIVES = 3;								//total number of lives the hero has (current + NUM_LIVES-1 spares)
	int INITIAL_ATTACKER_DIR = 3;					//initial direction taken by the hero
	int[] INITIAL_DEFENDER_DIRS = { 3, 1, 3, 1 };	//initial directions for the defenders (after leaving the lair)
	int DEFENDER_SPEED_REDUCTION = 2;					//difference in speed when defenders are edible (every DEFENDER_SPEED_REDUCTION, an enemy remains stationary)

	// Misc. configurations for game
	float DEFENDER_REVERSAL = 0.0015f;				//probability of a global enemy reversal event
	int EXTRA_LIFE_SCORE = 10000;					//extra life is awarded when this many points have been collected
	int EAT_DISTANCE = 2;							//distance in the connected graph considered close enough for an eating event to take place
	int NUM_DEFENDER = 4;							//number of defenders in the game
	int NUM_MAZES = 4;								//number of different mazes in the game
	int MAX_LEVELS = 16;							//maximum number of levels played before the end of the game

	enum DM { PATH, EUCLID, MANHATTAN };			//simple enumeration for use with the direction methods
}