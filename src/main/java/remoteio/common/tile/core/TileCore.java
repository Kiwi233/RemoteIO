package remoteio.common.tile.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import remoteio.common.network.VanillaPacketHelper;

/**
 * @author dmillerw
 */
public abstract class TileCore extends TileEntity {

    public abstract void writeCustomNBT(NBTTagCompound nbt);

    public abstract void readCustomNBT(NBTTagCompound nbt);

    public void onClientUpdate(NBTTagCompound nbt) {
    }

    public void onNeighborUpdated() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        writeCustomNBT(nbt);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        readCustomNBT(nbt);
    }

    public void markForUpdate() {
        if (hasWorldObj()) {
            getWorld().markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void markForRenderUpdate() {
        if (hasWorldObj()) {
            getWorld().markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    public void updateNeighbors() {
        if (hasWorldObj()) {
            getWorld().notifyBlockOfStateChange(pos, getBlockType());
        }
    }

    public void sendClientUpdate(NBTTagCompound tag) {
        VanillaPacketHelper.sendToAllWatchingTile(this, new SPacketUpdateTileEntity(pos, 1, tag));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        switch (packet.getTileEntityType()) {
            case 0:
                readFromNBT(packet.getNbtCompound());
                break;
            case 1:
                onClientUpdate(packet.getNbtCompound());
                break;
            default:
                break;
        }
        worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
}