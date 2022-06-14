**INITIALIZE UPDATE Command Message**

| Code | Value                      | Meaning                        |
| ---- | :------------------------- | ------------------------------ |
| CLA  | '80' - '83' or 'C0' - 'CF' |                                |
| INS  | '50'                       | INITIALIZE UPDATE              |
| P1   | 'xx'                       | Key Version Number             |
| P2   | '00'                       | Reference control parameter P2 |
| Lc   | '08'                       | Length of host challenge       |
| Data | 'xx xx…'                   | Host challenge                 |
| Le   | '00'                       |                                |

**INITIALIZE UPDATE Response Message**

| Name                     | Length   |
| ------------------------ | -------- |
| Key diversification data | 10 bytes |
| Key information          | 2 bytes  |
| Sequence Counter         | 2 bytes  |
| Card challenge           | 6 bytes  |
| Card cryptogram          | 8 bytes  |

