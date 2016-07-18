package remoteio.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
import remoteio.common.RemoteIO;
import remoteio.common.tile.core.TileCore;

/**
 * @author dmillerw
 */
public class TileTransceiver extends TileCore {

    public EnumFacing orientation = EnumFacing.UNKNOWN;

    public int channel = 0;

    private boolean forceUpdate = false;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", (byte) orientation.ordinal());
        nbt.setInteger("channel", channel);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        orientation = ForgeDirection.getOrientation(nbt.getByte("orientation"));
        channel = nbt.getInteger("channel");
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (forceUpdate || RemoteIO.channelRegistry.pollDirty(channel)) {
                TileEntity tileEntity = worldObj.getTileEntity(xCoord + orientation.offsetX, yCoord + orientation.offsetY, zCoord + orientation.offsetZ);
                if (tileEntity instanceof TileRemoteInterface) {
                    ((TileRemoteInterface) tileEntity).setRemotePosition(RemoteIO.channelRegistry.getChannelData(channel));
                }
            }
        }
    }

    public void setChannel(int channel) {
        this.channel = channel;
        this.forceUpdate = true;
        markForUpdate();
    }
}
