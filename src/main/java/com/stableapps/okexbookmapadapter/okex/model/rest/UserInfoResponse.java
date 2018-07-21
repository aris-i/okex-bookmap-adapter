package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {

	@JsonProperty("result")
	public boolean result;
	@JsonProperty("info")
	public Info info;
	@JsonProperty("error_code")
	public int errorCode;

}
