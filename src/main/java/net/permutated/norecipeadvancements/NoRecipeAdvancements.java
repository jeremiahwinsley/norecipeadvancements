package net.permutated.norecipeadvancements;


import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import net.permutated.norecipeadvancements.filters.MissingAdvancementFilter;
import net.permutated.norecipeadvancements.filters.UnknownRecipeCategoryFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NoRecipeAdvancements.MOD_ID)
public class NoRecipeAdvancements {
    public static final String MOD_ID = "norecipeadvancements";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    @SuppressWarnings("java:S1118")
    public NoRecipeAdvancements() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
            () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));

        Logger rootLogger = LogManager.getRootLogger();
        if (rootLogger instanceof org.apache.logging.log4j.core.Logger logger) {
            logger.addFilter(new MissingAdvancementFilter());
            logger.addFilter(new UnknownRecipeCategoryFilter());
        } else {
            LOGGER.error("Registration failed with unexpected class: {}", rootLogger.getClass());
        }
    }
}
