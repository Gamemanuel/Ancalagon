package org.firstinspires.ftc.teamcode.DriverControl.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx.ButtonEx;
import org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx.GamepadEx;
import org.firstinspires.ftc.teamcode.Utils.Robot;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

public abstract class TeleOp extends OpMode {

    Robot robot;
    private final Alliance alliance;
    GamepadEx gamepad2Ex;
    ButtonEx shooterToggleButton;
    boolean shooterManualOverride = false;

    public TeleOp(Alliance alliance) {
        this.alliance = alliance;
    }

    public void init() {
        // link telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        gamepad2Ex = new GamepadEx();
        gamepad2Ex.buttons.add(shooterToggleButton = new ButtonEx(gamepad2.y));
        // call the subsystems
        robot = new Robot(hardwareMap, alliance);
    }

    public void loop() {
        // run the ll loop every loop
        robot.ll.periodic();

        // run the drivetrain drive code.
        robot.sixWheelCMD.arcadeDrive(gamepad1.left_stick_y, gamepad1.right_stick_x);

        // --- Intake ---
        robot.intake.intake.setPower(gamepad2.left_trigger - gamepad2.right_trigger);

        // --- Turret Logic ---
        // Manual override triggers
        if (gamepad2.right_bumper) {
            robot.turretSubsystem.setPower(-0.5);
        } else if (gamepad2.left_bumper) {
            robot.turretSubsystem.setPower(0.5);
        } else {
            // Auto Turret Tracking
            robot.turretAuto.faceAprilTag(1.5, alliance);
        }

        // --- Shooter Logic ---
        // Toggle Manual override with Y button on gamepad2
        if (shooterToggleButton.wasJustPressed()) {
            shooterManualOverride = !shooterManualOverride;
        }

        if (shooterManualOverride) {
            // --- Manual mode ---
            robot.shooter.shooterMotors.setPower(-gamepad2.right_stick_y);
        } else {
            // --- Automatic Mode ---
            // Run the auto subsystem
            robot.shooterAutoCmd.execute();

            // Run the Periodic loop (Calculates PID + Feedforward)
            robot.shooter.periodic();
        }

        // Telemetry
        telemetry.addData("Shooter Mode", shooterManualOverride ? "MANUAL" : "AUTO");
        telemetry.addData("Shooter Target", robot.shooter.getTargetVelocity());
        telemetry.addData("Shooter Actual", robot.shooter.getCurrentVelocity());

        telemetry.update();

        gamepad2Ex.update();
    }
}