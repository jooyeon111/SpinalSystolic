# Spinal Systolic

A configurable Systolic Array hardware implementation using SpinalHDL.

## Overview

This project implements a Systolic Array architecture in SpinalHDL for matrix multiplication and convolution operations. It supports various dataflow patterns and arithmetic types.

## Features

- **Three Dataflow Patterns**
    - ReuseA: Reuses A input (input stationary)
    - ReuseB: Reuses B input (weight stationary)
    - ReuseC: Reuses C output (output stationary)

- **Flexible Arithmetic Operations**
    - Signed Integer (SInt) support
    - Unsigned Integer (UInt) support
    - Floating point will be supported later

- **Configurable Array Size**
    - Freely configurable row and column dimensions

## Key Components

### SystolicArray
Main array component that connects multiple Processing Elements.

### ProcessingElement
Individual PE that performs multiply-accumulate (MAC) operations.

### SkewBuffer
Delay buffer for dataflow that adjusts input data timing.

## Usage Example

```scala
// Define array dimensions
private val systolicArrayRow: Int = 5
private val systolicArrayCol: Int = 6

// Select dataflow pattern
private val dataflow = Dataflow.ReuseC  // Output stationary

// Configure integer type (Signed or Unsigned)
private val integerType = IntegerType.SignedInteger

// Set bit widths for inputs and output
private val bitWidthInputA = 8
private val bitWidthInputB = 8
private val bitWidthOutputC = 32

// Create systolic array configuration
val config = SystolicArrayConfig(
  row = systolicArrayRow,
  col = systolicArrayCol,
  dataflow = dataflow,
  integerConfig = IntegerConfig(
    integerType,
    PortBitWidthInfo(
      bitWidthInputA,
      bitWidthInputB,
      Some(bitWidthOutputC),
    ),
  )
)

// Generate Verilog with 100MHz clock
SpinalConfig(
  targetDirectory = "output",
  defaultClockDomainFrequency = FixedFrequency(100 MHz)
).generateVerilog(
  SystolicArray(config)
)
```
This main example creates a 5×6 systolic array with:

Output stationary dataflow (ReuseC)
Signed 8-bit inputs (A and B matrices)
32-bit output accumulation

## Requirements

- SpinalHDL
- Scala
