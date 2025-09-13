package org.bosons.decode;

import cloud.renderlabs.ftc.FTCLink;

import cloud.renderlabs.robotics.Robot;

import cloud.renderlabs.ftc.Devices.Motor;


/**
 * Created by Team 3658 
 * @author Brendan Lucas
 */

class Main {
	private Robot robot;

	public Main() throws Exception {
		this.robot = new Robot();

		this.robot
			// TODO: add parts as needed
			.addConfig(new Motor.Configuration() {{
				this.id = 1;
				this.label = "Motor1";
				this.debug = true;
			}}, Motor.FACTORY)
			.link(new FTCLink())
			.build()
		;
	};
};