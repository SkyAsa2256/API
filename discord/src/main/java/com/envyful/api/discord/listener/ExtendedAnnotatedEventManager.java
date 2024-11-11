package com.envyful.api.discord.listener;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.ClassWalker;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * This version of JDA's AnnotatedEventManager allows for the use of static methods
 * and scans the class hierarchy for annotated methods too
 * which means you don't need to annotate the methods in the class that you're registering as a listener
 * as it will search the parent classes too
 *
 */
public class ExtendedAnnotatedEventManager implements IEventManager {

    protected final Set<Object> listeners = ConcurrentHashMap.newKeySet();
    protected final Map<Class<?>, Map<Object, List<AnnotatedEventListener>>> methods = new ConcurrentHashMap<>();

    @Override
    public void register(@NonNull Object listener) {
        if (this.listeners.add(listener)) {
            this.updateMethods();
        }
    }

    @Override
    public void unregister(@NonNull Object listener) {
        if (this.listeners.remove(listener)) {
            this.updateMethods();
        }
    }

    @NonNull
    @Override
    public List<Object> getRegisteredListeners() {
        return List.copyOf(this.listeners);
    }

    @Override
    public void handle(@NonNull GenericEvent event) {
        for (var eventClass : ClassWalker.walk(event.getClass())) {
            Map<Object, List<AnnotatedEventListener>> listeners = this.methods.getOrDefault(eventClass, Map.of());

            for (Map.Entry<Object, List<AnnotatedEventListener>> entry : listeners.entrySet()) {
                for (AnnotatedEventListener method : entry.getValue()) {
                    if (event instanceof GenericInteractionCreateEvent) {
                        if (!method.receiveAcknowledged && ((GenericInteractionCreateEvent)event).isAcknowledged()) {
                            return;
                        }
                    }

                    try {
                        method.method.setAccessible(true);
                        method.method.invoke(entry.getKey(), event);
                    } catch (IllegalAccessException | InvocationTargetException e1) {
                        JDAImpl.LOG.error("Couldn't access annotated EventListener method", e1);
                    } catch (Throwable throwable) {
                        JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
                        if (throwable instanceof Error)
                            throw (Error)throwable;
                    }
                }
            }
        }
    }

    private void updateMethods() {
        this.methods.clear();

        for (Object listener : this.listeners) {
            boolean isClass = listener instanceof Class;
            Class<?> c = isClass ? (Class) listener : listener.getClass();

            for (Method m : c.getMethods()) {
                if (!m.isAnnotationPresent(SubscribeEvent.class) || (isClass && !Modifier.isStatic(m.getModifiers()))) {
                    continue;
                }

                Class<?>[] parameterTypes = m.getParameterTypes();
                if (parameterTypes.length != 1 || !GenericEvent.class.isAssignableFrom(parameterTypes[0])) {
                    continue;
                }

                Class<?> eventClass = parameterTypes[0];
                boolean receiveAcknowledged = m.getAnnotation(SubscribeEvent.class).receiveAcknowledged();

                this.methods.computeIfAbsent(eventClass, ___ -> new ConcurrentHashMap<>())
                        .computeIfAbsent(listener, ___ -> new CopyOnWriteArrayList<>())
                        .add(new AnnotatedEventListener(m, receiveAcknowledged));
            }
        }
    }

    private static class AnnotatedEventListener {

        protected final Method method;
        protected final boolean receiveAcknowledged;

        private AnnotatedEventListener(Method method, boolean receiveAcknowledged) {
            this.method = method;
            this.receiveAcknowledged = receiveAcknowledged;
        }

        public Method getMethod() {
            return this.method;
        }

        public boolean isReceiveAcknowledged() {
            return this.receiveAcknowledged;
        }
    }
}
