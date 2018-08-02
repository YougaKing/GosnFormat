package cn.dailyyoga.com.gosnformat;

import java.util.List;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/08/01 17:25
 * description:
 */
public class User {
    public int age;
    public String name;
    public Avatar avatar;
    public List<String> strings;

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", strings=" + strings +
                '}';
    }

    public static class Avatar{
        public String lage;
        public String small;
    }
}
