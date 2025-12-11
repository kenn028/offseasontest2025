package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.Turret.LockontoTargetCommand;
// import frc.robot.commands.TeleopSwerve;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.Turret;
// import frc.robot.subsystems.Coral;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.Turret.LockontoTargetCommand;

public class RobotContainer {
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();

  public final static CommandXboxController driver = new CommandXboxController(0);
  public final static CommandXboxController operator = new CommandXboxController(1);

  // private final Swerve s_Swerve = Swerve.getInstance();

  private final int translationAxis = XboxController.Axis.kLeftY.value;
  private final int strafeAxis = XboxController.Axis.kLeftX.value;
  private final int rotationAxis = XboxController.Axis.kRightX.value;

  // private final Coral coralSubsystem = new Coral();
  private final Turret turretSubsystem = new Turret();

  public RobotContainer() {

    // s_Swerve.setDefaultCommand(
    //             new TeleopSwerve(
    //                     s_Swerve,
    //                     () -> -driver.getRawAxis(translationAxis),
    //                     () -> -driver.getRawAxis(strafeAxis),
    //                     () -> -driver.getRawAxis(rotationAxis),
    //                     () -> false // true = robotcentric

    //             ));

    configureBindings();

  }

  private void configureBindings() {

    driver.b().whileTrue(turretSubsystem.spinCommand());
    driver.a().whileTrue(turretSubsystem.negativeSpin());

    driver.x().whileTrue(turretSubsystem.lockontoTargetCommand());
    driver.rightBumper().whileTrue(turretSubsystem.resetSetpoint());
    driver.y().onTrue(turretSubsystem.spinToAngleCommand(Constants.turretConstants.targetAngle));

    //driver.rightBumper().onTrue(turretSubsystem.zeroEncoder());
    //driver.leftBumper().onTrue(coralSubsystem.L1Command());
    
  }

  public Command getAutonomousCommand() {
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
