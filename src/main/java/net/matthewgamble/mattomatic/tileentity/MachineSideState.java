package net.matthewgamble.mattomatic.tileentity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum MachineSideState
{
    INACTIVE("inactive", 1),
    INPUT("input", 2),
    OUTPUT("output", 3);

    private final String name;
    private final int stateId;
    private final ITextComponent label;

    private MachineSideState(String name, int stateId)
    {
        this.name = name;
        this.stateId = stateId;
        this.label = new TranslationTextComponent("mattomatic.side_config.state." + name);
    }

    public static MachineSideState fromStateId(int stateId)
    {
        switch (stateId) {
            case 1:
                return INACTIVE;
            case 2:
                return INPUT;
            case 3:
                return OUTPUT;
            default:
                throw new IllegalArgumentException("No machine side state with ID " + stateId);
        }
    }

    public int getValue()
    {
        return this.stateId;
    }

    public String toString()
    {
        return this.getSerializedName();
    }

    public String getSerializedName()
    {
        return this.name;
    }

    public ITextComponent getLabel()
    {
        return this.label;
    }
}
