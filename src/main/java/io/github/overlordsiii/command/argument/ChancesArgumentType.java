package io.github.overlordsiii.command.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.overlordsiii.config.Chances;

import net.minecraft.text.Text;

public class ChancesArgumentType implements ArgumentType<Chances> {

	public static ChancesArgumentType chances() {
		return new ChancesArgumentType();
	}

	public static <S> Chances getChance(String name, CommandContext<S> context) {
		return context.getArgument(name, Chances.class);
	}

	@Override
	public Chances parse(StringReader reader) throws CommandSyntaxException {
		int argBeginning = reader.getCursor();
		if (!reader.canRead()) {
			reader.skip();
		}

		while (reader.canRead() && reader.peek() != ' ') {
			reader.skip();
		}

		String chancesString = reader.getString().substring(argBeginning, reader.getCursor());
		try {
			return Chances.valueOf(chancesString);
		} catch (Exception e) {
			throw new SimpleCommandExceptionType(Text.literal(e.getMessage())).createWithContext(reader);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return ArgumentType.super.listSuggestions(context, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return new ArrayList<>(Arrays.asList("VANILLA_FRIENDLY", "SLIGHTLY_OP", "VERY_OP"));
	}
}
