package jmri.jmrix.tams.configurexml;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TamsTurnoutManagerXmlTest.java
 *
 * Test for the TamsTurnoutManagerXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class TamsTurnoutManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("TamsTurnoutManagerXml constructor",new TamsTurnoutManagerXml());
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

