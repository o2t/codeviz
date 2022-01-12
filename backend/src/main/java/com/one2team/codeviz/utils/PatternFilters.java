package com.one2team.codeviz.utils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public class PatternFilters {

  public <T> Predicate<T> createInclusionFilter (List<String> patterns, Function<T, String> mapper) {
    List<Pattern> patternList = patterns.stream ()
      .map (Pattern::compile)
      .toList ();

    return item -> patternList.stream ()
      .map (pattern -> pattern.matcher (mapper.apply (item)))
      .anyMatch (Matcher::find);
  }

  public <T> Predicate<T> createExclusionFilter (List<String> patterns, Function<T, String> mapper) {
    List<Pattern> patternList = patterns.stream ()
      .map (Pattern::compile)
      .toList ();

    return item -> patternList.stream ()
      .map (pattern -> pattern.matcher (mapper.apply (item)))
      .allMatch (not (Matcher::find));
  }
}
