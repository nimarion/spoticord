package de.biosphere.spoticord.core;

import de.biosphere.spoticord.database.DataManager;
import net.dv8tion.jda.core.JDA;

public interface Spoticord {

    JDA getJDA();

    DataManager getDataManager();
}
