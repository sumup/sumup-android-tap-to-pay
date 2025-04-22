# Full Errors List

| **Base Type**                   | **Exception Type**          | **Error Code** | **Description**                                     |
|---------------------------------|-----------------------------|----------------|-----------------------------------------------------|
| **CommonException**             | Parsing                     | 101            | Error occurred during parsing.                      |
|                                 | Environment                 | 102            | Environment-related issue.                          |
|                                 | NotRegisteredTerminal       | 103            | Terminal is not registered.                         |
|                                 | AuthTokenProvider           | 105            | Authentication token issue.                         |
|                                 | Update                      | 106            | Update is required.                                 |
|                                 | SDKIsAlreadyInitialized     | 107            | SDK is already initialized.                         |
|                                 | SDKIsNotInitialized         | 108            | SDK is not initialized.                             |
|                                 | SDKTearDown                 | 109            | SDK teardown process.                               |
|                                 | TerminalRegistration        | 110            | Terminal registration issue.                        |
|                                 | MissingResult               | 111            | Missing result error.                               |
| **NetworkException**            | NetworkConnection           | 201            | General network error.                              |
|                                 | Authentication              | 202            | Authentication failure.                             |
|                                 | Server                      | 204            | Server-related issue.                               |
|                                 | Client                      | 205            | Client-related issue.                               |
|                                 | NetworkSecurity             | 206            | Secure network (mTLS) issue.                        |
| **PaymentException**            | InvalidPaymentAction        | 1001           | Invalid payment action.                             |
|                                 | UncertainTransaction        | 1002           | Transaction status is uncertain.                    |
|                                 | Timeout                     | 1003           | Payment process timed out.                          |
|                                 | Preprocessing               | 1005           | Error during preprocessing.                         |
|                                 | CombinationSelection        | 1006           | Error in combination selection.                     |
|                                 | Transaction                 | 1007           | Transaction-related issue.                          |
|                                 | ExtractPAN                  | 1008           | Error extracting PAN.                               |
|                                 | UnknownCVM                  | 1009           | Unknown Cardholder Verification Method.             |
|                                 | IncorrectAmount             | 1010           | Incorrect amount specified.                         |
|                                 | UnsupportedCardTechnology   | 1011           | Unsupported card technology.                        |
|                                 | UnexpectedCardReadState     | 1012           | Unexpected card read state.                         |
|                                 | UnexpectedOutcome           | 1013           | Unexpected transaction outcome.                     |
|                                 | IncorrectFormat             | 1014           | Card read process failed.                           |
|                                 | ReadEMVTagsException        | 1015           | Error reading EMV tags.                             |
|                                 | TechnoPollingStopped        | 1016           | Techno polling process stopped.                     |
|                                 | CancelationFailed           | 1017           | Cancelation process failed.                         |
|                                 | ChargeFailed                | 1018           | Charge process failed.                              |
|                                 | UnsupportedOnlinePin        | 1020           | Unsupported online PIN.                             |
|                                 | UnsupportedSignatureRequest | 1021           | Unsupported signature request.                      |
|                                 | ErrorAction                 | 1022           | Error in payment action.                            |
|                                 | TransactionInterrupted      | 1023           | Transaction was interrupted.                        |
|                                 | CardReadFailed              | 1024           | Card read failed.                                   |
|                                 | DeclinedOutcome             | 1025           | Card declined.                                      |
|                                 | EmptyCandidatesList         | 1026           | No candidates available.                            |
|                                 | UnknownKernel               | 1027           | Unknown kernel error.                               |
| **PaymentPreparationException** | PaymentAvailability         | 1101           | Payment availability issue.                         |
|                                 | KernelPreparation           | 1102           | Error during kernel preparation.                    |
|                                 | EmptyAntireplayData         | 1103           | Antireplay data is empty.                           |
|                                 | CheckoutFailed              | 1104           | Checkout process failed.                            |
| **TapToPayException**           | Unknown                     | 0              | An internal error that cannot be exposed externally |