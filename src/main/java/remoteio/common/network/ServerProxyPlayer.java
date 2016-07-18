package remoteio.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

/**
 * @author dmillerw
 */
public class ServerProxyPlayer extends EntityPlayerMP {

    private EntityPlayerMP parentPlayer;

    public ServerProxyPlayer(EntityPlayerMP parentPlayer) {
        super(parentPlayer.mcServer, (WorldServer) parentPlayer.worldObj, parentPlayer.getGameProfile(), parentPlayer.interactionManager);
        this.parentPlayer = parentPlayer;
    }

    @Override
    public float getDistanceToEntity(Entity entity) {
        return 6;
    }

    @Override
    public double getDistanceSq(double x, double y, double z) {
        return 6;
    }

    @Override
    public double getDistance(double x, double y, double z) {
        return 6;
    }

    @Override
    public double getDistanceSqToEntity(Entity entity) {
        return 6;
    }
}