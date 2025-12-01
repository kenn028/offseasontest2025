package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.kauailabs.navx.frc.AHRS;
//import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.Turret.StickRotationCommand;
import frc.robot.commands.Turret.spinCommand;
import frc.robot.commands.Turret.negativeSpin;
import frc.robot.commands.Turret.spinToAngleCommand;
import frc.robot.Constants;
import frc.robot.Constants.turretConstants;
import frc.robot.commands.Turret.LockontoTargetCommand;

public class Turret extends SubsystemBase {


    private final SparkMax turretMotor;
    private final PIDController pidController;
    private final AHRS gyro;

    private double outputSpeed;
    private double joystickSpeed;
    private double currentAngle;
    private double targetAngle;

    private static final double GEAR_RATIO = 7.5;

    public final int rotationAxis = XboxController.Axis.kRightX.value;

    public Turret() {
        

        turretMotor = new SparkMax(Constants.turretConstants.turretMotorChannel, SparkLowLevel.MotorType.kBrushless);
        setDefaultCommand(new StickRotationCommand(this));

        pidController = new PIDController(Constants.turretConstants.kP, Constants.turretConstants.kI, Constants.turretConstants.kD);
        //pidController.enableContinuousInput(-180.0, 180.0);
        pidController.setTolerance(Constants.turretConstants.turretTolerance);
        gyro = new AHRS(SPI.Port.kMXP);
    }

    ShuffleboardTab tab = Shuffleboard.getTab("Elevator");
    GenericEntry pEntry = tab.add("SET P", turretConstants.kP).getEntry();
    GenericEntry dEntry = tab.add("SET D", turretConstants.kD).getEntry();
    GenericEntry iEntry = tab.add("SET I", turretConstants.kI).getEntry();

    
    public boolean pidEnabled;

    public void enablePID() {
        pidEnabled = true;
    }

    public void disablePID() {
        pidEnabled = false;
        turretMotor.set(0);
    }

    public void updateJoystick(double joystickSpeed) {
        this.joystickSpeed = joystickSpeed;
    }

    public void runTurret(double speed) {
        turretMotor.set(speed);
    }

    public void updatePID() {
        double currentAngle = getTurretAngle();
        double output = pidController.calculate(currentAngle, targetAngle);

        turretMotor.set(output);
    }

    @Override
    public void periodic() {

        pidController.setP(pEntry.getDouble(turretConstants.kP));
        pidController.setI(iEntry.getDouble(turretConstants.kI));
        pidController.setD(dEntry.getDouble(turretConstants.kD));

        System.out.println("hello, pid is running");
        if (pidEnabled) {
            System.out.println("hello, pid is UPDATEDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
            updatePID();
        }
    }


    // public void turretMath(double joystickSpeed) {
    //     if(Math.abs(joystickSpeed) > 0.01) {
    //         outputSpeed = joystickSpeed;
    //     } else {
    //         outputSpeed = 0;
    //     }
    //     turretMotor.set(outputSpeed);
    // }

    public double getTurretAngle() {
        double motorRotations = turretMotor.getEncoder().getPosition();
        double turretRotations = motorRotations / GEAR_RATIO;
        return turretRotations * 360.0;
    }

    public void zeroEncoder() {
        turretMotor.getEncoder().setPosition(0.0);
    }

    public void setTargetAngle(double targetAngle) {
        this.targetAngle = targetAngle;
    }

    public boolean isAtTargetAngle() {
        return pidController.atSetpoint();
    }

    // public void setTargetAngle(double targetAngle) {
    //     this.targetAngle = targetAngle; // Update the desired turret angle
    // }



    // public void updateTurretAngle() {
    //     currentAngle = getTurretAngle();

    //     double output = pidController.calculate(currentAngle, targetAngle);

    //     turretMotor.set(output);
    // }


    public double getRobotHeading() {
        return gyro.getAngle(); 
    }

    public void setIdle() {
        turretMotor.set(0);
        joystickSpeed = 0;
    }


    public Command stickRotation(){
        return new StickRotationCommand(this);
    }

    public Command lockontoTargetCommand(){
        return new LockontoTargetCommand(this);
    }

    public Command spinCommand() {
        return new spinCommand(this);
    }

    public Command negativeSpin() {
        return new negativeSpin(this);
    }

    public Command spinToAngleCommand(double angle) {
        return new spinToAngleCommand(this, angle);
    }

    public void setDefaultCommand(Turret turretSubsystem, Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDefaultCommand'");
    }

}
