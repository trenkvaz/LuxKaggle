package mybot;

import lux.*;

import java.io.*;
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


   static int minStepsToTarget(int[] from, int[] to,int[][] mapUnitPuth){
        int sizeMap = 32;
          /* for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++)if(mapLogic[x][y][0]==-3)mapUnitPuth[x][y] = -1; //для начала непроходимые клетки -1 только вражеские города*/
        if(mapUnitPuth==null){
        mapUnitPuth = new int[sizeMap][sizeMap];

        for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++) {
                if(x==from[0]&&y==from[1])continue;
                if(x==to[0]&&y==to[1])continue;
                if((int)(Math.random()*10+1)<=2)mapUnitPuth[x][y] = -1;
            }
        }
        //saveMap(mapUnitPuth);

        long s = System.nanoTime();
        int steps = 1;

       s = System.nanoTime();
       steps = 1;
        int lenghMap = sizeMap*sizeMap;
        int[][] coordSteps = new int[3][lenghMap];
        int[][] nextCoordSteps = new int[lenghMap][3];
        int[][] savedSteps = new int[lenghMap][3];


       mapUnitPuth[from[0]][from[1]] = steps;
       coordSteps[0][0] = from[0]; coordSteps[1][0] = from[1]; coordSteps[2][0] = steps;

      int indSavedSteps = -1;
     out: while (mapUnitPuth[to[0]][to[1]]==0){
           int indNextCoordSteps = -1;
           for(int x=0; x<lenghMap; x++){
               if(coordSteps[2][x]==steps){
                   //System.out.println(steps);
                       for(int i=0; i<4; i++){
                           int sideX = coordSteps[0][x]+cardPoints[i][0];
                           int sideY = coordSteps[1][x]+cardPoints[i][1];
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
               coordSteps[0][x] = nextCoordSteps[x][0];
               coordSteps[1][x] = nextCoordSteps[x][1];
               coordSteps[2][x] = nextCoordSteps[x][2];
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




    static int minStepsToTarget2(int from, int to,int[] mapUnitPuth){
        int sizeMap = 32;
        int lenghMap = sizeMap*sizeMap;
          /* for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++)if(mapLogic[x][y][0]==-3)mapUnitPuth[x][y] = -1; //для начала непроходимые клетки -1 только вражеские города*/
        if(mapUnitPuth==null){
            mapUnitPuth = new int[lenghMap];

            for(int x=0; x<lenghMap; x++) {
                    if(x==from||x==to)continue;
                    if((int)(Math.random()*10+1)<=2)mapUnitPuth[x] = -1;
                }
        }
        //saveMap(mapUnitPuth);

        long s = System.nanoTime();
        int steps = 1;

        s = System.nanoTime();
        steps = 1;

        int[][] coordSteps = new int[2][lenghMap];
        int[][] nextCoordSteps = new int[2][lenghMap];
        int[][] savedSteps = new int[2][lenghMap];
        //int xFrom = from[0], yFrom = from[1], xTo = to[0], yTo = to[1];
        int[] turns = {-1,32,1,-32};
        int overMap = lenghMap-1;
        mapUnitPuth[from] = steps;
        coordSteps[0][0] = from; coordSteps[1][0] = steps;
        int[] coordStep0 = coordSteps[0];  int[] coordStep1 = coordSteps[1];   //int[] coordStep2 = coordSteps[2];
        int[] nextCoordSteps0 = nextCoordSteps[0]; int[] nextCoordSteps1 = nextCoordSteps[1];
        int[] savedSteps0 = savedSteps[0]; int[] savedSteps1 = savedSteps[1];
        int indSavedSteps = -1;
        boolean run = true; //int sideX = 0;
        out: while (run){
            int indNextCoordSteps = -1;
            for(int x=0; x<lenghMap; x++){
                int coord = coordStep0[x];
                if(coordStep1[x]==steps){
                   // System.out.println("step "+steps);
                    for(int i=0; i<4; i++){
                        //System.out.println("coord "+(coordStep0[x]+turns[i]));
                        if(i==0&&coord%32==0)continue;
                        //if(i==1&&coord>990)continue;
                        if(i==2&&coord+1%32==0){
                            //System.out.println("% "+(coordStep0[x]+1%32)+"  "+coordStep0[x]+1);
                            continue;}
                        //if(i==3&&coord<32)continue;
                       int sideX = coord+turns[i];
                        //System.out.println(sideX);
                        //int sideY = coordSteps[1][x]+cardPoints[i][1];
                        if(sideX<0||sideX>overMap)continue;
                        if(mapUnitPuth[sideX]==0){ int newstep = steps+1;
                            mapUnitPuth[sideX] = newstep;
                            if(sideX==to) run = false;
                            //System.out.println(mapUnitPuth[sideX][sideY]);
                            indNextCoordSteps++;
                            nextCoordSteps0[indNextCoordSteps] = sideX;
                            //nextCoordSteps[1][indNextCoordSteps] = sideY;
                            nextCoordSteps1[indNextCoordSteps] = newstep;
                            indSavedSteps++;
                            savedSteps0[indSavedSteps] = sideX;
                            //savedSteps[1][indSavedSteps] = sideY;
                            savedSteps1[indSavedSteps] = newstep;
                        }
                    }
                } else { if(x==0){ steps=0; break out; } break;}
            }
            //System.out.println(mapUnitPuth[to[0]][to[1]]);
            for(int x=0; x<=indNextCoordSteps; x++) {
                //if(nextCoordSteps[x][2]!=steps+1)break;
                coordStep0[x] = nextCoordSteps0[x];
                coordStep1[x] = nextCoordSteps1[x];
                //coordStep2[x] = nextCoordSteps[2][x];
                //coordSteps[x] = nextCoordSteps[x].clone();
            }

            steps++;
        }


        System.out.println("time 1 "+(System.nanoTime()-s)+"  steps "+steps);

        /*for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++) {
                if(mapUnitPuth[x]<1)continue;
                int distance = Math.abs(x-to[0])+Math.abs(y-to[1]);
                if(mapUnitPuth[to[0]][to[1]]-mapUnitPuth[x][y]<distance)mapUnitPuth[x][y] = 0;
            }

        mapUnitPuth[to[0]][to[1]]*=-1;*/
        mapUnitPuth[to]=-9;
        showMatrix2(mapUnitPuth);

       /* s = System.nanoTime();
        for(int i=indSavedSteps; i>0;  i--){if(mapUnitPuth[savedSteps[0][i]][savedSteps[1][i]]==0)continue;
            if(savedSteps[2][i]==0)continue;
            boolean isPreNum = false;
            if(savedSteps[2][i]!=steps){
                for(int a=0; a<4; a++){
                    int sideX = savedSteps[0][i]+cardPoints[a][0];
                    int sideY = savedSteps[1][i]+cardPoints[a][1];
                    if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
                    if(mapUnitPuth[sideX][sideY]<-1){
                        if(savedSteps[2][i]<Math.abs(mapUnitPuth[sideX][sideY])){isPreNum = true;break;}
                    }
                }
                if(!isPreNum){mapUnitPuth[savedSteps[0][i]][savedSteps[1][i]]=0;continue;}
            }

            for(int a=0; a<4; a++){
                int sideX = savedSteps[0][i]+cardPoints[a][0];
                int sideY = savedSteps[1][i]+cardPoints[a][1];
                if(sideX<0||sideX>sizeMap-1||sideY<0||sideY>sizeMap-1)continue;
                if(mapUnitPuth[sideX][sideY]<2)continue;
                if(mapUnitPuth[sideX][sideY]<Math.abs(mapUnitPuth[savedSteps[0][i]][savedSteps[1][i]])){
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
        System.out.println();*/

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

    static void showMatrix2(int[] mapUnitPuth){

            for(int x=0; x<mapUnitPuth.length; x++){
                //System.out.print("|");
                if(x!=0&&x%32==0){
                    System.out.println(RESET);
                    for(int y=0; y<32; y++)
                        System.out.print("|---");
                    System.out.println();
                }

                System.out.printf(RESET+"%s", "|");
                //System.out.printf(RED+"%1d\t", x);
                if(mapUnitPuth[x]==-1) System.out.printf(RED+"%1d\t", mapUnitPuth[x]);
                else if(mapUnitPuth[x]==0) System.out.printf(RESET+"%1d\t", mapUnitPuth[x]);
                else if(mapUnitPuth[x]==5) System.out.printf(GREEN+"%1d\t", mapUnitPuth[x]);
                else System.out.printf(BLUE+"%1d\t", mapUnitPuth[x]);
                //System.out.printf("%2d\t", mapUnitPuth[x][y]);
                // String.format()
                //System.out.print("|");

            }


    }



    static void saveMap(int[] mapUnitPuth){

        try {	FileOutputStream file=new FileOutputStream(System.getProperty("user.dir")+"\\mybot\\map.file");
            ObjectOutput out = new ObjectOutputStream(file);
            out.writeObject(mapUnitPuth);
            out.close();
            file.close();
        } catch(IOException e) {
            System.out.println(e);
        }

    }

    public static <T> T read_ObjectFromFile(){
        T type = null;
        try {	FileInputStream file=new FileInputStream(System.getProperty("user.dir")+"\\mybot\\map.file");
            ObjectInput out = new ObjectInputStream(file);
            type = (T) out.readObject();
            out.close();
            file.close();
        } catch(IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return type;
    }

    public static void main(String[] args) {
      // minStepsToTarget(new int[]{1,1},new int[]{25,25},read_ObjectFromFile());
       minStepsToTarget2(0,625,read_ObjectFromFile());
        /*int[] t = new int[900];
        long s =System.nanoTime();
        for(int i=0; i<100; i++)if(t[i]==0)continue;
        System.out.println((System.nanoTime()-s));*/
    }
}
