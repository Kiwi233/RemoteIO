package remoteio.client.documentation;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;
import remoteio.client.gui.GuiDocumentation;
import remoteio.common.lib.Strings;

/**
 * @author dmillerw
 */
public class DocumentationPageText
implements IDocumentationPage {
    private String unlocalizedPrefix;

    public DocumentationPageText(String unlocalizedPrefix) {
        this.unlocalizedPrefix = unlocalizedPrefix;
    }

    @Override
    public void renderScreen(GuiScreen guiScreen, int mouseX, int mouseY) {
        FontRenderer fRenderer = FMLClientHandler.instance().getClient().fontRendererObj;
        String localized = I18n.format(this.unlocalizedPrefix);
        String[] lines = Strings.wrap(localized, 20).split("\n");
        int x = 15;
        int y = 0;
        for (String str : lines) {
            fRenderer.drawString(str, x, y += fRenderer.FONT_HEIGHT + 2, GuiDocumentation.TEXT_COLOR);
        }
    }

    @Override
    public void updateScreen(GuiScreen guiScreen) {
    }
}