//package br.com.santos.william.moviebattle.omdb.dto;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.io.IOException;
//
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(SpringExtension.class)
//public class ImdbVotesDeserializeUnitTest {
//
//    @InjectMocks
//    private ImdbVotesDeserialize deserialize;
//
//    @Mock
//    private JsonParser parser;
//
//    @Mock
//    private DeserializationContext context;
//
//    @Test
//    public void deserializeValueWithComma() throws IOException {
//        given(parser.getText()).willReturn("1,000");
//
//        var value = deserialize.deserialize(parser, context);
//
//        Assertions.assertEquals(1000, value);
//    }
//}
