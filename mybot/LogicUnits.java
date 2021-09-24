package mybot;

import lux.*;

import java.util.ArrayList;
import java.util.Arrays;

import static mybot.MyBot.*;

public class LogicUnits {

    MyGameMap myGameMap;
    GameState gameState;
    Player player;
    Player opponet;
    int[][][] mapLogic;
    int sizeMap;
    static int[][] cardPoints = new int[][]{{0,-1},{1,0},{0,1},{-1,0}};

    public LogicUnits(GameState gameState1, MyGameMap myGameMap1){
        gameState = gameState1;
        player = gameState.players[gameState.id];
        opponet = gameState.players[(gameState.id + 1) % 2];
        myGameMap = myGameMap1;
        sizeMap = gameState1.map.height;
        mapLogic = new int[sizeMap][sizeMap][3];
        // карта спецобозначения: 0 - тип юнита или количество леса которое можно взять за ход, 1 - количество угля за ход, 2 количество урана за ход
        // -1 свой юнит, который будет на клетке раньше
        // -2 вражеский юниты для расчета пути считается что он проходим, для хода неясно
        // -3 вражеские строения
        // -4 свой город
        // тип ресурса 1 - лес, 2 - уголь, 3- уран
    }

     // Сделать !!! до назначения юнитов на задачи, чтобы работал метод расчета расстояний до цели
    void updateMapLogic(){
        for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++) Arrays.fill(mapLogic[x][y],0);

        int epoha = 1;
        if(player.researchedCoal()&&!player.researchedUranium())epoha = 2;
        else if(player.researchedUranium())epoha = 3;

        for(Unit oppUnits:opponet.units) mapLogic[oppUnits.pos.x][oppUnits.pos.y][0] = -2;

        for(City city:opponet.cities.values())
            for (CityTile cityTile:city.citytiles)
                mapLogic[cityTile.pos.x][cityTile.pos.y][0] = -3;

        for(City city:player.cities.values())
            for (CityTile cityTile:city.citytiles)
                mapLogic[cityTile.pos.x][cityTile.pos.y][0] = -4;

        for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++){
                if(mapLogic[x][y][0]==-2)continue;
                /*if(!gameState.map.map[x][y].hasResource())continue;
                if(epoha==1)if(gameState.map.map[x][y].resource.type.equals("uranium")||gameState.map.map[x][y].resource.type.equals("coal"))continue;
                if(epoha==2)if(gameState.map.map[x][y].resource.type.equals("uranium"))continue;*/
                int wood = 0, coal = 0, uranium = 0; // количество ресурсов с клетки за ход
                for(int i=0; i<4; i++){ // проверка по сторонам света наличия ресурсов прилегающих к клетке
                        int sideX = x+cardPoints[i][0];
                        int sideY = y+cardPoints[i][1];
                        if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
                        if(!gameState.map.map[sideX][sideY].hasResource())continue;
                        if(gameState.map.map[sideX][sideY].resource.type.equals("uranium")&&epoha<3)continue;
                        if(gameState.map.map[sideX][sideY].resource.type.equals("coal")&&epoha<2)continue;
                        wood++;
                        coal+=gameState.map.map[sideX][sideY].resource.amount;
                }
                if(gameState.map.map[x][y].hasResource()){
                if(gameState.map.map[x][y].resource.type.equals("uranium")&&epoha==3) mapLogic[x][y][0] = 3;
                else if(gameState.map.map[x][y].resource.type.equals("coal")&&epoha>1) mapLogic[x][y][0] = 2;
                else if(gameState.map.map[x][y].resource.type.equals("wood"))mapLogic[x][y][0] = 1;
                }
                mapLogic[x][y][1] = wood;
                mapLogic[x][y][2] = coal;
            }

    }


    public ArrayList<String> getActionsCities(){
        ArrayList<String> actions = new ArrayList<>();
        int countUnits = player.units.size();
        for(City city:player.cities.values())
            for (CityTile cityTile:city.citytiles){ // перебор всех городов, если город может ходить, проверка, что количество юнитов меньше городов, тогда строит рабочего
                if(!cityTile.canAct())continue;
                if(countUnits<player.cityTileCount){ actions.add(cityTile.buildWorker()); countUnits++; continue;   }
                actions.add(cityTile.research());
            }

        return actions;
    }


    public ArrayList<String> getActionsWorkers(){
        ArrayList<String> actions = new ArrayList<>();

        for(int[] cdTask:myGameMap.listCoordTasksByPriory){
            if(myGameMap.myMap[cdTask[0]][cdTask[1]].idWorkerTask==null)continue;
            if(myGameMap.myMap[cdTask[0]][cdTask[1]].typeTask==1)actions.add(getActionBuildCity(myGameMap.myMap[cdTask[0]][cdTask[1]].idWorkerTask,cdTask));



        }

        return actions;
    }


    String getActionBuildCity(String idWorker,int[] cdTask){
        int[] cdWorker = {myGameMap.mapIdWokers.get(idWorker).pos.x,myGameMap.mapIdWokers.get(idWorker).pos.y};
        int startX = cdWorker[0], endX = cdTask[0]+1;
        if(cdTask[0]<startX){startX = cdTask[0];endX = cdWorker[0];}
        int startY = cdWorker[1], endY = cdTask[1]+1;
        if(cdTask[1]<startY){startY = cdTask[1];endY = cdWorker[1];}
        int[] bestRes = new int[2];
        for(int x=startX; x<endX; x++)
            for(int y=startY; y<endY; y++){

            }




        return "";
    }


   static int minStepsToTarget(int[] from, int[] to){
        int sizeMap = 32;
          /* for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++)if(mapLogic[x][y][0]==-3)mapUnitPuth[x][y] = -1; //для начала непроходимые клетки -1 только вражеские города*/
        int[][] mapUnitPuth = new int[sizeMap][sizeMap];

        for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++) {
                if(x==from[0]&&y==from[1])continue;
                if(x==to[0]&&y==to[1])continue;
                if((int)(Math.random()*10+1)<=2)mapUnitPuth[x][y] = -1;
            }

        long s = System.nanoTime();
        int steps = 1;
        /*mapUnitPuth[from[0]][from[1]] = steps;
        while (mapUnitPuth[to[0]][to[1]]==0){
               for(int x=0; x<sizeMap; x++)
                   for(int y=0; y<sizeMap; y++)
                       if(mapUnitPuth[x][y]==steps){
                           for(int i=0; i<4; i++){
                               int sideX = x+cardPoints[i][0];
                               int sideY = y+cardPoints[i][1];
                               if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
                               if(mapUnitPuth[sideX][sideY]==0)mapUnitPuth[sideX][sideY] = steps+1;
                           }
                       }

               steps++;
        }
       System.out.println("time 1 "+(System.nanoTime()-s));*/
       //showMatrix(mapUnitPuth);
       /*for(int x=0; x<sizeMap; x++)
           for(int y=0; y<sizeMap; y++) {
               if(mapUnitPuth[x][y]!=-1)mapUnitPuth[x][y] = 0;
           }*/

       s = System.nanoTime();
       steps = 1;
        int[][] coordSteps = new int[sizeMap*sizeMap][3];
        int[][] nextCoordSteps = new int[sizeMap*sizeMap][3];
        int[][] savedSteps = new int[coordSteps.length][3];


       mapUnitPuth[from[0]][from[1]] = steps;
       coordSteps[0][0] = from[0]; coordSteps[0][1] = from[1]; coordSteps[0][2] = steps;

      int indSavedSteps = -1;
     out: while (mapUnitPuth[to[0]][to[1]]==0){
           int indNextCoordSteps = -1;
           for(int x=0; x<coordSteps.length; x++){
               if(coordSteps[x][2]==steps){
                   //System.out.println(steps);
                       for(int i=0; i<4; i++){
                           int sideX = coordSteps[x][0]+cardPoints[i][0];
                           int sideY = coordSteps[x][1]+cardPoints[i][1];
                           if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
                           if(mapUnitPuth[sideX][sideY]==0){mapUnitPuth[sideX][sideY] = steps+1;
                               //System.out.println(mapUnitPuth[sideX][sideY]);
                           indNextCoordSteps++;
                           nextCoordSteps[indNextCoordSteps][0] = sideX;
                           nextCoordSteps[indNextCoordSteps][1] = sideY;
                           nextCoordSteps[indNextCoordSteps][2] = steps+1;
                           indSavedSteps++;
                           savedSteps[indSavedSteps][0] = sideX;
                           savedSteps[indSavedSteps][1] = sideY;
                           savedSteps[indSavedSteps][2] = steps+1;
                           }
                       }
                   } else { if(x==0){ steps=0; break out; } break;}
           }
           //System.out.println(mapUnitPuth[to[0]][to[1]]);
           for(int x=0; x<=indNextCoordSteps; x++) {
               //if(nextCoordSteps[x][2]!=steps+1)break;
               coordSteps[x][0] = nextCoordSteps[x][0];
               coordSteps[x][1] = nextCoordSteps[x][1];
               coordSteps[x][2] = nextCoordSteps[x][2];
               //coordSteps[x] = nextCoordSteps[x].clone();
           }

           steps++;
       }


       System.out.println("time 1 "+(System.nanoTime()-s)+"  steps "+steps);

       for(int x=0; x<sizeMap; x++)
           for(int y=0; y<sizeMap; y++) {
               if(mapUnitPuth[x][y]<1)continue;
               int distance = Math.abs(x-to[0])+Math.abs(y-to[1]);
               if(mapUnitPuth[to[0]][to[1]]-mapUnitPuth[x][y]<distance)mapUnitPuth[x][y] = 0;
           }

       mapUnitPuth[to[0]][to[1]]*=-1;

       showMatrix(mapUnitPuth);

       s = System.nanoTime();
       for(int i=indSavedSteps; i>0;  i--){if(mapUnitPuth[savedSteps[i][0]][savedSteps[i][1]]==0)continue;
            if(savedSteps[i][2]==0)continue;
            boolean isPreNum = false;
            if(savedSteps[i][2]!=steps){
                for(int a=0; a<4; a++){
                    int sideX = savedSteps[i][0]+cardPoints[a][0];
                    int sideY = savedSteps[i][1]+cardPoints[a][1];
                    if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
                    if(mapUnitPuth[sideX][sideY]<-1){
                       if(savedSteps[i][2]<Math.abs(mapUnitPuth[sideX][sideY])){isPreNum = true;break;}
                    }
                }
                if(!isPreNum){mapUnitPuth[savedSteps[i][0]][savedSteps[i][1]]=0;continue;}
            }

           for(int a=0; a<4; a++){
               int sideX = savedSteps[i][0]+cardPoints[a][0];
               int sideY = savedSteps[i][1]+cardPoints[a][1];
               if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
               if(mapUnitPuth[sideX][sideY]<2)continue;
               if(mapUnitPuth[sideX][sideY]<Math.abs(mapUnitPuth[savedSteps[i][0]][savedSteps[i][1]])){
                   mapUnitPuth[sideX][sideY] *=-1;
               }
           }

       }
       System.out.println("time 2 "+(System.nanoTime()-s)+"  steps "+steps);

       for(int x=0; x<sizeMap; x++)
           for(int y=0; y<sizeMap; y++) {
               if(mapUnitPuth[x][y]<-1)mapUnitPuth[x][y]=5;}

       mapUnitPuth[to[0]][to[1]]=-9;

        showMatrix(mapUnitPuth);
       System.out.println();

        return 0;
    }

    static void showMatrix(int[][] mapUnitPuth){
        for(int y=0; y<mapUnitPuth.length; y++){
        for(int x=0; x<mapUnitPuth.length; x++){
            //System.out.print("|");
            System.out.printf(RESET+"%s", "|");
            if(mapUnitPuth[x][y]==-1) System.out.printf(RED+"%1d\t", mapUnitPuth[x][y]);
            else if(mapUnitPuth[x][y]==0) System.out.printf(RESET+"%1d\t", mapUnitPuth[x][y]);
            else if(mapUnitPuth[x][y]==5) System.out.printf(GREEN+"%1d\t", mapUnitPuth[x][y]);
            else System.out.printf(BLUE+"%1d\t", mapUnitPuth[x][y]);
            //System.out.printf("%2d\t", mapUnitPuth[x][y]);
           // String.format()
            //System.out.print("|");

        }
            System.out.println(RESET);
            for(int x=0; x<mapUnitPuth.length; x++)
                System.out.print("|---");
            System.out.println();
        }

    }

    public static void main(String[] args) {
       minStepsToTarget(new int[]{0,0},new int[]{31,31});

        /*int[] t = new int[900];
        long s =System.nanoTime();
        for(int i=0; i<100; i++)if(t[i]==0)continue;
        System.out.println((System.nanoTime()-s));*/
    }
}
