//package cn.dailyyoga.com.gosnformat;
//
//import com.google.gson.Gson;
//import com.google.gson.TypeAdapter;
//import com.google.gson.TypeAdapterFactory;
//import com.google.gson.internal.$Gson$Types;
//import com.google.gson.reflect.TypeToken;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonWriter;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * author: YougaKingWu@gmail.com
// * created on: 2018/08/01 16:45
// * description:
// */
//public class FormatTypeAdapterFactory implements TypeAdapterFactory {
//
//    @Override
//    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
//
//
//        List<Field> fields = findMatchedFields(type);
//
//        // 获取其他低优先级Factory创建的DelegateAdapter
//        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
//
//        return new TypeAdapter<T>() {
//            public void write(JsonWriter out, T value) throws IOException {
//                delegate.write(out, value);
//            }
//
//            public T read(JsonReader in) throws IOException {
//                T t = delegate.read(in);
//
//                replaceFields(t, type);
//                return t;
//            }
//        };
//    }
//
//
//    private static List<Field> findMatchedFields(TypeToken typeToken) {
//        List<Field> fieldList = new ArrayList<>();
//        Class raw = typeToken.getRawType();
//        while (shouldSearch(raw)) {
//            Field[] fields = raw.getDeclaredFields();
//            for (Field field : fields) {
//                field.setAccessible(true);
//
//                if (field.getType() == String.class ||
//                        field.getType() == Integer.class || field.getType() == Long.class ||
//                        field.getType() == Float.class || field.getType() == Double.class ||
//                        field.getType() == List.class || field.getType() == ArrayList.class) {
//                    fieldList.add(field);
//                }
//            }
//            // 解析父类
//            typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), typeToken.getRawType(), raw.getGenericSuperclass()));
//            raw = typeToken.getRawType();
//        }
//        return fieldList;
//    }
//
//
//    private static boolean shouldSearch(Class clazz) {
//        // 跳过不需要搜索的类
//        if (clazz == null || clazz == Object.class || clazz.isPrimitive() || clazz.isEnum() || clazz.isArray()) {
//            return false;
//        }
//        // 跳过Java和Android系统中的类
//        String packageName = clazz.getPackage().getName();
//        return !packageName.startsWith("java") && !packageName.startsWith("android");
//    }
//
//    private static void replaceFields(Object o, TypeToken typeToken) {
//        if (o == null) {
//            return;
//        }
//        // 对于嵌套注解的情况（NonNullField对应类型中又有NonNullField），
//        // 由于Gson会先解析内部数据，其TypeAdapter已经创建，此处map可以取到值
//        List<Field> fields = fieldMap.get(typeToken.getType());
//        if (fields == null || fields.isEmpty()) {
//            return;
//        }
//        for (Field field : fields) {
//            try {
//                Object fieldValue = field.get(o);
//                if (fieldValue == null) {
//                    Object value = constructField(field, resolveFieldType(typeToken, field));
//                    if (value == null) {
//                        throw new RuntimeException(String.format("Create field %s for type %s failure",
//                                field.getName(), typeToken.getType()));
//                    }
//                    field.set(o, value);
//                    log("    --> set field '%s.%s' to '%s'", typeToken.getType().getTypeName(), field.getName(), value);
//                }
//            } catch (IllegalArgumentException | IllegalAccessException e) {
//                L.e(e);
//            }
//        }
//    }
//}
