package edu.epam.fop.lambdas.calculator;

import edu.epam.fop.lambdas.insurance.Car;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public final class CarInsurancePolicies {

    private CarInsurancePolicies() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static InsuranceCalculator<Car> ageDependInsurance(LocalDate baseDate) {
        return car -> Optional.ofNullable(car)
                .map(Car::manufactureDate)
                .map(date -> {
                    long years = java.time.temporal.ChronoUnit.YEARS.between(date, baseDate);

                    if (years <= 1) {
                        return InsuranceCoefficient.MAX;
                    }
                    if (years <= 5) {
                        return InsuranceCoefficient.of(70);
                    }
                    if (years <= 10) {
                        return InsuranceCoefficient.of(30);
                    }
                    return InsuranceCoefficient.MIN;
                });
    }



    public static InsuranceCalculator<Car> priceAndOwningOfFreshCarInsurance(
            LocalDate baseDate,
            BigInteger priceThreshold,
            Period owningThreshold) {

        BigInteger x2 = priceThreshold.multiply(BigInteger.TWO);
        BigInteger x3 = priceThreshold.multiply(BigInteger.valueOf(3));

        return car -> Optional.ofNullable(car)
                .filter(c -> c.soldDate().isEmpty())
                .filter(c -> c.price() != null)
                .filter(c -> c.purchaseDate() != null)
                .filter(c -> c.price().compareTo(priceThreshold) >= 0)
                .filter(c ->
                        !c.purchaseDate()
                                .plus(owningThreshold)
                                .isBefore(baseDate)
                )
                .map(c -> {
                    if (c.price().compareTo(x3) >= 0) {
                        return InsuranceCoefficient.MAX;
                    }
                    if (c.price().compareTo(x2) >= 0) {
                        return InsuranceCoefficient.MED;
                    }
                    return InsuranceCoefficient.MIN;
                });
    }

}
