package com.facade.edi.starter.converter;

import java.io.IOException;

public interface Converter<Source, Target> {
    Target convert(Source t) throws IOException;

}
