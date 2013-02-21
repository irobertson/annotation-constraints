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
  public void assertNoLineMatches(Matcher<String>... matchers) throws IOException {
    StringDescription description = new StringDescription(new StringBuilder(file.toString()).append(" matched:"));
    boolean failed = false;
    for (Matcher<String> matcher : matchers) {
      for (int lineNumber = 0; lineNumber < lines.size(); ++lineNumber) {
        String line = lines.get(lineNumber);
        if (matcher.matches(line)) {
          failed = true;
          description.appendText("\n  ").appendDescriptionOf(matcher).appendText("\n    on line ")
            .appendText(String.valueOf(lineNumber)).appendText(": ").appendText(line);
        }
      }
    }
    if (failed) {
      throw new RuntimeException(description.toString());
    }
  }

  /**
   * Throws a {@link RuntimeException} if there's not a line in the file which matches each text matcher.
   *
   * @param matchers the text matchers
   */
  public void assertAnyLineMatches(Matcher<String>... matchers) throws IOException {
    StringDescription description = new StringDescription(new StringBuilder(file.toString()).append(" did not match:"));
    boolean failed = false;
    for (Matcher<String> matcher : matchers) {
      if (!anyLineMatches(matcher)) {
        failed = true;
        description.appendText("\n  ").appendDescriptionOf(matcher);
      }
    }
    if (failed) {
      throw new RuntimeException(description.toString());
    }
  }

  private boolean anyLineMatches(Matcher<String> matcher) {
    for (String s : lines) {
      if (matcher.matches(s)) {
        return true;
      }
    }
    return false;
  }
}
