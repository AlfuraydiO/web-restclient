# TODO

## Unit Testing

### `RestController.java`

- **`invokeRest(RestRequest requestEntity)`**:
  - Test that the `Content-Type` header is set to `application/json` by default.
  - Test that the `Authorization` header is correctly added for Basic Auth.
  - Test that the correct HTTP method and body are used.
  - Mock `HttpClient.send()` to test response handling for different status codes (e.g., 200, 404, 500).
  - Simulate `IOException` and `InterruptedException` to test error handling.

- **`saveRequest(RestRequest request)`**:
  - Mock `RestRequest.persist()` to verify it's called correctly.
  - Simulate exceptions during persistence to test error handling.

- **`getAllRequests()`**:
  - Mock `RestRequest.listAll()` to test data retrieval.

- **`deleteAllRestRequests()`**:
  - Mock `RestRequest.deleteAll()` to verify it's called.
  - Simulate exceptions during deletion to test error handling.

- **`deleteRestRequestById(Long id)`**:
  - Mock `RestRequest.deleteById(id)` to verify it's called with the correct ID.
  - Test the return value for successful and failed deletions.
  - Simulate exceptions during deletion to test error handling.
