package com.github.flussig.dacdoc.text;

import com.github.flussig.dacdoc.check.CheckResult;
import com.github.flussig.dacdoc.check.CheckStatus;
import com.github.flussig.dacdoc.check.SingleExecutionCheck;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit check for simple Anchor.
 */
public class DacDocCheckTest {
    @Test
    public void testSingleExecutionCheck() {
        // create single execution check with counter of executions
        SingleExecutionCheck check = new SingleExecutionCheck("", null) {
            public int counter = 0;

            @Override
            public CheckResult performCheck() {
                counter++;
                return new CheckResult(String.valueOf(counter), LocalDateTime.now(), CheckStatus.GREEN);
            }
        };

        // execute once: counter of executions = 1
        CheckResult checkResult = check.execute();
        assertEquals(1, Integer.valueOf(checkResult.getMessage()).intValue());

        // execute twice: counter of executions = 1
        checkResult = check.execute();
        assertEquals(1, Integer.valueOf(checkResult.getMessage()).intValue());

        // execute twice: counter of executions = 1
        checkResult = check.execute();
        assertEquals(1, Integer.valueOf(checkResult.getMessage()).intValue());
    }

    // TODO: url check test
}
