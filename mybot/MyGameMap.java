package mybot;

import lux.GameState;
import lux.Player;
import lux.Unit;

import java.util.*;

public class MyGameMap {

    MyCell[][] myMap;
    HashMap<String, Unit> mapIdWokers = new HashMap<>();
    //private HashMap<String,int[]> mapIdWorkersCoordCell = new HashMap<>();
    List<int[]> listCoordTasksByPriory = new ArrayList<>();
    HashMap<String,int[]> mapIdWorkerCoordTasks = new HashMap<>();
    GameState gameState;
    Player player;
    LogicUnits logicUnits;

    public MyGameMap(GameState gameState1){
        gameState = gameState1;
        myMap = new MyCell[gameState1.map.height][gameState1.map.height];
        player = gameState.players[gameState.id];
        for (int y = 0; y < gameState1.map.height; y++) {
            for (int x = 0; x < gameState1.map.height; x++) {
                this.myMap[y][x] = new MyCell();
            }
        }
        logicUnits = new LogicUnits(gameState1,this);
    }

    public void updateMyGameMap(){
        updateWorkers();
        updateWorkerTasks();
        appWorkersForTasks();
    }


    private void updateWorkers(){
        // удаление из яцеек всех рабочих по старым координатам
        for(Unit unit: mapIdWokers.values()) myMap[unit.pos.x][unit.pos.y].idWoker = null;

        mapIdWokers.clear();
        // обновление списка рабочих и ячеек с ними по новым координатам
        for(int i=0; i<player.units.size(); i++){
            if(player.units.get(i).isCart())continue;
            mapIdWokers.put(player.units.get(i).id,player.units.get(i));
            myMap[player.units.get(i).pos.x][player.units.get(i).pos.y].idWoker = player.units.get(i).id;
        }
    }


    private void updateWorkerTasks(){
       // проверка что рабочий с задачаей есть в новом списке рабочих, если его нет то привязка к задаче убирается в ячейке
        for(String idWoker:mapIdWorkerCoordTasks.keySet()){
            int[] cdTask = mapIdWorkerCoordTasks.get(idWoker);
            if(mapIdWokers.get(idWoker)==null){ myMap[cdTask[0]][cdTask[1]].idWorkerTask = null; }
         // проверка по рабочему с задачей на строительство, что в ячейка не занята городом
            // если занята, то рабочий с задачей удаляется из списка, а ячейка очищается от задачи и рабочего на ней
            if(myMap[cdTask[0]][cdTask[1]].typeTask==1&&gameState.map.getCell(cdTask[0],cdTask[1]).citytile!=null){
                mapIdWorkerCoordTasks.remove(myMap[cdTask[0]][cdTask[1]].idWorkerTask);
                myMap[cdTask[0]][cdTask[1]].typeTask = 0; myMap[cdTask[0]][cdTask[1]].idWorkerTask = null;

            }
        }
        // удаление из списка задач те координаты ячеек, которые очищены от задачи
        listCoordTasksByPriory.removeIf(cdTask -> myMap[cdTask[0]][cdTask[1]].typeTask == 0);


        // РАССМОТРЕТЬ ВОПРОС если юнит останется без задания на пол пути то всегда должно появляться новое задание рядом со старым чтобы на него переназначить !!!!
    }

    public void addNewTasks(List<int[]> listNewTask){
        for(int[] _newTask:listNewTask){
           /* counterTasks++;
            mapTasks[_newTask[0]][_newTask[1]][0] = 2; // задача выполняется
            mapTasks[_newTask[0]][_newTask[1]][1] = _newTask[2]; // передается приоритет задачи
            mapTasks[_newTask[0]][_newTask[1]][2] = _newTask[3]; // передается тип задачи
            mapTasks[_newTask[0]][_newTask[1]][3] = counterTasks; // счетчик задающий номер задачи
            Task task = new Task(counterTasks,_newTask[3],_newTask[0],_newTask[1]);*/
            myMap[_newTask[0]][_newTask[1]].typeTask = _newTask[3];
            int[] cdNewTask = new int[]{_newTask[0],_newTask[1]};
            // добавление номеров задач по приоритету
            if(_newTask[2]==-1)listCoordTasksByPriory.add(cdNewTask);
            else if(listCoordTasksByPriory.size()>=_newTask[2])listCoordTasksByPriory.add(_newTask[2],cdNewTask);
            else listCoordTasksByPriory.add(cdNewTask);
        }
    }


    private void appWorkersForTasks(){

            for(int[] cdNewTask:listCoordTasksByPriory){
                // проверка что задача строительство и рабочего на задаче нет
                if(myMap[cdNewTask[0]][cdNewTask[1]].typeTask==1&&myMap[cdNewTask[0]][cdNewTask[1]].idWorkerTask==null){
                String minStepWorker = null;
                for(Map.Entry<String,Unit> entry:mapIdWokers.entrySet()){
                    if(mapIdWorkerCoordTasks.containsKey(entry.getKey()))continue; // проверка что рабочие из главноего списка юнитов свободны от задач
                    if(minStepWorker==null){minStepWorker = entry.getKey(); continue;} // назначение первого ближайшего юнита, чтобы потом другие сравнивались с ним
                    // отборо рабочего с минимальным количество шагов до задачи сравниваются дистанция, охлаждание , количество шагов для набора 100 ресурсов, шаг кратен 20 ресурсам
                    // КРАТНОСТЬ 20 работает только с деревом !!!!!!!!!!!!!!!!!!!!!!
                    // РАССМОТРЕТЬ ЛИМИТ НА МАКСИМАЛЬНОЕ КОЛИЧЕСТВО ШАГОВ, чтобы с другого края карты юниты не прибегали !!!!!!!!!!!!!!!!!!!!!!!!!!!
                    Unit cur = mapIdWokers.get(minStepWorker);
                    Unit next = mapIdWokers.get(entry.getKey());
                    int stepsCur = Math.abs(cur.pos.x-cdNewTask[0])+Math.abs(cur.pos.y-cdNewTask[1])+(int)cur.cooldown+(cur.getCargoSpaceLeft()/20);
                    int stepsNext = Math.abs(next.pos.x-cdNewTask[0])+Math.abs(next.pos.y-cdNewTask[1])+(int)next.cooldown+(next.getCargoSpaceLeft()/20);
                    if(stepsNext<stepsCur)minStepWorker = entry.getKey();
                }
                // если найден свободный рабочий ближе всего к задаче, то он назначается на задачу
                if(minStepWorker!=null){
                    mapIdWorkerCoordTasks.put(minStepWorker,cdNewTask);
                    myMap[cdNewTask[0]][cdNewTask[1]].idWorkerTask = minStepWorker;
                }
              }
            }
    }




    class MyCell {

        int numberTask;
        int typeTask;
        String idWorkerTask;
        String idWoker;


    }
}
