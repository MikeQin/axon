package com.example.axon.msg;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RedeemCardCommand(
		@TargetAggregateIdentifier String id,
		int amount
) {

}
