package remoteio.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.handler.GuiHandler;

/**
 * @author dmillerw
 */
public class ItemPDA extends Item {
    public ItemPDA() {
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        player.openGui(RemoteIO.instance, GuiHandler.GUI_PDA, world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}