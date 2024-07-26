package rowlandsAdventure2.planets;

import java.util.ArrayList;

public class PlanetManager {

    public static ArrayList<Planet> planets = new ArrayList<Planet>();

    public static int addPlanet(Planet p){
        planets.add(p);
        return planets.size()-1;
    }

    public static Planet getFromIndex(int i){
        if (i >= 0 && i < planets.size())
            return planets.get(i);
        else
            return null;
    }

    public static void processPlanets(){
        for (Planet p : planets){
            p.checkGhostOverlaps();
        }
    }

}
