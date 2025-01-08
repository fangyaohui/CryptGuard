package com.example.crptguardusetest;

import com.crypt.cryptguard.utils.JSONProcessorUtils;
import com.example.crptguardusetest.Entity.ComplexUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CryptGuardUseTestApplicationTests {

    @Test
    void contextLoads() {
    }

    /***

     {
     "name": "John Doe",
     "email": "john.doe@example.com",
     "address": {
     "street": "123 Elm Street",
     "city": "Metropolis",
     "details": {
     "buildingCode": "A12",
     "securityCode": "9999"
     }
     },
     "accounts": [
     {
     "type": "savings",
     "balance": "1000",
     "credentials": {
     "password": "12345",
     "pin": "5678"
     }
     },
     {
     "type": "credit",
     "limit": "5000",
     "credentials": {
     "password": "54321",
     "pin": "8765"
     }
     }
     ],
     "metadata": {
     "createdDate": "2024-01-01",
     "tags": ["VIP", "test"],
     "settings": {
     "theme": "dark",
     "notifications": "enabled"
     }
     }
     }
     */

    @Test
    public void doJSONProcessorUtilsTest() throws JsonProcessingException, IllegalAccessException {

        String json = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Elm Street\",\n" +
                "    \"city\": \"Metropolis\",\n" +
                "    \"details\": {\n" +
                "      \"buildingCode\": \"A12\",\n" +
                "      \"securityCode\": \"9999\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"accounts\": [\n" +
                "    {\n" +
                "      \"type\": \"savings\",\n" +
                "      \"balance\": \"1000\",\n" +
                "      \"credentials\": {\n" +
                "        \"password\": \"12345\",\n" +
                "        \"pin\": \"5678\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"credit\",\n" +
                "      \"limit\": \"5000\",\n" +
                "      \"credentials\": {\n" +
                "        \"password\": \"54321\",\n" +
                "        \"pin\": \"8765\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"metadata\": {\n" +
                "    \"createdDate\": \"2024-01-01\",\n" +
                "    \"tags\": [\"VIP\", \"test\"],\n" +
                "    \"settings\": {\n" +
                "      \"theme\": \"dark\",\n" +
                "      \"notifications\": \"enabled\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String newJson = "{\"code\":200,\"message\":null,\"data\":{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"address\":{\"street\":\"123 Elm Street\",\"city\":\"Metropolis\",\"details\":{\"buildingCode\":\"A12\",\"securityCode\":\"9999\"}},\"accounts\":[{\"type\":\"savings\",\"balance\":\"1000\",\"limit\":null,\"credentials\":{\"password\":\"12345\",\"pin\":\"5678\"}},{\"type\":\"credit\",\"balance\":null,\"limit\":\"5000\",\"credentials\":{\"password\":\"54321\",\"pin\":\"8765\"}}],\"metadata\":{\"createdDate\":\"2024-01-01\",\"tags\":[\"VIP\",\"test\"],\"settings\":{\"theme\":\"dark\",\"notifications\":\"enabled\"}}},\"currentTimeMillis\":1735444496742}";


        // Process the JSON
        String result = JSONProcessorUtils.processJson(newJson, ComplexUser.class, true);
        System.out.println(result);
    }

}
