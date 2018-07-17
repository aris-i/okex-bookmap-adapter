package com.stableapps.okexbookmapadapter.okex.model.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymbolInfo {

	@JsonProperty("balance")
	public Double balance;
	@JsonProperty("rights")
	public Double rights;
	@JsonProperty("contracts")
	public List<Contract> contracts = null;

}
