package jmri.jmrit.beantable;

import apps.gui.GuiLafPreferencesManager;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import jmri.InstanceManager;
import jmri.Turnout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for the jmri.jmrit.beantable.TurnoutTableAction class.
 * @author Paul Bender Copyright (C) 2017	
 */
public class TurnoutTableActionTest extends AbstractTableActionBase {

    @Test
    public void testCTor() {
        Assert.assertNotNull("exists",a);
    }

    @Override
    public String getTableFrameName(){
        return Bundle.getMessage("TitleTurnoutTable");
    }

    @Override
    @Test
    public void testGetClassDescription(){
         Assert.assertEquals("Turnout Table Action class description","Turnout Table",a.getClassDescription());
    }

    /**
     * Check the return value of includeAddButton.
     * <p>
     * The table generated by this action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton(){
         Assert.assertTrue("Default include add button",a.includeAddButton());
    }

    /**
     * Check graphic state presentation.
     * @since 4.7.4
     */
    @Test
    public void testAddAndInvoke() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        a.actionPerformed(null); // show table
        // create 2 turnouts and see if they exist
        Turnout it1 = InstanceManager.turnoutManagerInstance().provideTurnout("IT1");
        Turnout it2 = InstanceManager.turnoutManagerInstance().provideTurnout("IT2");
        it1.setCommandedState(Turnout.THROWN);
        it1.setCommandedState(Turnout.CLOSED);

        // set graphic state column display preference to false, read by createModel()
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setGraphicTableState(false);

        TurnoutTableAction _tTable;
        _tTable = new TurnoutTableAction();
        Assert.assertNotNull("found TurnoutTable frame", _tTable);

        // set to true, use icons
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setGraphicTableState(true);
        TurnoutTableAction _t1Table;
        _t1Table = new TurnoutTableAction();
        Assert.assertNotNull("found TurnoutTable1 frame", _t1Table);

        _t1Table.addPressed(null);
        JFrame af = JFrameOperator.waitJFrame(Bundle.getMessage("TitleAddTurnout"), true, true);
        Assert.assertNotNull("found Add frame", af);
        // close Add pane
        _t1Table.cancelPressed(null);
        // more Turnout Add pane tests are in TurnoutTableWindowTest

        // Open Automation pane to test Automation menu
        jmri.jmrit.turnoutoperations.TurnoutOperationFrame tof = new jmri.jmrit.turnoutoperations.TurnoutOperationFrame(null);
        // create dialog (bypassing menu)
        JDialogOperator am = new JDialogOperator("Turnout Operation Editor");
        Assert.assertNotNull("found Automation menu dialog", am);
        log.debug("Ops dialog found");
        // close pane
        JButtonOperator jbo = new JButtonOperator(am, "OK");
        jbo.pushNoBlock(); // instead of .push();

        // Open Speed pane to test Speed menu, actually a JOptionPane (disabled for now, as is doesn't close)
//        _t1Table.setDefaultSpeeds(null); // create dialog (bypassing menu)
//        JDialogOperator as = new JDialogOperator(Bundle.getMessage("TurnoutGlobalSpeedMessageTitle"));
//        Assert.assertNotNull("found Speeds menu dialog", as);
//        log.debug("Speed pane found");
//        // close pane
//        JButtonOperator jbs = new JButtonOperator(as, "OK");
//        jbs.pushNoBlock(); //instead of .push();

        // clean up
        af.dispose();
        am.dispose();
        //as.dispose(); // disabled. see above
        tof.dispose();
        _tTable.dispose();
        _t1Table.dispose();
    }

    // The minimal setup for log4J
    @Before
    @Override
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        jmri.util.JUnitUtil.resetInstanceManager();
        jmri.util.JUnitUtil.initInternalTurnoutManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();
        a = new TurnoutTableAction();
    }

    @After
    @Override
    public void tearDown() {
        a = null;
        jmri.util.JUnitUtil.resetInstanceManager();
        apps.tests.Log4JFixture.tearDown();
    }

    private final static Logger log = LoggerFactory.getLogger(TurnoutTableActionTest.class.getName());

}
