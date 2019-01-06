package com.teamwizardry.wizardry.common.network.belt;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketPearlHolderSubtract extends PacketBase {

	@Save
	public int holderSlot;
	@Save
	public int holderSlotRemoval;

	public PacketPearlHolderSubtract(int holderSlot, int holderSlotRemoval) {
		this.holderSlot = holderSlot;
		this.holderSlotRemoval = holderSlotRemoval;
	}

	public PacketPearlHolderSubtract() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack holder = player.inventory.getStackInSlot(holderSlot);

		if (holder.getItem() != ModItems.PEARL_BELT) return;
		IPearlBelt belt = (IPearlBelt) holder.getItem();

		ItemStack output = belt.removePearl(holder, holderSlotRemoval);
		if (output.isEmpty()) return;

		player.addItemStackToInventory(output);
		ItemNBTHelper.setInt(holder, "scroll_slot", -1);
	}
}
