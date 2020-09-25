package com.auo.juppy.result;

public interface Reporter {
    // Console, Mail, slack.
    void notify(String msg);
}
