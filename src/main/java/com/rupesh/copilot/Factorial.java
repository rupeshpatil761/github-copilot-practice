package com.rupesh.copilot;

import java.math.BigInteger;

public class Factorial {

    public static void main(String[] args) {
        int number = 18; // Example input

        try {
            BigInteger factorial = calculateFactorial(number);
            System.out.println("Factorial of " + number + " is: " + factorial);
        } catch (IllegalArgumentException exception) {
            System.err.println("Unable to calculate factorial: " + exception.getMessage());
        }
    }

    // Iterative implementation avoids deep recursion and supports very large results.
    public static BigInteger calculateFactorial(int number) {
        if (number < 0) {
            throw new IllegalArgumentException(
                    "Factorial cannot be calculated for negative numbers: " + number
            );
        }

        // BigInteger is used to handle large results that can occur with factorials.
        BigInteger result = BigInteger.ONE;
        for (int current = 2; current <= number; current++) {
            result = result.multiply(BigInteger.valueOf(current));
        }
        return result;
    }
}