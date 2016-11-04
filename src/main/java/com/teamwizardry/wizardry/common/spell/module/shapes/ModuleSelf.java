package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleSelf extends Module {

	public ModuleSelf(ItemStack stack) {
		super(stack);
		attributes.addAttribute(Attribute.DURATION);
	}

	@Override
	public ModuleType getType() {
		return ModuleType.SHAPE;
	}

	@Override
	public String getDescription() {
		return "Casts the spell on the caster. IE: You";
	}

	@Override
	public String getDisplayName() {
		return "Self";
	}

	@Override
	public NBTTagCompound getModuleData() {
		NBTTagCompound compound = super.getModuleData();
		compound.setInteger(Constants.Module.DURATION, (int) attributes.apply(Attribute.DURATION, 1));
		compound.setDouble(Constants.Module.MANA, attributes.apply(Attribute.MANA, 5));
		compound.setDouble(Constants.Module.BURNOUT, attributes.apply(Attribute.BURNOUT, 5));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		stack.castEffects(caster);
		return true;
	}
}
