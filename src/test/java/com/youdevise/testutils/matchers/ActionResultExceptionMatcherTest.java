package com.youdevise.testutils.matchers;

import org.junit.Test;

import com.youdevise.testutils.operations.Action;
import com.youdevise.testutils.operations.ActionResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import static com.youdevise.testutils.matchers.ActionResultExceptionMatcher.throwsException;
import static com.youdevise.testutils.matchers.ExceptionMatcher.anExceptionOfType;
import static com.youdevise.testutils.matchers.MatcherMatcher.a_matcher_giving_a_mismatch_description_of;
import static com.youdevise.testutils.matchers.MatcherMatcher.a_matcher_that_matches;
import static com.youdevise.testutils.matchers.MatcherMatcher.a_matcher_with_description;
import static com.youdevise.testutils.operations.ActionRunner.running;

public class ActionResultExceptionMatcherTest {
    @Test public void
    describes_the_exception_it_expects_to_be_thrown() {
         assertThat(throwsException(new Exception()),
                    is(a_matcher_with_description(equalTo("throws the exception <java.lang.Exception>"))));

         assertThat(throwsException(anExceptionOfType(Exception.class)),
                    is(a_matcher_with_description(equalTo("throws a <java.lang.Exception>"))));

         assertThat(throwsException(anExceptionOfType(Exception.class).withTheMessage("Yo.")),
                    is(a_matcher_with_description(equalTo("throws a <java.lang.Exception> with the message \"Yo.\""))));

         assertThat(throwsException(anExceptionOfType(RuntimeException.class).causedBy(anExceptionOfType(Exception.class))),
                    is(a_matcher_with_description(equalTo("throws a <java.lang.RuntimeException> caused by a <java.lang.Exception>"))));

         assertThat(throwsException(anExceptionOfType(RuntimeException.class).withTheMessage("Yo.").causedBy(anExceptionOfType(Exception.class))),
                    is(a_matcher_with_description(equalTo("throws a <java.lang.RuntimeException> with the message \"Yo.\" caused by a <java.lang.Exception>"))));
    }

    @Test public void
    does_not_match_an_action_result_that_does_not_throw_an_exception() {
        assertThat(throwsException(new Exception()),
                   is(a_matcher_giving_a_mismatch_description_of(doNothing(), equalTo("did not throw an exception"))));

        assertThat(throwsException(anExceptionOfType(Exception.class)),
                   is(a_matcher_giving_a_mismatch_description_of(doNothing(), equalTo("did not throw an exception"))));
    }

    @Test public void
    does_not_match_an_action_result_that_throws_a_different_exception() {
        assertThat(throwsException(new RuntimeException()),
                   is(a_matcher_giving_a_mismatch_description_of(throwException(new Exception()), equalTo("threw an exception that was the exception <java.lang.Exception>"))));

        assertThat(throwsException(anExceptionOfType(RuntimeException.class)),
                   is(a_matcher_giving_a_mismatch_description_of(throwException(new Exception()), equalTo("threw an exception that was a <java.lang.Exception>"))));
    }

    @Test public void
    matches_an_action_result_that_throws_the_same_exception() {
        Exception exception = new Exception();
        assertThat(throwsException(exception), is(a_matcher_that_matches(throwException(exception))));

        assertThat(throwsException(anExceptionOfType(Exception.class)), is(a_matcher_that_matches(throwException(exception))));
    }

    private static ActionResult doNothing() {
        return running(new Action() {
            @Override
            public void execute() {
                // do nothing
            }
        });
    }

    private static ActionResult throwException(final Exception exception) {
        return running(new Action() {
            @Override
            public void execute() throws Exception {
                throw exception;
            }
        });
    }
}
