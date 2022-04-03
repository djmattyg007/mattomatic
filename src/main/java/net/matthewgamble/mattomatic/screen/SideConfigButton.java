package net.matthewgamble.mattomatic.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.tileentity.MachineSideState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SideConfigButton extends Button
{
    public static final int BTN_SIZE = 12;

    private static final ResourceLocation GUI = new ResourceLocation(MattomaticMod.MOD_ID, "textures/gui/btn_side_config.png");
    private static final int textureWidth = 12;
    private static final int textureHeight = 12 * 3;

    protected final Button.IPressable onPressRight;
    private MachineSideState sideState = MachineSideState.INACTIVE;

    public SideConfigButton(int x, int y, Button.ITooltip onTooltip, Button.IPressable onPress, Button.IPressable onPressRight)
    {
        super(x, y, BTN_SIZE, BTN_SIZE, StringTextComponent.EMPTY, onPress, onTooltip);
        this.onPressRight = onPressRight;
    }

    public SideConfigButton(int x, int y, MachineSideState currentSideState, Button.ITooltip onTooltip, Button.IPressable onPress, Button.IPressable onPressRight)
    {
        this(x, y, onTooltip, onPress, onPressRight);
        this.sideState = currentSideState;
    }

    public void setSideState(MachineSideState newSideState)
    {
        this.sideState = newSideState;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bind(GUI);

        int yPos = BTN_SIZE * (this.sideState.getValue() - 1);

        blit(
            matrixStack,
            this.x, this.y,
            0f, (float) yPos,
            this.width, this.height,
            textureWidth, textureHeight
        );
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean validMouseButton = mouseButton == 0 || mouseButton == 1;
        if (validMouseButton && this.clicked(mouseX, mouseY)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            if (mouseButton == 1) {
                this.onClickRight(mouseX, mouseY);
            } else {
                this.onClick(mouseX, mouseY);
            }
            return true;
        }

        return false;
    }

    public void onClickRight(double mouseX, double mouseY)
    {
        this.onPressRight();
    }

    public void onPressRight()
    {
        this.onPressRight.onPress(this);
    }
}
