package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.common.util.ConfigPropertyDouble;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeBeam extends Module implements IContinousSpell {

	@ConfigPropertyDouble(modid = Wizardry.MODID, category = "attributes", id = "shape_beam_default_range", comment = "The default range of a pure beam spell shape", defaultValue = 10)
	public static double defaultRange;

	public ModuleShapeBeam() {
		process(this);
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(ModItems.UNICORN_HORN);
	}

	@Override
	public double getManaToConsume() {
		return 5;
	}

	@Override
	public double getBurnoutToFill() {
		return 10;
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_beam";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Beam";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will run the spell via a beam emanating from the caster";
	}

	@Override
	public boolean run(@NotNull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double range = 10;
		if (attributes.hasKey(Attributes.EXTEND)) range += attributes.getDouble(Attributes.EXTEND);

		RayTraceResult trace = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), caster != null ? position.addVector(0, caster.getEyeHeight(), 0) : position, range, caster);
		if (trace == null) return false;

		if (trace.typeOfHit == RayTraceResult.Type.ENTITY)
			spell.crunchData(trace.entityHit, false);
		else if (trace.typeOfHit == RayTraceResult.Type.BLOCK) {
			spell.addData(BLOCK_HIT, trace.getBlockPos());
			spell.addData(TARGET_HIT, trace.hitVec);
		} else spell.addData(TARGET_HIT, trace.hitVec);
		return runNextModule(spell);
	}

	@Override
	public void runClient(@Nullable ItemStack stack, @NotNull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		Vec3d target = spell.getData(TARGET_HIT);

		if (position == null) return;
		if (target == null) return;

		Vec3d origin = position;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(position);
		}
		LibParticles.SHAPE_BEAM(world, target, origin, getColor() == null ? Color.WHITE : getColor());
	}

	@NotNull
	@Override
	public ModuleShapeBeam copy() {
		ModuleShapeBeam module = new ModuleShapeBeam();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
