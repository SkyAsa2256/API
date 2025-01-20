package com.envyful.api.platform;

import com.envyful.api.player.attribute.AttributeHolder;
import com.envyful.api.player.attribute.AttributeTrigger;
import com.envyful.api.player.attribute.trigger.ClearAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SaveAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SetAttributeTrigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class StandardPlatformHandler<A> implements PlatformHandler<A> {

    protected final Map<Class<?>, AttributeTrigger<?>> triggersByEvent = new HashMap<>();
    protected final Map<Class<?>, Supplier<AttributeTrigger<?>>> registeredTriggers = new HashMap<>();

    protected StandardPlatformHandler() {
        this.registerAttributeTrigger(SetAttributeTrigger.class, SetAttributeTrigger::new);
        this.registerAttributeTrigger(ClearAttributeTrigger.class, ClearAttributeTrigger::new);
        this.registerAttributeTrigger(SaveAttributeTrigger.class, SaveAttributeTrigger::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends AttributeHolder, Y> AttributeTrigger<X> getAttributeTriggerInstance(Class<Y> eventClass, Class<? extends AttributeTrigger<X>> triggerClass, Function<Y, List<X>> converter) {
        var trigger = triggersByEvent.computeIfAbsent(eventClass, aClass -> {
            var supplier = this.registeredTriggers.get(triggerClass);

            if (supplier == null) {
                throw new IllegalArgumentException("No trigger registered for class: " + triggerClass.getName());
            }

            var instance = (AttributeTrigger<X>) supplier.get();

            this.registerListeners(eventClass, converter, instance);

            return instance;
        });

        return (AttributeTrigger<X>) trigger;
    }

    protected abstract <X extends AttributeHolder, Y> void registerListeners(Class<Y> eventClass, Function<Y, List<X>> converter, AttributeTrigger<X> trigger);

    @Override
    @SuppressWarnings("unchecked")
    public <Y extends AttributeTrigger<X>, X extends AttributeHolder> void registerAttributeTrigger(Class<Y> triggerClass, Supplier<Y> constructor) {
        this.registeredTriggers.put(triggerClass, (Supplier<AttributeTrigger<?>>) constructor);
    }
}
