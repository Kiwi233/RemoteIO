package remoteio.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import remoteio.client.documentation.Documentation;
import remoteio.client.handler.SoundHandler;
import remoteio.client.handler.TooltipEventHandler;
import remoteio.client.render.*;
import remoteio.common.CommonProxy;
import remoteio.common.RemoteIO;
import remoteio.common.core.helper.EventHelper;
import remoteio.common.network.ClientProxyPlayer;
import remoteio.common.tile.*;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerBlockHandler(new RenderBlockRemoteInterface());

        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInterface.class, new RenderTileRemoteInterface());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInventory.class, new RenderTileRemoteInventory());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMachineReservoir.class, new RenderTileMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMachineHeater.class, new RenderTileMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileIntelligentWorkbench.class, new RenderTileIntelligentWorkbench());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, new RenderTileTransceiver());

        MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
        EventHelper.register(new TooltipEventHandler());

        RemoteIO.localizationUpdater.registerListener();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        Documentation.initialize();
    }

    @Override
    public void setClientPlayerSlot(int slot, ItemStack itemStack) {
        Minecraft.getMinecraft().thePlayer.openContainer.getSlot(slot).putStack(itemStack);
    }

    @Override
    public World getWorld(int dimension) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            return super.getWorld(dimension);
        } else {
            return Minecraft.getMinecraft().theWorld;
        }
    }

    @Override
    public void activateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player instanceof EntityPlayerMP) {
            super.activateBlock(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
        } else {
            EntityClientPlayerMP entityClientPlayerMP = (EntityClientPlayerMP) player;
            ClientProxyPlayer proxyPlayer = new ClientProxyPlayer(entityClientPlayerMP);
            proxyPlayer.inventory = entityClientPlayerMP.inventory;
            proxyPlayer.inventoryContainer = entityClientPlayerMP.inventoryContainer;
            proxyPlayer.openContainer = entityClientPlayerMP.openContainer;
            proxyPlayer.movementInput = entityClientPlayerMP.movementInput;

            Block block = entityClientPlayerMP.worldObj.getBlockState(pos).getBlock();
            if (block != null) {
                SoundHandler.INSTANCE.translateNextSound(pos);

                if (proxyPlayer.getHeldItem() != null) {
                    if (proxyPlayer.getHeldItem().getItem().onItemUseFirst(proxyPlayer.getHeldItem(), proxyPlayer, proxyPlayer.worldObj, pos, side, hitX, hitY, hitZ))
                        return;
                }
                block.onBlockActivated(entityClientPlayerMP.worldObj, pos, proxyPlayer, side, hitX, hitY, hitZ);
            }

            if (entityClientPlayerMP.openContainer != proxyPlayer.openContainer) {
                entityClientPlayerMP.openContainer = proxyPlayer.openContainer;
            }
        }
    }
}