package com.aregcraft.delta.api;

import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AttributeModifierBuilder {
    private final Modifiable modifiable;
    private Attribute attribute;
    private UUID uuid = UUID.randomUUID();
    private String name;
    private double amount;
    private AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
    private EquipmentSlot slot;

    public static AttributeModifierBuilder forItem(ItemWrapper item) {
        return new AttributeModifierBuilder(new ItemWrapperModifiable(item));
    }

    public static AttributeModifierBuilder forAttributable(Attributable attributable) {
        return new AttributeModifierBuilder(new AttributableModifiable(attributable));
    }

    private AttributeModifierBuilder(Modifiable modifiable) {
        this.modifiable = modifiable;
    }

    public AttributeModifierBuilder attribute(Attribute attribute) {
        this.attribute = attribute;
        name = Optional.ofNullable(name).orElseGet(attribute::name);
        return this;
    }

    public AttributeModifierBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public AttributeModifierBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AttributeModifierBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public AttributeModifierBuilder operation(AttributeModifier.Operation operation) {
        this.operation = operation;
        return this;
    }

    public AttributeModifierBuilder slot(EquipmentSlot slot) {
        this.slot = slot;
        return this;
    }

    public void add() {
        modifiable.addAttributeModifier(attribute, new AttributeModifier(uuid, name, amount, operation, slot));
    }

    public void remove() {
        modifiable.removeAttributeModifier(attribute, new AttributeModifier(uuid, name, amount, operation, slot));
    }

    private interface Modifiable {
        void addAttributeModifier(Attribute attribute, AttributeModifier modifier);

        void removeAttributeModifier(Attribute attribute, AttributeModifier modifier);
    }

    private static class AttributableModifiable implements Modifiable {
        private final Attributable attributable;

        private AttributableModifiable(Attributable attributable) {
            this.attributable = attributable;
        }

        @Override
        public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
            Objects.requireNonNull(attributable.getAttribute(attribute)).addModifier(modifier);
        }

        @Override
        public void removeAttributeModifier(Attribute attribute, AttributeModifier modifier) {
            Objects.requireNonNull(attributable.getAttribute(attribute)).removeModifier(modifier);
        }
    }

    private static class ItemWrapperModifiable implements Modifiable {
        private final ItemWrapper item;

        private ItemWrapperModifiable(ItemWrapper item) {
            this.item = item;
        }

        @Override
        public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
            item.addAttributeModifier(attribute, modifier);
        }

        @Override
        public void removeAttributeModifier(Attribute attribute, AttributeModifier modifier) {
            item.removeAttributeModifier(attribute, modifier);
        }
    }
}
