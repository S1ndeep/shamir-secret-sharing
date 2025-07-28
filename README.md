# Shamir's Secret Sharing (SSS) Implementation in Java

![Java](https://img.shields.io/badge/Java-17%2B-blue)


A robust implementation of Shamir's Secret Sharing algorithm that:
- Splits secrets into multiple shares
- Requires only a threshold number of shares to reconstruct
- Handles JSON-formatted input with base-encoded values
- Uses Lagrange interpolation for polynomial reconstruction

## Features

✔ **Threshold Cryptography** - (k,n) scheme where only k-of-n shares are needed  
✔ **JSON Input Support** - Processes share configurations from JSON files  
✔ **Base Decoding** - Handles shares encoded in different numerical bases (2-36)  
✔ **Arbitrary Precision** - Uses Java's BigInteger for cryptographic security  
✔ **Error Resilient** - Identifies invalid shares through majority voting  

## Installation

1. **Requirements**:
   - Java 17+
   - [Gson](https://github.com/google/gson) library (included)

2. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/shamir-secret-sharing.git
   cd shamir-secret-sharing
