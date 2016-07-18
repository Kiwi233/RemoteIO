package remoteio.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import remoteio.common.tile.TileTransceiver;

/**
 * @author dmillerw
 */
public class RenderTileTransceiver extends TileEntitySpecialRenderer {
    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation("remoteio:models/transceiver.obj"));

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partial) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);

        switch (((TileTransceiver)tileEntity).orientation) {
            case UP: {
                GlStateManager.rotate(180, 1, 0, 0);
                GlStateManager.translate(0, -1, 0);
                break;
            }

            case NORTH: {
                GlStateManager.rotate(90, 1, 0, 0);
                GlStateManager.translate(0, -0.5, -0.5);
                break;
            }

            case SOUTH: {
                GlStateManager.rotate(-90, 1, 0, 0);
                GlStateManager.translate(0, -0.5, 0.5);
                break;
            }

            case WEST: {
                GlStateManager.rotate(-90, 0, 0, 1);
                GlStateManager.translate(-0.5, -0.5, 0);
                break;
            }

            case EAST: {
                GlStateManager.rotate(90, 0, 0, 1);
                GlStateManager.translate(0.5, -0.5, 0);
                break;
            }
        }

        GlStateManager.disableTexture2D();
        MODEL.renderAll();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }
}
