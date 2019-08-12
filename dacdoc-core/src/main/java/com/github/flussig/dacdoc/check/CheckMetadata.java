package com.github.flussig.dacdoc.check;


import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE } )
public @interface CheckMetadata {
    String id();
}
