package com.example.axon.client.api;

import org.axonframework.axonserver.connector.ErrorCode;

public record Result(
        boolean isSuccess,
        String error,
        String id,
        int amount
) {

    public static Result ok(String id, int amount) {
        return new Result(true, null, id, amount);
    }

    public static Result Error(String error, String id, int amount) {
        if (error.contains(ErrorCode.INVALID_EVENT_SEQUENCE.errorCode())) {
            return new Result(false,
                              "An event for aggregate [" + id + "] at sequence ["
                                      + error.substring(error.length() - 1) + "] was already inserted. "
                                      + "You are either reusing the aggregate identifier "
                                      + "or concurrently dispatching commands for the same aggregate.", id, amount);
        }
        return new Result(false, "Error on aggregate [" + id + "]. " + error, id, amount);
    }
}
