package com.aregcraft.delta.api.item;

import com.aregcraft.delta.api.AttributeModifierBuilder;
import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.FormattingContext;
import com.aregcraft.delta.api.PersistentDataWrapper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class ItemWrapper {
    private final ItemStack stack;
    private ItemMeta meta;
    private FormattingContext nameFormattingContext = FormattingContext.DEFAULT;
    private FormattingContext loreFormattingContext = FormattingContext.DEFAULT;

    private ItemWrapper(ItemStack stack) {
        this.stack = stack;
        meta = stack.getItemMeta();
    }

    public static ItemWrapper wrap(ItemWrapper item) {
        return wrap(item.stack.clone()).createBuilder()
                .nameFormattingContext(item.nameFormattingContext)
                .loreFormattingContext(item.loreFormattingContext)
                .build();
    }

    public static ItemWrapper wrap(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return new ItemWrapper(stack);
    }

    public static ItemWrapper withMaterial(Material material) {
        return wrap(new ItemStack(material));
    }

    public static Builder builder() {
        return withMaterial(Material.AIR).createBuilder();
    }

    public Builder createBuilder() {
        return new Builder();
    }

    public void setFormattingContext(FormattingContext formattingContext) {
        setNameFormattingContext(formattingContext);
        setLoreFormattingContext(formattingContext);
    }

    public void setNameFormattingContext(FormattingContext nameFormattingContext) {
        this.nameFormattingContext = nameFormattingContext;
        setName(getName());
    }

    public void setLoreFormattingContext(FormattingContext loreFormattingContext) {
        this.loreFormattingContext = loreFormattingContext;
        setLore(getLore());
    }

    public Material getMaterial() {
        return stack.getType();
    }

    public void setMaterial(Material material) {
        stack.setType(material);
        meta = stack.getItemMeta();
    }

    public int getAmount() {
        return stack.getAmount();
    }

    public void setAmount(int amount) {
        stack.setAmount(amount);
    }

    public void incrementAmount(int amount) {
        setAmount(getAmount() + amount);
    }

    public void decrementAmount(int amount) {
        incrementAmount(-amount);
    }

    public void incrementAmount() {
        incrementAmount(1);
    }

    public void decrementAmount() {
        decrementAmount(1);
    }

    public int getDamage() {
        return ((Damageable) meta).getDamage();
    }

    public void setDamage(int damage) {
        editMeta(it -> ((Damageable) it).setDamage(damage));
    }

    public ItemDisplay getDisplay() {
        return new ItemDisplay(getName(), getLore());
    }

    public void setDisplay(ItemDisplay display) {
        setName(display.name());
        setLore(display.lore(), true);
    }

    public void appendDisplay(ItemDisplay display) {
        setName(display.name());
        appendLore(display.lore(), true);
    }

    public boolean hasName() {
        return meta.hasDisplayName() && !getName().isBlank();
    }

    public String getNameOrElse(String other) {
        return hasName() ? getName() : other;
    }

    public String getName() {
        return meta.getDisplayName();
    }

    public String getUnformattedName() {
        return nameFormattingContext.unformat(getName());
    }

    public void setName(String name) {
        editMeta(it -> it.setDisplayName(ChatColor.RESET + nameFormattingContext.format(name)));
    }

    public List<String> getLore() {
        return Optional.ofNullable(meta.getLore()).orElseGet(Collections::emptyList);
    }

    public List<String> getUnformattedLore() {
        return getLore().stream().map(loreFormattingContext::unformat).toList();
    }

    public void setLore(List<String> lore) {
        setLore(lore, false);
    }

    private void setLore(List<String> lore, boolean onlyDisplayable) {
        editMeta(it -> it.setLore(formatLore(lore, onlyDisplayable)));
    }

    public void setLore(String... lore) {
        setLore(List.of(lore));
    }

    public void filterDisplayableLore() {
        setLore(getLore().stream().filter(loreFormattingContext::isDisplayable).toList());
    }

    public void appendLore(List<String> lore) {
        appendLore(lore, false);
    }

    private void appendLore(List<String> lore, boolean onlyDisplayable) {
        var newLore = new ArrayList<>(getLore());
        newLore.addAll(lore);
        setLore(newLore, onlyDisplayable);
    }

    public void appendLore(String... lore) {
        appendLore(List.of(lore));
    }

    public void removeLore(List<String> lore) {
        var newLore = new ArrayList<>(getLore());
        newLore.removeAll(formatLore(lore));
        setLore(newLore);
    }

    public void removeLore(String... lore) {
        removeLore(List.of(lore));
    }

    private List<String> formatLore(List<String> lore) {
        return formatLore(lore, false);
    }

    private List<String> formatLore(List<String> lore, boolean onlyDisplayable) {
        return lore.stream()
                .filter(it -> !onlyDisplayable || loreFormattingContext.isDisplayable(it))
                .map(loreFormattingContext::formatMultiline)
                .flatMap(Collection::stream)
                .toList();
    }

    public boolean hasEnchants() {
        return meta.hasEnchants();
    }

    public boolean hasEnchant(Enchantment enchant) {
        return meta.hasEnchant(enchant);
    }

    public int getEnchantLevel(Enchantment enchant) {
        return meta.getEnchantLevel(enchant);
    }

    public Map<Enchantment, Integer> getEnchants() {
        return meta.getEnchants();
    }

    public void addEnchant(Enchantment enchant, int level) {
        editMeta(it -> it.addEnchant(enchant, level, true));
    }

    public void removeEnchant(Enchantment enchant) {
        editMeta(it -> it.removeEnchant(enchant));
    }

    public boolean hasConflictingEnchant(Enchantment enchant) {
        return meta.hasConflictingEnchant(enchant);
    }

    public Set<ItemFlag> getFlags() {
        return meta.getItemFlags();
    }

    public boolean hasFlag(ItemFlag flag) {
        return meta.hasItemFlag(flag);
    }

    public void addFlags(ItemFlag... flags) {
        editMeta(it -> it.addItemFlags(flags));
    }

    public void removeFlags(ItemFlag... flags) {
        editMeta(it -> it.removeItemFlags(flags));
    }

    public boolean isUnbreakable() {
        return meta.isUnbreakable();
    }

    public void setUnbreakable(boolean unbreakable) {
        editMeta(it -> it.setUnbreakable(unbreakable));
    }

    public void makeUnbreakable() {
        setUnbreakable(true);
    }

    public void makeBreakable() {
        setUnbreakable(false);
    }

    public boolean isGlowing() {
        return hasEnchants();
    }

    public void setGlowing(boolean glowing, DeltaPlugin plugin) {
        if (isGlowing() == glowing) {
            return;
        }
        if (glowing) {
            makeGlowing(plugin);
        } else {
            makeNotGlowing(plugin);
        }
    }

    public void makeGlowing(DeltaPlugin plugin) {
        var persistentData = getPersistentData(plugin);
        persistentData.set("glowing", true);
        persistentData.setIfAbsent("hide_enchants", hasFlag(ItemFlag.HIDE_ENCHANTS));
        addEnchant(getGlowingEnchant(), 1);
        addFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public void makeNotGlowing(DeltaPlugin plugin) {
        var persistentData = getPersistentData(plugin);
        if (!persistentData.check("glowing", true)) {
            return;
        }
        persistentData.remove("glowing");
        removeEnchant(getGlowingEnchant());
        if (persistentData.check("hide_enchants", false)) {
            removeFlags(ItemFlag.HIDE_ENCHANTS);
        }
        persistentData.remove("hide_enchants");
    }

    private Enchantment getGlowingEnchant() {
        return getMaterial() == Material.BOW ? Enchantment.WATER_WORKER : Enchantment.ARROW_INFINITE;
    }

    public boolean hasCustomModelData() {
        return meta.hasCustomModelData();
    }

    public int getCustomModelData() {
        return meta.getCustomModelData();
    }

    public void setCustomModelData(Integer data) {
        editMeta(it -> it.setCustomModelData(data));
    }

    public AttributeModifierBuilder createAttributeModifierBuilder() {
        return AttributeModifierBuilder.forItem(this);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        return Optional.ofNullable(meta.getAttributeModifiers()).orElseGet(ImmutableMultimap::of);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return meta.getAttributeModifiers(slot);
    }

    public Collection<AttributeModifier> getAttributeModifiers(Attribute attribute) {
        return Optional.ofNullable(meta.getAttributeModifiers(attribute)).orElseGet(Collections::emptyList);
    }

    public List<AttributeModifier> getAttributeModifiers(Attribute attribute, AttributeModifier.Operation operation) {
        return getAttributeModifiers(attribute).stream()
                .filter(it -> it.getOperation().equals(operation))
                .toList();
    }

    public double getAttributeValue(Attribute attribute) {
        return getAttributeModifierAdditionValue(attribute) * getAttributeModifierMultiplicationValue(attribute);
    }

    private double getAttributeModifierAdditionValue(Attribute attribute) {
        return getAttributeModifiers(attribute, AttributeModifier.Operation.ADD_NUMBER).stream()
                .mapToDouble(AttributeModifier::getAmount)
                .sum();
    }

    private double getAttributeModifierMultiplicationValue(Attribute attribute) {
        return getAttributeModifiers(attribute, AttributeModifier.Operation.MULTIPLY_SCALAR_1).stream()
                .mapToDouble(AttributeModifier::getAmount)
                .map(it -> it + 1)
                .reduce(1, (a, b) -> a * b);
    }

    public AttributeModifier getAttributeModifier(Attribute attribute, String name) {
        return getAttributeModifiers(attribute).stream()
                .filter(it -> it.getName().equals(name))
                .findAny().orElse(null);
    }

    public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        editMeta(it -> it.addAttributeModifier(attribute, modifier));
    }

    public void setAttributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {
        editMeta(it -> it.setAttributeModifiers(attributeModifiers));
    }

    public void removeAttributeModifiers(Attribute attribute) {
        editMeta(it -> it.removeAttributeModifier(attribute));
    }

    public void removeAttributeModifiers(EquipmentSlot slot) {
        editMeta(it -> it.removeAttributeModifier(slot));
    }

    public void removeAttributeModifiers(String name) {
        getAttributeModifiers().forEach((attribute, modifier) -> {
            if (modifier.getName().equals(name)) {
                removeAttributeModifier(attribute, modifier);
            }
        });
    }

    public void removeAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        editMeta(it -> it.removeAttributeModifier(attribute, modifier));
    }

    public PersistentDataWrapper getPersistentData(DeltaPlugin plugin) {
        return PersistentDataWrapper.wrap(plugin, meta, it -> stack.setItemMeta(meta));
    }

    public void dropNaturally(Location location) {
        Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, stack);
    }

    public ItemStack unwrap() {
        return stack;
    }

    @SuppressWarnings("unchecked")
    public <T extends ItemMeta> void editMeta(Consumer<T> action) {
        action.accept((T) meta);
        stack.setItemMeta(meta);
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
        stack.setItemMeta(meta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(stack, ((ItemWrapper) o).stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack);
    }

    public class Builder {
        private Builder() {
        }

        public Builder formattingContext(FormattingContext formattingContext) {
            setFormattingContext(formattingContext);
            return this;
        }

        public Builder nameFormattingContext(FormattingContext nameFormattingContext) {
            setNameFormattingContext(nameFormattingContext);
            return this;
        }

        public Builder loreFormattingContext(FormattingContext loreFormattingContext) {
            setLoreFormattingContext(loreFormattingContext);
            return this;
        }

        public Builder material(Material material) {
            setMaterial(material);
            return this;
        }

        public Builder amount(int amount) {
            setAmount(amount);
            return this;
        }

        public Builder incrementAmount(int amount) {
            ItemWrapper.this.incrementAmount(amount);
            return this;
        }

        public Builder decrementAmount(int amount) {
            ItemWrapper.this.decrementAmount(amount);
            return this;
        }

        public Builder incrementAmount() {
            ItemWrapper.this.incrementAmount();
            return this;
        }

        public Builder decrementAmount() {
            ItemWrapper.this.decrementAmount();
            return this;
        }

        public Builder damage(int damage) {
            setDamage(damage);
            return this;
        }

        public Builder display(ItemDisplay display) {
            setDisplay(display);
            return this;
        }

        public Builder name(String name) {
            setName(name);
            return this;
        }

        public Builder lore(List<String> lore) {
            setLore(lore);
            return this;
        }

        public Builder lore(String... lore) {
            setLore(lore);
            return this;
        }

        public Builder filterDisplayableLore() {
            ItemWrapper.this.filterDisplayableLore();
            return this;
        }

        public Builder appendLore(List<String> lore) {
            ItemWrapper.this.appendLore(lore);
            return this;
        }

        public Builder appendLore(String... lore) {
            ItemWrapper.this.appendLore(lore);
            return this;
        }

        public Builder removeLore(List<String> lore) {
            ItemWrapper.this.removeLore(lore);
            return this;
        }

        public Builder removeLore(String... lore) {
            ItemWrapper.this.removeLore(lore);
            return this;
        }

        public Builder addEnchant(Enchantment enchant, int level) {
            ItemWrapper.this.addEnchant(enchant, level);
            return this;
        }

        public Builder removeEnchant(Enchantment enchant) {
            ItemWrapper.this.removeEnchant(enchant);
            return this;
        }

        public Builder addFlags(ItemFlag... flags) {
            ItemWrapper.this.addFlags(flags);
            return this;
        }

        public Builder removeFlags(ItemFlag... flags) {
            ItemWrapper.this.removeFlags(flags);
            return this;
        }

        public Builder makeUnbreakable() {
            ItemWrapper.this.makeUnbreakable();
            return this;
        }

        public Builder makeBreakable() {
            ItemWrapper.this.makeBreakable();
            return this;
        }

        public Builder makeGlowing(DeltaPlugin plugin) {
            ItemWrapper.this.makeGlowing(plugin);
            return this;
        }

        public Builder makeNotGlowing(DeltaPlugin plugin) {
            ItemWrapper.this.makeNotGlowing(plugin);
            return this;
        }

        public Builder customModelData(Integer data) {
            setCustomModelData(data);
            return this;
        }

        public AttributeModifierBuilder attributeModifierBuilder() {
            return AttributeModifierBuilder.forItem(ItemWrapper.this);
        }

        public Builder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
            ItemWrapper.this.addAttributeModifier(attribute, modifier);
            return this;
        }

        public Builder attributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {
            setAttributeModifiers(attributeModifiers);
            return this;
        }

        public Builder removeAttributeModifiers(Attribute attribute) {
            ItemWrapper.this.removeAttributeModifiers(attribute);
            return this;
        }

        public Builder removeAttributeModifiers(EquipmentSlot slot) {
            ItemWrapper.this.removeAttributeModifiers(slot);
            return this;
        }

        public Builder removeAttributeModifiers(String name) {
            ItemWrapper.this.removeAttributeModifiers(name);
            return this;
        }

        public Builder removeAttributeModifier(Attribute attribute, AttributeModifier modifier) {
            ItemWrapper.this.removeAttributeModifier(attribute, modifier);
            return this;
        }

        public Builder persistentData(DeltaPlugin plugin, String key, Object value) {
            getPersistentData(plugin).set(key, value);
            return this;
        }
        
        public <T extends ItemMeta> Builder editMeta(Consumer<T> action) {
            ItemWrapper.this.editMeta(action);
            return this;
        }

        public Builder meta(ItemMeta meta) {
            ItemWrapper.this.setMeta(meta);
            return this;
        }

        public ItemWrapper build() {
            return ItemWrapper.this;
        }
    }
}
