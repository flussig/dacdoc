package org.flussig.documentation.text;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for simple DacDocAnchor.
 */
public class DacDocAnchorTest {
    @Test
    public void testParseDacDocPlaceholder() {
        String anchorText = "!DACDOC{[self](./README.md)}!";

        try {
            DacDocAnchor anchor = DacDocAnchor.from(anchorText);

            DacDocValidationResult validationResult = anchor.validate();

            assertTrue("internal validation of dacdoc anchor failed", validationResult.getIssues().isEmpty() );
        } catch(Exception e) {
            fail("exception when creating anchor");
        }
    }
}
