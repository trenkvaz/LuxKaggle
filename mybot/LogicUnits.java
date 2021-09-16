package mybot;

import lux.City;
import lux.CityTile;
import lux.GameState;
import lux.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class LogicUnits {

    GameState gameState;
    Player player;

    public LogicUnits(GameState gameState1){
        gameState = gameState1;
        player = gameState.players[gameState.id];
    }



    public ArrayList<String> getActionsCities(){
        ArrayList<String> actions = new ArrayList<>();
        int countUnits = player.units.size();
        for(City city:player.cities.values())
            for (CityTile cityTile:city.citytiles){
                if(!cityTile.canAct())continue;
                if(countUnits<player.cityTileCount){ actions.add(cityTile.buildWorker()); countUnits++; continue;   }
                actions.add(cityTile.research());
            }

        return actions;
    }

    
    public ArrayList<String> getActionsWorkers(){
        ArrayList<String> actions = new ArrayList<>();
        return actions;
    }


}
