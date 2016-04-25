package io.oopsie.sdk.basic;

import io.oopsie.sdk.basic.model.ApplicationId;
import io.oopsie.sdk.basic.model.MetaModel;
import org.junit.Test;

public class OopsieTest {

    @Test
    public void newInstance() {
        MetaModel model = new SampleMetaModelFactory().create(ApplicationId.of("techoopsie-person-reg"));

        Oopsie result = new Oopsie(model);

        // test behavior when in place
    }

    @Test(expected = NullPointerException.class)
    public void newInstanceMissingCustomerId() {
        new Oopsie(new SampleMetaModelFactory().createCustom("{\"applicationId\":\"techoopsie-person-reg\",\"resourceMetas\":[{\"resourceId\":\"person\",\"attributeMetas\":[{\"name\":\"givenName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"surName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"birthdate\",\"type\":\"DATE\"}]},{\"resourceId\":\"pet\",\"attributeMetas\":[{\"name\":\"name\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"cuteName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"birthdate\",\"type\":\"DATE\"}]}]}"));
    }
}