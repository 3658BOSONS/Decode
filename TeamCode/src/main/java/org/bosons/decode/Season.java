package org.bosons.decode;

import org.bosons.decode.Modules.Launcher;

public class Season {
	public static class Ball extends Launcher.Projectile {
		// We should have a bias for the purple balls over the green ones.
		// If we shoot only `GREEN`  balls, we must get 1 / 3 of the sequence correct.
		// If we shoot only `PURPLE` balls, we must get 2 / 3 of the sequence correct.
		public static final int PURPLE = 1; // 2 / 3
		public static final int GREEN = 0; // 1 / 3
	};
};
