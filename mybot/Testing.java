package mybot;

import java.util.ArrayList;
import java.util.Arrays;

public class Testing {

    static void sortFreeWorkers(ArrayList<String> listFreeWorkers, int[] freeIdDistance){
        //ArrayList<String> result = new ArrayList<>(listFreeWorkers.size());
        for (int left = 0; left < freeIdDistance.length; left++) {
            int minInd = left;
            for (int i = left+1; i < freeIdDistance.length; i++) {
                if (freeIdDistance[i] < freeIdDistance[minInd]) {
                    minInd = i;
                }
            }
            if(minInd==left)continue;
            //swap(list1, left, minInd);
            int tmp = freeIdDistance[left];
            freeIdDistance[left] = freeIdDistance[minInd];
            freeIdDistance[minInd] = tmp;
            String tmpId = listFreeWorkers.get(left);
            listFreeWorkers.set(left,listFreeWorkers.get(minInd));
            // result.add(listFreeWorkers.get(minInd));
            listFreeWorkers.set(minInd,tmpId);
        }

    }


    public static void main(String[] args) {

        long s =System.nanoTime();
        MyGameMap myGameMap = new MyGameMap();
        System.out.println((System.nanoTime()-s));
        s =System.nanoTime();
        int size = 100;
        System.out.println((System.nanoTime()-s));
        s =System.nanoTime();
        String[] u = new String[size];
        System.out.println((System.nanoTime()-s));
        s =System.nanoTime();
        for(int i=0; i<size; i++)u[i] = "U_"+i;
        System.out.println((System.nanoTime()-s));
        s =System.nanoTime();
        for(int i=0; i<size; i++)myGameMap.mapIdWorkerCDnewBuild.put(u[i],new int[2]);
        System.out.println((System.nanoTime()-s));

        s =System.nanoTime();
        int[][] arrIdWorkerCDnewBuild = new int[size][];
        for(int i=0; i<size; i++)arrIdWorkerCDnewBuild[i] = new int[2];
        System.out.println((System.nanoTime()-s));
        s =System.nanoTime();
        int[] numU = new int[size];
        for(int i=0; i<size; i++)numU[i] = Integer.parseInt(u[i].substring(2));
        System.out.println((System.nanoTime()-s));
        s =System.nanoTime();
        for(String ui:myGameMap.mapIdWorkerCDnewBuild.keySet())
            if(ui!=null)continue;
        System.out.println((System.nanoTime()-s));

        s =System.nanoTime();
        for(int[] ui:arrIdWorkerCDnewBuild)
            if(ui!=null)continue;
        System.out.println((System.nanoTime()-s));

        double[] coll = {0.75,1,2.25,3,1.5};

        int[] cl = new int[coll.length];
        s =System.nanoTime();
        for (int i=0; i<cl.length; i++)cl[i] = (int)(coll[i]/0.25);
        System.out.println((System.nanoTime()-s));
        System.out.println(Arrays.toString(cl));
    }
}
