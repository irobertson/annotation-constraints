package com.overstock.constraint.test;

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ContainsRegExMatcher extends TypeSafeMatcher<String> {

  private final Pattern pattern;

  public ContainsRegExMatcher(Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  protected boolean matchesSafely(String s) {
    return pattern.matcher(s).find();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("contains at least one match for regex ").appendValue(pattern.pattern());
  }

  @Factory
  public static Matcher<String> containsMatch(String pattern) {
    return new ContainsRegExMatcher(Pattern.compile(pattern));
  }

  @Factory
  public static Matcher<String> containsMatch(Pattern pattern) {
    return new ContainsRegExMatcher(pattern);
  }
}
