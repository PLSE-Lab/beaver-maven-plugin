/*
 * MIT License
 *
 * Copyright (c) 2022 ECU Programming Languages and Software Engineering Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.ecu.cs.plse.plugin.beaver;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
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