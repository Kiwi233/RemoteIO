package remoteio.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import remoteio.client.helper.IORenderHelper;
import remoteio.common.block.BlockMachine;
import remoteio.common.core.helper.ArrayHelper;
import remoteio.common.tile.TileMachineHeater;
import remoteio.common.tile.TileMachineReservoir;

/**
 * @author dmillerw
 */
public class RenderTileMachine extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        boolean render = false;
        if (tileEntity instanceof TileMachineReservoir) if (((TileMachineReservoir) tileEntity).filled) render = true;
        if (tileEntity instanceof TileMachineHeater) if (((TileMachineHeater) tileEntity).filled) render = true;
        if (!render) return;

        IIcon icon = ArrayHelper.safeGetArray(BlockMachine.overlays, tileEntity.getBlockMetadata());

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y, z);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        IORenderHelper.renderCube(icon);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}