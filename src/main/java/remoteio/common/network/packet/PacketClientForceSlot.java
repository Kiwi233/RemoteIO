package remoteio.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import remoteio.common.RemoteIO;
import remoteio.common.network.VanillaPacketHelper;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class PacketClientForceSlot implements IMessage, IMessageHandler<PacketClientForceSlot, IMessage> {

    public int slot;
    public ItemStack itemStack;

    public PacketClientForceSlot() {

    }

    public PacketClientForceSlot(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        if (itemStack != null) {
            buf.writeBoolean(true);
            try {
                VanillaPacketHelper.writeItemStackToBuffer(buf, itemStack);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        if (buf.readBoolean()) {
            try {
                itemStack = VanillaPacketHelper.readItemStackFromBuffer(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IMessage onMessage(PacketClientForceSlot message, MessageContext ctx) {
        RemoteIO.proxy.setClientPlayerSlot(message.slot, message.itemStack);
        return null;
    }
}
