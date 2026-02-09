package org.firstinspires.ftc.teamcode.DriverControl.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx.ButtonEx;
import org.firstinspires.ftc.teamcode.Utils.Library.GamepadEx.GamepadEx;

import java.util.ArrayList;
import java.util.List;

public class ButtonExTest extends OpMode {
    GamepadEx gamepadEx;
    ButtonEx A;

    public void init() {
        gamepadEx = new GamepadEx();
        gamepadEx.buttons.add(A = new ButtonEx(gamepad1.a));
    }

    public void loop() {
        telemetry.addData("Pressed", A.pressed());
        telemetry.addData("Released", A.released());
        telemetry.addData("Was just pressed", A.wasJustPressed());
        telemetry.addData("Was just released", A.wasJustReleased());
        telemetry.addData("State just changed", A.stateJustChanged());

        telemetry.update();

        gamepadEx.update();
    }
}
