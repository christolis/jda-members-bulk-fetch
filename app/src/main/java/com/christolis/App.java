package com.christolis;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final App app = new App();
    private static final List<Task> tasks = new ArrayList<>();

    private App() {}

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException(
                    "The first argument should be the Discord Bot's token");
        }

        App.getInstance().run(args[0]);
    }

    /**
     * Retrieves the list of tasks.
     *
     * @return the list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Registers tasks with the provided JDA instance.
     *
     * @param jda the JDA instance to register tasks with
     */
    private void registerTasks(JDA jda) {
        tasks.add(new MembersTask(jda));
    }

    /**
     * Runs the application with the given Discord Bot token.
     *
     * @param token the token of the Discord Bot
     */
    public void run(String token) {

        try {
            JDA jda = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new ReadyListener())
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .build();

            registerTasks(jda);
        } catch (InvalidTokenException exception) {
            logger.error("Invalid token", exception);
        }
    }

    /**
     * Retrieves the singleton instance of the App class.
     *
     * @return the singleton instance of the App class
     */
    public static App getInstance() {
        return app;
    }
}
