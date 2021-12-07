package net.permutated.norecipeadvancements.mixin;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementTreeNode;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.permutated.norecipeadvancements.NoRecipeAdvancements;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(AdvancementManager.class)
public class MixinAdvancementManager {

    @Final
    @Shadow
    private static Logger LOGGER;

    @Shadow
    private AdvancementList advancements;

    @Final
    @Shadow
    private LootPredicateManager predicateManager;

    @Overwrite
    protected void apply(Map<ResourceLocation, JsonElement> datapacks, IResourceManager resourceManager, IProfiler profiler) {
        Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
        AtomicInteger recipeAdvancements = new AtomicInteger();
        datapacks.forEach((location, jsonElement) -> {
            try {
                JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonElement, "advancement");
                Advancement.Builder advancement$builder = Advancement.Builder.fromJson(jsonobject, new ConditionArrayParser(location, this.predicateManager));
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
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", location, jsonparseexception.getMessage());
            }

        });
        AdvancementList advancementlist = new AdvancementList();
        advancementlist.add(map);

        NoRecipeAdvancements.LOGGER.info("Skipped loading {} recipe advancements.", recipeAdvancements.get());

        for(Advancement advancement : advancementlist.getRoots()) {
            if (advancement.getDisplay() != null) {
                AdvancementTreeNode.run(advancement);
            }
        }

        this.advancements = advancementlist;
    }
}
