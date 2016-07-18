package remoteio.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import remoteio.common.core.handler.ContainerHandler;
import remoteio.common.network.ServerProxyPlayer;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void setClientPlayerSlot(int slot, ItemStack itemStack) {

    }

    public World getWorld(int dimension) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimension);
    }

    public void activateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
        Container container = player.openContainer;
        ServerProxyPlayer proxyPlayer = new ServerProxyPlayer(entityPlayerMP);

        proxyPlayer.connection = entityPlayerMP.connection;
        proxyPlayer.inventory = entityPlayerMP.inventory;
        proxyPlayer.currentWindowId = entityPlayerMP.currentWindowId;
        proxyPlayer.inventoryContainer = entityPlayerMP.inventoryContainer;
        proxyPlayer.openContainer = entityPlayerMP.openContainer;
        proxyPlayer.worldObj = entityPlayerMP.worldObj;

        Block block = proxyPlayer.worldObj.getBlockState(pos).getBlock();
        if (block != null) {
            block.onBlockActivated(proxyPlayer.worldObj, pos, state, proxyPlayer, hand, heldItem, side, hitX, hitY, hitZ);
        }

        entityPlayerMP.interactionManager.thisPlayerMP = entityPlayerMP;
        if (container != proxyPlayer.openContainer) {
            entityPlayerMP.openContainer = proxyPlayer.openContainer;
        }

        ContainerHandler.INSTANCE.containerWhitelist.put(entityPlayerMP.getName(), entityPlayerMP.openContainer);
    }
}