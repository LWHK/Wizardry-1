package com.teamwizardry.wizardry.api.spell;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;

import net.minecraft.item.Item;

public class Module
{
    // Identifying data - must be unique
    protected final Pattern pattern;
    protected final String name;
    protected final Item item;

    // Variable data
    protected final Map<String, Range<Integer>> attributeRanges;

    // Modifier and Usage Metadata
    protected final List<String> tags;
    protected final List<String> hiddenTags;

    public Module(Pattern pattern, String name, Item item, Map<String, Range<Integer>> attributeRanges, List<String> tags, List<String> hiddenTags)
    {
        this.pattern = pattern;
        this.name = name;
        this.item = item;
        this.attributeRanges = attributeRanges;
        this.tags = tags;
        this.hiddenTags = hiddenTags;
    }

    public Pattern getPattern()
    { return pattern; }

    public String getName()
    { return name; }

    public Item getItem()
    { return item; }

    public Map<String, Range<Integer>> getAttributeRanges()
    { return attributeRanges; }

    public List<String> getTags()
    { return tags; }

    public List<String> getHiddenTags()
    { return hiddenTags; }

    public String toString()
    {
        return pattern.getRegistryName() + ":" + name + " = [" + item + ", " + attributeRanges + ", " + tags + ", " + hiddenTags + "]";
    }
}
