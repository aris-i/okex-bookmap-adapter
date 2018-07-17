/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aris
 */
public enum Interval {
	min1, min3, min5, min15, min30, hour1, hour2, hour4, hour6, hour12, day, day3, week;

	String regex = "([a-zA-Z]+)(\\d*)";
	Pattern p = Pattern.compile(regex);

	@Override
	public String toString() {
		Matcher m = p.matcher(name());
		if (m.matches()){
			String word = m.group(1);
			String digits = m.group(2);
			return digits+word;
		}
		return name();
	}

}
