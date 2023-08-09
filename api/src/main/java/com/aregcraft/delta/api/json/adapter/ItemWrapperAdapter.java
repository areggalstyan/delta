package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.FormattingContext;
import com.aregcraft.delta.api.InjectPlugin;
import com.aregcraft.delta.api.item.ItemWrapper;
import com.aregcraft.delta.api.json.JsonReader;
import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.aregcraft.delta.api.util.CollectionMaps;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@JsonAdapterFor(ItemWrapper.class)
public class ItemWrapperAdapter implements JsonDeserializer<ItemWrapper>, JsonSerializer<ItemWrapper> {
    private static final TypeToken<Map<Enchantment, Integer>> ENCHANTS_TYPE = new TypeToken<>() {};
    private static final TypeToken<Map<Attribute, Collection<AttributeModifier>>> ATTRIBUTE_MODIFIERS_TYPE =
            new TypeToken<>() {};
    private static final TypeToken<Map<String, PersistentDataEntry>> PERSISTENT_DATA_TYPE = new TypeToken<>() {};

    @InjectPlugin
    private DeltaPlugin plugin;

    @Override
    public ItemWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var reader = new JsonReader(context, json);
        var item = ItemWrapper.withMaterial(reader.get("material", Material.class));
        item.setFormattingContext(FormattingContext.withPlugin(plugin));
        reader.acceptInt("amount", item::setAmount);
        reader.acceptInt("damage", item::setDamage);
        reader.acceptString("name", item::setName);
        reader.accept("lore", String[].class, item::setLore);
        reader.accept("enchants", ENCHANTS_TYPE, it -> it.forEach(item::addEnchant));
        reader.accept("flags", ItemFlag[].class, item::addFlags);
        reader.acceptBoolean("unbreakable", item::setUnbreakable);
        reader.acceptBoolean("glowing", it -> item.setGlowing(it, plugin));
        reader.acceptInt("customModelData", item::setCustomModelData);
        reader.accept("attributeModifiers", ATTRIBUTE_MODIFIERS_TYPE,
                it -> CollectionMaps.forEach(it, item::addAttributeModifier));
        reader.accept("persistentData", PERSISTENT_DATA_TYPE, it -> it.forEach((key, entry) ->
                entry.set(item, Objects.requireNonNull(NamespacedKey.fromString(key)))));
        return item;
    }

    @Override
    public JsonElement serialize(ItemWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        var obj = new JsonObject();
        obj.add("material", context.serialize(src.getMaterial()));
        obj.addProperty("amount", src.getAmount());
        obj.addProperty("damage", src.getDamage());
        if (src.hasName()) {
            obj.addProperty("name", src.getUnformattedName());
        }
        obj.add("lore", context.serialize(src.getUnformattedLore()));
        obj.add("enchants", context.serialize(src.getEnchants()));
        obj.add("flags", context.serialize(src.getFlags()));
        obj.addProperty("unbreakable", src.isUnbreakable());
        obj.addProperty("glowing", src.isGlowing());
        if (src.hasCustomModelData()) {
            obj.addProperty("customModelData", src.getCustomModelData());
        }
        obj.add("attributeModifiers", context.serialize(src.getAttributeModifiers().asMap()));
        var persistentData = new JsonObject();
        obj.add("persistentData", persistentData);
        src.getMeta().getPersistentDataContainer().getKeys().forEach(it ->
                persistentData.add(it.toString(), context.serialize(new PersistentDataEntry(src, it))));
        return obj;
    }
}
