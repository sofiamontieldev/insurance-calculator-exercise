package edu.epam.fop.lambdas.calculator;

import edu.epam.fop.lambdas.insurance.*;
import edu.epam.fop.lambdas.insurance.Currency;

import java.math.BigInteger;
import java.util.*;

public final class PersonInsurancePolicies {

  private PersonInsurancePolicies() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

    public static InsuranceCalculator<Person> childrenDependent(int childrenCountThreshold) {
        return person -> Optional.ofNullable(person)
                .flatMap(Person::family)
                .map(Family::children)
                .filter(children -> children != null && !children.isEmpty())
                .map(children -> {
                    int percentage = Math.min(
                            100,
                            children.size() * 100 / childrenCountThreshold
                    );
                    return InsuranceCoefficient.of(percentage);
                })
                .or(() -> Optional.of(InsuranceCoefficient.MIN));
    }

    public static InsuranceCalculator<Person> employmentDependentInsurance(
            BigInteger salaryThreshold,
            Set<Currency> currencies) {

        return person -> Optional.ofNullable(person)
                .filter(p -> p.employmentHistory() != null && p.employmentHistory().size() >= 4)
                .filter(p -> p.account() != null && p.account().size() > 1)
                .filter(p -> p.injuries() == null || p.injuries().isEmpty())
                .filter(p -> p.accommodations() != null && !p.accommodations().isEmpty())
                .map(p -> p.employmentHistory().last())
                .filter(e -> e.endDate().isEmpty())
                .flatMap(Employment::salary)
                .filter(s ->
                        currencies.contains(s.currency())
                                && s.amount().compareTo(salaryThreshold) >= 0
                )
                .map(__ -> InsuranceCoefficient.MED);
    }


    public static InsuranceCalculator<Person> accommodationEmergencyInsurance(
            Set<Accommodation.EmergencyStatus> statuses) {

        return person -> Optional.ofNullable(person)
                .filter(p -> statuses != null && !statuses.isEmpty())
                .flatMap(p -> {
                    Set<Accommodation> acc = p.accommodations();
                    if (acc == null || acc.isEmpty()) {
                        return Optional.empty();
                    }

                    Accommodation smallest = null;

                    for (Accommodation a : acc) {
                        if (a == null || a.area() == null) {
                            continue;
                        }
                        if (smallest == null || a.compareTo(smallest) < 0) {
                            smallest = a;
                        }
                    }

                    if (smallest == null) {
                        return Optional.empty();
                    }

                    Optional<Accommodation.EmergencyStatus> statusOpt = smallest.emergencyStatus();
                    if (statusOpt.isEmpty() || !statuses.contains(statusOpt.get())) {
                        return Optional.empty();
                    }

                    int ordinal = statusOpt.get().ordinal();
                    int total = Accommodation.EmergencyStatus.values().length;

                    int coefficient = (int) (100 * (1.0 - (double) ordinal / total));

                    return Optional.of(new InsuranceCoefficient(coefficient));
                });
    }

    public static InsuranceCalculator<Person> injuryAndRentDependentInsurance(
            BigInteger rentThreshold) {

        return person -> Optional.ofNullable(person)
                .filter(p -> p.injuries() != null && !p.injuries().isEmpty())
                .filter(p -> {
                    Injury last = p.injuries().last();
                    return last.culprit().isPresent() && last.culprit().get() == p;
                })
                .filter(p -> p.accommodations() != null && !p.accommodations().isEmpty())
                .flatMap(p -> p.accommodations().stream()
                        .filter(a -> a.area() != null)
                        .max(Comparator.comparing(Accommodation::area))
                )
                .flatMap(Accommodation::rent)
                .filter(r -> r.currency() == Currency.GBP)
                .map(r -> {
                    if (r.amount().equals(BigInteger.ZERO)) {
                        return InsuranceCoefficient.MIN;
                    }
                    if (r.amount().compareTo(rentThreshold) >= 0) {
                        return InsuranceCoefficient.MAX;
                    }
                    return InsuranceCoefficient.MED;
                });
    }

  }

