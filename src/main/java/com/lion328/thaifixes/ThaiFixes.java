package com.lion328.thaifixes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThaiFixes
{

    private static Logger logger;

    public static Logger getLogger()
    {
        if (logger == null)
        {
            logger = LogManager.getLogger("ThaiFixes");
        }

        return logger;
    }
}
