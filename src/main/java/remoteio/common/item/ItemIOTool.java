package remoteio.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import remoteio.api.IIOTool;
import remoteio.common.core.TabRemoteIO;

/**
 * @author dmillerw
 */
public class ItemIOTool extends Item implements IIOTool {
    public ItemIOTool() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
}