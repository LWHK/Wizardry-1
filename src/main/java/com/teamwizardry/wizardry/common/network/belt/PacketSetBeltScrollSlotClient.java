package com.teamwizardry.wizardry.common.network.belt;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketSetBeltScrollSlotClient extends PacketBase {

	@Save
	public int itemSlot;
	@Save
	public int scrollSlot;

	public PacketSetBeltScrollSlotClient() {
	}

	public PacketSetBeltScrollSlotClient(int itemSlot, int scrollSlot) {

		this.itemSlot = itemSlot;
		this.scrollSlot = scrollSlot;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				ItemStack belt = Minecraft.getMinecraft().player.inventory.getStackInSlot(itemSlot);
				if (belt.getItem() != ModItems.PEARL_BELT) return;

				ItemNBTHelper.setInt(belt, "scroll_slot", scrollSlot);
			}
		});
	}
}
