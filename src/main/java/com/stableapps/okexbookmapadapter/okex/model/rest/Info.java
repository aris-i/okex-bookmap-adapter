package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Info {

	@JsonIgnore
	private Map<String, SymbolInfo> symbolsInfo = new HashMap<String, SymbolInfo>();

	@JsonAnyGetter
	public Map<String, SymbolInfo> getSymbolsInfo() {
		return this.symbolsInfo;
	}

	@JsonAnySetter
	public void setSymbolsInfo(String name, SymbolInfo value) {
		this.symbolsInfo.put(name, value);
	}
}
