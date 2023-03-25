package io.github.overlordsiii.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.passive.HorseEntity;

@Mixin(HorseEntity.class)
public interface HorseEntityInvoker {
	@Invoker("getHorseVariant")
	int callGetVariant();
}
