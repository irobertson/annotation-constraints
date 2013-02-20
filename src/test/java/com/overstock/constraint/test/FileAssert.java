package com.overstock.constraint.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class FileAssert {

  private final File file;

  private final List<String> lines;

  public FileAssert(File file) throws IOException {
    this.file = file;
    this.lines = Files.readLines(file, Charset.forName(Charsets.UTF_8.name()));
  }

  /**
   * Throws a {@link RuntimeException} if any line in the file matches one or more of the text matchers.
   *
   * @param matchers the text matchers
   */
  public void assertNoLineMatches(Matcher<String>... matchers) throws IOException { //TODO reduce duplication
    StringBuilder errorMessage = new StringBuilder(file.toString()).append(" matched: ");
    StringDescription description = new StringDescription(errorMessage);
    boolean failed = false;
    for (Matcher<String> matcher : matchers) {
      if (matches(lines, matcher)) {
        failed = true;
        errorMessage.append("\n  ");
        matcher.describeTo(description);
      }
    }
    if (failed) {
      throw new RuntimeException(errorMessage.toString());
    }
  }

  /**
   * Throws a {@link RuntimeException} if there's not a line in the file which matches each text matcher.
   *
   * @param matchers the text matchers
   */
  public void assertAnyLineMatches(Matcher<String>... matchers) throws IOException {
    StringBuilder errorMessage = new StringBuilder(file.toString()).append(" did not match: ");
    StringDescription description = new StringDescription(errorMessage);
    boolean failed = false;
    for (Matcher<String> matcher : matchers) {
      if (!matches(lines, matcher)) {
        failed = true;
        errorMessage.append("\n  ");
        matcher.describeTo(description);
      }
    }
    if (failed) {
      throw new RuntimeException(errorMessage.toString());
    }
  }

  private static boolean matches(List<String> list, Matcher<String> matcher) {
    for (String s : list) {
      if (matcher.matches(s)) {
        return true;
      }
    }
    return false;
  }
}
