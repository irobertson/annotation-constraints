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

  /**
   * Throws a {@link RuntimeException} if the file does not match all of the text matchers.
   *
   * @param file the file which should contain the expected text
   * @param matchers the text matchers
   * @throws IOException if the file cannot be read
   */
  public static void assertFileMatches(File file, Matcher<String>... matchers) throws IOException {
    List<String> lines = Files.readLines(file, Charset.forName(Charsets.UTF_8.name()));
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
