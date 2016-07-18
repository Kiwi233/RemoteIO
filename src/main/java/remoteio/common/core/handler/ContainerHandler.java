package remoteio.common.core.handler;

import com.google.common.collect.Maps;
import net.minecraft.inventory.Container;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

/**
 * @author dmillerw
 */
public class ContainerHandler {
    public static final ContainerHandler INSTANCE = new ContainerHandler();

    public Map<String, Container> containerWhitelist = Maps.newHashMap();

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntityPlayer().openContainer != null && event.getEntityPlayer().openContainer != event.getEntityPlayer().inventoryContainer) {
            Container whitelisted = containerWhitelist.get(event.getEntityPlayer().getName());
            if (whitelisted != null && whitelisted == event.getEntityPlayer().openContainer)
                event.setResult(Event.Result.ALLOW);
        }
    }
}