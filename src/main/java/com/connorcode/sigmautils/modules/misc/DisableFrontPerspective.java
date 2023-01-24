package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.option.Perspective;

public class DisableFrontPerspective extends Module {
    private static final BoolSetting allowFrontPerspective = perspectiveSetting("front", false);
    private static final BoolSetting allowBackPerspective = perspectiveSetting("back", true);
    private static final BoolSetting allowNormalPerspective = perspectiveSetting("normal", true);

    public DisableFrontPerspective() {
        super("disable_front_perspective", "No Front Perspective",
                "Removes the front perspective from perspective switching", Category.Misc);
    }

    private static BoolSetting perspectiveSetting(String perspective, boolean _default) {
        return new BoolSetting(DisableFrontPerspective.class, String.format("Allow %s perspective", perspective)).value(
                _default).build();
    }

    public static Perspective nextPerspective(Perspective old) {
        if (!allowFrontPerspective.value() && !allowBackPerspective.value() && !allowNormalPerspective.value())
            return old;

        Perspective next = Perspective.values()[(old.ordinal() + 1) % 3];
        if (next == Perspective.FIRST_PERSON && allowNormalPerspective.value()) return next;
        if (next == Perspective.THIRD_PERSON_BACK && allowBackPerspective.value()) return next;
        if (next == Perspective.THIRD_PERSON_FRONT && allowFrontPerspective.value()) return next;
        return nextPerspective(next);
    }
}
