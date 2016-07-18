package remoteio.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import remoteio.common.RemoteIO;
import remoteio.common.block.core.BlockIOCore;
import remoteio.common.core.TransferType;
import remoteio.common.core.UpgradeType;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.core.helper.RotationHelper;
import remoteio.common.lib.DimensionalCoords;
import remoteio.common.lib.VisualState;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class BlockRemoteInterface extends BlockIOCore {

    public static int renderID;

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean result = super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);

        if (result) {
            return true;
        }

        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(pos);

        if (tile.remotePosition != null && !player.isSneaking() && tile.hasUpgradeChip(UpgradeType.REMOTE_ACCESS)) {
            DimensionalCoords there = tile.remotePosition;
            RemoteIO.proxy.activateBlock(world, there.pos, player, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side), hitX, hitY, hitZ);
        }

        return true;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile != null && tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return tile.remotePosition.getBlock().getBlockHardness(tile.remotePosition.getWorld(), tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z);
        } else {
            return super.getBlockHardness(world, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile != null && tile.remotePosition != null && tile.hasTransferChip(TransferType.REDSTONE)) {
            tile.remotePosition.getBlock().onNeighborBlockChange(tile.remotePosition.getWorld(), tile.remotePosition.x, tile.remotePosition.y, tile.remotePosition.z, block);
            tile.markForUpdate();
        } else {
            super.onNeighborBlockChange(world, x, y, z, block);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile != null && tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return new ItemStack(tile.remotePosition.getBlock());
        } else {
            return super.getPickBlock(target, world, x, y, z);
        }
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
        if (tile != null && tile.remotePosition != null && tile.hasUpgradeChip(UpgradeType.REMOTE_CAMO)) {
            return tile.remotePosition.getMeta();
        } else {
            return super.getDamageValue(world, x, y, z);
        }
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        if (!world.isRemote) {
            TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
            if (tile != null && tile.hasTransferChip(TransferType.REDSTONE) && tile.remotePosition != null) {
                DimensionalCoords there = tile.remotePosition;
                Block remote = there.getBlock();

                if (remote.hasComparatorInputOverride()) {
                    return remote.getComparatorInputOverride(there.getWorld(), there.x, there.y, there.z, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side));
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
            if (tile != null && tile.hasTransferChip(TransferType.REDSTONE) && tile.remotePosition != null) {
                DimensionalCoords there = tile.remotePosition;
                Block remote = there.getBlock();

                if (remote.canProvidePower()) {
                    return remote.isProvidingWeakPower(there.getWorld(), there.x, there.y, there.z, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side));
                }
            }
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
            if (tile != null && tile.hasTransferChip(TransferType.REDSTONE) && tile.remotePosition != null) {
                DimensionalCoords there = tile.remotePosition;
                Block remote = there.getBlock();

                if (remote.canProvidePower()) {
                    return remote.isProvidingStrongPower(there.getWorld(), there.x, there.y, there.z, RotationHelper.getRotatedSide(0, tile.rotationY, 0, side));
                }
            }
        }
        return 0;
    }

    @Override
    public int getGuiID() {
        return GuiHandler.GUI_REMOTE_INTERFACE;
    }

	/* BEGIN COLLISION HANDLING */

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

        if (tile != null && tile.visualState == VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
            DimensionalCoords there = tile.remotePosition;
            Block remote = there.getBlock(world);

            int offsetX = there.x - x;
            int offsetY = there.y - y;
            int offsetZ = there.z - z;

            Vec3 offsetStart = Vec3.createVectorHelper(start.xCoord + offsetX, start.yCoord + offsetY, start.zCoord + offsetZ);
            Vec3 offsetEnd = Vec3.createVectorHelper(end.xCoord + offsetX, end.yCoord + offsetY, end.zCoord + offsetZ);

            MovingObjectPosition mob = remote.collisionRayTrace(world, there.x, there.y, there.z, offsetStart, offsetEnd);

            if (mob != null) {
                mob.blockX -= offsetX;
                mob.blockY -= offsetY;
                mob.blockZ -= offsetZ;
                mob.hitVec.xCoord -= offsetX;
                mob.hitVec.yCoord -= offsetY;
                mob.hitVec.zCoord -= offsetZ;
            }

            return mob;
        }

        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

        if (tile != null && tile.visualState == VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
            DimensionalCoords there = tile.remotePosition;
            Block remote = there.getBlock(world);

            int offsetX = there.x - x;
            int offsetY = there.y - y;
            int offsetZ = there.z - z;

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            //TODO: Rotate the player based on the block rotation to get accurate hit results

            // We're about to descend into madness here...
            player.prevPosX += offsetX;
            player.prevPosY += offsetY;
            player.prevPosZ += offsetZ;
            player.posX += offsetX;
            player.posY += offsetY;
            player.posZ += offsetZ;

            AxisAlignedBB aabb = remote.getSelectedBoundingBoxFromPool(world, there.x, there.y, there.z);

            // Ending the madness
            player.prevPosX -= offsetX;
            player.prevPosY -= offsetY;
            player.prevPosZ -= offsetZ;
            player.posX -= offsetX;
            player.posY -= offsetY;
            player.posZ -= offsetZ;

            if (aabb != null) {
                aabb.offset(-offsetX, -offsetY, -offsetZ);
            }

            return aabb;
        }

        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);

        if (tile != null && tile.visualState == VisualState.CAMOUFLAGE_REMOTE && tile.remotePosition != null) {
            DimensionalCoords there = tile.remotePosition;
            Block remote = there.getBlock(world);

            int offsetX = there.x - x;
            int offsetY = there.y - y;
            int offsetZ = there.z - z;

            AxisAlignedBB newAABB = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ).offset(offsetX, offsetY, offsetZ);
            List newList = new ArrayList();

            remote.addCollisionBoxesToList(world, there.x, there.y, there.z, newAABB, newList, entity);

            for (Object o : newList) {
                AxisAlignedBB aabb1 = (AxisAlignedBB) o;
                aabb1.offset(-offsetX, -offsetY, -offsetZ);

                if (aabb.intersectsWith(aabb1)) {
                    list.add(aabb1);
                }
            }

            return;
        }

        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

	/* END COLLISION HANDLING */

    @Override
    public int getRenderType() {
        return BlockRemoteInterface.renderID;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return true;
    }

    @Override
    public TileIOCore getTileEntity() {
        return new TileRemoteInterface();
    }
}