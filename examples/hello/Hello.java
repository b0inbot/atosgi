package boinsoft.atosgi.example;

import org.osgi.service.component.annotations.*;

/**
 * An Example component providing the "hello:status" gogo command.
 *
 * <p>Provides an example command, start and finish methods.
 */
@Component(
    // the properties tell gogo how to find this.
    property = {"osgi.command.scope=hello", "osgi.command.function=status"},
    // immediate and service must be set for gogo for some reason??
    immediate = true,
    service = Object.class)
public class Hello {

  public String status() {
    return "status=feeling-fine";
  }

  // activate and deactivate correspond to when the
  // component is enabled. on immediate=true, this is on
  // bundle startup but otherwise is on first reference
  // of the service interface that resolves to this
  // component.

  @Activate
  public void activate() {
    System.out.println("Hello SCR: activate");
  }

  @Deactivate
  public void deactivate() {
    System.out.println("Hello SCR: deactivate");
  }
}
