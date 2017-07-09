package jmri.jmrix.nce;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jmri.DccLocoAddress;

/**
 * NceConsistTest.java
 *
 * Description:	tests for the jmri.jmrix.nce.NceConsist class
 *
 * @author	Paul Bender Copyright (C) 2016,2017
 */

public class NceConsistTest extends jmri.implementation.AbstractConsistTestBase {

    // infrastructure objects, populated by setUp;
    NceInterfaceScaffold nnis;
    NceSystemConnectionMemo memo;


    @Test public void testCtor2() {
        // DccLocoAddress constructor test.
        NceConsist c = new NceConsist(new DccLocoAddress(3, false),memo);
        // send a reply the memory read instruction trigged by the constructor above.
        nnis.sendTestReply(new NceReply(nnis,"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),null);
        Assert.assertNotNull(c);
        c.dispose();
    }

    @Ignore("not quite ready yet")
    @Override
    @Test public void checkDisposeMethod(){
        // verify that c has been added to the traffic controller's
        // list of listeners.
        int listeners = nnis.numListeners();
        Assert.assertEquals("dispose check",listeners -1, nnis.numListeners());
    }

    @Override
    @Test public void checkSizeLimitAdvanced(){
        c.setConsistType(jmri.Consist.ADVANCED_CONSIST);
        Assert.assertEquals("Advanced Consist Limit",6,c.sizeLimit());   
    } 

    @Override
    @Test public void checkContainsAdvanced(){
        c.setConsistType(jmri.Consist.ADVANCED_CONSIST);
        jmri.DccLocoAddress A = new jmri.DccLocoAddress(200,true);
        jmri.DccLocoAddress B = new jmri.DccLocoAddress(250,true);
        // nothing added, should be false for all.
        Assert.assertFalse("Advanced Consist Contains",c.contains(A));   
        Assert.assertFalse("Advanced Consist Contains",c.contains(B));   
        // add just A
        c.restore(A,true); // use restore here, as it does not send
                           // any data to the command station
        nnis.sendTestReply(new NceReply(nnis,"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),null);
        Assert.assertTrue("Advanced Consist Contains",c.contains(A));   
        Assert.assertFalse("Advanced Consist Contains",c.contains(B));   
        // then add B
        c.restore(B,false);
        nnis.sendTestReply(new NceReply(nnis,"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),null);
        Assert.assertTrue("Advanced Consist Contains",c.contains(A));   
        Assert.assertTrue("Advanced Consist Contains",c.contains(B));   
    }

    @Override
    @Test public void checkGetLocoDirectionAdvanced(){
        c.setConsistType(jmri.Consist.ADVANCED_CONSIST);
        jmri.DccLocoAddress A = new jmri.DccLocoAddress(200,true);
        jmri.DccLocoAddress B = new jmri.DccLocoAddress(250,true);
        c.restore(A,true); // use restore here, as it does not send
                           // any data to the command station
        nnis.sendTestReply(new NceReply(nnis,"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),null);
        c.restore(B,false); // revese direction.
        nnis.sendTestReply(new NceReply(nnis,"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),null);
        Assert.assertTrue("Direction in Advanced Consist",c.getLocoDirection(A));   
        Assert.assertFalse("Direction in Advanced Consist",c.getLocoDirection(B));   
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        jmri.util.JUnitUtil.resetInstanceManager();
        // prepare an interface
        nnis = new NceInterfaceScaffold();
        memo = new NceSystemConnectionMemo();
        memo.setNceTrafficController(nnis);
        c = new NceConsist(3,memo);
        // send a reply the memory read instruction trigged by the constructor above.
        nnis.sendTestReply(new NceReply(nnis,"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),null);
    }
   
    @After
    public void tearDown() {
        c.dispose();
        c = null;
        apps.tests.Log4JFixture.tearDown();
        jmri.util.JUnitUtil.resetInstanceManager();
    }

}
