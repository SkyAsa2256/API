package com.envyful.api.discord.component;

import com.envyful.api.discord.listener.SubscribeEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 *
 * Static factory class for creating {@link Modal}s with ease
 * <br>
 * This allows you to give a modal an id, title, components, and interaction handler
 * rather than having to explicitly listen to the {@link ModalInteractionEvent} and check the id
 *
 */
public class ModalFactory {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static final Map<String, Builder> REGISTERED_MODALS = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onModalInteract(ModalInteractionEvent event) {
        Builder builder = REGISTERED_MODALS.get(event.getModalId().toLowerCase(Locale.ROOT));

        if (builder == null) {
            return;
        }

        builder.interactionHandler.accept(event);
    }

    public static Builder modal() {
        return new Builder();
    }

    public static class Builder {

        protected String id;
        protected String title;
        protected final List<ModalTopLevelComponent> components = new ArrayList<>(5);
        protected Consumer<ModalInteractionEvent> interactionHandler;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder row(ModalTopLevelComponent... components) {
            this.components.addAll(List.of(components));
            return this;
        }

        public Builder rows(ModalTopLevelComponent... rows) {
            this.components.addAll(List.of(rows));
            return this;
        }

        public Builder handler(Consumer<ModalInteractionEvent> interactionHandler) {
            this.interactionHandler = interactionHandler;
            return this;
        }

        public Modal build(JDA jda) {
            if (!REGISTERED.get()) {
                REGISTERED.set(true);
                jda.addEventListener(ModalFactory.class);
            }

            REGISTERED_MODALS.put(this.id.toLowerCase(Locale.ROOT), this);
            var builder = Modal.create(this.id, this.title);

            for (var component : this.components) {
                builder.addComponents(component);
            }

            return builder.build();
        }
    }
}
