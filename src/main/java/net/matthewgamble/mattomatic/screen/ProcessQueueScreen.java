package net.matthewgamble.mattomatic.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.container.ProcessQueueContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProcessQueueScreen extends ContainerScreen<ProcessQueueContainer>
{
    private final ResourceLocation GUI_SCREEN = new ResourceLocation(MattomaticMod.MOD_ID, "textures/gui/process_queue.png");
    private final ResourceLocation GUI_BTN_HORIZ = new ResourceLocation(MattomaticMod.MOD_ID, "textures/gui/btn_horiz.png");
    private final ResourceLocation GUI_BTN_VERT = new ResourceLocation(MattomaticMod.MOD_ID, "textures/gui/btn_vert.png");

    protected int imageHeight = 168;

    private ImageButton btnHoriz;
    private ImageButton btnVert;

    public ProcessQueueScreen(ProcessQueueContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init()
    {
        super.init();

//        Button.IPressable btnHorizHandler = (btnHoriz) -> {};
//        Button.ITooltip btnHorizTooltip = (btn, matrixStack, x, y) -> {
//            this.renderTooltip(matrixStack, this.font.split(new TranslationTextComponent("mattomatic.process_queue.button.horizontal.tooltip"), Math.max(this.width / 2 - 43, 170)), x, y);
//        };
//        this.btnHoriz = new ImageButton(this.leftPos + 70, this.topPos + 52, 20, 18, 0, 0, 19, GUI_BTN_HORIZ, 256, 256, btnHorizHandler, btnHorizTooltip, StringTextComponent.EMPTY);
//        this.addButton(btnHoriz);
//
//        Button.IPressable btnVertHandler = (btnVert) -> {};
//        Button.ITooltip btnVertTooltip = (btn, matrixStack, x, y) -> {
//            this.renderTooltip(matrixStack, this.font.split(new TranslationTextComponent("mattomatic.process_queue.button.vertical.tooltip"), Math.max(this.width / 2 - 43, 170)), x, y);
//        };
//        this.btnVert = new ImageButton(this.leftPos + 95, this.topPos + 52, 20, 18, 0, 0, 19, GUI_BTN_VERT, 256, 256, btnVertHandler, btnVertTooltip, StringTextComponent.EMPTY);
//        this.addButton(btnVert);

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y)
    {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(GUI_SCREEN);

        this.blit(
            matrixStack,
            this.leftPos, this.topPos,
            0, 0,
            this.imageWidth, this.imageHeight
        );
    }

    private void onContainerChange()
    {

    }
}
