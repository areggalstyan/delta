package com.aregcraft.delta.meta;

import java.util.List;

public record Ability(String name, String description, List<Property> properties) {
}
