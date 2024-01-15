package com.example.axon.query;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.axon.model.CardSummary;
import com.example.axon.msg.CardIssuedEvent;
import com.example.axon.msg.CardRedeemedEvent;
import com.example.axon.msg.CardSummaryQuery;
import com.example.axon.msg.CardSummaryQueryById;
import com.example.axon.msg.CardSummaryResponse;

@Profile("query")
@Service
@ProcessingGroup("card-summary")
public class CardSummaryProjection {

	private final Map<String, CardSummary> cardSummaryReadModel;

	public CardSummaryProjection(QueryUpdateEmitter queryUpdateEmitter) {
		this.cardSummaryReadModel = new ConcurrentHashMap<>();
	}

	@EventHandler
	public void on(CardIssuedEvent event, @Timestamp Instant timestamp) {
		/*
		 * Update our read model by inserting the new card. This is done so that
		 * upcoming regular (non-subscription) queries get correct data.
		 */
		CardSummary summary = CardSummary.issue(event.id(), event.amount(), timestamp);
		cardSummaryReadModel.put(event.id(), summary);

	}

	@SuppressWarnings("unused")
	@EventHandler
	public void on(CardRedeemedEvent event, @Timestamp Instant timestamp) {
		/*
		 * Update our read model by updating the existing card. This is done so that
		 * upcoming regular (non-subscription) queries get correct data.
		 */
		CardSummary summary = cardSummaryReadModel.computeIfPresent(event.id(),
				(id, card) -> card.redeem(event.amount(), timestamp));
	}
	
    @QueryHandler
    public CardSummaryResponse handle(CardSummaryQuery query) {
        List<CardSummary> cards = cardSummaryReadModel.values()
                                   .stream()
                                   .sorted(Comparator.comparing(CardSummary::lastUpdated))
                                   .toList();
        return new CardSummaryResponse(cards);
    }
    
    @QueryHandler
    public CardSummary handle(CardSummaryQueryById query) {
        return cardSummaryReadModel.get(query.id());
    }
}
