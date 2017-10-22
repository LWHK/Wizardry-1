package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierIncreasePotency extends ModuleModifier {

	@Nonnull
	@Override
	public String getID() {
		return "modifier_increase_potency";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Increase Potency";
	}

	@Override
	public String getShortHandName() {
		return "Potency++";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will increase the strength of the spell.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierIncreasePotency());
	}
}
