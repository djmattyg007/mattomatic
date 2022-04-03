package net.matthewgamble.mattomatic.block;

import net.minecraft.util.IStringSerializable;

public enum Fullness implements IStringSerializable
{
    EMPTY("empty"),
    PERCENT1("1p"),
    PERCENT25("25p"),
    PERCENT50("50p"),
    PERCENT75("75p"),
    PERCENT100("100p");

    private final String name;

    private Fullness(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }

    public static Fullness fromFraction(float percent)
    {
        if (percent <= 0) {
            return EMPTY;
        } else if (percent > 0 && percent < 0.25) {
            return PERCENT1;
        } else if (percent >= 0.25 && percent < 0.50) {
            return PERCENT25;
        } else if (percent >= 0.50 && percent < 0.75) {
            return PERCENT50;
        } else if (percent >= 0.75 && percent < 1) {
            return PERCENT75;
        } else if (percent >= 1) {
            return PERCENT100;
        } else {
            return EMPTY;
        }
    }
}
