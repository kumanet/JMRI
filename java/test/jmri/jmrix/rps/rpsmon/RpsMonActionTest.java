package jmri.jmrix.rps.rpsmon;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrix.rps.RpsSystemConnectionMemo;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class RpsMonActionTest {

    private RpsSystemConnectionMemo memo = null;

    @Test
    public void testCTor() {
        RpsMonAction t = new RpsMonAction(memo);
        Assert.assertNotNull("exists",t);
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        memo = new RpsSystemConnectionMemo();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(RpsMonActionTest.class);

}
