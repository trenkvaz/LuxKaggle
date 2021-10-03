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
        int[] map2 = new int[sizeMap*sizeMap];

        for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++) {
                if(x==from[0]&&y==from[1])continue;
                if(x==to[0]&&y==to[1])continue;
                int X = x+(y*32);
                if((int)(Math.random()*10+1)<=2){mapUnitPuth[x][y] = -1; map2[X] = -1;      }
            }
            saveMap(mapUnitPuth,"map1");
            saveMap(map2,"map2");
        }


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
        int lengthMap = sizeMap*sizeMap;

          /* for(int x=0; x<sizeMap; x++)
            for(int y=0; y<sizeMap; y++)if(mapLogic[x][y][0]==-3)mapUnitPuth[x][y] = -1; //для начала непроходимые клетки -1 только вражеские города*/

        if(mapUnitPuth==null){
            mapUnitPuth = new int[lengthMap];

            for(int x=0; x<lengthMap; x++) {
                    if(x==from||x==to)continue;
                    if((int)(Math.random()*10+1)<=2)mapUnitPuth[x] = -1;
                }
           // saveMap(mapUnitPuth,"map1_"+from+" "+to);
        }

        long s = System.nanoTime();

        int overMap = lengthMap-1;
        s = System.nanoTime();

        int temp =0;
        int left = from%32, right = to%32; if(right<left){ temp = left;  left= right; right = temp; }
        int up = from/32, down = to/32; if(down<up){ temp = up;  up= down; down = temp; }
        if(left!=0) left-=1;
        if(right!=31)right+=1;
        if(up!=0) up-=1;
        if(down!=overMap/32)down+=1;
        System.out.println("init 11 "+(System.nanoTime()-s));
        System.out.println("up "+up+" left "+left+" down "+down+" right "+right);

        s = System.nanoTime();
        int steps = 1;


        int[] turns = {-1,32,1,-32};

        mapUnitPuth[from] = steps;

        int[] coordStep0 = new int[lengthMap];  int[] coordStep1 = new int[lengthMap];
        int[] nextCoordSteps0 = new int[lengthMap]; int[] nextCoordSteps1 = new int[lengthMap];
        int[] savedSteps0 = new int[lengthMap]; int[] savedSteps1 = new int[lengthMap];
        coordStep0[0] = from; coordStep1[0] = steps;
        int indSavedSteps = -1;
        boolean run = true;
        System.out.println("init 2 "+(System.nanoTime()-s));
        s = System.nanoTime();
        out: while (run){
            int indNextCoordSteps = -1;
            for(int x=0; x<lengthMap; x++){

                if(coordStep1[x]==steps){
                   // System.out.println("step "+steps);
                    int coord = coordStep0[x];
                    int vertical = coord%32;
                    int horizont = coord/32;
                    for(int i=0; i<4; i++){

                        if(i==0&&vertical==left)continue;
                        if(i==2&&vertical==right) continue;

                        if(i==3&&horizont==up)continue;
                        if(i==1&&horizont==down)continue;

                        int sideX = coord+turns[i];

                        if(mapUnitPuth[sideX]==0){ int newstep = steps+1;
                            mapUnitPuth[sideX] = newstep;
                            if(sideX==to) run = false;

                            indNextCoordSteps++;
                            nextCoordSteps0[indNextCoordSteps] = sideX;
                            nextCoordSteps1[indNextCoordSteps] = newstep;
                            indSavedSteps++;
                            savedSteps0[indSavedSteps] = sideX;
                            savedSteps1[indSavedSteps] = newstep;
                        }
                    }
                } else { if(x==0){ steps=0; break out; } break;}
            }

            for(int x=0; x<=indNextCoordSteps; x++) {
                coordStep0[x] = nextCoordSteps0[x];
                coordStep1[x] = nextCoordSteps1[x];
            }
            steps++;
        }


        System.out.println("time 1 "+(System.nanoTime()-s)+"  steps "+steps);

        showMatrix2(mapUnitPuth);
        System.out.println(RESET);
        System.out.println();
        //if(test)return 0;

        for(int x=0; x<lengthMap; x++) {
                if(mapUnitPuth[x]<1)continue;
                int distance = Math.abs(x/32-to/32)+Math.abs(x%32-to%32);
                if(mapUnitPuth[to]-mapUnitPuth[x]<distance)mapUnitPuth[x] = 0;
            }

        mapUnitPuth[to]*=-1;
       // showMatrix2(mapUnitPuth);
        System.out.println();

        s = System.nanoTime();
        for(int i=indSavedSteps; i>0;  i--){
            int step = savedSteps1[i];
            if(mapUnitPuth[savedSteps0[i]]==0)continue;
            if(step==0)continue;
            int coord = savedSteps0[i];
            int vertical = coord%32;
            int horizont = coord/32;
            if(step!=steps){  // здесь обработка только ячеек не равных последнему шагу, так как последняя всегда имеет рядом шаг меньше ее
                boolean isPreNum = false;
                //System.out.println("s "+step+" "+steps);
                for(int a=0; a<4; a++){

                    if(a==0&&vertical==left)continue;
                    if(a==2&&vertical==right)continue;

                    if(a==3&&horizont==up)continue;
                    if(a==1&&horizont==down)continue;

                    int sideX = coord+turns[a];

                    // Обнуление ячеек которые не имеют предидущего шага
                    // поиск шагов вокруг шага, которые больше текущего шага, если такие не находятся, то текущая ячейка отмечается как ноль
                    // если текущая ячейка ноль то она вообще не обрабатывается дальше
                    // если найдена соседняя ячейка с шагом больше текущей, цикл останавливается и дальше идет нижний цикл с отмечанием соседних клеток

                    int num = mapUnitPuth[sideX];
                    if(num<-1){
                        if(step<Math.abs(num)){isPreNum = true;break;}
                    }
                }
                if(!isPreNum){mapUnitPuth[coord]=0;continue;}
            }

            // Если ячейка имеет предидущий шаг то ищутся вокруг шаги меньше, чем ее, исключая преграды, самый первый шаг и уже отмеченные ячейки
            // цикл отмечание соседних клеток среди которых есть шаг меньше текущего шага отмечание идет присвоением минуса

            for(int a=0; a<4; a++){

                if(a==0&&vertical==left)continue;
                if(a==2&&vertical==right)continue;
                if(a==3&&horizont==up)continue;
                if(a==1&&horizont==down)continue;

                int sideX = coord+turns[a];
                int num = mapUnitPuth[sideX];
                //System.out.println("num "+num);
                if(num<2)continue;
                if(num<Math.abs(mapUnitPuth[coord])){
                    mapUnitPuth[sideX] *=-1;
                }
            }

        }
        System.out.println("time 2 "+(System.nanoTime()-s)+"  steps "+steps);

        for(int x=0; x<lengthMap; x++) if(mapUnitPuth[x]<-1)mapUnitPuth[x]=5;

        mapUnitPuth[to]=-9;

        showMatrix2(mapUnitPuth);
        System.out.println(RESET);

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



    static void saveMap(int[] mapUnitPuth,String nameMap){

        try {	FileOutputStream file=new FileOutputStream(System.getProperty("user.dir")+"\\test_res\\"+nameMap+".file");
            ObjectOutput out = new ObjectOutputStream(file);
            out.writeObject(mapUnitPuth);
            out.close();
            file.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    static void saveMap(int[][] mapUnitPuth,String nameMap){

        try {	FileOutputStream file=new FileOutputStream(System.getProperty("user.dir")+"\\test_res\\"+nameMap+".file");
            ObjectOutput out = new ObjectOutputStream(file);
            out.writeObject(mapUnitPuth);
            out.close();
            file.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    public static <T> T read_ObjectFromFile(String nameMap){
        T type = null;
        try {	FileInputStream file=new FileInputStream(System.getProperty("user.dir")+"\\test_res\\"+nameMap+".file");
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
       //minStepsToTarget(new int[]{1,1},new int[]{25,25},read_ObjectFromFile("map1"));
        for(int i=0; i<100; i++)
        minStepsToTarget2((int) (Math.random()*1024),(int) (Math.random()*1024),null);   //read_ObjectFromFile("map1_0 990")
        /*int[] savepMap = read_ObjectFromFile("map1_0 990");
        for(int i=0; i<10; i++)
        minStepsToTarget2(0,990,savepMap.clone());*/

        /*int[] t = new int[900];
        long s =System.nanoTime();
        for(int i=0; i<100; i++)if(t[i]==0)continue;
        System.out.println((System.nanoTime()-s));*/
        /*int from = 33, to = 825;
        int[] bord = {from,from,to,to}; // по умолчанию верх-лево от, низ-право до
        if(from%32==to%32)bord[3] = from;
        else if(from%32>to%32){bord[1] = to; bord[3] = from; }
        if(from/32==to/32)bord[2] = from;
        else if(from/32>to/32){bord[0] = to; bord[2] = from; }
        System.out.println(Arrays.toString(bord));

        int up=bord[0]-32; if(up<0)up=bord[0]; bord[0] = up;


        bord[1] = bord[1]%32; if(bord[1]!=0)bord[1] = (bord[1]-1)%32;

        int down = bord[2]+32; if(down>1023)down = bord[2];bord[2] = down;

        bord[3] = bord[3]%32; if(bord[3]<31)bord[3]+=1;

        int right = bord[3]+1; if(right+1%32==0)right = bord[3];
        System.out.println(31%32);

        System.out.println(Arrays.toString(bord));*/
        /*System.out.println((-33/32));
        System.out.println((int) Math.floor( (double) 33 / 32 ));*/
    }
}
