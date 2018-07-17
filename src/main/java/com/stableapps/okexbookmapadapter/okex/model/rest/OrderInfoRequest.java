package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author aris
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInfoRequest {

	public enum Status {
		Unfilled("1"), Filled("2");
		private final String value;

		private Status(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}

	}

	String symbol;
	Expiration expiration;
	@Builder.Default
	Status status = Status.Unfilled;
	@Builder.Default
	String orderId = "-1";
	@Builder.Default
	String currentPage = "1";
	@Builder.Default
	String pageLength = "50";
}
