package io.github.overlordsiii.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "vorpal_enchantment")
@Config.Gui.Background("minecraft:textures/block/barrel_side.png")
public class VorpalConfig implements ConfigData {
	@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
	@ConfigEntry.Gui.Tooltip
	@Comment("Chooses the chances for head drop per enchantment level. VANILLA_FRIENDLY has values 5%, 10%, 15%. SLIGHTLY_OP has values 5%, 15%, 25%. VERY_OP has 25%, 50%, 75%")
	public Chances chances = Chances.SLIGHTLY_OP;

	@ConfigEntry.Gui.Tooltip
	@Comment("Allows for axes to be enchanted with this enchantment")
	public boolean axeWielding = true;

	@ConfigEntry.Gui.Tooltip
	@Comment("Allows Vorpal to be used with looting, fortune, and silk-touch")
	public boolean worksWithLooting = false;
}
