package com.sye.security.algorithms;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 9/18/2018 19:43
 * <p>
 * Luhn algorithm Description
 * <p>
 * The formula verifies a number against its included check digit, which is usually appended to
 * a partial account number to generate the full account number. This number must pass
 * the following test:
 * <p>
 * 1. From the rightmost digit, which is the check digit, and moving left, double the value of
 * every second digit. The check digit is not doubled; the first digit doubled is immediately to
 * the left of the check digit. If the result of this doubling operation is greater than 9
 * (e.g., 8 × 2 = 16), then add the digits of the product (e.g., 16: 1 + 6 = 7, 18: 1 + 8 = 9) or,
 * alternatively, the same result can be found by subtracting 9 from the product
 * (e.g., 16: 16 − 9 = 7, 18: 18 − 9 = 9).
 * <p>
 * 2. Take the sum of all the digits.
 * <p>
 * 3. If the total modulo 10 is equal to 0 (if the total ends in zero) then the number is valid
 * according to the Luhn formula; else it is not valid.
 * <p>
 * <p>
 * *****************************************************************************************
 */
public final class Luhn {

    public static final int generate(char[] chars, int len) {
        int digit, oddSum = 0, evenSum = 0;

        for (int i = 0; i < len; i++) {
            digit = chars[i] - '0';    // ascii to num
            /*(1)将奇数位数字相加(从1开始计数)*/
            if (i % 2 == 0) {
                oddSum += digit;
            } else {
                /*(2)将偶数位数字分别乘以2,分别计算个位数和十位数之和(从1开始计数)*/
                digit = digit * 2;
                if (digit < 10) {
                    evenSum += digit;
                } else {
                    evenSum += (digit + 1 - 10);
                }
            }
        }

        return (oddSum + evenSum) % 10;
    }
}
