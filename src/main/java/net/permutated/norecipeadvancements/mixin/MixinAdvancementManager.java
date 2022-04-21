package net.permutated.norecipeadvancements.mixin;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.permutated.norecipeadvancements.NoRecipeAdvancements;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ServerAdvancementManager.class)
public abstract class MixinAdvancementManager {

    @Final
    @Shadow
    private static Logger LOGGER;

    @Shadow
    private AdvancementList advancements;

    @Final
    @Shadow
    private PredicateManager predicateManager;

    @Overwrite
    protected void apply(Map<ResourceLocation, JsonElement> datapacks, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
        AtomicInteger recipeAdvancements = new AtomicInteger();
        datapacks.forEach((location, jsonElement) -> {
            try {
                JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonElement, "advancement");
                Advancement.Builder advancement$builder = Advancement.Builder.fromJson(jsonobject, new DeserializationContext(location, this.predicateManager));
                if (advancement$builder == null) {
                    LOGGER.debug("Skipping loading advancement {} as it's conditions were not met", location);
                    return;
                }

                // skip loading recipe advancements
                if (advancement$builder.getCriteria().containsKey("has_the_recipe")) {
                    recipeAdvancements.incrementAndGet();
                    return;
                }

                map.put(location, advancement$builder);
            } catch (Exception exception) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", location, exception.getMessage());
            }

        });
        AdvancementList advancementlist = new AdvancementList();
        advancementlist.add(map);

        NoRecipeAdvancements.LOGGER.info("Skipped loading {} recipe advancements.", recipeAdvancements.get());

        for(Advancement advancement : advancementlist.getRoots()) {
            if (advancement.getDisplay() != null) {
                TreeNodePosition.run(advancement);
            }
        }

        this.advancements = advancementlist;
    }
}
