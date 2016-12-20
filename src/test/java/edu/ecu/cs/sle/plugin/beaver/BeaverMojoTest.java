package edu.ecu.cs.sle.plugin.beaver;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;

public class BeaverMojoTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected BeaverMojo makeBeaverMojo(String testCase) throws Exception {
        // Find the proper POM file for the test project
        File unitBasedir = new File(getBasedir(), "src/test/resources/unit");
        assertNotNull(unitBasedir);
        assertTrue(unitBasedir.exists());

        File testPom = new File(unitBasedir, testCase + "/plugin-config.xml");
        assertNotNull(testPom);
        assertTrue(testPom.exists());

        BeaverMojo mojo = new BeaverMojo();
        assertNotNull(mojo);

        configureMojo(mojo, "beaver-maven-plugin", testPom);
        if (getVariableValueFromObject(mojo, "project") == null) {
            setVariableValueToObject(mojo, "project", new MavenProjectStub());
        }
        return mojo;
    }

    @Test
    public void testWithStandardOptions() throws Exception {
        BeaverMojo mojo = makeBeaverMojo("standard-options-test");
        assertNotNull(mojo);

        mojo.execute();
    }
}