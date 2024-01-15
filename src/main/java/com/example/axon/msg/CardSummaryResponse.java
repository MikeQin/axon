package com.example.axon.msg;

import java.util.List;

import com.example.axon.model.CardSummary;

public record CardSummaryResponse(
		List<CardSummary> cards
) {

}
