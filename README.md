# Insurance Calculator (Java – Optional & Functional Style)

This project is a practice-oriented exercise focused on modeling insurance calculation rules using Java Optional and a functional approach (map, filter, flatMap), with strong emphasis on null-safety and test-driven design.

The goal is to express complex business rules declaratively while avoiding defensive null checks and exceptions.

---

## Objective

- Practice advanced usage of Optional
- Model real-world insurance rules in a functional style
- Ensure null-safety across all policies
- Strictly comply with business rules validated by tests

---

## Domain Overview

### Insurance Model

- Subject (sealed interface)
- Car, Accommodation, Person
- Currency
- RepeatablePayment (rent, salary, etc.)
- Injury, Family

Key characteristics:
- Some getters return Optional
- Some entities implement Comparable
- No business logic inside domain objects

---

### Calculator Layer

- InsuranceCoefficient – value in range [0–100]
- InsuranceCalculator<S> – functional interface
- InsurancePolicies – rule definitions

Each policy returns Optional<InsuranceCoefficient> depending on whether all conditions are met.

---

## General Rules

- No exceptions are thrown
- All logic is null-safe
- Any object or collection may be null
- If requirements are not met, Optional.empty() is returned

---

## Implemented Policies

### Accommodation

rentDependentInsurance  
Monthly USD rent required.  
Coefficient formula:  
min(100, rent / divider * 100)

priceAndRoomsAndAreaDependentInsurance  
All thresholds must be met to return MAX, otherwise MIN.

---

### Car

ageDependentInsurance
- Less than 1 year → MAX
- Less than 5 years → 70
- Less than 10 years → 30
- 10 years or more → MIN

priceAndOwningOfFreshCarInsurance
- Price ≥ 3 × threshold → MAX
- 2 × threshold ≤ price < 3 × threshold → 50
- Price < 2 × threshold → MIN

---

### Person

childrenDependent  
Coefficient formula:  
min(100, childrenCount * 100 / threshold)

employmentDependentInsurance  
Returns 50 if all employment, salary, and account conditions are met.

accommodationEmergencyInsurance  
Selects the smallest accommodation by area.  
Coefficient formula:  
100 * (1 - emergencyStatus.ordinal / totalStatuses)

injuryAndRentDependentInsurance  
Largest rented accommodation in GBP.  
Coefficient formula:  
min(100, rent * 100 / rentThreshold)

---

## Approach

- Progressive filtering using Optional
- No nested conditionals
- Explicit handling of edge cases
- Tests used as the main source of truth
- Clarity over premature abstraction

---

## Key Learnings

- Optional chains can replace defensive code
- Tests often reveal hidden requirements
- Functional composition improves readability
- Small rule misinterpretations can break correctness
