package mybot;

import lux.*;

import java.util.*;

public class MyBot {

    MyGameMap myGameMap;

    private List<int[]> listNewTask = new ArrayList<>();
    private int counterTasks = 0;
    private int sizeMap = 0;
    GameState gameState;
    Player player;
    Player opponent;

    public MyBot(GameState gameState1){
        gameState = gameState1;
        player = gameState.players[gameState.id];
        opponent = gameState.players[(gameState.id + 1) % 2];
        //индексация последнего значения 0 - статус , 1 - приоритет, 2 тип задачи, 3 - номер задачи
        // Статус: 1 новая, 2 выполняется, 3 выполнено, 4 не может быть выволнено
        // Приоритет: 0 самый главный, 1.. по уменьшению, -1 без приоритета
        // Тип задачи: 1 строительство, 2 движение
        // Номер задачи: инкрментируется счетчиком
        sizeMap = gameState1.map.width;
        myGameMap = new MyGameMap(gameState1);
    }


    void setListNewTask(){
        // координаты задачи, приоритет, тип задачи
        listNewTask.add(new int[]{10,4,0,1});
    }

    public ArrayList<String> getActionList(){
        ArrayList<String> actions = new ArrayList<>();
        myGameMap.updateMyGameMap();
        myGameMap.addNewTasks(listNewTask);

        Unit unit = player.units.get(0);
        actions.add(unit.move(Direction.CENTER));

        return actions;
    }



}
