package com.envyful.api.forge.command.completion.number;

import com.envyful.api.command.injector.TabCompleter;
import net.minecraft.server.level.ServerPlayer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class IntegerTabCompleter implements TabCompleter<ServerPlayer> {

    @Override
    public List<String> getCompletions(ServerPlayer sender, String[] currentData, Annotation... completionData) {
        if (completionData.length != 1 || !(completionData[0] instanceof IntCompletionData)) {
            List<String> completions = new ArrayList<>();

            for (int i = 1; i <= 100; i++) {
                completions.add(i + "");
            }

            return completions;
        }

        List<String> completions = new ArrayList<>();
        IntCompletionData data = (IntCompletionData) completionData[0];

        for (int i = data.min(); i <= data.max(); i++) {
            completions.add(i + "");
        }

        return completions;
    }
}
