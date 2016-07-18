package remoteio.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.lib.DimensionalCoords;

/**
 * @author dmillerw
 */
public class ItemWirelessLocationChip extends Item {
    public static int getChannel(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        return itemStack.getTagCompound().getInteger("channel");
    }

    public static void setChannel(ItemStack itemStack, int channel) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        nbtTagCompound.setInteger("channel", channel);
        itemStack.setTagCompound(nbtTagCompound);
    }

    public ItemWirelessLocationChip() {
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            player.openGui(RemoteIO.instance, GuiHandler.GUI_SET_CHANNEL, world, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                RemoteIO.channelRegistry.setChannelData(getChannel(stack), new DimensionalCoords(world.provider.getDimension(), pos));
                player.addChatComponentMessage(new TextComponentString("chat.target.save"));
            }
        }

        if (!world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return false;
    }
}