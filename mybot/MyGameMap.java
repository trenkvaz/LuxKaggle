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
    HashMap<String,int[]> mapIdWorkerCDnewBuild = new HashMap<>();
    GameState gameState;
    Player player;
    Player opponent;
    LogicUnits logicUnits;

    int[][][] arrMyMap;
    // рабочий 0-х, 1-у, 2- коллдаун, 3-лес, 4 уголь,  5 уран
    int[][] arrWorkers;


    public MyGameMap(){}

    public MyGameMap(GameState gameState1){
        gameState = gameState1;
        myMap = new MyCell[gameState1.map.height][gameState1.map.height];
        player = gameState.players[gameState.id];
        opponent = gameState.players[(gameState.id + 1) % 2];
        for (int y = 0; y < gameState1.map.height; y++) {
            for (int x = 0; x < gameState1.map.height; x++) {
                this.myMap[y][x] = new MyCell();
            }
        }
        // 0-тип задачи, 1 -рабочий на задаче, 2 рабочий
        arrMyMap = new int[gameState1.map.height][gameState1.map.height][3];
        logicUnits = new LogicUnits(gameState1,this);
    }

    public void updateMyGameMap(){
        updateWorkers();
        updateWorkerTasks();
        appWorkersForTasks();
    }


    private void updateWorkers1(){
        // удаление из яцеек всех рабочих по старым координатам
        //for(Unit unit: mapIdWokers.values()) myMap[unit.pos.x][unit.pos.y].idWoker = null;

        if(arrWorkers!=null){
            for(int i=0; i<arrWorkers.length; i++){if(arrWorkers[i]==null)continue;
            arrMyMap[arrWorkers[i][0]][arrWorkers[i][1]][2]=0;
            }
        }

        //mapIdWokers.clear();
        arrWorkers = new int[player.units.size()+opponent.units.size()][6];
        // обновление списка рабочих и ячеек с ними по новым координатам
        for(int i=0; i<player.units.size(); i++){
            if(player.units.get(i).isCart())continue;
            int idWorker = Integer.parseInt(player.units.get(i).id.substring(2));
            arrWorkers[idWorker][0] = player.units.get(i).pos.x;
            arrWorkers[idWorker][1] = player.units.get(i).pos.y;
            arrWorkers[idWorker][2] = (int) (player.units.get(i).cooldown/0.25);

            mapIdWokers.put(player.units.get(i).id,player.units.get(i));
            myMap[player.units.get(i).pos.x][player.units.get(i).pos.y].idWoker = player.units.get(i).id;
        }
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
        for(String idWoker: mapIdWorkerCDnewBuild.keySet()){
            int[] cdTask = mapIdWorkerCDnewBuild.get(idWoker);
            if(mapIdWokers.get(idWoker)==null){ myMap[cdTask[0]][cdTask[1]].idWorkerTask = null; }
         // проверка по рабочему с задачей на строительство, что в ячейка не занята городом
            // если занята, то рабочий с задачей удаляется из списка, а ячейка очищается от задачи и рабочего на ней
            if(myMap[cdTask[0]][cdTask[1]].typeTask==1&&gameState.map.getCell(cdTask[0],cdTask[1]).citytile!=null){
                mapIdWorkerCDnewBuild.remove(myMap[cdTask[0]][cdTask[1]].idWorkerTask);
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
        //ArrayList<String> arrFreeWorkers = new ArrayList<>(30);
        // первоначальный список свободных от строительства рабочих

        int countFreeWorkers = mapIdWokers.size() - mapIdWorkerCDnewBuild.size();
        String[] arrFreeWorkers = new String[countFreeWorkers];
        int ind=-1;
        for(String idWorker: mapIdWokers.keySet()){
            if(mapIdWorkerCDnewBuild.containsKey(idWorker))continue;
            ind++;
            arrFreeWorkers[ind] = idWorker;
        }

        ArrayList<String> listSortedWorkersByDist = new ArrayList<>(30);

        int[] freeIdDistance = new int[arrFreeWorkers.length];

            for(int[] cdNewTask:listCoordTasksByPriory){
                // проверка что задача строительство и рабочего на задаче нет
                if(myMap[cdNewTask[0]][cdNewTask[1]].typeTask==1&&myMap[cdNewTask[0]][cdNewTask[1]].idWorkerTask==null){
               // создание массива с дистанциями свободных рабочих до цели
                for(int i=0; i<arrFreeWorkers.length; i++){
                    Unit cur = mapIdWokers.get(arrFreeWorkers[i]);
                    freeIdDistance[i] = Math.abs(cur.pos.x-cdNewTask[0])+Math.abs(cur.pos.y-cdNewTask[1]);
                }
               // параллельная сортировка списка свободных и их дистанций
                quickSort(arrFreeWorkers,freeIdDistance,0,arrFreeWorkers.length-1);



                String minStepWorker = null;



                for(Map.Entry<String,Unit> entry:mapIdWokers.entrySet()){
                    if(mapIdWorkerCDnewBuild.containsKey(entry.getKey()))continue; // проверка что рабочие из главноего списка юнитов свободны от задач
                    if(minStepWorker==null){minStepWorker = entry.getKey(); continue;} // назначение первого ближайшего юнита, чтобы потом другие сравнивались с ним
                    // отборо рабочего с минимальным количество шагов до задачи сравниваются дистанция, охлаждание , количество шагов для набора 100 ресурсов, шаг кратен 20 ресурсам
                    // КРАТНОСТЬ 20 работает только с деревом !!!!!!!!!!!!!!!!!!!!!!
                    // РАССМОТРЕТЬ ЛИМИТ НА МАКСИМАЛЬНОЕ КОЛИЧЕСТВО ШАГОВ, чтобы с другого края карты юниты не прибегали !!!!!!!!!!!!!!!!!!!!!!!!!!!
                    Unit cur = mapIdWokers.get(minStepWorker);
                    Unit next = mapIdWokers.get(entry.getKey());
                    int stepsCur = Math.abs(cur.pos.x-cdNewTask[0])+Math.abs(cur.pos.y-cdNewTask[1]);
                    int stepsNext = Math.abs(next.pos.x-cdNewTask[0])+Math.abs(next.pos.y-cdNewTask[1]);
                    if(stepsNext<stepsCur)minStepWorker = entry.getKey();
                }
                // если найден свободный рабочий ближе всего к задаче, то он назначается на задачу
                if(minStepWorker!=null){
                    mapIdWorkerCDnewBuild.put(minStepWorker,cdNewTask);
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


    public static void quickSort(String[] arrFreeWorkers,int[] array, int low, int high) {
        if (array.length == 0)
            return;//завершить выполнение, если длина массива равна 0
        if (low >= high)
            return;//завершить выполнение если уже нечего делить
        // выбрать опорный элемент
        int middle = low + (high - low) / 2;
        int opora = array[middle];
        //System.out.println("opora "+opora);
        // разделить на подмассивы, который больше и меньше опорного элемента
        String tmpId = null;
        int i = low, j = high;
        while (i <= j) {
            while (array[i] < opora) { i++; }
            while (array[j] > opora) { j--; }
            // System.out.println("i "+i+" j "+j);
            if (i <= j) {//меняем местами
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                tmpId = arrFreeWorkers[i];
                arrFreeWorkers[i] = arrFreeWorkers[j];
                arrFreeWorkers[j] = tmpId;
                i++;
                j--;
            }
        }
        // вызов рекурсии для сортировки левой и правой части
        if (low < j) quickSort(arrFreeWorkers,array, low, j);
        if (high > i) quickSort(arrFreeWorkers,array, i, high);
    }


    public static void main(String[] args) {


    }

}
