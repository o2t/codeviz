package com.one2team.codeviz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class Main2 {

  private static final Pattern PACKAGE = Pattern.compile ("(com\\.sciforma(?:\\.[a-z0-9_-]+)+)\\.[A-Z].*");

  private static final ObjectMapper MAPPER = new ObjectMapper ();

  record Entry(
    @JsonProperty ("name") String name,
    @JsonProperty ("size") long size,
    @JsonProperty ("imports") List<String> imports ) {

  }

  record Node(
    @JsonProperty ("id") String id,
    @JsonProperty ("group") int group ) {

  }

  record Link(
    @JsonProperty ("source") String sourceId,
    @JsonProperty ("target") String targetId,
    @JsonProperty ("value") int value ) {

  }

  record Graph(
    @JsonProperty ("nodes") List<Node> nodes,
    @JsonProperty ("links") List<Link> links ) {

  }

  public static void main (String... args) throws IOException {
    List<Entry> entries;
    try (var input = Files.newBufferedReader (Paths.get ("/Users/dbr/dev/sciforma/sciforma-p4-mirror/tmp/flare.json"))) {
      entries = MAPPER.readValue (input, new TypeReference<> () {
      });
    }

    var packageMap = entries.stream ()
      .filter (e -> packageName (e.name ()) != null)
      .collect (groupingBy (Main2::packageName, toList ()));

    List<Node> nodes = packageMap.keySet ().stream ()
      .map (packageName -> new Node (packageName, 1))
      .toList ();

    List<Link> links = packageMap.entrySet ().stream ()
      .flatMap (entry -> {
        var packageName = entry.getKey ();
        var content = entry.getValue ();
        return content.stream ()
          .map (Entry::imports)
          .flatMap (Collection::stream)
          .map (Main2::packageName)
          .distinct ()
          .sorted ()
          .map (i -> new Link (packageName, i, 1));
      })
      .toList ();

    try (var output = Files.newBufferedWriter (Paths.get ("/Users/dbr/dev/sciforma/sciforma-p4-mirror/tmp/graph.json"))) {
      MAPPER.writeValue (output, new Graph (nodes, links));
    }
  }


  private static String packageName (Entry entry) {
    return packageName (entry.name ());
  }

  private static String packageName (String name) {
    return Optional.ofNullable (name)
      .map (PACKAGE::matcher)
      .filter (Matcher::matches)
      .map (matcher -> matcher.group (1))
      .orElse (null);
  }
}
