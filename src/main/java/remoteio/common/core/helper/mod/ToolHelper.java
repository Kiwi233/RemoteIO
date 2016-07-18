package remoteio.common.core.helper.mod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import remoteio.api.IIOTool;

/**
 * @author dmillerw
 */
public class ToolHelper {
    public static boolean isTool(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (itemStack == null || itemStack.getItem() == null)
            return false;

        if (itemStack.getItem() instanceof IIOTool)
            return true;

        /*if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            if (itemStack.getItem() instanceof IAEWrench)
                return ((IAEWrench) itemStack.getItem()).canWrench(itemStack, entityPlayer, x, y, z);
        }*/

        return false;
    }
}