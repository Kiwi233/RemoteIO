package remoteio.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLLog;
import remoteio.client.helper.IORenderHelper;
import remoteio.common.lib.DimensionalCoords;
import remoteio.common.lib.VisualState;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;

/**
 * @author dmillerw
 */
public class RenderTileRemoteInterface extends TileEntitySpecialRenderer<TileRemoteInterface> {

    private static boolean shouldRender(VisualState visualState) {
        return visualState == VisualState.CAMOUFLAGE_REMOTE || visualState == VisualState.CAMOUFLAGE_BOTH;
    }

    @Override
    public void renderTileEntityAt(TileRemoteInterface remoteInterface, double x, double y, double z, float partialTicks, int destroyStage) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient worldClient = FMLClientHandler.instance().getWorldClient();

        if (remoteInterface.remotePosition != null && remoteInterface.remotePosition.inWorld(remoteInterface.getWorld()) && remoteInterface.visualState.isCamouflage()) {
            DimensionalCoords there = remoteInterface.remotePosition;

            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();

            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(90 * remoteInterface.rotationY, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            TileEntity remoteTile = there.getTileEntity(worldClient);

            if (remoteTile != null) {
                try {
                    TileEntityRendererDispatcher.instance.renderTileEntityAt(remoteInterface.remotePosition.getTileEntity(worldClient), 0, 0, 0, partialTicks);
                } catch (Exception ex) {
                    FMLLog.warning("Failed to render " + remoteInterface.remotePosition.getTileEntity(worldClient).getClass().getSimpleName() + ". Reason: " + ex.getLocalizedMessage());

                    // Maybe bring this back if becomes an issue
//					tile.camoRenderLock = true;
                    remoteInterface.markForRenderUpdate();
                }
            }

            GlStateManager.popMatrix();
        } else {
            if (!remoteInterface.visualState.isCamouflage()) {
                TileIOCore tile = (TileIOCore) worldClient.getTileEntity(new BlockPos(x, y, z));
                TextureAtlasSprite icon = mc.getRenderItem().getItemModelMesher().getParticleIcon(tile.simpleCamo.getItem(), tile.simpleCamo.getItemDamage());

                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.translate(x, y, z);

                bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

                char c0 = 61680;
                int j = c0 % 65536;
                int k = c0 / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
                GlStateManager.color(1, 1, 1, 1);

                IORenderHelper.renderCube(icon);

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }
    }
}