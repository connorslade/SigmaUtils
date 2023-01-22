package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleList;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class ParticleControl extends Module {
    static EnumSetting<Util.FilterType> filterType =
            Util.filterSetting(ParticleControl.class)
                    .description("Weather to whitelist or blacklist particles.")
                    .value(Util.FilterType.Whitelist)
                    .build();
    static DynamicListSetting<ParticleType<?>> particles =
            new DynamicListSetting<>(ParticleControl.class, "Particles", ParticleControl::getParticleList).value(
                    new ParticleType[]{ParticleTypes.BLOCK_MARKER}).build();

    public ParticleControl() {
        super("particle_control", "Particle Control", "Lets you particles from rendering.", Category.Rendering);
    }

    public static boolean disabled(ParticleType<?> particle) {
        var inList = particles.value().contains(particle);
        return filterType.value() == Util.FilterType.Blacklist == inList;
    }

    static SimpleList<ParticleType<?>> getParticleList(DynamicListSetting<ParticleType<?>> setting) {
        return new SimpleList<>(setting, Registry.PARTICLE_TYPE) {

            @Override
            public String getDisplay(ParticleType<?> value) {
                return Objects.requireNonNull(registry.getId(value)).toString();
            }
        };
    }
}
