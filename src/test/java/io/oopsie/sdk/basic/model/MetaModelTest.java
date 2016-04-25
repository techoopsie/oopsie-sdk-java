package io.oopsie.sdk.basic.model;

import io.oopsie.sdk.basic.SampleMetaModelFactory;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class MetaModelTest {

    @Test
    public void structureTest() throws URISyntaxException, IOException {
        MetaModel model = new SampleMetaModelFactory().create(ApplicationId.of("techoopsie-person-reg"));

        // this is ugly, hope to find a better way soon
        model.assertValid();

        assertEquals(ApplicationId.of("techoopsie-person-reg"), model.getApplicationId());
        assertEquals(CustomerId.of("techoopsie"), model.getCustomerId());

        ResourceMeta person = model.getResourceMetas().get(0);
        assertEquals(ResourceId.of("person"), person.getResourceId());

        AttributeMeta givenName = person.getAttributeMetas().get(0);
        assertEquals(AttributeName.of("givenName"), givenName.getName());
        assertEquals(AttributeType.PLAIN_TEXT, givenName.getType());

        AttributeMeta surName = person.getAttributeMetas().get(1);
        assertEquals(AttributeName.of("surName"), surName.getName());
        assertEquals(AttributeType.PLAIN_TEXT, surName.getType());

        AttributeMeta birthDate = person.getAttributeMetas().get(2);
        assertEquals(AttributeName.of("birthdate"), birthDate.getName());
        assertEquals(AttributeType.DATE, birthDate.getType());

        ResourceMeta pet = model.getResourceMetas().get(1);
        assertEquals(ResourceId.of("pet"), pet.getResourceId());

        AttributeMeta name = pet.getAttributeMetas().get(0);
        assertEquals(AttributeName.of("name"), name.getName());
        assertEquals(AttributeType.PLAIN_TEXT, name.getType());

        AttributeMeta cuteName = pet.getAttributeMetas().get(1);
        assertEquals(AttributeName.of("cuteName"), cuteName.getName());
        assertEquals(AttributeType.PLAIN_TEXT, cuteName.getType());

        AttributeMeta petBirthdate = pet.getAttributeMetas().get(2);
        assertEquals(AttributeName.of("birthdate"), petBirthdate.getName());
        assertEquals(AttributeType.DATE, petBirthdate.getType());
    }

    @Test(expected = NullPointerException.class)
    public void noCustomerField() {
        MetaModel model = new SampleMetaModelFactory().createCustom("{\"applicationId\":\"%s\",\"resourceMetas\":[{\"resourceId\":\"person\",\"attributeMetas\":[{\"name\":\"givenName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"surName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"birthdate\",\"type\":\"DATE\"}]},{\"resourceId\":\"pet\",\"attributeMetas\":[{\"name\":\"name\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"cuteName\",\"type\":\"PLAIN_TEXT\"},{\"name\":\"birthdate\",\"type\":\"DATE\"}]}]}");

        model.assertValid();
    }
}