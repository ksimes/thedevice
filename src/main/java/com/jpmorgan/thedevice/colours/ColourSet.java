package com.jpmorgan.thedevice.colours;

/**
 * Created by S.King on 15/02/2017.
 */
public enum ColourSet {
    Nothing(0),

    White(0xffffff),
    Red(0xff0000),
    Green(0x00ff00),
    Blue(0x0000ff),
    Yellow(0xffff00),

    Cyan(0x00ffff),
    Black(0x000000),
    Magenta(0xff007f),
    Amber(0xFFBF00),

    LightBlue(0x009cce),
    Purple(0xC231E1),
    Mustard(0xffce31),
    DarkOrange(0xe1632e),
    DarkRed(0xce0063),
    MossGreen(0x008000),
    LightGreen(0xc0dcc0),
    Aquamarine(0x008080),
    TwilightBlue(0x6666cc),
    TwilightViolet(0x9966cd),
    SkyBlue(0x00ccff),
    SoftPink(0xff9999),
    PowderBlue(0xccccff),
    RegalRed(0xcc3366),
    RedBrown(0xcc6633),
    Plum(0x660066),
    KentuckyGreen(0x339966),
    IceBlue(0x99ffff),
    GrassGreen(0x009933),
    ForestGreen(0x006633),
    ElectricBlue(0x6666ff),
    EasterPurple(0xcc66ff),
    StandardBlue(0x336699),
    DustyRose(0xcc6699),
    DesertBlue(0x336699),
    ArmyGreen(0x669966),
    AutumnOrange(0xff6633),
    AvocadoGreen(0x669933),
    BabyBlue(0x6699ff),
    BananaYellow(0xcccc33),
    BrickRed(0xcc3300),
    Brown(0x996633),
    Crimson(0x993366),
    OceanGreen(0x996633),
    Walnut(0x663300),
    BrightGreen(0x66FF00);

    private long value;

    private ColourSet(long numVal) {
        this.value = numVal;
    }

    public long getVal() {
        return value;
    }

    public static ColourSet match(String text)
    {
        ColourSet result = Nothing;
        for(ColourSet c : ColourSet.values()) {
            if(c.name().toUpperCase().equals(text.trim().toUpperCase())) {
                result = c;
            }
        }
        return result;
    }

}
