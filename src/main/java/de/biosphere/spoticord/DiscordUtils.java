package de.biosphere.spoticord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.LinkedList;
import java.util.List;

public class DiscordUtils {

    public static Member getAddressedMember(final Message message) {
        if (message.getMentionedMembers().isEmpty()) {
            return message.getMember();
        }
        if (message.getMentionedMembers().get(0).getUser().isBot() && message.getMentionedMembers().size() > 1) {
            return message.getMentionedMembers().get(1);
        }
        return message.getMentionedMembers().get(0).getUser().isBot() ? message.getMember()
                : message.getMentionedMembers().get(0);
    }

    /**
     * @param message a {@link Message}
     * @param index   the index of a mentioned member without the bot-mention at the start
     * @return a {@link Member}, when not found null
     */
    public static Member getRequiredMember(final Message message, final int index) {
        final List<Member> mentionedMembers = new LinkedList<>(message.getMentionedMembers());
        if (mentionedMembers.isEmpty()) return null;
        if (index < 0) return null;
        final Member botMember = message.getGuild().getSelfMember();
        mentionedMembers.remove(botMember);
        if (mentionedMembers.size() <= index) return null;
        return mentionedMembers.get(index);
    }

}