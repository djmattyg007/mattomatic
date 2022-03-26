package net.matthewgamble.mattomatic.tileentity;

public enum MachineSideState
{
    INACTIVE("inactive", 1),
    INPUT("input", 2),
    OUTPUT("output", 3);

    private final String name;
    private final int stateId;

    private MachineSideState(String name, int stateId)
    {
        this.name = name;
        this.stateId = stateId;
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
}
