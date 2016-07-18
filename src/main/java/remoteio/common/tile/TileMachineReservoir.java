package remoteio.common.tile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import remoteio.common.tile.core.TileCore;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class TileMachineReservoir extends TileCore implements IFluidHandler, ITickable {
    public boolean filled = false;

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("filled", filled);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        filled = nbt.getBoolean("filled");
    }

    @Override
    public void update() {
        if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0) {
            updateReservoir();
            if (filled) push();
        }
    }

    @Override
    public void onNeighborUpdated() {
        updateReservoir();
    }

    private void updateReservoir() {
        int found = 0;
        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            Block block = worldObj.getBlock(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (block != null && (block == Blocks.water || block == Blocks.flowing_water)) found++;
        }
        boolean newFilled = found >= 2;
        if (filled != newFilled) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        filled = newFilled;
    }

    private void push() {
        int found = 0;
        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (tileEntity instanceof IFluidHandler) found++;
        }

        final int amount = (int) ((float) FluidContainerRegistry.BUCKET_VOLUME / (float) found);

        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tileEntity = worldObj.getTileEntity(xCoord + forgeDirection.offsetX, yCoord + forgeDirection.offsetY, zCoord + forgeDirection.offsetZ);
            if (tileEntity instanceof IFluidHandler) {
                ((IFluidHandler) tileEntity).fill(forgeDirection.getOpposite(), new FluidStack(FluidRegistry.WATER, amount), true);
            }
        }
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return new FluidStack(FluidRegistry.WATER, resource.amount);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return new FluidStack(FluidRegistry.WATER, maxDrain);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new FluidTankProperties[]{new FluidTankProperties(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), Fluid.BUCKET_VOLUME)};
    }
}