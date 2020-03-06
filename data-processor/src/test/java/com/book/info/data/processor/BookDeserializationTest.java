package com.book.info.data.processor;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class BookDeserializationTest {

  private static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  static <T> List<T> readFileList(String file, Class<T> tClass) throws IOException {
    JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, tClass);
    InputStream resourceAsStream = BookDeserializationTest.class.getClassLoader().getResourceAsStream(file);
    Objects.requireNonNull(resourceAsStream, "file is not found");
    return objectMapper.readValue(resourceAsStream, type);
  }

  @Test
  void testReadFile() throws IOException {
    List<Book> books = readFileList("books-v1.json", Book.class);
    assertThat(books).isNotNull();
    assertThat(books).hasSize(394);
  }
}
