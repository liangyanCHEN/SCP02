**EXTERNAL AUTHENTICATE Command Message**

| Code | Value                      | Meaning                           |
| ---- | :------------------------- | --------------------------------- |
| CLA  | '84' - '87' or 'E0' - 'EF' |                                   |
| INS  | '82'                       | EXTERNAL AUTHENTICATE             |
| P1   | 'xx'                       | Security level                    |
| P2   | '00'                       | Reference control parameter P2    |
| Lc   | '10'                       | Length of host cryptogram and MAC |
| Data | 'xx xx…'                   | Host cryptogram and MAC           |
| Le   |                            | Not present                       |

**EXTERNAL AUTHENTICATE Response Message**

| Name                                                   | Length |
| ------------------------------------------------------ | ------ |
| The data field of the response message is not present. |        |

