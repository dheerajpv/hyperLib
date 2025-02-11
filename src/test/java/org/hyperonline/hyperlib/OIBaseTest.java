package org.hyperonline.hyperlib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hyperonline.hyperlib.oi.ButtonData;
import org.hyperonline.hyperlib.oi.ButtonData.Action;
import org.hyperonline.hyperlib.oi.JoystickData;
import org.hyperonline.hyperlib.oi.MapJoystick;
import org.hyperonline.hyperlib.oi.MapJoystick.Role;
import org.hyperonline.hyperlib.oi.MapJoystick.Type;
import org.hyperonline.hyperlib.oi.OI;
import org.hyperonline.hyperlib.oi.WhenPressed;
import org.hyperonline.hyperlib.oi.WhenReleased;
import org.hyperonline.hyperlib.oi.WhileHeld;
import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj2.command.Command;

public class OIBaseTest {

    public static class SingleJoystickMap {
        @MapJoystick(port = 0, role = Role.LEFT_DRIVER, type = Type.LOGITECH_2_AXIS)
        public static class LeftDriver {
            @WhenPressed(0) public final Command foo = null;
            @WhenPressed(4) public final Command bar = null;
            @WhileHeld(2) public final Command baz = null;
            @WhenReleased(2) public final Command buzz = null;
        }
    }
    
    
    public static class DoubleJoystickMap {
        @MapJoystick(port = 0, role = Role.LEFT_DRIVER, type = Type.LOGITECH_2_AXIS)
        public static class LeftDriver {
            @WhenPressed(0) public final Command foo = null;
            @WhenPressed(1) public final Command bar = null;
        }
        
        @MapJoystick(port = 1, role = Role.RIGHT_DRIVER, type = Type.LOGITECH_3_AXIS)
        public static class RightDriver {
            @WhileHeld(0) public final Command baz = null;
            @WhenReleased(1) public final Command buzz = null;
        }
    }
    /**
     * Test mapping a single joystick
     */
    @Test
    public void testSingleJoystick() {
        OI oi = new OI(SingleJoystickMap.class, false);
        
        ArrayList<JoystickData> joysticks = oi.getJoystickData();
        assertEquals(1, joysticks.size());
        
        JoystickData js = joysticks.get(0);
        assertEquals(0, js.port());
        assertEquals("LeftDriver", js.name());
        assertEquals(Role.LEFT_DRIVER, js.role());
        assertEquals(Type.LOGITECH_2_AXIS, js.type());
    }
    /**
     * Test mapping with two joysticks
     */
    @Test
    public void testTwoJoysticks() {
        OI oi = new OI(DoubleJoystickMap.class, false);
        
        ArrayList<JoystickData> joysticks = oi.getJoystickData();
        assertEquals(2, joysticks.size());
        
        JoystickData js1 = joysticks.get(0);
        assertEquals(0, js1.port());
        assertEquals("LeftDriver", js1.name());
        assertEquals(Role.LEFT_DRIVER, js1.role());
        assertEquals(Type.LOGITECH_2_AXIS, js1.type());
        
        JoystickData js2 = joysticks.get(1);
        assertEquals(1, js2.port());
        assertEquals("RightDriver", js2.name());
        assertEquals(Role.RIGHT_DRIVER, js2.role());
        assertEquals(Type.LOGITECH_3_AXIS, js2.type());
    }
    /**
     * Test buttons on a single joystick
     */
    @Test
    public void testButtonsSingleJoystick() {
        OI oi = new OI(SingleJoystickMap.class, false);
        JoystickData js = oi.getJoystickData().get(0);
        List<ButtonData> buttons = js.buttons();
        
        assertEquals(4, buttons.size());
        
        assertEquals(0, buttons.get(0).port());
        assertEquals(Action.WHEN_PRESSED, buttons.get(0).action());
        assertEquals("foo", buttons.get(0).name());
        
        assertEquals(4, buttons.get(1).port());
        assertEquals(Action.WHEN_PRESSED, buttons.get(1).action());
        assertEquals("bar", buttons.get(1).name());
        
        assertEquals(2, buttons.get(2).port());
        assertEquals(Action.WHILE_HELD, buttons.get(2).action());
        assertEquals("baz", buttons.get(2).name());
        
        assertEquals(2, buttons.get(3).port());
        assertEquals(Action.WHEN_RELEASED, buttons.get(3).action());
        assertEquals("buzz", buttons.get(3).name());
    }
    
    /**
     * Test buttons with double joysticks
     */
    @Test
    public void testButtonsDoubleJoystick() {
        OI oi = new OI(DoubleJoystickMap.class, false);
        List<JoystickData> joysticks = oi.getJoystickData();
        List<ButtonData> buttons = joysticks.get(0).buttons();
        assertEquals(2, buttons.size());
        
        assertEquals(0, buttons.get(0).port());
        assertEquals(Action.WHEN_PRESSED, buttons.get(0).action());
        assertEquals("foo", buttons.get(0).name());
        
        assertEquals(1, buttons.get(1).port());
        assertEquals(Action.WHEN_PRESSED, buttons.get(1).action());
        assertEquals("bar", buttons.get(1).name());
        
        buttons = joysticks.get(1).buttons();
        assertEquals(2, buttons.size());
        
        assertEquals(0, buttons.get(0).port());
        assertEquals(Action.WHILE_HELD, buttons.get(0).action());
        assertEquals("baz", buttons.get(0).name());
        
        assertEquals(1, buttons.get(1).port());
        assertEquals(Action.WHEN_RELEASED, buttons.get(1).action());
        assertEquals("buzz", buttons.get(1).name());
    }
}
