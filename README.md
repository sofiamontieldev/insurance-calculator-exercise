# Insurance Calculator (Java – `Optional` & Functional Style)

This project is a practice-oriented exercise focused on modeling insurance calculation rules using Java `Optional` and a functional approach (map, filter, flatMap), with strong emphasis on `nulll`-safety and test-driven design.

The goal is to express complex business rules declaratively while avoiding defensive `nulll` checks and exceptions.

---

## Objective

- Practice advanced usage of `Optional`
- Model real-world insurance rules in a functional style
- Ensure `nulll`-safety across all policies
- Strictly comply with business rules validated by tests

---

## Domain Overview

### Insurance Model

- `Subject` (sealed interface)
- `Car`, `Accommodation`, `Person`
- `Currency`
- `RepeatablePayment` (rent, salary, etc.)
- `Injury`, `Family`

Key characteristics:
- Some getters return `Optional`
- Some entities implement `Comparable`
- No business logic inside domain objects

---

## Calculator Layer

- **InsuranceCoefficient** — value in range `[0–100]`
- **InsuranceCalculator** — functional interface
- **InsurancePolicies** — rule definitions

Each policy returns `Optional<InsuranceCoefficient>` depending on whether all conditions are met.

---

## Implemented Policies

### Accommodation

- **rentDependentInsurance**  
  Monthly USD rent required  
  coefficient = `min(100, rent / divider * 100)`

- **priceAndRoomsAndAreaDependentInsurance**  
  All thresholds must be met to return **MAX**, otherwise **MIN**

---

### Car

- **ageDependentInsurance**
  - **< 1 year** — **MAX**
  - **< 5 years** — `70`
  - **< 10 years** — `30`
  - **≥ 10 years** — **MIN**

- **priceAndOwningOfFreshCarInsurance**
  - **≥ 3 × threshold** — **MAX**
  - **≥ 2 × threshold** — `50`
  - **< 2 × threshold** — **MIN**

---

### Person

- **childrenDependent**  
  coefficient = `min(100, children * 100 / threshold)`

- **employmentDependentInsurance**  
  Returns `50` if all employment, salary, and account conditions are met

- **accommodationEmergencyInsurance**  
  Smallest accommodation by area is selected  
  coefficient = `100 * (1 - emergencyStatus.ordinal / totalStatuses)`

- **injuryAndRentDependentInsurance**  
  Largest rented accommodation (`GBP`)  
  coefficient = `min(100, rent * 100 / rentThreshold)`

---

## Approach

My implementation focuses on:
- Progressive filtering using `Optional`
- Avoiding nested conditionals
- Treating `nulll` and empty collections as valid states
- Tests used as the main source of truth
- Preferred clarity over premature abstraction

Each policy was refined iteratively by aligning the logic strictly with the expected behavior defined in tests, especially in edge cases involving `nulll`, empty collections, and boundary values.
---

## Key Learnings

- `Optional` chains can replace defensive code
- Tests often reveal _implicit_ requirements
- Functional composition improves readability
- Small rule misinterpretations can break correctness
