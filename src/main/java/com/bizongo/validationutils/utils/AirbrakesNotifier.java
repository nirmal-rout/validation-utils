package com.bizongo.validationutils.utils;

import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.Notifier;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

@Service
public class AirbrakesNotifier {
    @Value("${airbrake.ProjectId}")
    private int airbrakeProjectId;

    @Value("${airbrake.ProjectKey}")
    private String airbrakeProjectKey;

    @Autowired
    Environment env;

    public void notify(Exception ex, HttpServletRequest request, String message) {
        Notifier notifier = new Notifier(airbrakeProjectId, airbrakeProjectKey);
        String environment = env.getActiveProfiles()[0];
        notifier.addFilter(
                (Notice notice) -> {
                    if(request != null){
                        notice.setContext("uri", request.getRequestURI());
                        String body = "";
                        try {
                            body = IOUtils.toString(request.getInputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        };
                        notice.setContext("body", body);
                    }
                    else if(message!=null){
                        notice.setContext("body", message);
                    }
                    notice.setContext("environment", environment);
                    return notice;
                });
        notifier.report(ex);
    }
}
