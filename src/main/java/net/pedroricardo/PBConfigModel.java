package net.pedroricardo;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = PedrosBakery.MOD_ID)
@Config(wrapperName = "PBConfig", name = "pedrosbakery")
public class PBConfigModel {
    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMinSize = 4;

    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMaxSize = 16;

    @RangeConstraint(min = 1, max = 16)
    @RestartRequired
    public int bakingTrayDefaultSize = 8;

    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMinHeight = 4;

    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMaxHeight = 16;

    @RangeConstraint(min = 1, max = 16)
    @RestartRequired
    public int bakingTrayDefaultHeight = 8;

    @RangeConstraint(min = 0, max = Integer.MAX_VALUE)
    public int maxCakeHeight = 128;

    @RangeConstraint(min = 0, max = Integer.MAX_VALUE)
    public int biteSize = 2;

    @RangeConstraint(min = 1, max = Integer.MAX_VALUE)
    public int ticksUntilCakeBaked = 2000;

    @RangeConstraint(min = 1, max = Integer.MAX_VALUE)
    public int ticksUntilPieBaked = 2000;

    @Sync(Option.SyncMode.NONE)
    public CakeRenderQuality cakeRenderQuality = CakeRenderQuality.ALL_BORDERS;

    @RangeConstraint(min = 1, max = 16)
    public int beaterBatterAmount = 4;

    @RangeConstraint(min = 0, max = 100)
    public int cakeBiteFood = 2;

    @RangeConstraint(min = 0, max = 1000)
    public float cakeBiteSaturation = 0.1f;

    @RangeConstraint(min = 0, max = 100)
    public int cupcakeFood = 2;

    @RangeConstraint(min = 0, max = 1000)
    public float cupcakeSaturation = 0.1f;

    @RangeConstraint(min = 0, max = 100)
    public int pieSliceFood = 2;

    @RangeConstraint(min = 0, max = 1000)
    public float pieSliceSaturation = 0.1f;

    public enum CakeRenderQuality {
        SIMPLE(false, false, false),
        BORDERS_ON_SIDES(true, false, false),
        BORDERS_ON_SIDES_AND_TOP(true, true, false),
        ALL_BORDERS(true, true, true);

        private final boolean sides;
        private final boolean top;
        private final boolean bottom;

        CakeRenderQuality(boolean sides, boolean top, boolean bottom) {
            this.sides = sides;
            this.top = top;
            this.bottom = bottom;
        }

        public boolean renderSideBorders() {
            return this.sides;
        }

        public boolean renderTopBorder() {
            return this.top;
        }

        public boolean renderBottomBorder() {
            return this.bottom;
        }
    }
}
