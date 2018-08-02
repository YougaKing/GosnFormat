package cn.dailyyoga.com.gosnformat;

import com.google.gson.Gson;
import com.google.gson.internal.bind.GsonFormat;

import org.junit.Test;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/08/01 16:41
 * description:
 */
public class GsonFormatTest {


    public static String json = "{ " +
            "\"name\": {}," +
            "\"age\": 123," +
            "\"avatar\": []," +
            "\"strings\": {}" +
            " }";

    @Test
    public void format() {

        Gson gson = GsonFormat.newGson();
        System.out.println(json);


        User result = gson.fromJson(json, User.class);


        System.out.println(result);
    }
}