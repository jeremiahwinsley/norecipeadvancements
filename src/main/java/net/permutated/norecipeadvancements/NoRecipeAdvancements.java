package net.permutated.norecipeadvancements;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.permutated.norecipeadvancements.filters.MissingAdvancementFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NoRecipeAdvancements.MOD_ID)
public class NoRecipeAdvancements
{
    public static final String MOD_ID = "norecipeadvancements";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    @SuppressWarnings("java:S1118")
    public NoRecipeAdvancements() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        Logger rootLogger = LogManager.getRootLogger();
        if (rootLogger instanceof org.apache.logging.log4j.core.Logger) {
            ((org.apache.logging.log4j.core.Logger) rootLogger).addFilter(new MissingAdvancementFilter());
        } else {
            LOGGER.error("Registration failed with unexpected class: {}", rootLogger.getClass());
        }
    }
}
