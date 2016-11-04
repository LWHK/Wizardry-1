package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 8/28/2016.
 */
public class ItemFairyImbuedApple extends ItemWizardry implements IItemColorProvider {

	public ItemFairyImbuedApple() {
		super("fairy_imbued_apple");
		setMaxStackSize(64);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	@Override
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF);
	}
}
