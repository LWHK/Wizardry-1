package com.teamwizardry.wizardry.init.plugin;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.plugin.PluginContext;
import com.teamwizardry.wizardry.api.plugin.WizardryPlugin;
import com.teamwizardry.wizardry.common.core.fairytasks.FairyTaskBreakBlock;
import com.teamwizardry.wizardry.common.core.fairytasks.FairyTaskGrabItems;
import com.teamwizardry.wizardry.common.core.fairytasks.FairyTaskMove;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class InternalWizardryPlugin implements WizardryPlugin {

	@Override
	public void onInit(PluginContext context) {
		context.addFairyTask(new ResourceLocation(Wizardry.MODID, "grab_items"), (stack, fairy) -> stack.getItem().equals(Items.APPLE), FairyTaskGrabItems::new);
		context.addFairyTask(new ResourceLocation(Wizardry.MODID, "move"), (stack, fairy) -> stack.getItem().equals(Items.RABBIT_FOOT), FairyTaskMove::new);
		context.addFairyTask(new ResourceLocation(Wizardry.MODID, "break_block"), (stack, fairy) -> stack.getItem().equals(Items.IRON_PICKAXE), FairyTaskBreakBlock::new);
	}
}
