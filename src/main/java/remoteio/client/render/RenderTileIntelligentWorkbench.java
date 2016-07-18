package remoteio.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import remoteio.common.lib.ModBlocks;
import remoteio.common.tile.TileIntelligentWorkbench;

/**
 * @author dmillerw
 */
public class RenderTileIntelligentWorkbench extends TileEntitySpecialRenderer<TileIntelligentWorkbench> {

    @Override
    public void renderTileEntityAt(TileIntelligentWorkbench intelligentWorkbench, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y, z);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        TextureAtlasSprite icon = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Item.getItemFromBlock(ModBlocks.intelligentWorkbench));

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(0, 1.001, 0).tex(icon.getMinU(), icon.getMinV());
        vertexbuffer.pos(0, 1.001, 1).tex(icon.getMinU(), icon.getMaxV());
        vertexbuffer.pos(1, 1.001, 1).tex(icon.getMaxU(), icon.getMaxV());
        vertexbuffer.pos(1, 1.001, 0).tex(icon.getMaxU(), icon.getMinV());

        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}