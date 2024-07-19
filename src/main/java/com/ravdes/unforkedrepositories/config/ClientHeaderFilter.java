package com.ravdes.unforkedrepositories.config;

import com.ravdes.unforkedrepositories.exceptions.InvalidHeaderException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

//Header filter class for checking if Accept header is set to application/json

class ClientHeaderFilter {

	private ClientHeaderFilter() {
	}

	public static ExchangeFilterFunction apply() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			HttpHeaders headers = clientRequest.headers();
			if (!headers.containsKey("Accept")) {
				return Mono.error(new InvalidHeaderException("Lack of Accept header!"));


			} else if (!headers.getAccept().contains(MediaType.APPLICATION_JSON)) {
				return Mono.error(new InvalidHeaderException("Value of Accept header must be 'application/json'"));
			}
			return Mono.just(clientRequest);
		});
	}
}
