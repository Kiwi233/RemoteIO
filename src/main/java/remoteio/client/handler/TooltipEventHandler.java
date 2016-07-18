package remoteio.client.handler;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author dmillerw
 */
public class TooltipEventHandler {
    @SubscribeEvent
    public void itemTooltipEvent(ItemTooltipEvent event) {
        if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("inhibit")) {
            byte inhibit = event.getItemStack().getTagCompound().getByte("inhibit");
            event.getToolTip().add(I18n.format("inhibitor.tooltip"));
            if (inhibit == 1) {
                event.getToolTip().add(" - " + I18n.format("inhibitor.item"));
            } else {
                event.getToolTip().add(" - " + I18n.format("inhibitor.block"));
            }
        }
    }
}