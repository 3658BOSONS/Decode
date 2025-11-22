package org.bosons.decode.Modules;

// Good luck editing this file. ;)
// Hours wasted: 2.

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.PubSubSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GamepadWrapper {
	public static enum User {
		DRIVER,
		ACCESSORIES,
	};
	public static final Error NotConnected = new Error("Gamepad not found!");

	public static GamepadWrapper connect(GamepadWrapper.User user, OpMode opMode) {
		switch (user) {
			case DRIVER: {
				Gamepad gamepad = opMode.gamepad1;
				if (gamepad == null) {
					throw NotConnected;
				};
				return new GamepadWrapper(gamepad);
			}//;
			case ACCESSORIES: {
				Gamepad gamepad = opMode.gamepad2;
				if (gamepad == null) {
					throw NotConnected;
				};
				return new GamepadWrapper(gamepad);
			}//;
			default: {
				throw new Error("Invalid user!");
			}//;
		}//;
	};

	public static abstract class Event {
		public static enum Type {
			BUTTON,
			STICK,
		};

		public abstract GamepadWrapper.Event.Type getEventType();
		public Map<String, Object> exports = new HashMap<>();

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
				this.exports.put("button", button);
				this.exports.put("amount", amount);
			};

			@Override
			public GamepadWrapper.Event.Type getEventType() {
				return GamepadWrapper.Event.Type.BUTTON;
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
			public GamepadWrapper.Event.Type getEventType() {
				return GamepadWrapper.Event.Type.STICK;
			};
		};
	};

	public static class State {
		public Map<String, Float> buttons;
		public Map<String, Map<String, Float>> stickPositions;
		public Map<String, Map<String, Float>> triggerPositions;
	};

	private Gamepad gamepad;

	public final PubSubSupport<GamepadWrapper.Event> eventBus = new MBassador<>();

	public final Map<String, Float> buttonMap = new HashMap<>();
	public final Map<String, Map<String, Float>> stickPosition = new HashMap<>();

	public GamepadWrapper(Gamepad gamepad) {
		this.gamepad = gamepad;
		Objects.requireNonNull(this.buttonMap, "GamepadWrapper map was null!");

		GamepadWrapper.State currentState = new GamepadWrapper.State();
		GamepadWrapper.State previousState = new GamepadWrapper.State();
		previousState.buttons = new HashMap<>();
		previousState.stickPositions = new HashMap<>();


		// Post events when gamepad buttons are pressed.
		// Create a new thread to avoid blocking the main thread.
		new Thread(() -> {
			// FIXME!
			Map<String, Float> liveButtonMap = new HashMap<>();
			Map<String, Map<String, Float>> liveStickPosition = new HashMap<>();

			while (true) {
				// Who the hell decided it would be a good idea to make each button a separate property?
				this.buttonMap.put("A", this.gamepad.a ? 1.0f : 0.0f);
				this.buttonMap.put("B", this.gamepad.b ? 1.0f : 0.0f);
				this.buttonMap.put("X", this.gamepad.x ? 1.0f : 0.0f);
				this.buttonMap.put("Y", this.gamepad.y ? 1.0f : 0.0f);

				this.buttonMap.put("UP", this.gamepad.dpad_up ? 1.0f : 0.0f);
				this.buttonMap.put("DOWN", this.gamepad.dpad_down ? 1.0f : 0.0f);
				this.buttonMap.put("LEFT", this.gamepad.dpad_left ? 1.0f : 0.0f);
				this.buttonMap.put("RIGHT", this.gamepad.dpad_right ? 1.0f : 0.0f);

				this.buttonMap.put("RIGHT_BUMPER", this.gamepad.right_bumper ? 1.0f : 0.0f);
				this.buttonMap.put("LEFT_BUMPER", this.gamepad.left_bumper ? 1.0f : 0.0f);

				this.buttonMap.put("LEFT_TRIGGER", this.gamepad.left_trigger);
				this.buttonMap.put("RIGHT_TRIGGER", this.gamepad.right_trigger);

				this.stickPosition.put("LEFT_STICK", new HashMap<>(
						Map.of(
								"X", this.gamepad.left_stick_x,
								"Y", this.gamepad.left_stick_y
						)
				));

				this.stickPosition.put("RIGHT_STICK", new HashMap<>(
						Map.of(
								"X", this.gamepad.right_stick_x,
								"Y", this.gamepad.right_stick_y
						)
				));

				currentState.buttons = buttonMap;
				currentState.stickPositions = stickPosition;

				// Find the difference between the current and previous state.
				/*for (Map.Entry<String, Float> entry : currentState.buttons.entrySet()) {
					if (!Objects.equals(entry.getValue(), previousState.buttons.get(entry.getKey()))) {
						liveButtonMap.put(entry.getKey(), entry.getValue());
					};
					if (!Objects.equals(currentState.stickPositions.get(entry.getKey()), previousState.stickPositions.get(entry.getKey()))) {
						liveStickPosition.put(entry.getKey(), currentState.stickPositions.get(entry.getKey()));
					};
				};*/

				// Emit the events
				for (Map.Entry<String, Float> entry : liveButtonMap.entrySet()) {
					eventBus.publish(new GamepadWrapper.Event.ButtonEvent(GamepadWrapper.Event.ButtonEvent.Button.valueOf(entry.getKey()), entry.getValue()));
				};
				for (Map.Entry<String, Map<String, Float>> entry : liveStickPosition.entrySet()) {
					Map<String, Float> updatedStickPosition = entry.getValue();
					eventBus.publish(new GamepadWrapper.Event.StickEvent(GamepadWrapper.Event.StickEvent.Stick.valueOf(entry.getKey()), updatedStickPosition.get("X"), updatedStickPosition.get("Y")));
				};

				// Set the current state to the previous state.
				previousState.buttons = currentState.buttons;
				previousState.stickPositions = currentState.stickPositions;

				// Give the system some breathing room.
				// Ignore warnings:
				try {
					// noinspection BusyWait
					Thread.sleep(10);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				};
			}//;
		}).start();
	};
};