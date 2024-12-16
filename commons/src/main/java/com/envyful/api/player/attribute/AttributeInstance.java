package com.envyful.api.player.attribute;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.player.Attribute;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.CompletableFuture;

public class AttributeInstance<A extends Attribute<B>, B> {

    private A attribute;
    private CompletableFuture<A> loadingAttribute;

    public AttributeInstance(A attribute) {
        this.attribute = attribute;
        this.loadingAttribute = null;
    }

    public AttributeInstance(CompletableFuture<A> loadingAttribute) {
        this.attribute = null;
        this.loadingAttribute = loadingAttribute.whenComplete((a, throwable) -> {
            if (throwable != null) {
                UtilLogger.logger().ifPresent(logger -> logger.error("Failed to load attribute", throwable));
            } else {
                this.attribute = a;
                this.loadingAttribute = null;
            }
        });
    }

    public CompletableFuture<A> getAttribute() {
        return this.loadingAttribute == null ? CompletableFuture.completedFuture(this.attribute) : this.loadingAttribute;
    }

    @Nullable
    public A getAttributeNow() {
        return this.attribute;
    }
}
