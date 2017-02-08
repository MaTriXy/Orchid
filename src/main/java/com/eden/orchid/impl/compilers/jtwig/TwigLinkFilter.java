package com.eden.orchid.impl.compilers.jtwig;

import com.eden.orchid.api.registration.AutoRegister;
import com.eden.orchid.utilities.OrchidUtils;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.JtwigFunction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AutoRegister
public class TwigLinkFilter implements JtwigFunction {

    @Override
    public String name() {
        return "link";
    }

    @Override
    public Collection<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public Object execute(FunctionRequest request) {
        List<Object> fnParams = request.maximumNumberOfArguments(1)
                                       .minimumNumberOfArguments(1)
                                       .getArguments();

        Object value = fnParams.get(0);

        return OrchidUtils.linkTo(value.toString());
    }
}