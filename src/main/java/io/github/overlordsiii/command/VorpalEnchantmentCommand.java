package io.github.overlordsiii.command;

import com.google.common.base.Throwables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.overlordsiii.VorpalEnchantmentMod;
import io.github.overlordsiii.command.argument.ChancesArgumentType;
import io.github.overlordsiii.command.suggestion.ChancesSuggestionProvider;
import io.github.overlordsiii.config.Chances;
import io.github.overlordsiii.config.VorpalConfig;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.lang.reflect.Field;

public class VorpalEnchantmentCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("vorpal-enchantment")
			.then(literal("toggle")
				.then(literal("axeWielding")
					.executes(context -> executeToggle(context, "axeWielding", "Axe wielding was toggled %s")))
				.then(literal("worksWithLooting")
					.executes(context -> executeToggle(context, "worksWithLooting", "worksWithLooting was toggled %s"))))
			.then(literal("setChance")
				.then(argument("chanceType", ChancesArgumentType.chances())
					.suggests(new ChancesSuggestionProvider())
						.executes(context -> executeSetChance(context, ChancesArgumentType.getChance("chanceType", context), "Head drop chance has now been set to %s")))));
	}

	private static int executeSetChance(CommandContext<ServerCommandSource> ctx, Chances chance, String displayText) {
		String text = String.format(displayText, chance.toString());
		ctx.getSource().sendFeedback(
			Text.literal(text).formatted(Formatting.YELLOW)
				.styled(style -> style.withClickEvent(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND
						, "/vorpal-enchantment setChacne " + chance))
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT
						, Text.literal("Set chance for head drop to " + chance)))), true);

		VorpalEnchantmentMod.CONFIG.chances = chance;
		VorpalEnchantmentMod.CONFIG_MANAGER.save();
		Text opText = Text.literal("Head drop chance has now been set to " + chance +  "\" by " + (ctx.getSource().getPlayer() == null ? "Server command terminal" : ctx.getSource().getPlayer().getName().getString())).formatted(Formatting.LIGHT_PURPLE);
		sendToOps(ctx, opText);

		return 1;
	}

	private static int executeToggle(CommandContext<ServerCommandSource> ctx, String literal, String displayText) {
		String onOrOff;
		try {
			Field field = VorpalConfig.class.getField(literal);
			boolean newValue = !field.getBoolean(VorpalEnchantmentMod.CONFIG);
			field.setBoolean(VorpalEnchantmentMod.CONFIG, newValue);
			onOrOff = onOrOff(newValue);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			logError(ctx, e);
			return -1;
		}
		//      System.out.println("onOrOff = " + onOrOff);
		String text = String.format(displayText, onOrOff);
		ctx.getSource().sendFeedback(
			Text.literal(text).formatted(Formatting.YELLOW)
				.styled(style -> style.withClickEvent(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND
						, "/vorpal-enchantment toggle " + literal))
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT
						, Text.literal("Toggle the " + literal + " option")))), true);
		VorpalEnchantmentMod.CONFIG_MANAGER.save();
		Text opText = Text.literal("The " + literal + " option has been toggled \"" + onOrOff + "\" by " + (ctx.getSource().getPlayer() == null ? "Server command terminal" : ctx.getSource().getPlayer().getName().getString())).formatted(Formatting.LIGHT_PURPLE);
		sendToOps(ctx, opText);
		return 1;
	}

	private static String onOrOff(boolean bl) {
		return bl ? "on" : "off";
	}

	private static void logError(CommandContext<ServerCommandSource> ctx, Exception e) {
		if (ctx.getSource().getPlayer() != null) {
			ctx.getSource().getPlayer().sendMessage(Text.literal("Exception Thrown! Exception: " + Throwables.getRootCause(e)), false);
		}
		e.printStackTrace();
	}

	private static void sendToOps(CommandContext<ServerCommandSource> ctx, Text text){
		ctx.getSource().getServer().getPlayerManager().getPlayerList().forEach((serverPlayerEntity -> {
			if (ctx.getSource().getServer().getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())){
				serverPlayerEntity.sendMessage(text, false);
			}
		}));
	}


}
