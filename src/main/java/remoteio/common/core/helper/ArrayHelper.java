package remoteio.common.core.helper;

/**
 * @author dmillerw
 */
public class ArrayHelper {

    public static <T> T safeGetArray(T[] array, int index) {
        return array[index < 0 ? 0 : index >= array.length ? array.length - 1 : index];
    }

    public static String toString(Object... objects) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            sb.append("[").append(i).append("] ");
            sb.append(obj != null ? obj.toString() : "null");
            if (i != objects.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}