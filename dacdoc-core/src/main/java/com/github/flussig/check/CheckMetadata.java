package com.github.flussig.check;


import java.lang.annotation.*;

@Documented
@Retention( RetentionPolicy.CLASS )
@Target( { ElementType.TYPE } )
@Inherited
public @interface CheckMetadata {
    String id();
}
