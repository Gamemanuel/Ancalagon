package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.MotorGroup;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.PositionType;
import org.firstinspires.ftc.teamcode.Utils.SolversLib.PIDFController.PIDFController;

@Config
public class ShooterSubsystem {

    // Hardware
    public MotorGroup shooterMotors;
    private DcMotorEx shooterLeader;
    private DcMotorEx shooterFollower;
    private VoltageSensor batteryVoltageSensor;

    // Control
    public PIDFController shooterPIDF;
    private double targetVelocity = 0;

    // PIDF Coefficients - Tunable via FTC Dashboard
    // Note: Signs may need adjustment based on motor direction
    public static double kP = 0.00055;
    public static double kI = 0.0;
    public static double kD = 0.00004;
    public static double kF = 0.0;
    
    // Feedforward gain for velocity control
    public static double kV = 0.00169;
    
    // Integration bounds to prevent windup
    public static double INTEGRAL_MIN = -0.5;
    public static double INTEGRAL_MAX = 0.5;

    public ShooterSubsystem(HardwareMap hMap) {
        // Initialize both shooter motors
        shooterLeader = hMap.get(DcMotorEx.class, "shooter");
        shooterFollower = hMap.get(DcMotorEx.class, "shooter2");
        
        // Reset encoders
        shooterLeader.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterFollower.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // Run without encoder (manual velocity control)
        shooterLeader.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterFollower.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Create motor group (adjust directions as needed for your robot)
        shooterMotors = new MotorGroup(
                shooterLeader,
                shooterFollower,
                DcMotorSimple.Direction.FORWARD,
                DcMotorSimple.Direction.FORWARD
        );
        
        // Use average velocity from both motors for smoother control
        shooterMotors.setPositionType(PositionType.AVERAGE);

        // Initialize voltage sensor
        batteryVoltageSensor = hMap.voltageSensor.iterator().next();

        // Initialize PIDF controller with coefficients
        PIDFCoefficients coeffs = new PIDFCoefficients(kP, kI, kD, kF);
        shooterPIDF = new PIDFController(coeffs);
        
        // Set integration bounds to prevent windup
        shooterPIDF.integrationControl.setIntegrationBounds(INTEGRAL_MIN, INTEGRAL_MAX);
    }

    public void periodic() {
        // 1. Update coefficients from Dashboard
        PIDFCoefficients coeffs = new PIDFCoefficients(kP, kI, kD, kF);
        shooterPIDF.setCoefficients(coeffs);
        shooterPIDF.integrationControl.setIntegrationBounds(INTEGRAL_MIN, INTEGRAL_MAX);

        // 2. Get current state (average velocity from both motors)
        double currentVelocity = shooterMotors.getVelocity();
        double currentVoltage = batteryVoltageSensor.getVoltage();

        // 3. Calculate PIDF output
        double pidOutput = shooterPIDF.calculateOutput(currentVelocity);

        // 4. Calculate feedforward
        double feedforward = kV * targetVelocity;

        // 5. Voltage compensation and power clamping
        double targetPower = (feedforward + pidOutput) * (12.0 / currentVoltage);
        targetPower = Math.max(-1.0, Math.min(1.0, targetPower));

        // 6. Apply power to both motors
        shooterMotors.setPower(targetPower);
    }

    /**
     * Sets the target velocity for the shooter motors.
     * @param target Target velocity in ticks per second (positive for shooting)
     */
    public void setTargetVelocity(double target) {
        targetVelocity = target;
        shooterPIDF.setSetPoint(target);
    }

    /**
     * Gets the current target velocity.
     * @return Target velocity in ticks per second
     */
    public double getTargetVelocity() {
        return targetVelocity;
    }

    /**
     * Gets the current average velocity of both shooter motors.
     * @return Current velocity in ticks per second
     */
    public double getCurrentVelocity() {
        return shooterMotors.getVelocity();
    }

    /**
     * Gets the velocity of the leader motor.
     * @return Leader motor velocity in ticks per second
     */
    public double getLeaderVelocity() {
        return shooterMotors.getLeaderVelocity();
    }

    /**
     * Gets the velocity of the follower motor.
     * @return Follower motor velocity in ticks per second
     */
    public double getFollowerVelocity() {
        return shooterMotors.getFollowerVelocity();
    }

    /**
     * Gets the velocity error (setpoint - actual).
     * @return Velocity error
     */
    public double getVelocityError() {
        return targetVelocity - getCurrentVelocity();
    }

    /**
     * Checks if the shooter is at the target velocity within a tolerance.
     * @param tolerance Acceptable error in ticks per second
     * @return True if within tolerance
     */
    public boolean atTargetVelocity(double tolerance) {
        return Math.abs(getVelocityError()) < tolerance;
    }
}