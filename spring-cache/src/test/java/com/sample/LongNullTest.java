package com.sample;

import java.util.Optional;

public class LongNullTest {

	/**
	 * Long null check
	 * @param args
	 */
	public static void main(String[] args) {
		Long test = null;
		if (Optional.ofNullable(test).orElse(0L) == 0){
			System.out.println("a : " + test);
		} else {
			System.out.println("b : " +test);
		}
	}
}
