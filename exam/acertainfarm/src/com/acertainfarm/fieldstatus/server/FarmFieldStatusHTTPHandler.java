package com.acertainfarm.fieldstatus.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusHTTPHandler extends AbstractHandler {
    private FarmFieldStatus fieldStatus = null;

    public FarmFieldStatusHTTPHandler(FarmFieldStatus fieldStatus) {
        this.fieldStatus = fieldStatus;
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {

    }
}
