package com.aregcraft.delta.api.entity;

import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class EquipmentWrapper {
    private final EntityEquipment equipment;

    private EquipmentWrapper(EntityEquipment equipment) {
        this.equipment = equipment;
    }

    public static EquipmentWrapper wrap(LivingEntity entity) {
        return wrap(entity.getEquipment());
    }

    public static EquipmentWrapper wrap(EntityEquipment equipment) {
        return new EquipmentWrapper(equipment);
    }

    public ItemWrapper getItem(EquipmentSlot slot) {
        return ItemWrapper.wrap(equipment.getItem(slot));
    }

    public void setItem(EquipmentSlot slot, ItemWrapper item) {
        equipment.setItem(slot, unwrapOrNull(item));
    }

    public ItemWrapper getHelmet() {
        return ItemWrapper.wrap(equipment.getHelmet());
    }

    public void setHelmet(ItemWrapper helmet) {
        equipment.setHelmet(unwrapOrNull(helmet));
    }

    public ItemWrapper getChestplate() {
        return ItemWrapper.wrap(equipment.getChestplate());
    }

    public void setChestplate(ItemWrapper chestplate) {
        equipment.setChestplate(unwrapOrNull(chestplate));
    }

    public ItemWrapper getLeggings() {
        return ItemWrapper.wrap(equipment.getLeggings());
    }

    public void setLeggings(ItemWrapper leggings) {
        equipment.setLeggings(unwrapOrNull(leggings));
    }

    public ItemWrapper getBoots() {
        return ItemWrapper.wrap(equipment.getBoots());
    }

    public void setBoots(ItemWrapper boots) {
        equipment.setBoots(unwrapOrNull(boots));
    }

    public ItemWrapper getItemInMainHand() {
        return ItemWrapper.wrap(equipment.getItemInMainHand());
    }

    public void setItemInMainHand(ItemWrapper itemInMainHand) {
        equipment.setItemInMainHand(unwrapOrNull(itemInMainHand));
    }

    public ItemWrapper getItemInOffHand() {
        return ItemWrapper.wrap(equipment.getItemInOffHand());
    }

    public void setItemInOffHand(ItemWrapper itemInOffHand) {
        equipment.setItemInOffHand(unwrapOrNull(itemInOffHand));
    }

    private ItemStack unwrapOrNull(ItemWrapper item) {
        return Optional.ofNullable(item).map(ItemWrapper::unwrap).orElse(null);
    }
}
