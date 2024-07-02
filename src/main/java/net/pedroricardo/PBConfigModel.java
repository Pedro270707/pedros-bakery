package net.pedroricardo;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.RestartRequired;

@Modmenu(modId = PedrosBakery.MOD_ID)
@Config(wrapperName = "PBConfig", name = "pedrosbakery")
public class PBConfigModel {
    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMinSize = 8;

    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMaxSize = 8;

    @RangeConstraint(min = 1, max = 16)
    @RestartRequired
    public int bakingTrayDefaultSize = 8;

    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMinHeight = 8;

    @RangeConstraint(min = 1, max = 16)
    public int bakingTrayMaxHeight = 8;

    @RangeConstraint(min = 1, max = 16)
    @RestartRequired
    public int bakingTrayDefaultHeight = 8;

    @RangeConstraint(min = 0, max = Integer.MAX_VALUE)
    public int maxCakeHeight = 128;

    @RangeConstraint(min = 0, max = Integer.MAX_VALUE)
    public int biteSize = 2;

    @RangeConstraint(min = 1, max = Integer.MAX_VALUE)
    public int ticksUntilBaked = 2000;

    public CakeRenderQuality cakeRenderQuality = CakeRenderQuality.ALL_BORDERS;

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
