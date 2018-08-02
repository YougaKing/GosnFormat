package com.google.gson.internal.bind;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import static com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/08/01 16:40
 * description:
 */
public class GsonFormat {

    public static Gson newGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactory() {
                    @Override
                    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
                        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
                        return new TypeAdapter<T>() {
                            @Override
                            public void write(JsonWriter out, T value) throws IOException {
                                JsonElement tree = delegate.toJsonTree(value);
                                elementAdapter.write(out, tree);
                            }

                            @Override
                            public T read(JsonReader in) {
                                try {
                                    JsonElement tree = elementAdapter.read(in);
                                    if (delegate instanceof Adapter) {
                                        if (!tree.isJsonObject()) {
                                            return null;
                                        } else {
                                            return delegate.fromJsonTree(tree);
                                        }
//                                        return realRead(((Adapter<T>) delegate), tree);
                                    } else {
                                        return delegate.fromJsonTree(tree);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        };
                    }
                })
                .registerTypeAdapter(String.class, new StringDeserializer())
                .registerTypeAdapter(List.class, new ListDeserializer())
                .registerTypeAdapter(int.class, new IntegerDeserializer())
                .registerTypeAdapter(Integer.class, new IntegerDeserializer())
                .create();
    }

    private static <T> T realRead(Adapter<T> delegate, JsonElement tree) {
        JsonReader in = new JsonTreeReader(tree);
        try {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (!tree.isJsonObject()) return null;

            Field constructorField = Adapter.class.getDeclaredField("constructor");
            constructorField.setAccessible(true);//允许访问私有字段
            ObjectConstructor<T> constructor = (ObjectConstructor<T>) constructorField.get(delegate);//获得私有字段值

            Field boundFieldsField = Adapter.class.getDeclaredField("boundFields");
            boundFieldsField.setAccessible(true);//允许访问私有字段
            Map<String, BoundField> boundFields = (Map<String, BoundField>) boundFieldsField.get(delegate);//获得私有字段值

            T instance = constructor.construct();
            try {
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    BoundField field = boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                    } else {
                        field.read(in, instance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            in.endObject();
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class StringDeserializer implements JsonDeserializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                e.fillInStackTrace();
                return "";
            }
        }
    }

    public static class ListDeserializer implements JsonDeserializer<List<?>> {
        @Override
        public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                //这里要自己负责解析了
                Gson newGson = new Gson();
                return newGson.fromJson(json, typeOfT);
            } else {
                //和接口类型不符，返回空List
                return Collections.EMPTY_LIST;
            }
        }
    }

    public static class IntegerDeserializer implements JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return (int) json.getAsJsonPrimitive().getAsDouble();
            } catch (Exception e) {
                e.fillInStackTrace();
                return 0;
            }
        }
    }
}
