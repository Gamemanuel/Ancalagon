# Shooter Subsystem Improvements

## Overview
This document describes the improvements made to the shooter subsystem, including the addition of a second motor, improved velocity control, and better tunability.

## Key Changes

### 1. Dual Motor Support
- **Before**: Single motor (`shooter`)
- **After**: Two motors in a MotorGroup (`shooter` and `shooter2`)
- **Benefit**: More power, redundancy, and smoother velocity control using averaged feedback

### 2. Enhanced MotorGroup Class
Added velocity control methods to the `MotorGroup` class:
- `getVelocity()`: Returns average velocity from both motors (configurable via `PositionType`)
- `getLeaderVelocity()`: Get leader motor velocity
- `getFollowerVelocity()`: Get follower motor velocity
- `getLeader()` and `getFollower()`: Direct motor access for advanced control

### 3. Improved ShooterSubsystem API
New clean API methods:
- `getCurrentVelocity()`: Get current average velocity magnitude (returns positive values)
- `getVelocityError()`: Get error between target and actual
- `atTargetVelocity(tolerance)`: Check if at target within tolerance
- `getLeaderVelocity()` and `getFollowerVelocity()`: Individual motor velocities for debugging

**Important**: All velocity getter methods return positive magnitudes for intuitive telemetry display, even though motors may read negative velocities internally.

### 4. Better Tunability
All tuning parameters are now exposed as public static variables for FTC Dashboard:
```java
public static double kP = -0.00055;  // Negative because motor reads negative velocity
public static double kI = 0.0;
public static double kD = -0.00004;  // Negative because motor reads negative velocity
public static double kF = 0.0;
public static double kV = 0.00169;   // Feedforward velocity gain
public static double INTEGRAL_MIN = -0.5;
public static double INTEGRAL_MAX = 0.5;
```

**Note on Signs**: The PID coefficients are negative because the motors read negative velocities when spinning in the shooting direction. The code internally handles this by:
1. Negating target velocities when setting setpoints
2. Using negative PID gains
3. Returning positive magnitudes from getter methods for user-friendly telemetry

### 5. Anti-Windup Protection
- Added integration bounds to prevent integral windup
- Configurable via `INTEGRAL_MIN` and `INTEGRAL_MAX`
- Helps maintain stability during velocity changes

### 6. Enhanced Telemetry
ShooterTuning now displays:
- Target velocity (positive magnitude)
- Average velocity from both motors (positive magnitude)
- Individual motor velocities (leader and follower, positive magnitudes)
- Velocity error (positive when below target)
- All tuning parameters (kV, kP, kI, kD)

## Hardware Configuration Required

### Motor Names
You need to configure two motors in your robot configuration:
- **Primary motor**: `"shooter"` (leader)
- **Secondary motor**: `"shooter2"` (follower)

### Motor Directions
The code currently sets both motors to `Direction.FORWARD`. You may need to adjust this in `ShooterSubsystem.java` line 59-64:
```java
shooterMotors = new MotorGroup(
    shooterLeader,
    shooterFollower,
    DcMotorSimple.Direction.FORWARD,  // Adjust if needed
    DcMotorSimple.Direction.FORWARD   // Adjust if needed
);
```

**Common configurations:**
- If motors are mounted facing each other: One FORWARD, one REVERSE
- If motors are mounted same direction: Both FORWARD or both REVERSE

## Understanding Sign Conventions

The original code had a confusing sign convention that has been preserved for backward compatibility:

1. **Motor Behavior**: Motors read negative velocity when spinning in shooting direction
2. **User Input**: User passes positive target (e.g., `setTargetVelocity(1500)`)
3. **Internal**: Code negates to -1500 to match motor sign convention
4. **PID**: Uses negative gains (-0.00055) with negative setpoint
5. **Display**: Getter methods negate again to show positive values in telemetry

This means:
- When motor spins at -1500 ticks/sec (actual shooting)
- You set target to +1500 (user-friendly)
- Internal setpoint is -1500 (matches motor)
- Telemetry shows +1500 target, +1500 actual (user-friendly)

## Tuning Guide

### Step 1: Test Motor Directions
1. Set target velocity to a small positive value (e.g., 500 ticks/sec)
2. Check that both motors spin in the correct direction for shooting
3. If a motor spins backwards, change its direction in the code

### Step 2: Verify Sign Convention
1. Spin up motors to target velocity
2. Check telemetry shows positive values
3. Verify velocity error decreases as motors accelerate
4. Error should be positive when below target, negative when above

### Step 3: Tune Feedforward (kV)
1. Start with shooter at rest
2. Set target velocity to desired value (e.g., 1500 ticks/sec)
3. Observe steady-state velocity
4. Adjust kV until steady-state velocity is close to target (within ~10%)
   - If too slow: Increase kV magnitude (e.g., 0.00169 → 0.002)
   - If too fast: Decrease kV magnitude (e.g., 0.00169 → 0.0015)

### Step 4: Tune PID (kP, kD)
1. With kV tuned, adjust kP magnitude for faster response
   - Start with small values (e.g., -0.0001 to -0.001)
   - Increase magnitude until you see oscillation, then back off 50%
   - Keep sign negative (e.g., -0.00055 → -0.0008)
2. Add kD magnitude to reduce overshoot
   - Start very small (e.g., -0.00001 to -0.0001)
   - Increase magnitude until smooth response
   - Keep sign negative
3. kI can remain at 0 for velocity control (or very small if needed)

### Step 5: Tune Integration Bounds
- If you use kI, adjust INTEGRAL_MIN/MAX to prevent windup
- Typical values: -0.5 to 0.5

### Step 6: Verify
- Test shooting sequence in auto mode
- Check that velocity recovers quickly after each shot
- Monitor individual motor velocities for any discrepancies

## Migration Notes

### API Compatibility
The public API has been maintained for backward compatibility:
- `setTargetVelocity(double)` - Same interface, takes positive values
- `getTargetVelocity()` - Same interface, returns positive values
- Added new methods: `getCurrentVelocity()`, `getVelocityError()`, etc.

### Internal Changes
- Removed direct access to `shooter` motor (was public)
- Added `shooterMotors` MotorGroup (public for manual override)
- Removed `SCoeffs` static field (now individual kP, kI, kD, kF)

### Files Modified
- `ShooterSubsystem.java` - Core changes
- `MotorGroup.java` - Added velocity methods
- `ShooterTuning.java` - Enhanced telemetry
- `TeleOp.java` - Updated API calls
- `Shoot3CMD.java` - Updated API calls
- `Shoot3CMDClose.java` - Updated API calls
- `Shoot3CMDWrightly.java` - Updated API calls

## Testing Recommendations

1. **Static Test**: Run `ShooterTuning` OpMode
   - Verify both motors spin in correct direction
   - Verify velocities track together
   - Verify telemetry shows sensible positive values
   - Tune parameters via FTC Dashboard

2. **Dynamic Test**: Run autonomous shooting sequence
   - Verify velocity recovers after each shot
   - Check for consistency across multiple shots

3. **Long Run Test**: Run for extended period
   - Check for drift or instability
   - Monitor battery voltage effects

## Troubleshooting

### Motors don't spin or spin wrong direction
- Check motor configuration names in hardware config
- Adjust motor directions in `ShooterSubsystem.java` lines 59-64

### Telemetry shows negative values
- This is expected for internal motor readings
- Public getter methods should return positive values
- If not, check sign negations in getter methods

### Velocities don't track well
- Check that both motors have good encoder connections
- Verify motor directions are set correctly
- Increase kP magnitude for better tracking

### Oscillation or instability
- Reduce kP magnitude (make it less negative, e.g., -0.001 → -0.0005)
- Increase kD magnitude (make it more negative, e.g., -0.00004 → -0.00008)
- Check integration bounds if using kI

### One motor significantly different velocity
- Check mechanical coupling/alignment
- Check encoder connection
- Verify motor is functioning properly

### Velocity error stays positive but motors don't spin
- Check sign of PID coefficients (should be negative)
- Verify motor direction configuration
- Check power output isn't being inverted

## Benefits of These Improvements

1. **More Power**: Two motors provide more torque and faster spin-up
2. **Better Control**: Averaged velocity feedback reduces noise
3. **Easier Tuning**: All parameters accessible via Dashboard
4. **Better Stability**: Integration bounds prevent windup
5. **Better Debugging**: Individual motor velocities visible
6. **Cleaner API**: User-facing methods use intuitive positive values
7. **Maintainability**: Well-documented sign conventions

## References

This implementation follows best practices from successful FTC teams and uses patterns similar to those found in competitive FTC codebases. The use of:
- MotorGroup pattern for synchronized control
- PIDF with feedforward for velocity control
- Voltage compensation for consistent performance
- Integration bounds for stability

These are all proven techniques for high-performance FTC shooters.
