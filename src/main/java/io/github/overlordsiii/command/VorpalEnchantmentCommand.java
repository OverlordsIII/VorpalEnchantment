package io.github.overlordsiii.command;

import com.google.common.base.Throwables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.overlordsiii.VorpalEnchantment;
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
					.executes(context -> executeToggle(context, "axeWielding", "commands.axeWielding.toggle")))
				.then(literal("worksWithLooting")
					.executes(context -> executeToggle(context, "worksWithLooting", "commands.worksWithLooting.toggle"))))
			.then(literal("setChance")
				.then(argument("chanceType", ChancesArgumentType.chances())
					.suggests(new ChancesSuggestionProvider())
						.executes(context -> executeSetChance(context, ChancesArgumentType.getChance("chanceType", context), "commands.setChance"))))
			.then(literal("help")
				.executes(VorpalEnchantmentCommand::executeHelp)));
	}

	private static int executeHelp(CommandContext<ServerCommandSource> ctx) {
		ctx.getSource().sendFeedback(Text.translatable("commands.help.axes", VorpalEnchantmentMod.CONFIG.axeWielding).formatted(Formatting.GREEN), false);
		ctx.getSource().sendFeedback(Text.translatable("commands.help.looting", VorpalEnchantmentMod.CONFIG.worksWithLooting).formatted(Formatting.GREEN), false);
		ctx.getSource().sendFeedback(Text.translatable("commands.help.chance").formatted(Formatting.GREEN), false);
		ctx.getSource().sendFeedback(Text.translatable("commands.help.chance.very_op").formatted(Formatting.GREEN), false);
		ctx.getSource().sendFeedback(Text.translatable("commands.help.chance.slightly_op").formatted(Formatting.GREEN), false);
		ctx.getSource().sendFeedback(Text.translatable("commands.help.chance.vanilla_friendly").formatted(Formatting.GREEN), false);
		ctx.getSource().sendFeedback(Text.translatable("commands.help.chance.current", VorpalEnchantmentMod.CONFIG.chances.toString()).formatted(Formatting.GREEN), false);

		return 1;
	}

	private static int executeSetChance(CommandContext<ServerCommandSource> ctx, Chances chance, String displayText) {
		ctx.getSource().sendFeedback(
			Text.translatable(displayText, chance.toString()).formatted(Formatting.YELLOW)
				.styled(style -> style.withClickEvent(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND
						, "/vorpal-enchantment setChacne " + chance))
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT
						, Text.translatable("commands.head.drop.chance", chance)))), true);

		VorpalEnchantmentMod.CONFIG.chances = chance;
		VorpalEnchantmentMod.CONFIG_MANAGER.save();
		Text opText = Text.translatable("commands.head.drop.op", chance, (ctx.getSource().getPlayer() == null ? "Server command terminal" : ctx.getSource().getPlayer().getName().getString())).formatted(Formatting.LIGHT_PURPLE);
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
		ctx.getSource().sendFeedback(
			Text.translatable(displayText, onOrOff).formatted(Formatting.YELLOW)
				.styled(style -> style.withClickEvent(
					new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND
						, "/vorpal-enchantment toggle " + literal))
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT
						, Text.translatable("commands.suggest.toggle", literal)))), true);
		VorpalEnchantmentMod.CONFIG_MANAGER.save();
		Text opText = Text.translatable("commands.toggle.op", literal, onOrOff, (ctx.getSource().getPlayer() == null ? "Server command terminal" : ctx.getSource().getPlayer().getName().getString())).formatted(Formatting.LIGHT_PURPLE);
		sendToOps(ctx, opText);
		return 1;
	}

	private static String onOrOff(boolean bl) {
		return bl ? "on" : "off";
	}

	private static void logError(CommandContext<ServerCommandSource> ctx, Exception e) {
		if (ctx.getSource().getPlayer() != null) {
			ctx.getSource().getPlayer().sendMessage(Text.translatable("commands.error", Throwables.getRootCause(e)), false);
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
