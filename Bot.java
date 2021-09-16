import java.util.ArrayList;
import java.util.HashMap;

import lux.*;
import mybot.*;

public class Bot {

  public MyBot myBot;



  void playOpp(GameState gameState, Agent agent ){
    Player player = gameState.players[gameState.id];
    Player opponent = gameState.players[(gameState.id + 1) % 2];
    GameMap gameMap = gameState.map;



    ArrayList<String> actions = new ArrayList<>();
    ArrayList<Cell> resourceTiles = new ArrayList<>();
    for (int y = 0; y < gameMap.height; y++) {
      for (int x = 0; x < gameMap.width; x++) {
        Cell cell = gameMap.getCell(x, y);
        if (cell.hasResource()) {
          resourceTiles.add(cell);
        }
      }
    }

    // we iterate over all our units and do something with them
    for (int i = 0; i < player.units.size(); i++) {
      Unit unit = player.units.get(i);
      if (unit.isWorker() && unit.canAct()) {
        if (unit.getCargoSpaceLeft() > 0) {
          // if the unit is a worker and we have space in cargo, lets find the nearest resource tile and try to mine it
          Cell closestResourceTile = null;
          double closestDist = 9999999;
          for (Cell cell : resourceTiles) {

            if (cell.resource.type.equals(GameConstants.RESOURCE_TYPES.COAL) && !player.researchedCoal()) continue;
            if (cell.resource.type.equals(GameConstants.RESOURCE_TYPES.URANIUM) && !player.researchedUranium()) continue;
            double dist = cell.pos.distanceTo(unit.pos);
            if (dist < closestDist) {
              closestDist = dist;
              closestResourceTile = cell;
            }
          }

          if (closestResourceTile != null) {
            Direction dir = unit.pos.directionTo(closestResourceTile.pos);
            // move the unit in the direction towards the closest resource tile's position.
            actions.add(unit.move(dir));
          }
        } else {
          // if unit is a worker and there is no cargo space left, and we have cities, lets return to them
          if (player.cities.size() > 0) {
            City city = player.cities.values().iterator().next();
            double closestDist = 999999;
            CityTile closestCityTile = null;
            for (CityTile citytile : city.citytiles) {
              double dist = citytile.pos.distanceTo(unit.pos);
              if (dist < closestDist) {
                closestCityTile = citytile;
                closestDist = dist;
              }
            }
            if (closestCityTile != null) {
              Direction dir = unit.pos.directionTo(closestCityTile.pos);
              actions.add(unit.move(dir));
              //actions.add(Annotate.circle(unit.pos.x, unit.pos.y));
            }
          }
        }
      }
    }

    // you can add debug annotations using the static methods of the Annotate class.
    // actions.add(Annotate.circle(0, 0));

    /** AI Code Goes Above! **/

    /** Do not edit! **/
    StringBuilder commandBuilder = new StringBuilder("");
    for (int i = 0; i < actions.size(); i++) {
      if (i != 0) {
        commandBuilder.append(",");
      }
      commandBuilder.append(actions.get(i));
    }
    System.out.println(commandBuilder.toString());
    // end turn
    agent.endTurn();

  }


   void game(Agent agent){
     GameState gameState = agent.gameState;
     if(gameState.id==1) myBot = new MyBot(gameState);
     while (true) {
      /** Do not edit! **/
      // wait for updates
      agent.update();


      /** AI Code Goes Below! **/

//      Player player = gameState.players[gameState.id];
//      Player opponent = gameState.players[(gameState.id + 1) % 2];
//      GameMap gameMap = gameState.map;
      if(gameState.id==0)playOpp(gameState,agent);
      if(gameState.id==1)playMy(agent);
//      System.out.println("h "+gameMap.height+"  w "+gameMap.width);

    }
  }


  void playMy(Agent agent){

    ArrayList<String> actions = myBot.getActionList();

    StringBuilder commandBuilder = new StringBuilder("");
    for (int i = 0; i < actions.size(); i++) {
      if (i != 0) {
        commandBuilder.append(",");
      }
      commandBuilder.append(actions.get(i));
    }
    System.out.println(commandBuilder.toString());
    // end turn
    agent.endTurn();
  }





  public static void main(final String[] args) throws Exception {
    Agent agent = new Agent();
    Bot bot = new Bot();
    // initialize
    agent.initialize();

    bot.game(agent);


  }
}
