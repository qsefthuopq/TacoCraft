package io.github.franiscoder.tacocraft;

import io.github.franiscoder.tacocraft.block.FurnaceBlock;
import io.github.franiscoder.tacocraft.client.gui.FurnaceGUI;
import io.github.franiscoder.tacocraft.init.ModBlocks;
import io.github.franiscoder.tacocraft.init.ModGen;
import io.github.franiscoder.tacocraft.init.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class TacoCraft implements ModInitializer {
    public static final String MODID = "tacocraft";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            id("item_group"),
            () -> new ItemStack(ModBlocks.FURNACE_BLOCK));
    private static final Identifier GRASS_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/grass");

    public static ScreenHandlerType<FurnaceGUI> Furnace;


    public static Identifier id(String string) {
        return new Identifier(MODID, string);
    }

    @Override
    public void onInitialize() {
        ModBlocks.registerBlocks();
        ModBlocks.registerBlockItems();
        ModBlocks.registerBlockEntities();
        ModItems.registerItems();
        ModGen.register();

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (GRASS_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(ModItems.CORN_SEED).build())
                        .withCondition(RandomChanceLootCondition.builder(0.125F).build());

                supplier.withPool(poolBuilder.build());
            }
        });
        Furnace = ScreenHandlerRegistry.registerExtended(FurnaceBlock.ID, (syncId, inventory, buf) -> new FurnaceGUI(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, buf.readBlockPos())));
    }
}
