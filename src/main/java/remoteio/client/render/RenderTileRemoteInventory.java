package remoteio.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import remoteio.client.helper.IORenderHelper;
import remoteio.common.lib.ModBlocks;
import remoteio.common.tile.TileRemoteInventory;

/**
 * @author dmillerw
 */
public class RenderTileRemoteInventory extends TileEntitySpecialRenderer<TileRemoteInventory> {

    public void renderRemoteInterfaceAt(TileRemoteInventory tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!tile.visualState.isCamouflage()) {
            TextureAtlasSprite icon = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Item.getItemFromBlock(ModBlocks.remoteInventory));

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