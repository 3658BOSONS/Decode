package org.bosons.decode.Modules;

// Good luck editing this file. ;)
// Hours wasted: 2

import com.qualcomm.robotcore.hardware.Gamepad;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.PubSubSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GamepadWrapper {
	public static abstract class Event {
		public abstract String getEventType();

		public static class ButtonEvent extends GamepadWrapper.Event {
			public static enum Button {
				A,
				B,
				X,
				Y,
				UP,
				DOWN,
				LEFT,
				RIGHT,
				LEFT_BUMPER,
				RIGHT_BUMPER,
				LEFT_TRIGGER,
				RIGHT_TRIGGER,
			};

			public final GamepadWrapper.Event.ButtonEvent.Button button;
			public Float amount;

			public ButtonEvent(GamepadWrapper.Event.ButtonEvent.Button button, Float amount) {
				this.button = button;
				this.amount = amount;
			};

			@Override
			public String getEventType() {
				return "Button";
			};
		};

		public static class StickEvent extends GamepadWrapper.Event {
			public static enum Stick {
				LEFT_STICK,
				RIGHT_STICK,
			};

			public final GamepadWrapper.Event.StickEvent.Stick stick;
			public Float x;
			public Float y;

			public StickEvent(GamepadWrapper.Event.StickEvent.Stick stick, Float x, Float y) {
				this.stick = stick;
				this.x = x;
				this.y = y;
			};

			@Override
			public String getEventType() {
				return "Stick";
			};
		};
	};

	public static class State {
		public Map<String, Float> buttons;
		public Map<String, Map<String, Float>> stickPositions;
		public Map<String, Map<String, Float>> triggerPositions;
	};

	private Gamepad gamepad;
	private final PubSubSupport<Object> eventBus = new MBassador<>();

	public GamepadWrapper(Gamepad gamepad) {
		this.gamepad = gamepad;

		GamepadWrapper.State currentState = new GamepadWrapper.State();
		GamepadWrapper.State previousState = new GamepadWrapper.State();


		// Post events when gamepad buttons are pressed.
		// Create a new thread to avoid blocking the main thread.
		new Thread(() -> {
			Map<String, Float> buttonMap = new HashMap<>();
			Map<String, Map<String, Float>> stickPosition = new HashMap<>();

			Map<String, Float> liveButtonMap = new HashMap<>();
			Map<String, Map<String, Float>> liveStickPosition = new HashMap<>();

			while (true) {
				// Who the hell decided it would be a good idea to make each button a separate property?
				buttonMap.put("A", this.gamepad.a ? 1.0f : 0.0f);
				buttonMap.put("B", this.gamepad.b ? 1.0f : 0.0f);
				buttonMap.put("X", this.gamepad.x ? 1.0f : 0.0f);
				buttonMap.put("Y", this.gamepad.y ? 1.0f : 0.0f);

				buttonMap.put("UP", this.gamepad.dpad_up ? 1.0f : 0.0f);
				buttonMap.put("DOWN", this.gamepad.dpad_down ? 1.0f : 0.0f);
				buttonMap.put("LEFT", this.gamepad.dpad_left ? 1.0f : 0.0f);
				buttonMap.put("RIGHT", this.gamepad.dpad_right ? 1.0f : 0.0f);

				buttonMap.put("RIGHT_BUMPER", this.gamepad.right_bumper ? 1.0f : 0.0f);
				buttonMap.put("LEFT_BUMPER", this.gamepad.left_bumper ? 1.0f : 0.0f);

				buttonMap.put("LEFT_TRIGGER", this.gamepad.left_trigger);
				buttonMap.put("RIGHT_TRIGGER", this.gamepad.right_trigger);

				stickPosition.put("LEFT_STICK", new HashMap<>(
						Map.of(
								"X", this.gamepad.left_stick_x,
								"Y", this.gamepad.left_stick_y
						)
				));

				stickPosition.put("RIGHT_STICK", new HashMap<>(
						Map.of(
								"X", this.gamepad.right_stick_x,
								"Y", this.gamepad.right_stick_y
						)
				));

				currentState.buttons = buttonMap;
				currentState.stickPositions = stickPosition;

				// Find the difference between the current and previous state.
				liveButtonMap = new HashMap<>();
				liveStickPosition = new HashMap<>();

				for (Map.Entry<String, Float> entry : currentState.buttons.entrySet()) {
					if (!Objects.equals(currentState.buttons.get(entry.getKey()), previousState.buttons.get(entry.getKey()))) {
						liveButtonMap.put(entry.getKey(), entry.getValue());
					};
					if (!Objects.equals(currentState.buttons.get(entry.getKey()), previousState.buttons.get(entry.getKey()))) {
						liveButtonMap.put(entry.getKey(), currentState.buttons.get(entry.getKey()));
					};
					if (!Objects.equals(currentState.stickPositions.get(entry.getKey()), previousState.stickPositions.get(entry.getKey()))) {
						liveStickPosition.put(entry.getKey(), currentState.stickPositions.get(entry.getKey()));
					};
				};

				// Emit the events
				for (Map.Entry<String, Float> entry : liveButtonMap.entrySet()) {
					eventBus.publish(new GamepadWrapper.Event.ButtonEvent(GamepadWrapper.Event.ButtonEvent.Button.valueOf(entry.getKey()), entry.getValue()));
				};
				for (Map.Entry<String, Map<String, Float>> entry : liveStickPosition.entrySet()) {
					Map<String, Float> updatedStickPosition = entry.getValue();
					eventBus.publish(new GamepadWrapper.Event.StickEvent(GamepadWrapper.Event.StickEvent.Stick.valueOf(entry.getKey()), updatedStickPosition.get("X"), updatedStickPosition.get("Y")));
				};

				// Give the system some breathing room.
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				};
			}//;
		}).start();
	};
};