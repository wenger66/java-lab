package org.qimi.lab.threadmc;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class Resources {

    private static Map<String, Integer> MNEMONIC_LOOKUP = Collections.synchronizedMap(new IdentityHashMap());

    private Resources()
    {
        throw new AssertionError();
    }

    public static String format(String paramString, Object... paramVarArgs)
    {
        return MessageFormat.format(paramString, paramVarArgs);
    }

}
