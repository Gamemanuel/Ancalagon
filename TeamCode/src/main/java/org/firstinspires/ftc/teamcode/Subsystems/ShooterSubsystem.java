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

    // SIGNS FIXED: kP and kD must be POSITIVE.
    // setPoint = +950, velocity = 0 → error = +950
    public static PIDFCoefficients SCoeffs = new PIDFCoefficients(-0.00055, 0, -0.00004, 0);
    public static double kV = 0.000633;

    private double TargetVelocity = 0;

    public ShooterSubsystem(HardwareMap hMap) {
        shooter = hMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        batteryVoltageSensor = hMap.voltageSensor.iterator().next();

        shooterPIDF = new PIDFController(SCoeffs);
    }

    public void periodic() {
        // 1. Update Coefficients
        shooterPIDF.setCoefficients(SCoeffs);

        // 2. Get Current State
        double currentVelocity = shooter.getVelocity();
        double currentVoltage = batteryVoltageSensor.getVoltage();

        // 3. PID Calculation
        double pidOutput = shooterPIDF.calculateOutput(currentVelocity);

        // 4. Feedforward
        double feedforward = kV * TargetVelocity;

        // 5. Voltage Compensation + Clamp
        double targetPower = (feedforward + pidOutput) * (12.0 / currentVoltage);
        targetPower = Math.max(-1.0, Math.min(1.0, targetPower));

        // 6. Apply
        shooter.setPower(targetPower);
    }

    public void setTargetVelocity(double target) {
        TargetVelocity = -target;
        shooterPIDF.setSetPoint(-target);
    }

    public double getTargetVelocity() {
        return TargetVelocity;
    }
}