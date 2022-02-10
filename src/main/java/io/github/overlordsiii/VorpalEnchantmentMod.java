package io.github.overlordsiii;

import io.github.overlordsiii.config.VorpalConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class VorpalEnchantmentMod implements ModInitializer {

	public static VorpalConfig CONFIG;

	static {
		AutoConfig.register(VorpalConfig.class, JanksonConfigSerializer::new);
	}

	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		CONFIG = AutoConfig.getConfigHolder(VorpalConfig.class).getConfig();

		Registry.register(Registry.ENCHANTMENT, new Identifier("vorpal_enchantment", "vorpal_enchantment"), new VorpalEnchantment(Enchantment.Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

	}
}
