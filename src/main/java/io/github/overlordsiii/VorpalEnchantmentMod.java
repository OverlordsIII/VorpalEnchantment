package io.github.overlordsiii;

import io.github.overlordsiii.command.VorpalEnchantmentCommand;
import io.github.overlordsiii.command.argument.ChancesArgumentType;
import io.github.overlordsiii.config.VorpalConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class VorpalEnchantmentMod implements ModInitializer {

	public static VorpalConfig CONFIG;
	public static ConfigManager<VorpalConfig> CONFIG_MANAGER;

	static {
		CONFIG_MANAGER = (ConfigManager<VorpalConfig>) AutoConfig.register(VorpalConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(VorpalConfig.class).getConfig();

	}

	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {

		CONFIG = AutoConfig.getConfigHolder(VorpalConfig.class).getConfig();

		Registry.register(Registry.ENCHANTMENT, new Identifier("vorpal_enchantment", "vorpal_enchantment"), new VorpalEnchantment(Enchantment.Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

		ArgumentTypeRegistry.registerArgumentType(new Identifier("vorpal-enchantment", "chances"), ChancesArgumentType.class, ConstantArgumentSerializer.of(ChancesArgumentType::chances));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			VorpalEnchantmentCommand.register(dispatcher);
		});
	}

}
