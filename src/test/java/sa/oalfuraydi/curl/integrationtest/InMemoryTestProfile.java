
package sa.oalfuraydi.curl.integrationtest;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class InMemoryTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("quarkus.datasource.jdbc.url", "jdbc:h2:mem:test");
    }
}
