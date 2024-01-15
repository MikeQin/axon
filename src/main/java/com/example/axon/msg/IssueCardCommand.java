package com.example.axon.msg;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record IssueCardCommand(
        @TargetAggregateIdentifier String id,
        int amount
) {

}