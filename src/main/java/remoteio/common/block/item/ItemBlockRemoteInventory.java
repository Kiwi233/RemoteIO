package remoteio.common.block.item;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemBlockRemoteInventory extends ItemBlock {
    public ItemBlockRemoteInventory(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("targetPlayer")) {
            tooltip.add(String.format(I18n.format("tooltip.bound"), stack.getTagCompound().getString("targetPlayer")));
        } else {
            tooltip.add(I18n.format("tooltip.inventory.creative"));
        }
    }
}