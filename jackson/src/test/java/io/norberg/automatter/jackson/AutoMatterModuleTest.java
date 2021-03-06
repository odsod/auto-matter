package io.norberg.automatter.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AutoMatterModuleTest {

  static final Foo FOO = new FooBuilder()
      .a(17)
      .b("foobar")
      .aCamelCaseField(true)
      .build();

  static final WithInner.Bar BAR = new BarBuilder()
      .a(17)
      .b("foobar")
      .aCamelCaseField(true)
      .build();

  static final WithInner.PublicBar PUBLIC_BAR = new PublicBarBuilder()
      .a(17)
      .b("foobar")
      .aCamelCaseField(true)
      .build();

  static WithCollections WITH_COLLECTIONS = new WithCollectionsBuilder()
      .build();

  ObjectMapper mapper;

  @Before
  public void setUp() {
    mapper = new ObjectMapper()
        .registerModule(new AutoMatterModule());
  }

  @Test
  public void testDefault() throws IOException {
    final String json = mapper.writeValueAsString(FOO);
    final Foo parsed = mapper.readValue(json, Foo.class);
    assertThat(parsed, is(FOO));
  }

  @Test
  public void testInner() throws IOException {
    final String json = mapper.writeValueAsString(BAR);
    final WithInner.Bar parsed = mapper.readValue(json, WithInner.Bar.class);
    assertThat(parsed, is(BAR));
  }

  @Test
  public void testPublicInner() throws IOException {
    final String json = mapper.writeValueAsString(PUBLIC_BAR);
    final WithInner.PublicBar parsed = mapper.readValue(json, WithInner.PublicBar.class);
    assertThat(parsed, is(PUBLIC_BAR));
  }

  @Test
  public void testUnderScoreNamingStrategy() throws IOException {
    mapper.setPropertyNamingStrategy(CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    final String json = mapper.writeValueAsString(FOO);
    final JsonNode tree = mapper.readTree(json);
    assertThat(tree.has("a_camel_case_field"), is(true));
    final Foo parsed = mapper.readValue(json, Foo.class);
    assertThat(parsed, is(FOO));
  }

  @Test
  public void testExplicitJsonProperty() throws IOException {
    final String json = mapper.writeValueAsString(FOO);
    final JsonNode tree = mapper.readTree(json);
    assertThat(tree.has("foobar"), is(true));
    final Foo parsed = mapper.readValue(json, Foo.class);
    assertThat(parsed, is(FOO));
  }

  @Test
  public void testEmptyCollections() throws Exception {
    final String json = mapper.writeValueAsString(WITH_COLLECTIONS);
    final WithCollections parsed = mapper.readValue(json, WithCollections.class);
    assertThat(parsed, is(WITH_COLLECTIONS));
    assertThat(parsed.list().isEmpty(), is(true));
    assertThat(parsed.set().isEmpty(), is(true));
    assertThat(parsed.map().isEmpty(), is(true));
  }

  @Test
  public void testDefaultEmptyCollections() throws Exception {
    final String json = "{}";
    final WithCollections parsed = mapper.readValue(json, WithCollections.class);
    assertThat(parsed, is(WITH_COLLECTIONS));
    assertThat(parsed.list().isEmpty(), is(true));
    assertThat(parsed.set().isEmpty(), is(true));
    assertThat(parsed.map().isEmpty(), is(true));
  }
}
