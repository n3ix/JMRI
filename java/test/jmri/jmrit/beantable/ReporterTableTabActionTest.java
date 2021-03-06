package jmri.jmrit.beantable;

import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import jmri.InstanceManager;

import jmri.Reporter;
import jmri.ReporterManager;
import jmri.jmrix.internal.InternalReporterManager;
import jmri.jmrix.internal.InternalSystemConnectionMemo;
import jmri.managers.AbstractProxyManager;
import jmri.managers.ProxyReporterManager;
import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.*;
import org.netbeans.jemmy.operators.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class ReporterTableTabActionTest extends AbstractTableTabActionBase {

    @Test
    public void testCTor() {
        Assert.assertNotNull("exists", a);
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleReporterTable");
    }

    /**
     * Check the return value of includeAddButton.
     * <p>
     * The table generated by this action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton() {
        Assert.assertTrue("Default include add button", a.includeAddButton());
    }
    
    @Test
    public void testMultiSystemTabs(){
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        
        JUnitUtil.resetInstanceManager();
        // Not returning null v4.23.4ish
        // Assert.assertNull("No Manager at start",InstanceManager.getNullableDefault(ReporterManager.class));
    
        ProxyReporterManager l = new ProxyReporterManager(); // has 2 systems: IR, JR
        l.addManager(new InternalReporterManager(new InternalSystemConnectionMemo("J", "Juliet")));
        l.addManager(new InternalReporterManager(new InternalSystemConnectionMemo("I", "India")));
        InstanceManager.setReporterManager(l);
        
        // Test that Proxy Reporter Manager has Juliet, India, and nothing else.
        ReporterManager plm = InstanceManager.getDefault(ReporterManager.class);
        if (!(plm instanceof AbstractProxyManager)) {
            Assert.fail("Instance ReporterManager Not a proxy Reporter Manager");
        }
        
        try {
            @SuppressWarnings("unchecked")
            AbstractProxyManager<Reporter> proxy = (AbstractProxyManager<Reporter>) plm;
            int numLm = proxy.getDisplayOrderManagerList().size();
            Assert.assertEquals("2 Reporter Managers, Juliet, India",2, numLm);
            
            String s = proxy.getDisplayOrderManagerList().get(0).getMemo().getUserName();
            Assert.assertEquals("Reporter Manager 0 , Juliet","Juliet", s);
            
            s = proxy.getDisplayOrderManagerList().get(1).getMemo().getUserName();
            Assert.assertEquals("Reporter Manager 1, India","India", s);
        } catch (ClassCastException e){
            Assert.fail("catch Instance Reporter Manager Not a proxy Reporter Manager");
        }
        
        
        InstanceManager.getDefault(ReporterManager.class).provideReporter("IR1");
        InstanceManager.getDefault(ReporterManager.class).provideReporter("IR2");
        InstanceManager.getDefault(ReporterManager.class).provideReporter("IR3");
        InstanceManager.getDefault(ReporterManager.class).provideReporter("IR4");
        InstanceManager.getDefault(ReporterManager.class).provideReporter("IR5");
        
        InstanceManager.getDefault(ReporterManager.class).provideReporter("JR8");
        InstanceManager.getDefault(ReporterManager.class).provideReporter("JR9");
        
        TabbedReporterTableFrame sf = new TabbedReporterTableFrame();
        sf.initTables();
        sf.initComponents();
        sf.pack();
        sf.setVisible(true);
        
        JFrame f = JFrameOperator.waitJFrame(sf.getTitle(), true, true);
        JFrameOperator jfo = new JFrameOperator(f);
        JTabbedPaneOperator tabOperator = new JTabbedPaneOperator(jfo);
        Assert.assertEquals("3 manager tabs",3, tabOperator.getTabCount());
        
        tabOperator.selectPage("All");
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        JTableOperator controltbl = new JTableOperator(jfo, 0);
        Assert.assertEquals("All tab 7 Reporters",7, controltbl.getRowCount());
        
        tabOperator.selectPage("Juliet");
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        controltbl = new JTableOperator(jfo, 0);
        Assert.assertEquals("Juliet tab 2 Reporters",2, controltbl.getRowCount());
        
        tabOperator.selectPage("India");
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        controltbl = new JTableOperator(jfo, 0);
        Assert.assertEquals("India tab 5 Reporters",5, controltbl.getRowCount());
        
        // jmri.util.swing.JemmyUtil.pressButton(jfo, "Not a Button, pause test");
        jfo.requestClose();
    
    }
    
    private static class TabbedReporterTableFrame extends ListedTableFrame<Reporter> {
        
        public TabbedReporterTableFrame(){
            super();
            tabbedTableItemListArrayArray.clear(); // reset static BeanTable list
        }
        
        @Override
        public void initTables() {
            addTable("jmri.jmrit.beantable.ReporterTableTabAction", Bundle.getMessage("MenuItemReporterTable"), false);
        }
    }

    @BeforeEach
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();
        helpTarget = "package.jmri.jmrit.beantable.ReporterTable"; 
        a = new ReporterTableTabAction();
    }

    @AfterEach
    @Override
    public void tearDown() {
        a = null;
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(ReporterTableTabActionTest.class);

}
