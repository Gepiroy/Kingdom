package rooms;

import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.server.v1_16_R3.EntityTypes;

public final class ReflectionUtil {

    private ReflectionUtil() {
    	
    }

    @Nullable
    public static Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void register(String name, int id, Class<?> registryClass) {
       ((Map) getPrivateField("c", EntityTypes.class, null)).put(name, registryClass);
       ((Map) getPrivateField("d", EntityTypes.class, null)).put(registryClass, name);
       ((Map) getPrivateField("f", EntityTypes.class, null)).put(registryClass, id);
    }
}