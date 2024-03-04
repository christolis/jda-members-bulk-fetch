package com.christolis;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * A listener implementation to handle JDA events.
 */
public class ReadyListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);

    /**
     * Executes tasks when JDA is ready.
     *
     * @param event the GenericEvent triggering this method
     */
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            logger.info("JDA is now ready! Executing tasks...");
            App.getInstance().getTasks().forEach(task -> CompletableFuture.runAsync(task::execute));
        }
    }
}
