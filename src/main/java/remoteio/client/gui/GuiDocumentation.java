package remoteio.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import remoteio.client.documentation.Documentation;
import remoteio.client.documentation.DocumentationEntry;
import remoteio.client.documentation.IDocumentationPage;

import java.util.List;

/**
 * @author dmillerw
 */
public class GuiDocumentation
extends GuiScreen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("remoteio:textures/gui/tablet_green.png");

    public static final int BACK_COLOR = 0x304029;
    public static final int TEXT_COLOR = 0x3C5033;
    public static final int TEXT_HIGHLIGHT_COLOR = 0x729A61;

    public static final int XSIZE = 142;
    public static final int YSIZE = 180;

    private static final int SCREEN_X = 11;
    private static final int SCREEN_Y = 9;
    private static final int SCREEN_WIDTH = 122;
    private static final int SCREEN_HEIGHT = 153;

    private static final int HOME_X = 65;
    private static final int HOME_Y = 167;
    private static final int HOME_OVER_X = 142;
    private static final int HOME_OVER_Y = 147;
    private static final int HOME_WIDTH = 18;
    private static final int HOME_HEIGHT = 8;

    private Documentation.Category currentCategory = null;
    private List<DocumentationEntry> categoryCache = null;
    private DocumentationEntry currentEntry = null;
    private IDocumentationPage currentPage = null;

    private int guiLeft;
    private int guiTop;

    @Override
    public void initGui() {
        currentCategory = null;
        currentEntry = null;
        currentPage = null;

        this.guiLeft = (this.width - XSIZE) / 2;
        this.guiTop = (this.height - YSIZE) / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (currentPage != null)
            currentPage.updateScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partial) {
        super.drawScreen(mouseX, mouseY, partial);

        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, XSIZE, YSIZE);

        if (mouseX >= guiLeft + HOME_X && mouseX <= guiLeft + HOME_X + HOME_WIDTH && mouseY >= guiTop + HOME_Y && mouseY <= guiTop + HOME_Y + HOME_HEIGHT) {
            drawTexturedModalRect(guiLeft + HOME_X, guiTop + HOME_Y, HOME_OVER_X, HOME_OVER_Y, HOME_WIDTH, HOME_HEIGHT);
        }

        int selection = -1;
        int minY = 0;
        int maxY = 0;
        final int mousePadding = 25;
        final int offset = mc.fontRendererObj.FONT_HEIGHT / 2;
        final int standardY = guiTop - offset;
        final int middle = SCREEN_Y + SCREEN_HEIGHT / 2;

        if (currentCategory == null) {
            if (mouseX >= guiLeft + mousePadding && mouseX <= guiLeft + XSIZE - mousePadding) {
                if (mouseY >= standardY - offset + SCREEN_Y + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + SCREEN_Y + SCREEN_HEIGHT / 4) {
                    selection = 0;
                    minY = standardY - offset + SCREEN_Y + SCREEN_HEIGHT / 4;
                    maxY = standardY + offset * 3 + SCREEN_Y + SCREEN_HEIGHT / 4;
                } else if (mouseY >= standardY + middle - offset && mouseY <= standardY + middle + offset * 3) {
                    selection = 1;
                    minY = standardY + middle - offset;
                    maxY = standardY + middle + offset * 3;
                } else if (mouseY >= standardY - offset + middle + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + middle + SCREEN_HEIGHT / 4) {
                    selection = 2;
                    minY = standardY - offset + middle + SCREEN_HEIGHT / 4;
                    maxY = standardY + offset * 3 + middle + SCREEN_HEIGHT / 4;
                }
            }

            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
            vertexbuffer.putColor4(BACK_COLOR);
            if (selection != -1) {
                vertexbuffer.pos(guiLeft + mousePadding, maxY, 0).endVertex();
                vertexbuffer.pos(guiLeft + XSIZE - mousePadding, maxY, 0).endVertex();
                vertexbuffer.pos(guiLeft + XSIZE - mousePadding, minY, 0).endVertex();
                vertexbuffer.pos(guiLeft + mousePadding, minY, 0).endVertex();
            }
            tessellator.draw();
            GlStateManager.enableTexture2D();

            mc.fontRendererObj.drawString("BLOCK", centeredX("BLOCK"), guiTop - offset + SCREEN_Y + SCREEN_HEIGHT / 4, selection == 0 ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
            mc.fontRendererObj.drawString("ITEM", centeredX("ITEM"), guiTop - offset + middle, selection == 1 ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
            mc.fontRendererObj.drawString("OTHER", centeredX("OTHER"), guiTop - offset + middle + SCREEN_HEIGHT / 4, selection == 2 ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
        } else if (currentEntry == null) {
            mc.fontRendererObj.drawString(currentCategory.name() + ":", centeredX(currentCategory.name() + ":"), guiTop + SCREEN_Y + 5, TEXT_COLOR);

            if (categoryCache != null && !categoryCache.isEmpty()) {
                for (int i = 0; i < categoryCache.size(); i++) {
                    DocumentationEntry entry = categoryCache.get(i);
                    String localizedName = I18n.format(entry.getUnlocalizedName()).toUpperCase();
                    if (mc.fontRendererObj.getStringWidth(localizedName) >= SCREEN_WIDTH) {
                        localizedName = mc.fontRendererObj.trimStringToWidth(localizedName, SCREEN_WIDTH - (mc.fontRendererObj.getStringWidth(".....")));
                        localizedName = localizedName + "...";
                    }
                    boolean selected = mouseX >= guiLeft + mousePadding && mouseX <= guiLeft + XSIZE - mousePadding && mouseY >= guiTop + SCREEN_Y + 20 + (15 * i) && mouseY <= guiTop + SCREEN_Y + 20 + (15 * i) + 10;
                    GlStateManager.disableTexture2D();
                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer vertexbuffer = tessellator.getBuffer();
                    vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
                    vertexbuffer.putColor4(BACK_COLOR);
                    if (selected) {
                        vertexbuffer.pos(guiLeft + mousePadding, guiTop + SCREEN_Y + 20 + (15 * i) + 10, 0).endVertex();
                        vertexbuffer.pos(guiLeft + XSIZE - mousePadding, guiTop + SCREEN_Y + 20 + (15 * i) + 10, 0).endVertex();
                        vertexbuffer.pos(guiLeft + XSIZE - mousePadding, guiTop + SCREEN_Y + 20 + (15 * i), 0).endVertex();
                        vertexbuffer.pos(guiLeft + mousePadding, guiTop + SCREEN_Y + 20 + (15 * i), 0).endVertex();
                    }
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    mc.fontRendererObj.drawString(localizedName, centeredX(localizedName), guiTop + SCREEN_Y + 20 + (15 * i), selected ? TEXT_HIGHLIGHT_COLOR : TEXT_COLOR);
                }
            }
        } else if (currentPage == null) {
            this.currentPage = this.currentEntry.pages.getFirst();
        }

        if (currentPage != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft, guiTop, 0);
            currentPage.renderScreen(this, mouseX, mouseY);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            int selection = -1;
            final int mousePadding = 25;
            final int offset = mc.fontRendererObj.FONT_HEIGHT / 2;
            final int standardY = guiTop - offset;
            final int middle = SCREEN_Y + SCREEN_HEIGHT / 2;

            if (currentCategory == null) {
                if (mouseX >= guiLeft + mousePadding && mouseX <= guiLeft + XSIZE - mousePadding) {
                    if (mouseY >= standardY - offset + SCREEN_Y + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + SCREEN_Y + SCREEN_HEIGHT / 4) {
                        selection = 0;
                    } else if (mouseY >= standardY + middle - offset && mouseY <= standardY + middle + offset * 3) {
                        selection = 1;
                    } else if (mouseY >= standardY - offset + middle + SCREEN_HEIGHT / 4 && mouseY <= standardY + offset * 3 + middle + SCREEN_HEIGHT / 4) {
                        selection = 2;
                    }
                }

                switch (selection) {
                    case 0:
                        currentCategory = Documentation.Category.BLOCK;
                        break;
                    case 1:
                        currentCategory = Documentation.Category.ITEM;
                        break;
                    case 2:
                        currentCategory = Documentation.Category.OTHER;
                        break;
                    default:
                        break;
                }

                if (currentCategory != null) {
                    this.categoryCache = Documentation.get(currentCategory);
                }
            } else if (currentEntry == null) {
                this.currentEntry = getEntry(mouseX, mouseY);
            }

            if (mouseX >= guiLeft + HOME_X && mouseX <= guiLeft + HOME_X + HOME_WIDTH && mouseY >= guiTop + HOME_Y && mouseY <= guiTop + HOME_Y + HOME_HEIGHT) {
                this.currentCategory = null;
                this.categoryCache = null;
                this.currentEntry = null;
                this.currentPage = null;
            }
        }
    }

    private DocumentationEntry getEntry(int x, int y) {
        if (this.categoryCache == null) {
            return null;
        }

        int mousePadding = 25;
        for (int i = 0; i < this.categoryCache.size(); i++) {
            boolean selected = x >= guiLeft + mousePadding && x <= guiLeft + XSIZE - mousePadding && y >= guiTop + SCREEN_Y + 20 + (15 * i) && y <= guiTop + SCREEN_Y + 20 + (15 * i) + 10;
            if (selected) {
                return this.categoryCache.get(i);
            }
        }

        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int centeredX(String string) {
        return guiLeft + SCREEN_X + (SCREEN_WIDTH / 2) - mc.fontRendererObj.getStringWidth(string) / 2;
    }
}