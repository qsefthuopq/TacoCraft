package io.github.franiscoder.tacocraft.block.inventory;


import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@code SidedInventory} implementation with only default methods + an item list getter.
 *
 * <h2>Reading and writing to tags</h2>
 * Use  and
 * on {@linkplain #getItems() the item list}.
 * <p>
 * License: <a href="https://creativecommons.org/publicdomain/zero/1.0/">CC0</a>
 *
 * @author Juuz
 */
@FunctionalInterface
public interface FurnaceInventory extends SidedInventory {
    List<InventoryListener> listeners = new ArrayList<>();

    /**
     * Creates an inventory from the item list.
     *
     * @param items the item list
     * @return a new inventory
     */
    static FurnaceInventory of(DefaultedList<ItemStack> items) {
        return () -> items;
    }

    // Creation

    /**
     * Creates a new inventory with the size.
     *
     * @param size the inventory size
     * @return a new inventory
     */
    static FurnaceInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    /**
     * Gets the item list of this inventory.
     * Must return the same instance every time it's called.
     *
     * @return the item list
     */
    DefaultedList<ItemStack> getItems();

    // SidedInventory

    /**
     * Gets the available slots to automation on the side.
     *
     * <p>The default implementation returns an array of all slots.
     *
     * @param side the side
     * @return the available slots
     */
    @Override
    default int[] getInvAvailableSlots(Direction side) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }

        return result;
    }

    /**
     * Returns true if the stack can be inserted in the slot at the side.
     *
     * <p>The default implementation returns true.
     *
     * @param slot  the slot
     * @param stack the stack
     * @param side  the side
     * @return true if the stack can be inserted
     */
    @Override
    default boolean canInsertInvStack(int slot, ItemStack stack, Direction side) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
    }

    /**
     * Returns true if the stack can be extracted from the slot at the side.
     *
     * <p>The default implementation returns true.
     *
     * @param slot  the slot
     * @param stack the stack
     * @param side  the side
     * @return true if the stack can be extracted
     */
    @Override
    default boolean canExtractInvStack(int slot, ItemStack stack, Direction side) {
        return true;
    }

    // Inventory

    /**
     * Returns the inventory size.
     *
     * <p>The default implementation returns the size of {@link #getItems()}.
     *
     * @return the inventory size
     */
    @Override
    default int getInvSize() {
        return getItems().size();
    }

    /**
     * @return true if this inventory has only empty stacks, false otherwise
     */
    @Override
    default boolean isInvEmpty() {
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the item in the slot.
     *
     * @param slot the slot
     * @return the item in the slot
     */
    @Override
    default ItemStack getInvStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * Takes a stack of the size from the slot.
     *
     * <p>(default implementation) If there are less items in the slot than what are requested,
     * takes all items in that slot.
     *
     * @param slot  the slot
     * @param count the item count
     * @return a stack
     */
    @Override
    default ItemStack takeInvStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }

        return result;
    }

    /**
     * Removes the current stack in the {@code slot} and returns it.
     *
     * <p>The default implementation uses
     *
     * @param slot the slot
     * @return the removed stack
     */
    @Override
    default ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Replaces the current stack in the {@code slot} with the provided stack.
     *
     * <p>If the stack is too big for this inventory ({@link Inventory#getInvMaxStackAmount()}),
     * it gets resized to this inventory's maximum amount.
     *
     * @param slot  the slot
     * @param stack the stack
     */
    @Override
    default void setInvStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getInvMaxStackAmount()) {
            stack.setCount(getInvMaxStackAmount());
        }
    }

    /**
     * Clears {@linkplain #getItems() the item list}}.
     */
    @Override
    default void clear() {
        getItems().clear();
    }

    @Override
    default void markDirty() {
    }

    @Override
    default boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    default void addListener(InventoryListener listener) {
        this.listeners.add(listener);
    }

    @Override
    default boolean isValidInvStack(int slot, ItemStack stack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
    }
}