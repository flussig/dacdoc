package com.github.flussig.dacdoc.example;

import com.github.flussig.dacdoc.check.CheckMetadata;
import com.github.flussig.dacdoc.check.CheckResult;
import com.github.flussig.dacdoc.check.CheckStatus;
import com.github.flussig.dacdoc.check.SingleExecutionCheck;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CheckMetadata(id = "employeesAlphaNumeric")
class EmployeesAlphaNumericCheck extends SingleExecutionCheck {

    public EmployeesAlphaNumericCheck(String argument, File file) {
        super(argument, file);
    }

    public CheckResult performCheck() {
        List<String> lines = Stream.of(argument.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        System.out.println(lines.size());

        CheckStatus checkStatus;

        // check that names contain only alphanumeric characters
        if(lines.stream().anyMatch(l -> l.matches("^.*[^a-zA-Z0-9 ].*$"))) {
            checkStatus = CheckStatus.RED;
        } else {
            checkStatus = CheckStatus.GREEN;
        }

        return new CheckResult("", LocalDateTime.now(), checkStatus);
    }
}