# Systolic Array Architecture Documentation

## Table of Contents
1. [Overview](#overview)
2. [Core Concepts](#core-concepts)
3. [Architecture Components](#architecture-components)
4. [Dataflow Patterns](#dataflow-patterns)
5. [Peripherals](#peripherals)
---

## Overview

This is a parameterized systolic array implementation written in SpinalHDL 

### Key Features
- **Multiple Data Types**: Signed/Unsigned integers, BFloat16, and Float16 (FP16)
- **Three Dataflow Patterns**: Weight Stationary (ReuseA), Input Stationary (ReuseB), and Output Stationary (ReuseC)
- **Parameterized Design**: Configurable array dimensions and bit widths
- **Mixed-Precision Support**: BFloat16/FP16 inputs with FP32 accumulation
- **Modular Architecture**: Clean separation of concerns with type providers and arithmetic traits

---

## Core Concepts

### Systolic Array Fundamentals

A systolic array is a network of processing elements (PEs) that compute and pass data through the array:
![SystolicArrayImage](SystolicArray.eps)

Each PE performs a multiply-accumulate (MAC) operation:

```
output = (inputA × inputB) + inputC
```

Systolic Arrays are widely used for high performance computing especially for GEMM operations in deep neural networks.

The key advantages are:
- **High Throughput**: Pipelined computation with data reuse
- **Scalability**: Easy to scale to larger array sizes
- **Energy Efficiency**: Local data movement minimizes memory access

### Matrix Multiplication Mapping

For matrix multiplication `A × B = C`:
- **A matrix**: Input activations (rows flow through array)
- **B matrix**: Weights (columns flow through array)  
- **C matrix**: Output results (accumulated in array)

Different dataflow patterns optimize for different matrix dimensions and memory access patterns.

---

## Dataflow Patterns

### 1. ReuseA (Input Stationary)

**Architecture Overview**

**GEMM Mapping**


### 2. ReuseB (Weight Stationary)

**Architecture Overview**

**GEMM Mapping**

### 3. ReuseC (Output Stationary)

**Architecture Overview**

**GEMM Mapping**


---

## Data Types

### 1. Integer Types

#### Signed Integer (SInt)
- Configurable bit width for inputs A and B
- Automatic bit width growth for accumulation
- Two's complement arithmetic

#### Unsigned Integer (UInt)
- Configurable bit width for inputs A and B
- Automatic bit width growth for accumulation
- Natural number arithmetic

**Configuration**:
```scala
val config = SystolicArrayConfig.signedInteger(
  row = 4,
  col = 4,
  dataflow = Dataflow.ReuseA,
  bitWidthA = 8,
  bitWidthB = 8,
  bitWidthOutputC = Some(32)
)
```

### 2. BFloat16 (Brain Floating Point 16)

**Format**: 1 sign + 8 exponent + 7 mantissa bits

**Characteristics**:
- Same dynamic range as FP32 (8-bit exponent)
- Lower precision than FP16 (7-bit vs 10-bit mantissa)
- Direct truncation from/to FP32
- Popular in deep learning (Google TPU, etc.)

**Computation Path**:
```
BF16 × BF16 → FP32 (multiply)
FP32 + FP32 → FP32 (accumulate)
```

**Configuration**:
```scala
val config = SystolicArrayConfig.bfloat16(
  row = 4,
  col = 4,
  dataflow = Dataflow.ReuseC
)
```

### 3. Float16 (IEEE 754 Half Precision)

**Format**: 1 sign + 5 exponent + 10 mantissa bits

**Characteristics**:
- Higher precision than BF16 (10-bit mantissa)
- Smaller dynamic range (5-bit exponent, bias=15)
- IEEE 754 standard format
- Common in graphics and mobile ML

**Computation Path**:
```
FP16 × FP16 → FP32 (multiply with rebias)
FP32 + FP32 → FP32 (accumulate)
```


**Configuration**:
```scala
val config = SystolicArrayConfig.float16(
  row = 4,
  col = 4,
  dataflow = Dataflow.ReuseB
)
```

---

## Peripherals 

### Skew Buffer


### Deskew Buffer
