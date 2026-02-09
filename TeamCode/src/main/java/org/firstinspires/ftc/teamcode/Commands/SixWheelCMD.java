package org.firstinspires.ftc.teamcode.Commands;

import static java.lang.Thread.sleep;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.Utils.Library.Motor.MotorGroup;

public class SixWheelCMD {

    public SixWheelCMD() {}
    /**
     * A basic command that runs the drivetrain for a specified amount of time and then stops.
     *
     * @Creator Will (Finch)
     * @param left left side of drivetrain
     * @param right right side of drivetrain
     * @param time how long it runs
     */
    public void driveBasic(DcMotorEx leftSide, DcMotorEx rightSide, float left, float right, long time) throws InterruptedException {
        //set all motor powers
        setMotors(leftSide, rightSide, left,right);
        sleep(time); //wait for however long
        //stop
        setMotors(leftSide, rightSide,0,0);
    }
    /**
     * A basic command that sets the motors to a specified power. Beware that it won't stop until
     * said to.
     *
     * @Creator Will (Finch)
     * @param leftSide motors or motor group left side of drivetrain
     * @param rightSide motors or motor group of the right side of drivetrain
     * @param leftPower left side of drivetrain
     * @param rightPower right side of drivetrain
     */
    public void setMotors(DcMotorEx leftSide, DcMotorEx rightSide, float leftPower, float rightPower) {
        leftSide.setPower((double) leftPower);
        rightSide.setPower((double) rightPower);
    }

    public void setMotors(MotorGroup leftSide, MotorGroup rightSide, float leftPower, float rightPower) {
        leftSide.setPower((double) leftPower);
        rightSide.setPower((double) rightPower);
    }
}