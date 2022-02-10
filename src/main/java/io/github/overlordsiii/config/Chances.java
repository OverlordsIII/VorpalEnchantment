package io.github.overlordsiii.config;

public enum Chances {
	VANILLA_FRIENDLY(0.05, 0.1, 0.15),
	SLIGHTLY_OP(0.05, 0.15, 0.25),
	VERY_OP(0.25, 0.5, 1);

	final double lv1;
	final double lv2;
	final double lv3;

	Chances(double lv1, double lv2, double lv3) {
		this.lv1 = lv1;
		this.lv2 = lv2;
		this.lv3 = lv3;
	}

	public double getLv1Chance() {
		return lv1;
	}

	public double getLv2Chance() {
		return lv2;
	}

	public double getLv3Chance() {
		return lv3;
	}
}
