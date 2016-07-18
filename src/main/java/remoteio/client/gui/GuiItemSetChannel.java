package remoteio.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import remoteio.client.gui.button.GuiBetterButton;
import remoteio.common.inventory.container.ContainerNull;
import remoteio.common.item.ItemWirelessLocationChip;
import remoteio.common.lib.ModInfo;
import remoteio.common.network.PacketHandler;
import remoteio.common.network.packet.PacketServerSetChannel;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class GuiItemSetChannel extends GuiContainer {

    private static final ResourceLocation GUI_BLANK = new ResourceLocation(ModInfo.RESOURCE_PREFIX + "textures/gui/blank.png");

    private final ItemStack itemStack;

    public GuiBetterButton buttonDec;
    public GuiBetterButton buttonInc;

    public GuiTextField textFieldChannel;

    public int channel = 0;

    public GuiItemSetChannel(ItemStack itemStack) {
        super(new ContainerNull());
        this.itemStack = itemStack;
    }

    private int getChannel() {
        String text = textFieldChannel.getText();
        if (text != null && !text.isEmpty()) {
            try {
                return Integer.valueOf(text);
            } catch (NumberFormatException ex) {
                return 0;
            }
        }
        return 0;
    }

    public void initGui() {
        super.initGui();

        buttonList.add(buttonDec = new GuiBetterButton(0, guiLeft + 107, guiTop + 19, 12, 12, "-"));
        buttonList.add(buttonInc = new GuiBetterButton(1, guiLeft + 121, guiTop + 19, 12, 12, "+"));
        textFieldChannel = new GuiTextField(0, mc.fontRendererObj, 5, 20, 100, 10);
        textFieldChannel.setFocused(true);
        textFieldChannel.setCanLoseFocus(false);
        textFieldChannel.setText(String.valueOf(ItemWirelessLocationChip.getChannel(itemStack)));
        channel = getChannel();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        textFieldChannel.updateCursorCounter();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(GUI_BLANK);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(I18n.format("container.remoteio.channel"), 5, 5, 4210752);
        fontRendererObj.drawSplitString(I18n.format("container.remoteio.channel_desc"), 5, 35, 170, 4210752);
        textFieldChannel.drawTextBox();
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);
        if (key == Keyboard.KEY_BACK || Character.isDigit(character)) {
            textFieldChannel.textboxKeyTyped(character, key);
            channel = getChannel();
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        int rate = GuiScreen.isShiftKeyDown() ? 100 : GuiScreen.isCtrlKeyDown() ? 1 : 10;
        if (guiButton.id == 0) {
            channel = (Math.max(0, channel - rate));
            textFieldChannel.setText(String.valueOf(channel));
        } else if (guiButton.id == 1) {
            channel = (Math.min(1000000, channel + rate));
            textFieldChannel.setText(String.valueOf(channel));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        PacketServerSetChannel packet = new PacketServerSetChannel();
        packet.channel = channel;
        PacketHandler.INSTANCE.sendToServer(packet);
    }
}