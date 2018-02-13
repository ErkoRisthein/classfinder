package ee.risthein.erko.classfinder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

/**
 * @author Erko Risthein
 */
public class ClassFinderTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenConstructedWithNull() {
        new ClassFinder(null);
    }

    @Test
    public void shouldNotMatchNull() throws Exception {
        assertNoMatch(null, "FooBar");
    }

    @Test
    public void shouldNotMatchEmptyString() throws Exception {
        assertNoMatch("", "FooBar");
    }

    @Test
    public void shouldFindExactMatch() throws Exception {
        assertMatch("FooBar", "FooBar");
    }

    @Test
    public void shouldNotMatch() throws Exception {
        assertNoMatch("Foo", "Bar");
    }

    @Test
    public void shouldMatchStartingWith() throws Exception {
        assertMatch("Foo", "FooBar");
    }

    @Test
    public void shouldMatchEndingWith() throws Exception {
        assertMatch("Bar", "FooBar");
    }

    @Test
    public void shouldMatchEndingWithAsterisk() throws Exception {
        assertMatch("Foo*", "FooBar");
    }

    @Test
    public void shouldMatchStartingWithAsterisk() throws Exception {
        assertMatch("*Bar", "FooBar");
    }

    @Test
    public void shouldMatchMiddleAsterisk() throws Exception {
        assertMatch("Foo*Bar", "FooBar");
    }

    @Test
    public void shouldMatchCapitalLetters() throws Exception {
        assertMatch("FB", "FooBar");
    }

    @Test
    public void shouldNotMatchInverseCapitalLetters() throws Exception {
        assertNoMatch("BF", "FooBar");
    }

    @Test
    public void shouldMatchPartialWords() throws Exception {
        assertMatch("FoBa", "FooBar");
    }

    @Test
    public void shouldMatchPartialWords2() throws Exception {
        assertMatch("FBar", "FooBar");
    }

    @Test
    public void shouldMatchComplexMiddleAsterisk() throws Exception {
        assertMatch("F*Baz", "FooBarBaz");
    }

    @Test
    public void shouldMatchComplexStartingAsterisk() throws Exception {
        assertMatch("*BB", "FooBarBaz");
    }

    @Test
    public void shouldMatchEndingWordWithSpace() throws Exception {
        assertMatch("FBar ", "FooBar");
    }

    @Test
    public void shouldNotMatchEndingWordWithSpace() throws Exception {
        assertNoMatch("FBar ", "FooBarBaz");
    }

    @Test
    public void shouldStripPackageNames() throws Exception {
        assertMatch("FooBar", "a.b.FooBar", "FooBar");
    }

    @Test
    public void shouldMatchMultipleClassNames() throws Exception {
        assertMatch("FB", "FooBar\nFooBarBaz", "FooBar", "FooBarBaz");
    }

    @Test
    public void shouldSortClassNames() throws Exception {
        assertMatch("FB", "BFooBar\nAFooBar", "AFooBar", "BFooBar");
    }

    @Test
    public void shouldMatchWithMultipleAsterisks() throws Exception {
        assertMatch("*F**D*", "FooBarDaz");
    }

    private static void assertMatch(String pattern, String className) throws Exception {
        assertMatch(pattern, className, className);
    }

    private static void assertMatch(String pattern, String allClassNames, String... matchedClassNames) throws Exception {
        assertThat(classFinder(allClassNames).findMatching(pattern), contains(matchedClassNames));
    }

    private static void assertNoMatch(String pattern, String className) throws Exception {
        assertThat(classFinder(className).findMatching(pattern), not(contains(className)));
    }

    private static ClassFinder classFinder(String className) throws Exception {
        return new ClassFinder(stream(className));
    }

    private static ByteArrayInputStream stream(String string) throws Exception {
        return new ByteArrayInputStream(string.getBytes(UTF_8));
    }


}
