package io.oopsie.sdk.basic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oopsie.sdk.basic.model.ApplicationId;
import io.oopsie.sdk.basic.model.MetaModel;

public class SampleMetaModelFactory {

    public MetaModel create(ApplicationId applicationId) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(
                    String.format("{\"customerId\":\"techoopsie\",\"applicationId\":\"%s\",\"resourceMetas\":[{\"resourceId\":\"person\",\"attributeMetas\":[{\"name\":\"givenName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"surName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"birthdate\",\"type\":\"DATE\"}]},{\"resourceId\":\"pet\",\"attributeMetas\":[{\"name\":\"name\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"cuteName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"birthdate\",\"type\":\"DATE\"}]}]}",
                            applicationId.getValue()),
                    MetaModel.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MetaModel createCustom(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, MetaModel.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
