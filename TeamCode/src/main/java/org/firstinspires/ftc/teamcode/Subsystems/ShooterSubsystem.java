package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.MotorGroup;
import org.firstinspires.ftc.teamcode.Utils.SolversLib.PIDFController.PIDFController;

@Config
public class ShooterSubsystem {

    // Individual motors for monitoring
    public DcMotorEx shooterLeft;
    public DcMotorEx shooterRight;

    // Motor group for unified control
    public MotorGroup shooterGroup;

    public PIDFController shooterPIDF;
    private VoltageSensor batteryVoltageSensor;

    // ===== TUNING PARAMETERS (Dashboard Tunable) =====
    // Start conservative, then tune following the guide
    public static PIDFCoefficients SCoeffs = new PIDFCoefficients(-0.0008, -0.00001, -0.00006, 0);
    public static double kV = 0.00169;  // Feedforward velocity constant

    // Velocity filtering (reduces encoder noise)
    public static double VELOCITY_FILTER_GAIN = 0.7;  // 0.0 = no filter, 1.0 = max smoothing

    // Acceleration limiting (prevents overshoot)
    public static double MAX_ACCEL_PER_LOOP = 150.0;  // ticks/sec change limit per loop

    // Tolerance for "at speed" detection
    public static double AT_SPEED_TOLERANCE = 50.0;  // ±50 ticks/sec

    // Motor sync monitoring
    public static double MOTOR_SYNC_WARNING_THRESHOLD = 100.0;  // Warn if motors differ by this much
    public static boolean ENABLE_SYNC_MONITORING = true;

    // Internal state
    private double TargetVelocity = 0;
    private double filteredVelocityLeft = 0;
    private double filteredVelocityRight = 0;
    private double lastCommandedPower = 0;
    private long lastUpdateTime = 0;

    public ShooterSubsystem(HardwareMap hMap) {
        // Initialize both motors
        shooterLeft = hMap.get(DcMotorEx.class, "shooterLeft");
        shooterRight = hMap.get(DcMotorEx.class, "shooterRight");

        // Reset and configure both motors
        shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        shooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Both motors should coast to reduce friction
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Create motor group (adjust directions based on your physical setup!)
        // IMPORTANT: One motor likely needs to be REVERSE to spin the same direction
        shooterGroup = new MotorGroup(
                shooterLeft,
                shooterRight,
                DcMotorSimple.Direction.FORWARD,  // Adjust based on your robot!
                DcMotorSimple.Direction.REVERSE   // One should probably be reversed
        );

        batteryVoltageSensor = hMap.voltageSensor.iterator().next();
        shooterPIDF = new PIDFController(SCoeffs);

        lastUpdateTime = System.currentTimeMillis();
    }

    public void periodic() {
        // 1. Update Coefficients (allows live tuning via dashboard)
        shooterPIDF.setCoefficients(SCoeffs);

        // 2. Get Current State from BOTH motors
        double rawVelocityLeft = shooterLeft.getVelocity();
        double rawVelocityRight = shooterRight.getVelocity();
        double currentVoltage = batteryVoltageSensor.getVoltage();

        // 3. Apply velocity filtering to each motor (exponential moving average)
        filteredVelocityLeft = (VELOCITY_FILTER_GAIN * filteredVelocityLeft) +
                ((1 - VELOCITY_FILTER_GAIN) * rawVelocityLeft);
        filteredVelocityRight = (VELOCITY_FILTER_GAIN * filteredVelocityRight) +
                ((1 - VELOCITY_FILTER_GAIN) * rawVelocityRight);

        // 4. Calculate average velocity for PIDF control
        double averageVelocity = (filteredVelocityLeft + filteredVelocityRight) / 2.0;

        // 5. Check for motor synchronization issues
        if (ENABLE_SYNC_MONITORING) {
            double velocityDifference = Math.abs(filteredVelocityLeft - filteredVelocityRight);
            if (velocityDifference > MOTOR_SYNC_WARNING_THRESHOLD) {
                // This indicates a mechanical problem or one motor is failing!
                // You'll see this in telemetry
            }
        }

        // 6. PID Calculation (using average filtered velocity)
        double pidOutput = shooterPIDF.calculateOutput(averageVelocity);

        // 7. Feedforward
        double feedforward = kV * TargetVelocity;

        // 8. Combine and apply voltage compensation
        double targetPower = (feedforward + pidOutput) * (12.0 / currentVoltage);

        // 9. Acceleration limiting (prevents sudden power spikes)
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastUpdateTime) / 1000.0;
        if (dt > 0.001) {
            double maxDeltaPower = (MAX_ACCEL_PER_LOOP * dt) / 1000.0;
            double powerChange = targetPower - lastCommandedPower;

            if (Math.abs(powerChange) > maxDeltaPower) {
                targetPower = lastCommandedPower + Math.signum(powerChange) * maxDeltaPower;
            }
        }

        // 10. Clamp power to safe range
        targetPower = Math.max(-1.0, Math.min(1.0, targetPower));

        // 11. Apply power to BOTH motors via MotorGroup
        shooterGroup.setPower(targetPower);

        // Update state
        lastCommandedPower = targetPower;
        lastUpdateTime = currentTime;
    }

    /**
     * Set target velocity for both flywheels
     * @param target Desired velocity in ticks/sec (positive value)
     */
    public void setTargetVelocity(double target) {
        TargetVelocity = -target;  // Negative because motors spin backwards
        shooterPIDF.setSetPoint(-target);
    }

    public double getTargetVelocity() {
        return TargetVelocity;
    }

    /**
     * Get the average filtered velocity of both motors
     */
    public double getFilteredVelocity() {
        return (filteredVelocityLeft + filteredVelocityRight) / 2.0;
    }

    /**
     * Get individual motor velocities for debugging
     */
    public double getLeftVelocity() {
        return filteredVelocityLeft;
    }

    public double getRightVelocity() {
        return filteredVelocityRight;
    }

    /**
     * Get the velocity difference between motors (for sync monitoring)
     */
    public double getVelocityMismatch() {
        return Math.abs(filteredVelocityLeft - filteredVelocityRight);
    }

    /**
     * Returns true if the shooter is within AT_SPEED_TOLERANCE of target velocity
     */
    public boolean isAtSpeed() {
        double averageVelocity = getFilteredVelocity();
        return Math.abs(TargetVelocity - averageVelocity) <= AT_SPEED_TOLERANCE;
    }

    /**
     * Returns the velocity error in ticks/sec
     */
    public double getVelocityError() {
        return Math.abs(TargetVelocity - getFilteredVelocity());
    }

    /**
     * Check if motors are synchronized within acceptable range
     */
    public boolean areMotorsSynced() {
        return getVelocityMismatch() < MOTOR_SYNC_WARNING_THRESHOLD;
    }

    /**
     * Emergency stop - immediately cuts power to both motors
     */
    public void emergencyStop() {
        shooterGroup.setPower(0);
        TargetVelocity = 0;
        shooterPIDF.setSetPoint(0);
    }
}