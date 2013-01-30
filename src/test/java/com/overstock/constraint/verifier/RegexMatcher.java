package com.overstock.constraint.verifier;

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class RegexMatcher extends TypeSafeMatcher<String> {

  private final String regex;
  private final Pattern compiledRegex;

  private RegexMatcher(String regex) {
    this.regex = regex;
    this.compiledRegex = Pattern.compile(regex);
  }

  @Override
  public boolean matchesSafely(final String item) {
    return compiledRegex.matcher(item).matches();
  }

  public void describeTo(Description description) {
    description.appendText("matches regex ").appendValue(regex);
  }

  /**
   * Match the regexp against the whole input string
   *
   * @param regex the regular expression to match
   * @return a matcher which matches the whole input string
   */
  public static Matcher<String> matches(final String regex) {
    return new RegexMatcher(regex);
  }
}