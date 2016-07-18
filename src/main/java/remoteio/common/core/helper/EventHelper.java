package remoteio.common.core.helper;

import net.minecraftforge.common.MinecraftForge;

/**
 * @author dmillerw
 */
public class EventHelper {

    public static void register(Object instance) {
        MinecraftForge.EVENT_BUS.register(instance);
    }
}