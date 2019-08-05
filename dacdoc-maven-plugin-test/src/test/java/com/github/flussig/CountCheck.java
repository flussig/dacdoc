package com.github.flussig;

import com.github.flussig.check.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CheckMetadata(id = "countCheck")
public class CountCheck extends SingleExecutionCheck {
    public CountCheck(String argument, File file) {
        super(argument, file);
    }

    @Override
    public CheckResult performCheck() {
        List<String> lines = Stream.of(argument.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        System.out.println(lines.size());

        CheckStatus checkStatus;
        if(lines.size() == 4) {
            checkStatus = CheckStatus.GREEN;
        } else {
            checkStatus = CheckStatus.RED;
        }

        return new CheckResult("", LocalDateTime.now(), checkStatus);
    }
}
