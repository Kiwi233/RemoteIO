package remoteio.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import remoteio.common.core.handler.ContainerHandler;

/**
 * @author dmillerw
 */
public class PacketClientAllowContainer implements IMessage, IMessageHandler<PacketClientAllowContainer, IMessage> {

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(PacketClientAllowContainer message, MessageContext ctx) {
        ContainerHandler.INSTANCE.containerWhitelist.put(Minecraft.getMinecraft().thePlayer.getName(), Minecraft.getMinecraft().thePlayer.openContainer);
        return null;
    }
}