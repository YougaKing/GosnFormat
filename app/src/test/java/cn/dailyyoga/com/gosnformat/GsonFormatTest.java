package cn.dailyyoga.com.gosnformat;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/08/01 16:41
 * description:
 */
public class GsonFormatTest {


    public static String json = "{ " +
            "\"name\": {}," +
            "\"age\": 123," +
            "\"avatar\": null," +
            "\"isRegister\": 0," +
            "\"strings\": [\"12312\",null,\"3213\"]" +
            " }";

    @Test
    public void format() {

        User user = new User();
        List<String> stringList = new ArrayList<>();
        stringList.add("12312");
        stringList.add(null);
        stringList.add("null");
        stringList.add("hah");
        user.strings = stringList;

        Gson gson = GsonFormat.newGson();
        System.out.println("json:" + json);


//        System.out.println("result:" + gson.toJson(user));

        User result = gson.fromJson(json, User.class);


        System.out.println("result:" + result);
    }
}