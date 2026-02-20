package org.firstinspires.ftc.teamcode.DriverControl.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.Utils.Alliance;
import org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx.ButtonEx;
import org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx.GamepadEx;
import org.firstinspires.ftc.teamcode.Utils.Robot;

public abstract class TeleOp extends OpMode {

    private final Alliance alliance;
    Robot robot;
    GamepadEx gamepad2Ex;
    ButtonEx shooterToggleButton;
    boolean shooterManualOverride = false;

    public TeleOp(Alliance alliance) {
        this.alliance = alliance;
    }

    public void init() {
        // Link telemetry from RC to FTC-Dashboard
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Setup the gamepadEx variable
        gamepad2Ex = new GamepadEx();
        gamepad2Ex.buttons.add(shooterToggleButton = new ButtonEx(gamepad2.y));

        // Call the subsystems
        robot = new Robot(hardwareMap, alliance);
    }

    public void loop() {
        // ======================================================
        // Refresh Values for Subsystems
        // ======================================================

        // fetch ll
        robot.ll.periodic();

        // ======================================================
        // Drivetrain Logic - Gamepad 1 Controls
        // ======================================================
        robot.sixWheelCMD.arcadeDrive(gamepad1.left_stick_y, gamepad1.right_stick_x);

        // ======================================================
        // Intake Logic - Gamepad 2 Controls
        // ======================================================
        robot.intake.intake.setPower(gamepad2.left_trigger - gamepad2.right_trigger);

        // ======================================================
        // Turret Logic - Gamepad 2 Controls
        // ======================================================

        // Manual override triggers
        if (gamepad2.right_bumper) {
            robot.turretSubsystem.setPower(-0.5);
        } else if (gamepad2.left_bumper) {
            robot.turretSubsystem.setPower(0.5);
        } else {
            // Auto Turret Tracking
            robot.turretAuto.faceAprilTag(1.5, alliance);
        }

        // ======================================================
        // Shooter Logic - Gamepad 2 Controls
        // ======================================================

        // Toggle Manual override w/ Y button on gamepad2
        if (shooterToggleButton.wasJustPressed()) {
            shooterManualOverride = !shooterManualOverride;
        }

        // Manual mode
        if (shooterManualOverride) {

            robot.shooter.shooterMotors.setPower(-gamepad2.right_stick_y);
        }

        // Automatic Mode
        else {
            // Run the auto subsystem
            robot.shooterAutoCmd.execute();

            // Run the Periodic loop
            robot.shooter.periodic();
        }

        // ======================================================
        // End Functions + Cleanup
        // ======================================================

        // Telemetry
        telemetry.addData("Shooter Mode", shooterManualOverride ? "MANUAL" : "AUTO"); // fetch the shooting mode?
        telemetry.addData("Shooter Target", robot.shooter.getTargetVelocity()); // fetch shooter target velocity
        telemetry.addData("Shooter Actual", robot.shooter.getCurrentVelocity()); // fetch shooter actual velocity

        // update static variables
        telemetry.update();
        gamepad2Ex.update();
    }
}