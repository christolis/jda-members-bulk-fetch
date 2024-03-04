package com.christolis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task to fetch and save members from a Discord guild.
 */
public class MembersTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(MembersTask.class);
    private static final String FILE_NAME = "members.txt";
    private static final int SAVE_MEMBER_TIMES = 3;
    private final JDA jda;

    /**
     * Constructs a new MembersTask with the specified JDA instance.
     *
     * @param jda the JDA instance to use
     */
    public MembersTask(JDA jda) {
        this.jda = jda;
    }

    /**
     * Executes the task to fetch and save members.
     */
    @Override
    public void execute() {
        logger.info("Fetching members for {} times...", SAVE_MEMBER_TIMES);
        fetchAndSaveMembers(SAVE_MEMBER_TIMES);
    }

    /**
     * Fetches and saves members from the first guild found in the JDA instance.
     *
     * @param times the amount of times to perform this operation
     */
    public void fetchAndSaveMembers(int times) {
        CompletableFuture.runAsync(() -> getFirstGuild(jda).ifPresentOrElse(
                guild -> guild.loadMembers()
                    .onSuccess(members -> IntStream.range(0, times)
                        .forEach(n -> this.saveMembers(members))),
                () -> logger.error("No guild found")));
    }

    /**
     * Retrieves the first guild from the provided JDA instance.
     *
     * @param jda the JDA instance
     * @return an Optional containing the first guild if found, otherwise empty
     */
    public Optional<Guild> getFirstGuild(JDA jda) {
        List<Guild> guilds = jda.getGuilds();

        if (guilds.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(guilds.getFirst());
    }

    /**
     * Saves the list of members to a file.
     *
     * @param members the list of members to save
     */
    public void saveMembers(List<Member> members) {
        int count = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Member member : members) {
                String memberId = member.getUser().getId();

                writer.write(memberId);
                writer.newLine();

                count++;
            }
        } catch (IOException e) {
            logger.error("Error saving members", e);
        }

        logger.info("Done saving {} members.", count);
    }
}
