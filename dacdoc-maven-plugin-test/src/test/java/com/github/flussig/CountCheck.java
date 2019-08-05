package com.github.flussig;

import com.github.flussig.check.*;

import java.io.File;
import java.time.LocalDateTime;

@CheckMetadata(id = "counterCheck")
public class CountCheck extends SingleExecutionCheck {
    public CountCheck(String argument, File file) {
        super(argument, file);
    }

    @Override
    public CheckResult performCheck() {
        String[] lines = argument.split("\n");

        CheckStatus checkStatus;
        if(lines.length == 4) {
            checkStatus = CheckStatus.GREEN;
        } else {
            checkStatus = CheckStatus.RED;
        }

        return new CheckResult("", LocalDateTime.now(), checkStatus);
    }
}
