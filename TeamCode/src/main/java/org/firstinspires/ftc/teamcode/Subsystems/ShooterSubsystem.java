package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.Utils.SolversLib.PIDFController.PIDFController;

@Config
public class ShooterSubsystem {

    public DcMotorEx shooter;
    public PIDFController shooterPIDF;
    private VoltageSensor batteryVoltageSensor;

    // ===== TUNING PARAMETERS (Dashboard Tunable) =====
    // Start with these conservative values and tune using the guide below
    public static PIDFCoefficients SCoeffs = new PIDFCoefficients(-0.0008, -0.00001, -0.00006, 0);
    public static double kV = 0.00169;  // Feedforward velocity constant

    // Velocity filtering (reduces encoder noise)
    public static double VELOCITY_FILTER_GAIN = 0.7;  // 0.0 = no filter, 1.0 = max smoothing

    // Acceleration limiting (prevents overshoot)
    public static double MAX_ACCEL_PER_LOOP = 150.0;  // ticks/sec² change limit

    // Tolerance for "at speed" detection
    public static double AT_SPEED_TOLERANCE = 50.0;  // ±50 ticks/sec

    // Internal state
    private double TargetVelocity = 0;
    private double filteredVelocity = 0;
    private double lastCommandedPower = 0;
    private long lastUpdateTime = 0;

    public ShooterSubsystem(HardwareMap hMap) {
        shooter = hMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);  // Reduces friction

        batteryVoltageSensor = hMap.voltageSensor.iterator().next();
        shooterPIDF = new PIDFController(SCoeffs);

        lastUpdateTime = System.currentTimeMillis();
    }

    public void periodic() {
        // 1. Update Coefficients (allows live tuning)
        shooterPIDF.setCoefficients(SCoeffs);

        // 2. Get Current State
        double rawVelocity = shooter.getVelocity();
        double currentVoltage = batteryVoltageSensor.getVoltage();

        // 3. Apply velocity filtering (exponential moving average)
        filteredVelocity = (VELOCITY_FILTER_GAIN * filteredVelocity) +
                ((1 - VELOCITY_FILTER_GAIN) * rawVelocity);

        // 4. PID Calculation (using filtered velocity)
        double pidOutput = shooterPIDF.calculateOutput(filteredVelocity);

        // 5. Feedforward
        double feedforward = kV * TargetVelocity;

        // 6. Combine and apply voltage compensation
        double targetPower = (feedforward + pidOutput) * (12.0 / currentVoltage);

        // 7. Acceleration limiting (prevents sudden power spikes)
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastUpdateTime) / 1000.0;
        if (dt > 0.001) {
            double maxDeltaPower = (MAX_ACCEL_PER_LOOP * dt) / 1000.0;  // Convert to power units
            double powerChange = targetPower - lastCommandedPower;

            if (Math.abs(powerChange) > maxDeltaPower) {
                targetPower = lastCommandedPower + Math.signum(powerChange) * maxDeltaPower;
            }
        }

        // 8. Clamp and apply
        targetPower = Math.max(-1.0, Math.min(1.0, targetPower));
        shooter.setPower(targetPower);

        // Update state
        lastCommandedPower = targetPower;
        lastUpdateTime = currentTime;
    }

    public void setTargetVelocity(double target) {
        TargetVelocity = -target;
        shooterPIDF.setSetPoint(-target);
    }

    public double getTargetVelocity() {
        return TargetVelocity;
    }

    public double getFilteredVelocity() {
        return filteredVelocity;
    }

    /**
     * Returns true if the shooter is within AT_SPEED_TOLERANCE of target velocity
     */
    public boolean isAtSpeed() {
        return Math.abs(TargetVelocity - filteredVelocity) <= AT_SPEED_TOLERANCE;
    }

    /**
     * Returns the velocity error in ticks/sec
     */
    public double getVelocityError() {
        return Math.abs(TargetVelocity - filteredVelocity);
    }
}