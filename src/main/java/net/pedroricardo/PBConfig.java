package net.pedroricardo;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class PBConfig extends Config {
    public PBConfig() {
        super(Identifier.of(PedrosBakery.MOD_ID, "config"));
    }

    public ValidatedInt bakingTrayMinSize = new ValidatedInt(4, 16, 1, ValidatedNumber.WidgetType.SLIDER);

    public ValidatedInt bakingTrayMaxSize = new ValidatedInt(16, 16, 1, ValidatedNumber.WidgetType.SLIDER);

    @RequiresAction(action = Action.RESTART)
    public ValidatedInt bakingTrayDefaultSize = new ValidatedInt(8, 16, 1, ValidatedNumber.WidgetType.SLIDER);

    public ValidatedInt bakingTrayMinHeight = new ValidatedInt(4, 16, 1, ValidatedNumber.WidgetType.SLIDER);

    public ValidatedInt bakingTrayMaxHeight = new ValidatedInt(16, 16, 1, ValidatedNumber.WidgetType.SLIDER);

    @RequiresAction(action = Action.RESTART)
    public ValidatedInt bakingTrayDefaultHeight = new ValidatedInt(8, 16, 1, ValidatedNumber.WidgetType.SLIDER);

    public ValidatedInt maxCakeHeight = new ValidatedInt(128, Integer.MAX_VALUE, 0);

    public ValidatedFloat biteSize = new ValidatedFloat(2, Float.MAX_VALUE, 0, ValidatedNumber.WidgetType.TEXTBOX);

    public ValidatedInt ticksUntilCakeBaked = new ValidatedInt(2000, Integer.MAX_VALUE, 0);

    public ValidatedInt ticksUntilPieBaked = new ValidatedInt(2000, Integer.MAX_VALUE, 0);

    @NonSync
    public ValidatedEnum<CakeRenderQuality> cakeRenderQuality = new ValidatedEnum<>(CakeRenderQuality.ALL_BORDERS);

    public ValidatedInt beaterBatterAmount = new ValidatedInt(4, 16, 1);

    public ValidatedInt cakeBiteFood = new ValidatedInt(2, 100, 0);

    public ValidatedFloat cakeBiteSaturation = new ValidatedFloat(0.1f, 1000, 0);

    public ValidatedInt cupcakeFood = new ValidatedInt(2, 100, 0);

    public ValidatedFloat cupcakeSaturation = new ValidatedFloat(0.1f, 1000, 0);

    public ValidatedInt pieSliceFood = new ValidatedInt(2, 100, 0);

    public ValidatedFloat pieSliceSaturation = new ValidatedFloat(0.1f, 1000, 0);

    public enum CakeRenderQuality implements EnumTranslatable {
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

        @Override
        public @NotNull String prefix() {
            return "pedrosbakery.config.enum.cakeRenderQuality";
        }
    }
}
