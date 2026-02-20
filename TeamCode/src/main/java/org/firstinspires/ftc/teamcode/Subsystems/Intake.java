package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public DcMotorEx intake;
    public Intake (HardwareMap hMap) {
        intake = hMap.get(DcMotorEx.class, "intake");
    }
}
