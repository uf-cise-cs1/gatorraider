package edu.ufl.cise.gatorraider.models;
import java.util.List;

public interface Defender extends Actor
{
    int getVulnerableTime();
    int getLairTime();

    boolean isVulnerable();
//    boolean requiresAction();

    List<Integer> getPossibleDirs();
    List<Node> getPossibleLocations();
}
