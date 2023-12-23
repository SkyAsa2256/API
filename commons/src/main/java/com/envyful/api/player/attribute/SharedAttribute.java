package com.envyful.api.player.attribute;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.data.TableData;
import com.envyful.api.player.save.SaveManager;
import com.envyful.api.player.save.attribute.DataDirectory;
import com.envyful.api.player.save.impl.JsonSaveManager;
import com.envyful.api.player.save.impl.SQLSaveManager;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * Abstract implementation of the attribute class storing the manager
 * and id of the attribute
 *
 * @param <A> The attribute ID type
 * @param <B> The manager instance for the attribute
 */
public abstract class SharedAttribute<A, B> extends ManagedAttribute<A, B> {

    protected final transient PlayerManager<?, ?> playerManager;

    protected A id;
    protected long lastSave = -1L;
    protected Database database;

    protected SharedAttribute(B manager, PlayerManager<?, ?> playerManager) {
        super(manager);

        this.playerManager = playerManager;
    }

    @Override
    public boolean isShared() {
        return true;
    }

    @Override
    public void load(A id) {
        this.id = id;

        this.load();
    }

    @Override
    public void save(A id) {
        this.id = id;

        if (!this.shouldSave()) {
            return;
        }

        this.lastSave = System.currentTimeMillis();

        this.save();
    }

    @Override
    public boolean shouldSave() {
        if (this.lastSave == -1L) {
            return true;
        }

        return super.shouldSave() && (System.currentTimeMillis() - this.lastSave) >= TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public CompletableFuture<A> getId() {
        return CompletableFuture.completedFuture(this.id);
    }

    @Override
    public void deleteAll() {
        this.findTargets().forEach(s -> {
            if (this.getDatabase() == null) {
                this.playerManager.getSaveManager().delete(s);
            } else {
                this.playerManager.getSaveManager().delete(this.getDatabase(), s);
            }
        });
    }

    protected Database getDatabase() {
        return this.database;
    }

    protected List<String> findTargets() {
        List<String> targets = Lists.newArrayList();
        SaveManager<?> saveManager = this.playerManager.getSaveManager();

        if (saveManager instanceof SQLSaveManager) {
            TableData tableData = this.getClass().getAnnotation(TableData.class);

            if (tableData == null) {
                UtilLogger.logger().ifPresent(logger -> logger.error("No table data annotation found for {}", this.getClass().getName()));
                return Collections.emptyList();
            }

            targets.addAll(Lists.newArrayList(tableData.value()));
        } else if (saveManager instanceof JsonSaveManager) {
            DataDirectory dataDirectory = this.getClass().getAnnotation(DataDirectory.class);

            if (dataDirectory == null) {
                UtilLogger.logger().ifPresent(logger -> logger.error("No data directory annotation found for {}", this.getClass().getName()));
                return Collections.emptyList();
            }

            targets.addAll(Lists.newArrayList(dataDirectory.value()));
        } else {
            UtilLogger.logger().ifPresent(logger -> logger.error("Unknown save manager type {}", saveManager.getClass().getName()));
            return Collections.emptyList();
        }

        return targets;
    }
}
