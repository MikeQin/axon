package com.example.axon.command;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Profile;

import com.example.axon.msg.CardIssuedEvent;
import com.example.axon.msg.CardRedeemedEvent;
import com.example.axon.msg.IssueCardCommand;
import com.example.axon.msg.RedeemCardCommand;

@Profile("command")
@Aggregate(cache = "gift_card_cache")
public class GiftCardAggregate {
	
    @AggregateIdentifier
    private String giftCardId;
    private int remainingValue;	
	
    /**
     * Registering the Aggregate must use Constructor
     * @param command
     */
    @CommandHandler
	public GiftCardAggregate(IssueCardCommand command) {
        if (command.amount() <= 0) {
            throw new IllegalArgumentException("amount <= 0");
        }
        apply(new CardIssuedEvent(command.id(), command.amount()));		
	}

    @CommandHandler
    public void handle(RedeemCardCommand command) {
        if (command.amount() <= 0) {
            throw new IllegalArgumentException("amount <= 0");
        }
        if (command.amount() > remainingValue) {
            throw new IllegalStateException("amount > remaining value");
        }
        apply(new CardRedeemedEvent(giftCardId, command.amount()));
    }
    
    @EventSourcingHandler
    public void on(CardIssuedEvent event) {
        giftCardId = event.id();
        remainingValue = event.amount();
    }

    @EventSourcingHandler
    public void on(CardRedeemedEvent event) {
        remainingValue -= event.amount();
    }    
    
    public GiftCardAggregate() {
        // Required by Axon to construct an empty instance to initiate Event Sourcing.
    }
}
