package com.github.flussig.check;


import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE } )
public @interface CheckMetadata {
    String id();
}
