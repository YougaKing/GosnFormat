package cn.dailyyoga.com.gosnformat;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/08/01 16:40
 * description:
 */
public class GsonFormat {

    public static Gson newGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new ObjectTypeAdapterFactory())
                .registerTypeAdapter(String.class, new StringDeserializer())
                .registerTypeAdapter(List.class, new ListDeserializer())
                .registerTypeAdapter(int.class, new IntegerDeserializer())
                .registerTypeAdapter(Integer.class, new IntegerDeserializer())
                .registerTypeAdapter(long.class, new LongDeserializer())
                .registerTypeAdapter(Long.class, new LongDeserializer())
                .registerTypeAdapter(float.class, new FloatDeserializer())
                .registerTypeAdapter(Float.class, new FloatDeserializer())
                .registerTypeAdapter(double.class, new DoubleDeserializer())
                .registerTypeAdapter(Double.class, new DoubleDeserializer())
                .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                .registerTypeAdapter(boolean.class, new BooleanSerializer())
                .create();
    }

    public static class ObjectTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
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
    }

    public static class StringDeserializer implements JsonDeserializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String string = json.getAsJsonPrimitive().getAsString();
                return string == null ? "" : string;
            } catch (Exception e) {
                e.printStackTrace();
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
                List<?> list = newGson.fromJson(json, typeOfT);
                if (list == null) return Collections.EMPTY_LIST;
                list.removeAll(Collections.singleton(null));
                return list;
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
                e.printStackTrace();
                return 0;
            }
        }
    }

    public static class LongDeserializer implements JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return (long) json.getAsJsonPrimitive().getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
                return 0L;
            }
        }
    }

    public static class FloatDeserializer implements JsonDeserializer<Float> {
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return (float) json.getAsJsonPrimitive().getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
                return 0F;
            }
        }
    }

    public static class DoubleDeserializer implements JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsJsonPrimitive().getAsDouble();
            } catch (Exception e) {
                e.printStackTrace();
                return 0D;
            }
        }
    }

    public static class BooleanSerializer implements JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsJsonPrimitive().isNumber()) {
                    return json.getAsJsonPrimitive().getAsDouble() != 0F;
                } else {
                    return json.getAsJsonPrimitive().getAsBoolean();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
