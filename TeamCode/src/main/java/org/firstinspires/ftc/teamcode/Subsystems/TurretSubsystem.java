package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TurretSubsystem {
    public DcMotorEx Turret;

    public TurretSubsystem(HardwareMap hMap) {
        Turret = hMap.get(DcMotorEx.class, "turntable");
    }

    public void setPower(double power) {
        Turret.setPower(power);
    }
}