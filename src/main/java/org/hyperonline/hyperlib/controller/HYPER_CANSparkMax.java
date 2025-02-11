package org.hyperonline.hyperlib.controller;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxLimitSwitch;
import com.revrobotics.SparkMaxRelativeEncoder;
import edu.wpi.first.util.sendable.SendableBuilder;
import org.hyperonline.hyperlib.controller.sensor.HYPER_SparkMaxAnalogSensor;
import org.hyperonline.hyperlib.controller.sensor.HYPER_SparkMaxLimitSwitch;
import org.hyperonline.hyperlib.controller.sensor.HYPER_SparkMaxRelativeEncoder;

import java.util.function.DoubleConsumer;

/**
 * wrapper for added behavior on the {@link CANSparkMax}.
 *
 * <strong>added behavior</strong>
 * <ul>
 *     <li>make the CANSparkMax sendable for use with shuffleboard</li>
 *     <li>add DoubleConsumer that sets speed for us in active RIO PIDs</li>
 *     <li>shim for setNeutralMode over setIdleMode to match CTRE methods</li>
 *     <li>automatically add datapoints to LiveWindow</li>
 *     <li>add methods to get Sendable sensors from CAN ({@link HYPER_SparkMaxRelativeEncoder}, {@link HYPER_SparkMaxAnalogSensor}, {@link HYPER_SparkMaxLimitSwitch})</li>
 * </ul>
 *
 * @author Chris McGroarty
 */
public class HYPER_CANSparkMax extends CANSparkMax implements SendableMotorController {

  public DoubleConsumer consumeSpeed = speed -> this.set(speed);

  /**
   * Create a new object to control a SPARK MAX motor Controller.
   *
   * @param deviceId The device ID.
   * @param type The motor type connected to the controller. Brushless motor wires must be connected
   *     to their matching colors and the hall sensor must be plugged in. Brushed motors must be
   */
  public HYPER_CANSparkMax(int deviceId, MotorType type) {
    super(deviceId, type);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("Motor Controller");
    builder.setActuator(true);
    builder.setSafeState(this::stopMotor);
    builder.addDoubleProperty("Value", this::get, this::set);
    builder.addDoubleProperty("Current", this::getOutputCurrent, null);
    builder.addDoubleProperty("Applied Output", this::getAppliedOutput, null);
  }

  /**
   * wrapper for setNeutralMode to match with CTRE controllers.
   *
   * @param neutralMode the NeutralMode to choose the IdleMode from
   */
  public void setNeutralMode(NeutralMode neutralMode) {
    IdleMode idleMode = CANSparkMax.IdleMode.kCoast;
    if (neutralMode == NeutralMode.Brake) {
      idleMode = CANSparkMax.IdleMode.kBrake;
    }

    this.setIdleMode(idleMode);
  }

  /**
   * Returns and object for interfacing with the encoder connected to the encoder pins or front port
   * of the SPARK MAX.
   *
   * <p>The default encoder type is assumed to be the hall effect for brushless. This can be
   * modified for brushed DC to use a quadrature encoder.
   *
   * <p>Assumes that the encoder the is integrated encoder, configured as: EncoderType.kHallEffect,
   * 0 counts per revolution.
   *
   * @return An object for interfacing with the integrated encoder.
   */
  public HYPER_SparkMaxRelativeEncoder getEncoderSendable() {
    return getEncoderSendable(SparkMaxRelativeEncoder.Type.kHallSensor, 0);
  }

  /**
   * Returns and object for interfacing with the encoder connected to the encoder pins or front port
   * of the SPARK MAX.
   *
   * <p>The default encoder type is assumed to be the hall effect for brushless. This can be
   * modified for brushed DC to use a quadrature encoder.
   *
   * @param sensorType The encoder type for the motor: kHallEffect or kQuadrature
   * @param countsPerRev The counts per revolution of the encoder
   * @return An object for interfacing with an encoder
   */
  public HYPER_SparkMaxRelativeEncoder getEncoderSendable(
      SparkMaxRelativeEncoder.Type sensorType, int countsPerRev) {
    return new HYPER_SparkMaxRelativeEncoder(getEncoder(sensorType, countsPerRev));
  }

  /**
   * Returns an object for interfacing with an encoder connected to the alternate data port
   * configured pins. This is defined as :
   *
   * <p>Mutli-function Pin: Encoder A Limit Switch Reverse: Encoder B
   *
   * <p>This call will disable the limit switch inputs
   *
   * @return Returns an object for interfacing with an encoder connected to the alternate data port
   *     configured pins
   */
  public HYPER_SparkMaxRelativeEncoder getAlternateEncoderSendable() {
    return getAlternateEncoderSendable(SparkMaxAlternateEncoder.Type.kQuadrature, 0);
  }

  /**
   * Returns an object for interfacing with an encoder connected to the alternate data port
   * configured pins. This is defined as :
   *
   * <p>Mutli-function Pin: Encoder A Limit Switch Reverse: Encoder B
   *
   * <p>This call will disable the limit switch inputs.
   *
   * @param countsPerRev the Counts per revolution of the encoder
   * @param sensorType The encoder type for the motor: currently only kQuadrature
   * @return Returns an object for interfacing with an encoder connected to the alternate data port
   *     configured pins
   */
  public HYPER_SparkMaxRelativeEncoder getAlternateEncoderSendable(
      SparkMaxAlternateEncoder.Type sensorType, int countsPerRev) {
    return new HYPER_SparkMaxRelativeEncoder(getAlternateEncoder(sensorType, countsPerRev));
  }

  /**
   * Returns an object for interfacing with an analog sensor connected to the data port.
   *
   * @param mode The mode of the analog sensor, either absolute or relative
   * @return An object for interfacing with a connected analog sensor.
   */
  public HYPER_SparkMaxAnalogSensor getAnalogSendable(SparkMaxAnalogSensor.Mode mode) {
    return new HYPER_SparkMaxAnalogSensor(getAnalog(mode));
  }

  /**
   * Returns an object for interfacing with the forward limit switch connected to the appropriate
   * pins on the data port.
   *
   * <p>This call will disable support for the alternate encoder.
   *
   * @param switchType Whether the limit switch is normally open or normally closed.
   * @return An object for interfacing with the forward limit switch.
   */
  public HYPER_SparkMaxLimitSwitch getForwardLimitSwitchSendable(
      SparkMaxLimitSwitch.Type switchType) {
    return new HYPER_SparkMaxLimitSwitch(getForwardLimitSwitch(switchType));
  }

  /**
   * Returns an object for interfacing with the reverse limit switch connected to the appropriate
   * pins on the data port.
   *
   * <p>This call will disable support for the alternate encoder.
   *
   * @param switchType Whether the limit switch is normally open or normally closed.
   * @return An object for interfacing with the reverse limit switch.
   */
  public HYPER_SparkMaxLimitSwitch getReverseLimitSwitchSendable(
      SparkMaxLimitSwitch.Type switchType) {
    return new HYPER_SparkMaxLimitSwitch(getReverseLimitSwitch(switchType));
  }
}
