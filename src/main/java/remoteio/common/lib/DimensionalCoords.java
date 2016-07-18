package remoteio.common.lib;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import remoteio.common.RemoteIO;

/**
 * @author dmillerw
 */
public class DimensionalCoords {
    public static DimensionalCoords create(TileEntity tile) {
        return new DimensionalCoords(tile.getWorld(), tile.getPos());
    }

    public static DimensionalCoords create(EntityLivingBase entity) {
        return new DimensionalCoords(entity.worldObj, entity.getPosition());
    }


    public static DimensionalCoords fromNBT(NBTTagCompound nbt) {
        return new DimensionalCoords(nbt.getInteger("dimension"), nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
    }

    public int dimensionID;

    public BlockPos pos;

    public DimensionalCoords(World world, BlockPos pos) {
        this(world.provider.getDimension(), pos);
    }

    public DimensionalCoords(int dimensionID, double x, double y, double z) {
        this(dimensionID, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public DimensionalCoords(int dimensionID, BlockPos pos) {
        this.dimensionID = dimensionID;
        this.pos = pos;
    }

    public DimensionalCoords(int dimensionID, int x, int y, int z) {
        this.dimensionID = dimensionID;
        this.pos = new BlockPos(x, y, z);
    }

    public boolean withinRange(DimensionalCoords coords, int range) {
        int xRange = Math.abs(this.pos.getX() - coords.pos.getX());
        int yRange = Math.abs(this.pos.getY() - coords.pos.getY());
        int zRange = Math.abs(this.pos.getZ() - coords.pos.getZ());

        return (xRange <= range && yRange <= range && zRange <= range);
    }

    /* WORLD WRAPPERS */

    public boolean inWorld(World world) {
        return world.provider.getDimension() == this.dimensionID;
    }

    public World getWorld() {
        return RemoteIO.proxy.getWorld(dimensionID);
    }

    public boolean blockExists() {
        return getBlock() != null && !getBlock().isAir(getState(getWorld()), getWorld(), pos);
    }

    public boolean blockExists(World world) {
        return getBlock(world) != null && !getBlock().isAir(getState(world), world, pos);
    }

    public Block getBlock() {
        return getWorld() != null ? getWorld().getBlockState(pos).getBlock() : null;
    }

    public IBlockState getState() {
        return getWorld() != null ? getWorld().getBlockState(pos) : null;
    }

    public TileEntity getTileEntity() {
        return getWorld() != null ? getWorld().getTileEntity(pos) : null;
    }

    public Block getBlock(World world) {
        return getState(world).getBlock();
    }

    public IBlockState getState(World world) {
        return world.getBlockState(pos);
    }

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(pos);
    }

    public void markForUpdate() {
        if (getWorld() != null) getWorld().markBlockForUpdate(pos);
    }

    /* END */

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("dimension", this.dimensionID);
        nbt.setInteger("x", this.pos.getX());
        nbt.setInteger("y", this.pos.getZ());
        nbt.setInteger("z", this.pos.getZ());
    }

    public int hashCode() {
        return this.dimensionID & this.pos.getX() & this.pos.getY() & this.pos.getZ();
    }

    public DimensionalCoords copy() {
        return new DimensionalCoords(this.dimensionID, this.pos);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DimensionalCoords)) {
            return false;
        }

        return equals((DimensionalCoords) obj);
    }

    public boolean equals(DimensionalCoords coords) {
        return (this.dimensionID == coords.dimensionID) && (this.pos == coords.pos);
    }

    @Override
    public String toString() {
        return "[" + dimensionID + " : " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
    }
}