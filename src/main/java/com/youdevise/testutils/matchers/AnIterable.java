package com.youdevise.testutils.matchers;

import java.util.Comparator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.not;

public class AnIterable<T> extends TypeSafeDiagnosingMatcher<Iterable<? super T>> {
    
    public static <T> AnIterable<T> of(Class<T> klass) {
        return new AnIterable<T>(klass);
    }
    
    private final Class<T> klass;
    
    private AnIterable(Class<T> klass) {
        this.klass = klass;
    }
    
    public AnIterableWhich<T> which(Matcher<Iterable<T>> matcher) {
        return new AnIterableWhich<T>(this, matcher);
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("an iterable of ").appendText(klass.getName());
    }
    @Override
    protected boolean matchesSafely(Iterable<? super T> item, Description mismatchDescription) {
        if (Iterables.any(item, not(instanceOf(klass)))) {
            mismatchDescription.appendText("not all elements were instances of ").appendText(klass.getName());
            return false;
        }
        return true;
    }
    
    public AnIterableWhich<T> withoutDuplicates() {
        return new AnIterableWhich<T>(this, new WithoutDuplicatesMatcher<T>());
    }
    
    public AnIterableWhich<T> withoutContents() {
        return new AnIterableWhich<T>(this, new IsEmptyMatcher<T>());
    }

    public AnIterableWhich<T> inAscendingOrder() {
        return new AnIterableWhich<T>(this, Sorted.with(getNaturalOrdering()));
    }
    
    public AnIterableWhich<T> inDescendingOrder() {
        return new AnIterableWhich<T>(this, Sorted.with(getNaturalOrdering().reverse()));
    }
    
    public AnIterableWhich<T> inSortedOrder(Comparator<T> comparator) {
        return new AnIterableWhich<T>(this, Sorted.with(comparator));
    }
    
    public AnIterableWhich<T> inSortedOrder(Ordering<T> ordering) {
        return new AnIterableWhich<T>(this, Sorted.with(ordering));
    }
    
    private Ordering<T> getNaturalOrdering() {
        if (!Comparable.class.isAssignableFrom(klass)) {
            throw new UnsupportedOperationException(String.format("No natural comparable for %s", klass));
        }
        Comparator<T> comparator = new Comparator<T>() {
            @Override public int compare(T o1, T o2) {
                @SuppressWarnings("unchecked")
                Comparable<T> c1 = (Comparable<T>) o1;
                return c1.compareTo(o2);
            }
        };
        return Ordering.from(comparator);
    }
}