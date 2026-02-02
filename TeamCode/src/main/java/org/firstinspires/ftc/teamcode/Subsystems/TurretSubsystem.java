package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Utils.Alliance;

public class TurretSubsystem {
    CRServo Turret;
    LLSubsystem ll;

    public TurretSubsystem(HardwareMap hMap, Alliance alliance) {
        Turret = hMap.get(CRServo.class, "turntable");
        ll = new LLSubsystem(hMap, alliance);
    }

    public void setPower(double power) {
        Turret.setPower(power);
    }
}