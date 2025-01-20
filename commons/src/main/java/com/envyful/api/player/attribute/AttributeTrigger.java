package com.envyful.api.player.attribute;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.attribute.data.AttributeData;
import com.envyful.api.player.attribute.trigger.ClearAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SaveAttributeTrigger;
import com.envyful.api.player.attribute.trigger.SetAttributeTrigger;

import java.util.List;
import java.util.function.Function;

public interface AttributeTrigger<T extends AttributeHolder> {

    void addAttribute(AttributeData<?, T> attribute);

    void trigger(T holder);

    static <A extends AttributeHolder, B> AttributeTrigger<A> singleSet(Class<B> eventClass, Function<B, A> converter) {
        return set(eventClass, b -> List.of(converter.apply(b)));
    }

    static <A extends AttributeHolder, B> AttributeTrigger<A> set(Class<B> eventClass, Function<B, List<A>> converter) {
        return PlatformProxy.getAttributeTriggerInstance(eventClass, clazz(SetAttributeTrigger.class), converter);
    }

    static <A extends AttributeHolder, B> AttributeTrigger<A> singleClear(Class<B> eventClass, Function<B, A> converter) {
        return clear(eventClass, b -> List.of(converter.apply(b)));
    }

    static <A extends AttributeHolder, B> AttributeTrigger<A> clear(Class<B> eventClass, Function<B, List<A>> converter) {
        return PlatformProxy.getAttributeTriggerInstance(eventClass, clazz(ClearAttributeTrigger.class), converter);
    }

    static <A extends AttributeHolder, B> AttributeTrigger<A> singleSave(Class<B> eventClass, Function<B, A> converter) {
        return save(eventClass, b -> List.of(converter.apply(b)));
    }

    static <A extends AttributeHolder, B> AttributeTrigger<A> save(Class<B> eventClass, Function<B, List<A>> converter) {
        return PlatformProxy.getAttributeTriggerInstance(eventClass, clazz(SaveAttributeTrigger.class), converter);
    }

    @SuppressWarnings("unchecked")
    private static <A extends AttributeHolder, B extends AttributeTrigger<A>> Class<B> clazz(Class<?> triggerClass) {
        return (Class<B>) triggerClass;
    }
}
