package com.envyful.api.command.injector;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ArgumentInjectionFunction<A, B> {

    A apply(B sender, List<Annotation> annotations, String[] args);

}
