package remoteio.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import remoteio.common.core.handler.ContainerHandler;
import remoteio.common.network.ServerProxyPlayer;

/**
 * @author dmillerw
 */
public class PacketServerOpenRemoteGUI implements IMessage, IMessageHandler<PacketServerOpenRemoteGUI, IMessage> {

    public int x;
    public int y;
    public int z;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketServerOpenRemoteGUI message, MessageContext ctx) {
        EntityPlayerMP entityPlayerMP = ctx.getServerHandler().playerEntity;
        Container container = entityPlayerMP.openContainer;
        ServerProxyPlayer proxyPlayer = new ServerProxyPlayer(entityPlayerMP);

        proxyPlayer.connection = entityPlayerMP.connection;
        proxyPlayer.inventory = entityPlayerMP.inventory;
        proxyPlayer.currentWindowId = entityPlayerMP.currentWindowId;
        proxyPlayer.inventoryContainer = entityPlayerMP.inventoryContainer;
        proxyPlayer.openContainer = entityPlayerMP.openContainer;
        proxyPlayer.worldObj = entityPlayerMP.worldObj;

        BlockPos pos = new BlockPos(message.x, message.y, message.z);
        Block block = proxyPlayer.worldObj.getBlockState(pos).getBlock();
        if (block != null)
            block.onBlockActivated(proxyPlayer.worldObj, pos, proxyPlayer.worldObj.getBlockState(pos), proxyPlayer, proxyPlayer.getActiveHand(), proxyPlayer.getHeldItemMainhand(), EnumFacing.DOWN, 0, 0, 0);

        entityPlayerMP.interactionManager.thisPlayerMP = entityPlayerMP;
        if (container != proxyPlayer.openContainer) {
            entityPlayerMP.openContainer = proxyPlayer.openContainer;
        }

        ContainerHandler.INSTANCE.containerWhitelist.put(entityPlayerMP.getName(), entityPlayerMP.openContainer);

        return null;
    }
}