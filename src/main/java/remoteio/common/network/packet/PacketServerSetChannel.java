package remoteio.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import remoteio.common.item.ItemWirelessLocationChip;
import remoteio.common.lib.ModItems;
import remoteio.common.tile.TileTransceiver;

/**
 * @author dmillerw
 */
public class PacketServerSetChannel implements IMessage, IMessageHandler<PacketServerSetChannel, IMessage> {

    public BlockPos pos = BlockPos.ORIGIN;
    public int channel = 0;

    @Override
    public void toBytes(ByteBuf buf) {
        if (pos.getY() > 0) {
            buf.writeBoolean(true);
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        } else {
            buf.writeBoolean(false);
        }
        buf.writeInt(channel);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }
        channel = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketServerSetChannel message, MessageContext ctx) {
        EntityPlayerMP entityPlayerMP = ctx.getServerHandler().playerEntity;
        if (message.pos.getY() > 0) {
            TileTransceiver tileTransceiver = (TileTransceiver) entityPlayerMP.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
            tileTransceiver.setChannel(message.channel);
        } else {
            ItemStack itemStack = entityPlayerMP.getHeldItemMainhand();
            if (itemStack != null && itemStack.getItem() == ModItems.wirelessLocationChip) {
                ItemWirelessLocationChip.setChannel(itemStack, message.channel);
            }
        }
        return null;
    }
}