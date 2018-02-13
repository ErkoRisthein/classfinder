package ee.risthein.erko.classfinder;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Character.isUpperCase;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Collection;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author Erko Risthein
 */
public class ClassFinder {

    private final InputStream classNamesStream;

    public ClassFinder(InputStream classNamesStream) {
        if (classNamesStream == null) {
            throw new IllegalArgumentException();
        }
        this.classNamesStream = classNamesStream;
    }

    public Collection<String> findMatching(String pattern) {
        return isNullOrEmpty(pattern) ? emptyList() : getMatches(pattern);
    }

    private Collection<String> getMatches(String pattern) {
        try (BufferedReader reader = newReader()) {
            return reader.lines()
                    .map(ClassFinder::trimPackages)
                    .filter(className -> match(pattern, className))
                    .sorted()
                    .collect(toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String trimPackages(String className) {
        return Iterables.getLast(Splitter.on('.').trimResults().omitEmptyStrings().split(className));
    }

    private static boolean match(String pattern, String className) {
        int fromIndex = 0;
        for (String str : split(pattern)) {
            int foundIndex = className.indexOf(str, fromIndex);
            if (!found(foundIndex)) {
                return false;
            }
            fromIndex = foundIndex + str.length();
        }
        return !shouldMatchLastWord(pattern) || matchedLastWord(fromIndex, className);
    }

    private static Iterable<String> split(String pattern) {
        return Splitter.on('*').trimResults().omitEmptyStrings().split(addAsterisks(pattern));
    }

    private static String addAsterisks(String pattern) {
        String result = "";
        for (char c : pattern.toCharArray()) {
            result += isUpperCase(c) ? ("*" + c) : c;
        }
        return result;
    }

    private static boolean found(int index) {
        return index >= 0;
    }

    private static boolean shouldMatchLastWord(String pattern) {
        return pattern.endsWith(" ");
    }

    private static boolean matchedLastWord(int fromIndex, String className) {
        return fromIndex == className.length();
    }

    private BufferedReader newReader() {
        return new BufferedReader(new InputStreamReader(classNamesStream, UTF_8));
    }
}
