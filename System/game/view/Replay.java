package game.view;

import game.controllers.AttackerController;
import game.controllers.DefenderController;
import game.models.Game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * This class allows one to record games to replay them later. This may be done in the Exec class.
 * It simply records all the directions taken by the controllers AFTER directions were corrected and/or
 * random ghost reversals. The game must be replayed using _ReplayGame_ which does not have random reversal events
 * and allows ghosts to reverse (to mirror random ghost reversals that took place during the original
 * game play).
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class Replay
{
    private AttackerController pacMan;
    private DefenderController ghosts;

    private ArrayList<Integer> pacManActions;
    private ArrayList<int[]> ghostActions;

    public Replay(String fileName)
    {
        loadActions(fileName);
        this.pacMan=new ReplayMsPacman();
        this.ghosts=new ReplayDefenderTeam();
    }
 
	public void loadActions(String fileName)
    {
        ArrayList[] data=loadData(fileName);
        pacManActions=data[0];
        ghostActions=data[1];
    }

    public static void saveActions(String actions,String fileName,boolean append)
    {
        try
        {
            FileOutputStream outS=new FileOutputStream(fileName,append);
            PrintWriter pw=new PrintWriter(outS);

            pw.println(actions);

            pw.flush();
            outS.close();

        }
        catch (Exception e)
        {
            System.out.println("Could not save data!");
        }
    }
    
    public AttackerController getPacMan()
    {
        return pacMan;
    }

    public DefenderController getGhosts()
    {
        return ghosts;
    }

	public ArrayList[] loadData(String fileName)
    {
    	ArrayList[] data=new ArrayList[2];
        data[0]=new ArrayList<Integer>();
        data[1]=new ArrayList<int[]>();

        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"/"+fileName)));
            String input=br.readLine();

            while(input!=null && !input.equals(""))
            {
                input=input.trim();
                String[] numbers=input.split("\t");

                if(!numbers[0].equals("#"))                     //ignore comments
                {
                    data[0].add(Integer.parseInt(numbers[1]));  //action for Ms Pac-Man

                    int[] ghostActions=new int[4];              //actions for ghosts

                    for(int i=0;i<ghostActions.length;i++)
                	    ghostActions[i]=Integer.parseInt(numbers[i+2]);

                    data[1].add(ghostActions);
                }

                input=br.readLine();
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

        return data;
    }
        
	//Simple controller that simply plays the next recorded action
    class ReplayMsPacman implements AttackerController
    {
        public void init(Game game) { }
        public void shutdown(Game game) { }

        public int update(Game game, long timeDue)
        {
            return pacManActions.get(game.getTotalTime());
        }
    }

	//Simple controller that simply plays the next recorded action
    class ReplayDefenderTeam implements DefenderController
    {
        public void init(Game game) { }
        public void shutdown(Game game) { }
        public int[] update(Game game,long timeDue)
        {
            return ghostActions.get(game.getTotalTime());
        }
    }
}