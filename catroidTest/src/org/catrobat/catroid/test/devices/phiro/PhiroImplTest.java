/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.devices.phiro;

import android.test.AndroidTestCase;

import com.google.common.base.Stopwatch;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper;
import org.catrobat.catroid.devices.arduino.phiro.Phiro;
import org.catrobat.catroid.devices.arduino.phiro.PhiroImpl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class PhiroImplTest extends AndroidTestCase {

	private Phiro phiro;
	private ConnectionDataLogger logger;

	private static final int PIN_SPEAKER_OUT = 3;

	private static final int PIN_RGB_RED_LEFT = 4;
	private static final int PIN_RGB_GREEN_LEFT = 5;
	private static final int PIN_RGB_BLUE_LEFT = 6;

	private static final int PIN_RGB_RED_RIGHT = 7;
	private static final int PIN_RGB_GREEN_RIGHT = 8;
	private static final int PIN_RGB_BLUE_RIGHT = 9;

	private static final int PIN_LEFT_MOTOR_BACKWARD = 10;
	private static final int PIN_LEFT_MOTOR_FORWARD = 11;

	private static final int PIN_RIGHT_MOTOR_FORWARD = 12;
	private static final int PIN_RIGHT_MOTOR_BACKWARD = 13;

	private static final int MIN_PWM_PIN = 3;
	private static final int MAX_PWM_PIN = 13;

	private static final int MIN_SENSOR_PIN = 0;
	private static final int MAX_SENSOR_PIN = 5;

	private static final int PWM_MODE = 3;

	private static final int SET_PIN_MODE_COMMAND = 0xF4;
	private static final int REPORT_ANALOG_PIN_COMMAND = 0xC0;
	private static final int ANALOG_MESSAGE_COMMAND = 0xE0;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		phiro = new PhiroImpl();
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		phiro.setConnection(logger.getConnectionProxy());
	}

	@Override
	protected void tearDown() throws Exception {
		phiro.disconnect();
		logger.disconnect();
		super.tearDown();
	}

	private static final int SPEED_IN_PERCENT = 42;

	public void testMoveLeftMotorForward() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.moveLeftMotorForward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_LEFT_MOTOR_FORWARD);
	}

	public void testMoveLeftMotorBackward() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.moveLeftMotorBackward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_LEFT_MOTOR_BACKWARD);
	}

	public void testMoveRightMotorForward() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.moveRightMotorForward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_RIGHT_MOTOR_FORWARD);
	}

	public void testMoveRightMotorBackward() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.moveRightMotorBackward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_RIGHT_MOTOR_BACKWARD);
	}

	public void testStopLeftMotor() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.stopLeftMotor();
		testSpeed(0, PIN_LEFT_MOTOR_FORWARD);
		testSpeed(0, PIN_LEFT_MOTOR_BACKWARD);
	}

	public void testStopRightMotor() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.stopRightMotor();
		testSpeed(0, PIN_RIGHT_MOTOR_FORWARD);
		testSpeed(0, PIN_RIGHT_MOTOR_BACKWARD);
	}

	public void testStopAllMovements() {
		phiro.initialise();
		doTestFirmataInitialization();

		phiro.stopAllMovements();
		testSpeed(0, PIN_LEFT_MOTOR_FORWARD);
		testSpeed(0, PIN_LEFT_MOTOR_BACKWARD);
		testSpeed(0, PIN_RIGHT_MOTOR_FORWARD);
		testSpeed(0, PIN_RIGHT_MOTOR_BACKWARD);
	}

	public void testSetLeftRGBLightColor() {
		phiro.initialise();
		doTestFirmataInitialization();

		int red = 242;
		int green = 0;
		int blue = 3;

		phiro.setLeftRGBLightColor(red, green, blue);
		testLight(red, PIN_RGB_RED_LEFT);
		testLight(green, PIN_RGB_GREEN_LEFT);
		testLight(blue, PIN_RGB_BLUE_LEFT);
	}

	public void testSetRightRGBLightColor() {
		phiro.initialise();
		doTestFirmataInitialization();

		int red = 242;
		int green = 1;
		int blue = 3;

		phiro.setRightRGBLightColor(red, green, blue);
		testLight(red, PIN_RGB_RED_RIGHT);
		testLight(green, PIN_RGB_GREEN_RIGHT);
		testLight(blue, PIN_RGB_BLUE_RIGHT);
	}

	public void testPlayTone() throws InterruptedException {
		phiro.initialise();
		doTestFirmataInitialization();

		int tone = 294;
		int durationInSeconds = 1;

		phiro.playTone(tone, durationInSeconds);

		assertEquals("Wrong command, ANALOG_MESSAGE command on speaker pin expected",
				ANALOG_MESSAGE_COMMAND | BytesHelper.encodeChannel(PIN_SPEAKER_OUT), getNextMessage());
		assertEquals("Wrong lsb speed", BytesHelper.lsb(tone), getNextMessage());
		assertEquals("Wrong msb speed", BytesHelper.msb(tone), getNextMessage());

		Stopwatch stopwatch = Stopwatch.createStarted();
		while (stopwatch.elapsed(TimeUnit.SECONDS) < durationInSeconds) {
			assertEquals("Phiro play tone was stopped to early", 0, logger.getSentMessages().size());
			Thread.sleep(durationInSeconds * 100);
		}

		assertEquals("Wrong command, ANALOG_MESSAGE command on speaker pin expected",
				ANALOG_MESSAGE_COMMAND | BytesHelper.encodeChannel(PIN_SPEAKER_OUT), getNextMessage());
		assertEquals("Wrong lsb speed", 0, getNextMessage());
		assertEquals("Wrong msb speed", 0, getNextMessage());
	}
	
	private void doTestFirmataInitialization() {
		for (int i = MIN_PWM_PIN; i <= MAX_PWM_PIN; ++i) {
			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, getNextMessage());
			assertEquals("Wrong pin used to set pin mode", i, getNextMessage());
			assertEquals("Wrong pin mode is used", PWM_MODE, getNextMessage());
		}

		testReportAnalogPin(true);
	}

	private void testReportAnalogPin(boolean enable) {
		for (int i = MIN_SENSOR_PIN; i <= MAX_SENSOR_PIN; ++i) {
			assertEquals("Wrong Command, REPORT_ANALOG_PIN command expected",
					REPORT_ANALOG_PIN_COMMAND | BytesHelper.encodeChannel(i), getNextMessage());
			assertEquals("Wrong pin mode is used", enable ? 1 : 0, getNextMessage());
		}
	}

	private int getNextMessage() {
		byte[] message = logger.getNextSentMessage();
		assertNotNull("Their is no message", message);
		ByteBuffer bb = ByteBuffer.wrap(message);
		return bb.getInt();
	}

	private void testSpeed(int speed_in_percent, int pin) {
		int speed = percentToSpeed(speed_in_percent);
		assertEquals("Wrong command, ANALOG_MESSAGE command expected",
				ANALOG_MESSAGE_COMMAND | BytesHelper.encodeChannel(pin), getNextMessage());
		assertEquals("Wrong lsb speed", BytesHelper.lsb(speed), getNextMessage());
		assertEquals("Wrong msb speed", BytesHelper.msb(speed), getNextMessage());
	}

	private void testLight(int color, int pin) {
		assertEquals("Wrong command, ANALOG_MESSAGE command expected",
				ANALOG_MESSAGE_COMMAND | BytesHelper.encodeChannel(pin), getNextMessage());
		assertEquals("Wrong lsb color", BytesHelper.lsb(color), getNextMessage());
		assertEquals("Wrong msb color", BytesHelper.msb(color), getNextMessage());
	}

	private int percentToSpeed(int percent) {
		if (percent <= 0) {
			return 0;
		}
		if (percent >= 100) {
			return 255;
		}

		return (int)(percent * 2.55);
	}


}
