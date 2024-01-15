package com.example.axon.client.api;

import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.axon.model.CardSummary;
import com.example.axon.msg.CardSummaryQuery;
import com.example.axon.msg.CardSummaryQueryById;
import com.example.axon.msg.CardSummaryResponse;
import com.example.axon.msg.IssueCardCommand;
import com.example.axon.msg.RedeemCardCommand;

import lombok.extern.java.Log;

@RestController
@Profile("gui")
@RequestMapping("/giftcard")
@Log
public class GiftCardController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public GiftCardController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }
    
    @PostMapping(value="/issue")
    public ResponseEntity<Result> issueCard(@RequestParam(name = "amount", required = true) int amount) {
    	UUID id = UUID.randomUUID();
    	commandGateway.sendAndWait(new IssueCardCommand(id.toString(), amount));
    	log.info(Result.ok(id.toString(), amount).toString());
    	return ResponseEntity.ok(Result.ok(id.toString(), amount));
    }
    
    @PostMapping(value="/redeem")
    public ResponseEntity<Result> redeemCard(@RequestParam(name = "id", required = true) String id, 
    		@RequestParam(name = "amount", required = true) int amount) {
    	commandGateway.sendAndWait(new RedeemCardCommand(id, amount));
    	log.info(Result.ok(id, amount).toString());
    	return ResponseEntity.ok(Result.ok(id, amount));
    }
    
    @GetMapping(value="/query")
    public ResponseEntity<List<CardSummary>> queryCardSummaries() {
    	CardSummaryResponse resp = queryGateway.query(new CardSummaryQuery(), ResponseTypes.instanceOf(CardSummaryResponse.class)).join();
    	return ResponseEntity.ok(resp.cards());
    }
    
    @GetMapping(value="/query/{id}")
    public ResponseEntity<CardSummary> queryCardSummaryById(@PathVariable String id) {
    	CardSummary card = queryGateway.query(new CardSummaryQueryById(id), ResponseTypes.instanceOf(CardSummary.class)).join();
    	return ResponseEntity.ok(card);
    }    
}
