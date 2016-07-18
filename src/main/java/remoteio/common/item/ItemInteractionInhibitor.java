package remoteio.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import remoteio.common.core.TabRemoteIO;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemInteractionInhibitor extends Item {

    public ItemInteractionInhibitor() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // Status
        switch (stack.getItemDamage()) {
            case 0:
            case 2:
                tooltip.add(" - " + I18n.format("inhibitor.inactive"));
                break;
            case 1:
            case 3:
                tooltip.add(" - " + I18n.format("inhibitor.active"));
                break;
        }

        // Block/Item
        switch (stack.getItemDamage()) {
            case 0:
            case 1:
                tooltip.add(" - " + I18n.format("inhibitor.block"));
                break;
            case 2:
            case 3:
                tooltip.add(" - " + I18n.format("inhibitor.item"));
                break;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            if (stack.getItemDamage() == 0) {
                stack.setItemDamage(1);
            } else if (stack.getItemDamage() == 1) {
                stack.setItemDamage(0);
            } else if (stack.getItemDamage() == 2) {
                stack.setItemDamage(3);
            } else if (stack.getItemDamage() == 3) {
                stack.setItemDamage(2);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, 0));
        subItems.add(new ItemStack(this, 1, 2));
    }
}