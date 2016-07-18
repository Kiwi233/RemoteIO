package remoteio.client.documentation;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public interface IDocumentationPage {
    @SideOnly(Side.CLIENT)
    void renderScreen(GuiScreen guiScreen, int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    void updateScreen(GuiScreen guiScreen);
}