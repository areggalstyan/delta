package com.aregcraft.delta.api.entity;

import com.aregcraft.delta.api.*;
import com.aregcraft.delta.api.PersistentDataWrapper;
import com.aregcraft.delta.api.item.ItemWrapper;
import com.aregcraft.delta.api.util.CollectionMaps;
import com.aregcraft.delta.api.util.Compatibility;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class EntityBuilder {
    private final Map<Attribute, Collection<AttributeModifier>> attributeModifiers = new HashMap<>();
    private final Map<String, Object> persistentData = new HashMap<>();
    private EntityType type;
    private String name = "";
    private double health;
    private boolean nameVisible;
    private boolean gravity = true;
    private boolean ai = true;
    private boolean invisible;
    private boolean glowing;
    private boolean canPickupItems;
    private boolean invulnerable;
    private boolean visualFire;
    private boolean adult = true;
    private ItemWrapper helmet;
    private ItemWrapper chestplate;
    private ItemWrapper leggings;
    private ItemWrapper boots;
    private ItemWrapper itemInMainHand;
    private ItemWrapper itemInOffHand;
    private transient FormattingContext nameFormattingContext = FormattingContext.DEFAULT;

    private EntityBuilder() {
    }

    public static EntityBuilder create() {
        return new EntityBuilder();
    }

    public static EntityBuilder createArmorStand() {
        return new EntityBuilder()
                .type(EntityType.ARMOR_STAND)
                .nameVisible(false)
                .gravity(false)
                .ai(false)
                .invisible(true)
                .invulnerable(true)
                .persistentData(NoninteractiveListener.NONINTERACTIVE_KEY, true);
    }

    public EntityBuilder attributeModifier(Attribute attribute, AttributeModifier modifier) {
        attributeModifiers.putIfAbsent(attribute, new ArrayList<>());
        attributeModifiers.get(attribute).add(modifier);
        return this;
    }

    public EntityBuilder persistentData(String key, Object value) {
        persistentData.put(key, value);
        return this;
    }

    public EntityBuilder type(EntityType type) {
        this.type = type;
        return this;
    }

    public EntityBuilder name(String name) {
        this.name = name;
        return this;
    }

    public EntityBuilder health(double health) {
        this.health = health;
        return this;
    }

    public EntityBuilder nameVisible(boolean nameVisible) {
        this.nameVisible = nameVisible;
        return this;
    }

    public EntityBuilder gravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public EntityBuilder ai(boolean ai) {
        this.ai = ai;
        return this;
    }

    public EntityBuilder invisible(boolean invisible) {
        this.invisible = invisible;
        return this;
    }

    public EntityBuilder glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public EntityBuilder canPickupItems(boolean canPickupItems) {
        this.canPickupItems = canPickupItems;
        return this;
    }

    public EntityBuilder invulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
        return this;
    }

    public EntityBuilder visualFire(boolean visualFire) {
        this.visualFire = visualFire;
        return this;
    }

    public EntityBuilder adult(boolean adult) {
        this.adult = adult;
        return this;
    }

    public EntityBuilder helmet(ItemWrapper helmet) {
        this.helmet = helmet;
        return this;
    }

    public EntityBuilder chestplate(ItemWrapper chestplate) {
        this.chestplate = chestplate;
        return this;
    }

    public EntityBuilder leggings(ItemWrapper leggings) {
        this.leggings = leggings;
        return this;
    }

    public EntityBuilder boots(ItemWrapper boots) {
        this.boots = boots;
        return this;
    }

    public EntityBuilder itemInMainHand(ItemWrapper itemInMainHand) {
        this.itemInMainHand = itemInMainHand;
        return this;
    }

    public EntityBuilder itemInOffHand(ItemWrapper itemInOffHand) {
        this.itemInOffHand = itemInOffHand;
        return this;
    }

    public EntityBuilder nameFormattingContext(FormattingContext nameFormattingContext) {
        this.nameFormattingContext = nameFormattingContext;
        return this;
    }

    public <T extends LivingEntity> T build(Location location, DeltaPlugin plugin) {
        return build(Entities.<T>spawnEntity(type, location), plugin);
    }

    public <T extends LivingEntity> T build(Location location) {
        return build(Entities.<T>spawnEntity(type, location));
    }

    public <T extends LivingEntity> T build(T entity, DeltaPlugin plugin) {
        PersistentDataWrapper.wrap(plugin, entity).setAll(persistentData);
        return build(entity);
    }

    public <T extends LivingEntity> T build(T entity) {
        CollectionMaps.forEach(attributeModifiers, (attribute, modifier) ->
                Objects.requireNonNull(entity.getAttribute(attribute)).addModifier(modifier));
        entity.setHealth(health > 0
                ? health
                : Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        entity.setCustomName(nameFormattingContext.format(name));
        entity.setCustomNameVisible(nameVisible);
        entity.setGravity(gravity);
        entity.setAI(ai);
        entity.setInvisible(invisible);
        entity.setGlowing(glowing);
        entity.setCanPickupItems(canPickupItems);
        entity.setInvulnerable(invulnerable);
        Compatibility.setEntityVisualFire(entity, visualFire);
        if (entity instanceof Ageable ageable) {
            if (adult) {
                ageable.setAdult();
            } else {
                ageable.setBaby();
            }
        }
        var equipment = EquipmentWrapper.wrap(entity);
        equipment.setHelmet(helmet);
        equipment.setChestplate(chestplate);
        equipment.setLeggings(leggings);
        equipment.setBoots(boots);
        equipment.setItemInMainHand(itemInMainHand);
        equipment.setItemInOffHand(itemInOffHand);
        return entity;
    }
}
