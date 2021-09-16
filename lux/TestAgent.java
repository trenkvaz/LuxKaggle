package lux;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestAgent {

    static String home_folder = "E:\\Lux_AI";

    public static void message(String mes,int numAgent,String namefile){

        try {
            OutputStream os = new FileOutputStream(new File(home_folder+"\\test\\"+namefile+".txt"), true);
            os.write((mes+"\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
