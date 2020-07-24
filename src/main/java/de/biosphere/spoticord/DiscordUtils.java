package de.biosphere.spoticord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

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

}