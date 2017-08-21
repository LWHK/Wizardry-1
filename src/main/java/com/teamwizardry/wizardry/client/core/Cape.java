package com.teamwizardry.wizardry.client.core;

import com.google.common.primitives.Doubles;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

abstract class Vertex {

	Vec3d lastPos;
	public Vec3d pos;
	boolean pinned;
	Vec3d scratch = Vec3d.ZERO;

	Vertex(Vec3d pos, boolean pinned) {
		this.lastPos = this.pos = pos;
		this.pinned = pinned;
	}

	Vertex(Vec3d pos) {
		this.lastPos = this.pos = pos;
		this.pinned = false;
	}

	abstract Vec3d normal();
}

class Constraint {

	public final Vertex a;
	public final Vertex b;
	public final double length;
	private final boolean hard;

	Constraint(Vertex a, Vertex b, double length, boolean hard) {
		this.a = a;
		this.b = b;
		this.length = length;
		this.hard = hard;
	}

	void resolve(double coeff) {
		if (a.pinned && b.pinned) return;
		Vec3d delta = b.pos.subtract(a.pos);
		double distance = delta.lengthVector();

		if (distance == 0.0) return;

		Vec3d normalBToA = delta.scale(1 / distance);
		double deltaDistance = this.length - distance;

		if (deltaDistance == 0.0) return;
		if (!Doubles.isFinite(distance)) return;

		if (a.pinned || hard) {
			b.pos = b.pos.add(normalBToA.scale(deltaDistance * coeff));
		} else if (b.pinned) {
			a.pos = a.pos.add(normalBToA.scale(-1).scale(deltaDistance * coeff));
		} else {
			b.pos = b.pos.add(normalBToA.scale((deltaDistance / 2) * coeff));
			a.pos = a.pos.add(normalBToA.scale(-1).scale((deltaDistance / 2) * coeff));
		}
	}
}

public class Cape {
	private double inertialDampingCoeff = 1.0;
	double dragDampingCoeff = 0.8;
	private double springCoeff = 0.8;
	Vec3d windVelocity = Vec3d.ZERO;
	double windForce = 0.1;
	Vec3d gravity = Vec3d.ZERO;
	List<Vertex> points = new ArrayList<>();
	private List<Constraint> constraints = new ArrayList<>();

	public Cape(ClothDefinition def) {
		def.generate(points, constraints);
	}

	public void tick() {
		for (Vertex vertex : points) {
			vertex.lastPos = vertex.scratch;
		}

		assembleBBs();
		shiftForAirForce();
		shiftForInertia();
		shiftForAcceleration();
		applyDampening();
		for (int i = 0; i < 3; i++) {
			resolve();
		}

		for (Vertex vertex : points) {
			vertex.scratch = vertex.pos;
		}
	}

	private void assembleBBs() {
	}

	private void shiftForInertia() {
		for (Vertex vertex : points) {
			if (vertex.pinned) continue;
			vertex.pos = vertex.pos.add(vertex.pos.subtract(vertex.lastPos).scale(inertialDampingCoeff));
		}
	}

	private void shiftForAcceleration() {
		for (Vertex vertex : points) {
			if (vertex.pinned) continue;
			vertex.pos = vertex.pos.add(gravity);

		}
	}

	private void shiftForAirForce() {
		for (Vertex vertex : points) {
			if (vertex.pinned) continue;

			Vec3d vel = vertex.pos.subtract(vertex.lastPos);
			Vec3d airVel = vel.add(windVelocity);
			Vec3d norm = vertex.normal();
			Vec3d proj = norm.scale(norm.dotProduct(airVel));

			vertex.scratch = proj.scale(windForce);
		}

		for (Vertex vertex : points) {
			if (!vertex.pinned) vertex.pos = vertex.pos.add(vertex.scratch);
		}
	}

	private void applyDampening() {
		for (Vertex vertex : points) {
			vertex.pos = vertex.lastPos.add(vertex.pos.subtract(vertex.lastPos).scale(dragDampingCoeff));
		}
	}

	private void resolve() {
		for (Constraint constraint : constraints) {
			constraint.resolve(springCoeff);
		}
	}
}

interface ClothDefinition {
	void generate(List<Vertex> points, List<Constraint> constraints);
}

class GridCloth implements ClothDefinition {

	public final Vec3d origin;
	private final Vec3d widthUnit;
	private final Vec3d heightUnit;
	public final int width;
	public final int height;

	GridCloth(Vec3d origin, Vec3d widthUnit, Vec3d heightUnit, int width, int height) {
		this.origin = origin;
		this.widthUnit = widthUnit;
		this.heightUnit = heightUnit;
		this.width = width;
		this.height = height;
	}

	@Override
	public void generate(List<Vertex> points, List<Constraint> constraints) {
		List<Vertex> newPoints = new ArrayList<>();
		List<Constraint> newConstraints = new ArrayList<>();
		addPoints(newPoints);

		for (int i = 0; i < newPoints.size() - 1; i++) {
			addPositiveNeighbors(newConstraints, newPoints, i);
		}

		points.addAll(newPoints);
		constraints.addAll(newConstraints);
	}

	private void addPoints(List<Vertex> list) {

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {

				int finalH = h;
				int finalW = w;
				Vertex vertex = new Vertex(origin.add(widthUnit.scale(finalW)).add(heightUnit.scale(finalH))) {
					@Override
					Vec3d normal() {
						Vec3d c = this.pos;

						if (!valid(finalW, finalH - 1)) return null;
						Vec3d u = list.get(getI(finalW, finalH - 1)).pos;

						if (!valid(finalW, finalH + 1)) return null;
						Vec3d d = list.get(getI(finalW, finalH + 1)).pos;

						if (!valid(finalW - 1, finalH)) return null;
						Vec3d l = list.get(getI(finalW - 1, finalH)).pos;

						if (!valid(finalW + 1, finalH)) return null;
						Vec3d r = list.get(getI(finalW + 1, finalH)).pos;

						Vec3d avg = Vec3d.ZERO;

						if (u != null && l != null) {
							avg = avg.add(c.subtract(u)).crossProduct(c.subtract(l));
						}
						if (u != null && r != null) {
							avg = avg.add(c.subtract(r)).crossProduct(c.subtract(u));
						}
						if (d != null && l != null) {
							avg = avg.add(c.subtract(l)).crossProduct(c.subtract(d));
						}
						if (d != null && r != null) {
							avg = avg.add(c.subtract(d)).crossProduct(c.subtract(r));
						}

						if (avg.x == 0.0 && avg.y == 0.0 && avg.z == 0.0) return avg;
						return avg.normalize();
					}
				};

				list.add(vertex);

			}
		}
	}

	private void addPositiveNeighbors(List<Constraint> constraints, List<Vertex> list, int index) {
		Vertex v = list.get(index);
		int w = getW(index);
		int h = getH(index);

		if (valid(w, h + 1))
			constraints.add(new Constraint(v, list.get(getI(w, h + 1)), (v.pos.subtract(list.get(getI(w, h + 1)).pos).lengthVector()), true)); // down
		if (valid(w + 1, h))
			constraints.add(new Constraint(v, list.get(getI(w + 1, h)), (v.pos.subtract(list.get(getI(w + 1, h)).pos).lengthVector()), false)); // right
		if (valid(w + 1, h + 1))
			constraints.add(new Constraint(v, list.get(getI(w + 1, h + 1)), (v.pos.subtract(list.get(getI(w + 1, h + 1)).pos).lengthVector()), false)); // down-right
		if (valid(w - 1, h + 1))
			constraints.add(new Constraint(v, list.get(getI(w - 1, h + 1)), (v.pos.subtract(list.get(getI(w - 1, h + 1)).pos).lengthVector()), false)); // down-left
	}

	private int getW(int index) {
		return index % (width + 1);
	}

	private int getH(int index) {
		return Math.floorDiv(index, width + 1);
	}

	private int getI(int w, int h) {
		return w + (width + 1) * h;
	}

	private boolean valid(int w, int h) {
		return (w >= 0 && h >= 0) && (w <= width && h <= height);
	}
}

class LineCloth implements ClothDefinition {

	public final Vec3d origin;
	private final Vec3d unit;
	public final int length;

	LineCloth(Vec3d origin, Vec3d unit, int length) {

		this.origin = origin;
		this.unit = unit;
		this.length = length;
	}

	@Override
	public void generate(List<Vertex> points, List<Constraint> constraints) {
		ArrayList<Vertex> newPoints = new ArrayList<>();
		ArrayList<Constraint> newConstraints = new ArrayList<>();
		addPoints(newPoints);

		for (int i = 0; i < length - 1; i++) {
			newConstraints.add(new Constraint(newPoints.get(i), newPoints.get(i + 1), (newPoints.get(i).pos.subtract(newPoints.get(i + 1).pos)).lengthVector(), true));
		}

		points.addAll(newPoints);
		constraints.addAll(newConstraints);
	}

	private void addPoints(ArrayList<Vertex> list) {
		for (int i = 0; i < length - 1; i++) {
			Vertex vertex = new Vertex(origin.add(unit.scale(i))) {
				@Override
				Vec3d normal() {
					return Vec3d.ZERO;
				}
			};
			list.add(vertex);
		}
	}
}