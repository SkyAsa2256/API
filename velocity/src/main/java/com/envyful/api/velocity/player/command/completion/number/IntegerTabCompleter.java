package com.envyful.api.velocity.player.command.completion.number;

import com.envyful.api.command.injector.TabCompleter;
import com.google.common.collect.Lists;
import com.velocitypowered.api.proxy.Player;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 *
 * A tab completer for integer values
 *
 */
public class IntegerTabCompleter implements TabCompleter<Player> {

    @Override
    public List<String> getCompletions(Player sender, String[] currentData, Annotation... completionData) {
        if (completionData.length != 1 || !(completionData[0] instanceof IntCompletionData)) {
            List<String> completions = Lists.newArrayListWithCapacity(100);

            for (int i = 1; i <= 100; i++) {
                completions.add(String.valueOf(i));
            }

            return completions;
        }

        IntCompletionData data = (IntCompletionData) completionData[0];
        List<String> completions = Lists.newArrayListWithCapacity(data.max() - data.min());

        for (int i = data.min(); i <= data.max(); i++) {
            completions.add(String.valueOf(i));
        }

        return completions;
    }
}
