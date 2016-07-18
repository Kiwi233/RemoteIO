package remoteio.client.helper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * @author dmillerw
 */
public class IORenderHelper {
    public static void renderCube(TextureAtlasSprite icon) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        // TOP
        vertexbuffer.pos(0, 1.001, 0).tex(icon.getMinU(), icon.getMinV()).endVertex();
        vertexbuffer.pos(0, 1.001, 1).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(1, 1.001, 1).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(1, 1.001, 0).tex(icon.getMaxU(), icon.getMinV()).endVertex();

        // BOTTOM
        vertexbuffer.pos(1, -0.001, 0).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        vertexbuffer.pos(1, -0.001, 1).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(0, -0.001, 1).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(0, -0.001, 0).tex(icon.getMinU(), icon.getMinV()).endVertex();

        // NORTH
        vertexbuffer.pos(0, 1, -0.001).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        vertexbuffer.pos(1, 1, -0.001).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(1, 0, -0.001).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(0, 0, -0.001).tex(icon.getMinU(), icon.getMinV()).endVertex();

        // SOUTH
        vertexbuffer.pos(0, 0, 1.001).tex(icon.getMinU(), icon.getMinV()).endVertex();
        vertexbuffer.pos(1, 0, 1.001).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(1, 1, 1.001).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(0, 1, 1.001).tex(icon.getMaxU(), icon.getMinV()).endVertex();

        // EAST
        vertexbuffer.pos(1.001, 1, 0).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        vertexbuffer.pos(1.001, 1, 1).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(1.001, 0, 1).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(1.001, 0, 0).tex(icon.getMinU(), icon.getMinV()).endVertex();

        // WEST
        vertexbuffer.pos(-0.001, 0, 0).tex(icon.getMinU(), icon.getMinV()).endVertex();
        vertexbuffer.pos(-0.001, 0, 1).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(-0.001, 1, 1).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        vertexbuffer.pos(-0.001, 1, 0).tex(icon.getMaxU(), icon.getMinV()).endVertex();

        tessellator.draw();
    }
}