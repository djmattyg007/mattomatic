package net.matthewgamble.mattomatic.block;

import net.minecraft.state.EnumProperty;

public class BlockStateProperties
{
    public static final EnumProperty<Fullness> FULLNESS = EnumProperty.create("fullness", Fullness.class);
}
