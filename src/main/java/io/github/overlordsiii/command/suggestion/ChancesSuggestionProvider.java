package io.github.overlordsiii.command.suggestion;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.overlordsiii.config.Chances;

import net.minecraft.server.command.ServerCommandSource;

public class ChancesSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		for (Chances value : Chances.values()) {
			builder.suggest(value.toString());
		}

		return builder.buildFuture();
	}
}
