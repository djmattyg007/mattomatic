package net.matthewgamble.mattomatic.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.MattomaticNet;
import net.matthewgamble.mattomatic.container.ProcessQueueContainer;
import net.matthewgamble.mattomatic.packets.MachineSideStateChangePacket;
import net.matthewgamble.mattomatic.tileentity.MachineSideState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
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
    private static final ResourceLocation GUI_SCREEN = new ResourceLocation(MattomaticMod.MOD_ID, "textures/gui/process_queue.png");

    private static final ITextComponent txtDirectionTop = new TranslationTextComponent("mattomatic.side_config.direction.top");
    private static final ITextComponent txtDirectionBottom = new TranslationTextComponent("mattomatic.side_config.direction.bottom");
    private static final ITextComponent txtDirectionNorth = new TranslationTextComponent("mattomatic.side_config.direction.north");
    private static final ITextComponent txtDirectionSouth = new TranslationTextComponent("mattomatic.side_config.direction.south");
    private static final ITextComponent txtDirectionEast = new TranslationTextComponent("mattomatic.side_config.direction.east");
    private static final ITextComponent txtDirectionWest = new TranslationTextComponent("mattomatic.side_config.direction.west");

    protected int imageHeight = 168;

    private SideConfigButton btnSCTop;
    private SideConfigButton btnSCBottom;
    private SideConfigButton btnSCNorth;
    private SideConfigButton btnSCSouth;
    private SideConfigButton btnSCEast;
    private SideConfigButton btnSCWest;

    public ProcessQueueScreen(ProcessQueueContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init()
    {
        super.init();

        String tooltipLabelSep = " - ";

        // btnTop
        Button.IPressable btnTopHandler = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToNext(0));
        };
        Button.IPressable btnTopHandlerRight = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToPrev(0));
        };
        Button.ITooltip btnTopTooltip = (btn, matrixStack, x, y) -> {
            MachineSideState sideState = this.menu.getSideState(0);
            String label = txtDirectionTop.getString() + tooltipLabelSep + sideState.getLabel().getString();
            this.renderTooltip(matrixStack, this.font.split(new StringTextComponent(label), Math.max(this.width / 2 - 43, 170)), x, y);
        };
        this.btnSCTop = new SideConfigButton(this.leftPos + 64, this.topPos + 42, btnTopTooltip, btnTopHandler, btnTopHandlerRight);
        this.addButton(this.btnSCTop);

        // btnBottom
        Button.IPressable btnBottomHandler = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToNext(1));
        };
        Button.IPressable btnBottomHandlerRight = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToPrev(1));
        };
        Button.ITooltip btnBottomTooltip = (btn, matrixStack, x, y) -> {
            MachineSideState sideState = this.menu.getSideState(1);
            String label = txtDirectionBottom.getString() + tooltipLabelSep + sideState.getLabel().getString();
            this.renderTooltip(matrixStack, this.font.split(new StringTextComponent(label), Math.max(this.width / 2 - 43, 170)), x, y);
        };
        this.btnSCBottom = new SideConfigButton(this.btnSCTop.x, this.btnSCTop.y + SideConfigButton.BTN_SIZE + 2, btnBottomTooltip, btnBottomHandler, btnBottomHandlerRight);
        this.addButton(this.btnSCBottom);

        // btnNorth
        Button.IPressable btnNorthHandler = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToNext(2));
        };
        Button.IPressable btnNorthHandlerRight = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToPrev(2));
        };
        Button.ITooltip btnNorthTooltip = (btn, matrixStack, x, y) -> {
            MachineSideState sideState = this.menu.getSideState(2);
            String label = txtDirectionNorth.getString() + tooltipLabelSep + sideState.getLabel().getString();
            this.renderTooltip(matrixStack, this.font.split(new StringTextComponent(label), Math.max(this.width / 2 - 43, 170)), x, y);
        };
        this.btnSCNorth = new SideConfigButton(this.leftPos + 88, this.topPos + 39, btnNorthTooltip, btnNorthHandler, btnNorthHandlerRight);
        this.addButton(this.btnSCNorth);

        // btnSouth
        Button.IPressable btnSouthHandler = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToNext(3));
        };
        Button.IPressable btnSouthHandlerRight = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToPrev(3));
        };
        Button.ITooltip btnSouthTooltip = (btn, matrixStack, x, y) -> {
            MachineSideState sideState = this.menu.getSideState(3);
            String label = txtDirectionSouth.getString() + tooltipLabelSep + sideState.getLabel().getString();
            this.renderTooltip(matrixStack, this.font.split(new StringTextComponent(label), Math.max(this.width / 2 - 43, 170)), x, y);
        };
        this.btnSCSouth = new SideConfigButton(this.btnSCNorth.x, this.btnSCNorth.y + SideConfigButton.BTN_SIZE + 8, btnSouthTooltip, btnSouthHandler, btnSouthHandlerRight);
        this.addButton(this.btnSCSouth);

        // btnEast
        Button.IPressable btnEastHandler = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToNext(4));
        };
        Button.IPressable btnEastHandlerRight = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToPrev(4));
        };
        Button.ITooltip btnEastTooltip = (btn, matrixStack, x, y) -> {
            MachineSideState sideState = this.menu.getSideState(4);
            String label = txtDirectionEast.getString() + tooltipLabelSep + sideState.getLabel().getString();
            this.renderTooltip(matrixStack, this.font.split(new StringTextComponent(label), Math.max(this.width / 2 - 43, 170)), x, y);
        };
        this.btnSCEast = new SideConfigButton(this.leftPos + 99, this.btnSCNorth.y + 10, btnEastTooltip, btnEastHandler, btnEastHandlerRight);
        this.addButton(this.btnSCEast);

        // btnWest
        Button.IPressable btnWestHandler = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToNext(5));
        };
        Button.IPressable btnWestHandlerRight = (btn) -> {
            MattomaticNet.messageServer(MachineSideStateChangePacket.setToPrev(5));
        };
        Button.ITooltip btnWestTooltip = (btn, matrixStack, x, y) -> {
            MachineSideState sideState = this.menu.getSideState(5);
            String label = txtDirectionWest.getString() + tooltipLabelSep + sideState.getLabel().getString();
            this.renderTooltip(matrixStack, this.font.split(new StringTextComponent(label), Math.max(this.width / 2 - 43, 170)), x, y);
        };
        this.btnSCWest = new SideConfigButton(this.btnSCEast.x - SideConfigButton.BTN_SIZE - 10, this.btnSCEast.y, btnWestTooltip, btnWestHandler, btnWestHandlerRight);
        this.addButton(this.btnSCWest);

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.btnSCTop.setSideState(this.menu.getSideState(0));
        this.btnSCBottom.setSideState(this.menu.getSideState(1));
        this.btnSCNorth.setSideState(this.menu.getSideState(2));
        this.btnSCSouth.setSideState(this.menu.getSideState(3));
        this.btnSCEast.setSideState(this.menu.getSideState(4));
        this.btnSCWest.setSideState(this.menu.getSideState(5));

        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.buttons.forEach((button) -> {
            if (button.isHovered()) {
                button.renderToolTip(matrixStack, mouseX, mouseY);
            }
        });
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
}
