package edu.epam.fop.lambdas.calculator;

import edu.epam.fop.lambdas.insurance.Accommodation;
import edu.epam.fop.lambdas.insurance.Currency;

import java.math.BigInteger;
import java.util.Optional;

public final class AccommodationInsurancePolicies {

    private AccommodationInsurancePolicies() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static InsuranceCalculator<Accommodation> rentDependentInsurance(BigInteger divider) {

        return accommodation ->
                Optional.ofNullable(accommodation)
                        .flatMap(Accommodation::rent)
                        .filter(rent -> rent.amount().compareTo(BigInteger.ZERO) > 0)
                        .filter(rent -> rent.currency() == Currency.USD)
                        .filter(rent -> rent.unit().getMonths() > 0)
                        .map(rent -> {
                            BigInteger coefficient = rent.amount()
                                    .multiply(BigInteger.valueOf(100))
                                    .divide(divider);

                            return coefficient.compareTo(BigInteger.valueOf(100)) > 0
                                    ? InsuranceCoefficient.MAX
                                    : new InsuranceCoefficient(coefficient.intValue());
                        });
    }


    static InsuranceCalculator<Accommodation> priceAndRoomsAndAreaDependentInsurance(
            BigInteger priceThreshold,
            int roomsThreshold,
            BigInteger areaThreshold) {

        return entity -> Optional.ofNullable(entity)
                .filter(e -> e.price() != null && e.price().compareTo(priceThreshold) >= 0)
                .filter(e -> e.rooms() != null && e.rooms().compareTo(roomsThreshold) >= 0)
                .filter(e -> e.area() != null && e.area().compareTo(areaThreshold) >= 0)
                .map(e -> InsuranceCoefficient.MAX)
                .or(() -> Optional.of(InsuranceCoefficient.MIN));

    }
}
