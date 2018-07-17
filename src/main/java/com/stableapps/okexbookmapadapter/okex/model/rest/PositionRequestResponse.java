package com.stableapps.okexbookmapadapter.okex.model.rest;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionRequestResponse {

	public boolean result;
	public List<Position> holding;

}
