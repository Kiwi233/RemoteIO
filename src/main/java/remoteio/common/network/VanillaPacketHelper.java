package remoteio.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class VanillaPacketHelper {

    /* IO */
    public static void writeNBTTagCompoundToBuffer(ByteBuf byteBuf, NBTTagCompound nbtTagCompound) throws IOException {
        if (nbtTagCompound == null) {
            byteBuf.writeShort(-1);
        } else {
            byte[] abyte = CompressedStreamTools.compress(nbtTagCompound);
            byteBuf.writeShort((short) abyte.length);
            byteBuf.writeBytes(abyte);
        }
    }

    public static NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf byteBuf) throws IOException {
        short short1 = byteBuf.readShort();

        if (short1 < 0) {
            return null;
        } else {
            byte[] abyte = new byte[short1];
            byteBuf.readBytes(abyte);
            return CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L));
        }
    }

    public static void writeItemStackToBuffer(ByteBuf byteBuf, ItemStack itemStack) throws IOException {
        if (itemStack == null) {
            byteBuf.writeShort(-1);
        } else {
            byteBuf.writeShort(Item.getIdFromItem(itemStack.getItem()));
            byteBuf.writeByte(itemStack.stackSize);
            byteBuf.writeShort(itemStack.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (itemStack.getItem().isDamageable() || itemStack.getItem().getShareTag()) {
                nbttagcompound = itemStack.getTagCompound();
            }

            writeNBTTagCompoundToBuffer(byteBuf, nbttagcompound);
        }
    }

    public static ItemStack readItemStackFromBuffer(ByteBuf byteBuf) throws IOException {
        ItemStack stack = null;
        short short1 = byteBuf.readShort();

        if (short1 >= 0) {
            byte b0 = byteBuf.readByte();
            short short2 = byteBuf.readShort();
            stack = new ItemStack(Item.getItemById(short1), b0, short2);
            stack.setTagCompound(readNBTTagCompoundFromBuffer(byteBuf));
        }

        return stack;
    }

    /* SENDING */
    public static void sendToAllWatchingTile(TileEntity tile, Packet packet) {
        if (!tile.hasWorldObj()) {
            return;
        }

        sendToAllWatchingChunk(tile.getWorld().getChunkFromBlockCoords(tile.getPos()), packet);
    }

    /**
     * Sends the specified packet to all players either in specified chunk, or at least have that chunk loaded
     */
    public static void sendToAllWatchingChunk(Chunk chunk, Packet packet) {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        World world = chunk.getWorld();

        if (world instanceof WorldServer) {
            PlayerManager playerManager = ((WorldServer) world).getPlayerManager();
            for (Object obj : manager.playerEntityList) {
                EntityPlayerMP player = (EntityPlayerMP) obj;

                if (playerManager.isPlayerWatchingChunk(player, chunk.xPosition, chunk.zPosition)) {
//					if (!player.loadedChunks.contains(new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition))) {
                    player.connection.sendPacket(packet);
//					}
                }
            }
        }
    }

    public static void sendToAllInDimension(int dimension, Packet packet) {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (Object obj : playerList.getPlayerList()) {
            EntityPlayerMP player = (EntityPlayerMP) obj;

            if (player.getEntityWorld().provider.getDimension() == dimension) {
                player.connection.sendPacket(packet);
            }
        }
    }

    public static void sendToAllInRange(int dimension, int x, int y, int z, int range, Packet packet) {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (Object obj : playerList.getPlayerList()) {
            EntityPlayerMP player = (EntityPlayerMP) obj;

            if (player.getEntityWorld().provider.getDimension() == dimension && player.getDistance(x, y, z) <= range) {
                player.connection.sendPacket(packet);
            }
        }
    }
}