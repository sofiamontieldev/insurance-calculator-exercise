# Insurance Calculator (Java – Optional & Functional Style)

This project is a practice-oriented exercise focused on designing insurance calculation rules using **Java Optional** and a functional approach (`map`, `filter`, `flatMap`, etc.), while keeping the code **null-safe, expressive, and test-driven**.

Rather than being a simple implementation task, this exercise explores how complex business rules can be modeled declaratively using Optional chains and small, composable conditions.

---

## Goal of the Exercise

The main objective was to:

- Practice extensive use of `Optional` instead of defensive `null` checks
- Model real-world insurance rules in a clean, readable, and safe way
- Respect strict business constraints validated through unit tests
- Avoid throwing exceptions and ensure null-safety at all times

---

## Domain Overview

The domain is split into two main packages.

### insurance package

Contains the domain model, mostly plain data objects:

- **Subject** – sealed interface implemented by insurable entities
- **Car**, **Accommodation**, **Person** – insurance subjects
- **Currency** – supported currencies
- **RepeatablePayment** – represents rent, salary, or similar payments
- **Injury**, **Family** – supporting domain objects

Notable characteristics:

- Several getters return `Optional<T>`
- Some classes implement `Comparable` to simplify selection from `SortedSet`
- No business logic inside domain objects

---

### calculator package

Contains the insurance logic:

- **InsuranceCoefficient** – wrapper for an integer in range `[0–100]`
- **InsuranceCalculator<S>** – functional interface
- **InsurancePolicies** – rule definitions for each subject type

Each policy returns an `Optional<InsuranceCoefficient>` depending on whether all business rules are satisfied.

---

## General Constraints

All implementations follow these principles:

- No method throws exceptions
- All logic is null-safe
- Any object or collection may be `null`
- Getters returning `Optional` never return `null`
- If requirements are not met, the result is `Optional.empty()`

---

## Accommodation Insurance Policies

### rentDependentInsurance

Calculates insurance based on monthly rent.

Conditions:

- Rent must exist
- Rent period must be monthly
- Rent currency must be USD
- Rent amount must be greater than 0

Formula:
coefficient = min(100, rent / divider * 100)

---

### priceAndRoomsAndAreaDependentInsurance

Returns `InsuranceCoefficient.MAX` only if all thresholds are met:

- Price ≥ priceThreshold
- Rooms ≥ roomsThreshold
- Area ≥ areaThreshold

Otherwise, returns `InsuranceCoefficient.MIN`.

---

## Car Insurance Policies

### ageDependentInsurance

Based on car age relative to a base date:

- Less than 1 year → `InsuranceCoefficient.MAX`
- Less than 5 years → `70`
- Less than 10 years → `30`
- 10 years or more → `InsuranceCoefficient.MIN`
- Unknown manufacture date → `Optional.empty()`

---

### priceAndOwningOfFreshCarInsurance

Applies only if:

- The car is not sold
- Price ≥ priceThreshold
- Ownership duration ≥ owningThreshold

Result:

- Price ≥ 3 × threshold → `InsuranceCoefficient.MAX`
- 2 × threshold ≤ price < 3 × threshold → `50`
- Price < 2 × threshold → `InsuranceCoefficient.MIN`

---

## Person Insurance Policies

### childrenDependent

Insurance coefficient based on the number of children relative to a threshold:

coefficient = min(100, childrenCount * 100 / threshold)

Requirements:

- Person must have a family
- Person must have at least one child

---

### employmentDependentInsurance

Returns an insurance coefficient of `50` if all conditions are met:

- At least four employment records
- Multi-currency account
- No recorded injuries
- At least one accommodation
- Currently employed
- Salary ≥ threshold
- Salary currency must be allowed

If any condition is not met, returns `Optional.empty()`.

---

### accommodationEmergencyInsurance

Rules:

- Person must have at least one accommodation
- The smallest accommodation by area is selected
- Its emergency status must be allowed

Formula:

100 * (1 - emergencyStatus.ordinal() / totalStatuses)
---

### injuryAndRentDependentInsurance

Requirements:

- Person must have at least one injury
- Person must be the culprit of the last injury
- The largest accommodation (by area) must be rented
- Rent currency must be GBP

Formula:
coefficient = min(100, rent * 100 / rentThreshold)

If any condition is not met, returns `Optional.empty()`.

---

## My Approach and Solution

My implementation focuses on:

- Progressive filtering using `Optional`
- Avoiding nested conditionals
- Treating `null` and empty collections as valid states
- Letting tests drive subtle business rules
- Preferring clarity over premature abstraction

Each policy was refined iteratively by aligning the logic strictly with the expected behavior defined in tests, especially in edge cases involving null values, empty collections, and boundary conditions.

---

## Key Takeaways

- Business rules become clearer when expressed as transformations
- Tests often reveal implicit requirements
- `Optional` chains can replace large amounts of defensive code
- Small logical misinterpretations can cause subtle but critical bugs


