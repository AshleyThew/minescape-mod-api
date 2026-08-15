package com.minescape.mod.api.channel.general.action;

import java.util.Objects;

/**
 * Data class representing a change in what the player is doing.
 * <p>
 * An action is any timed activity the player performs — mining a rock, cooking
 * a fish, climbing an obstacle — and one message is sent each time an action
 * starts, is cancelled or runs to completion. Only the player performing the
 * action receives these.
 * <p>
 * Actions replace one another rather than stacking: starting a new action while
 * one is already running sends {@link #STATE_CANCELLED} for the old action
 * before {@link #STATE_STARTED} for the new one. An action that completes and
 * immediately repeats — cooking a second fish from the same stack — sends
 * {@link #STATE_FINISHED} followed by a fresh {@link #STATE_STARTED}.
 */
public class GameplayActionData {

    /** The player began the action. */
    public static final String STATE_STARTED = "STARTED";
    /** The action ended before completing. */
    public static final String STATE_CANCELLED = "CANCELLED";
    /** The action ran to completion. */
    public static final String STATE_FINISHED = "FINISHED";

    private final String action;
    private final String state;
    private final long durationMillis;

    /**
     * Creates a new GameplayActionData instance with no duration.
     *
     * @param action the name of the action
     * @param state  the state the action moved into
     */
    public GameplayActionData(String action, String state) {
        this(action, state, 0L);
    }

    /**
     * Creates a new GameplayActionData instance.
     *
     * @param action         the name of the action
     * @param state          the state the action moved into
     * @param durationMillis the milliseconds the action is expected to take, or
     *                       0 when it is not timed or has already ended
     */
    public GameplayActionData(String action, String state, long durationMillis) {
        this.action = action;
        this.state = state;
        this.durationMillis = durationMillis;
    }

    /**
     * Gets the name of the action, such as {@code MINING}, {@code COOKING} or
     * {@code AGILITY_OBSTACLE}.
     * <p>
     * This is a string rather than an enum on purpose: the server can add an
     * action without requiring a new API release. Treat any value you do not
     * recognise as valid. Actions the server has not named yet report
     * {@code UNKNOWN}.
     *
     * @return the action name
     * @see <a href=
     *      "https://github.com/AshleyThew/minescape-mod-api/blob/main/docs/action-values.md">Action
     *      values</a>
     */
    public String action() {
        return action;
    }

    /**
     * Gets the state the action moved into: {@link #STATE_STARTED},
     * {@link #STATE_CANCELLED} or {@link #STATE_FINISHED}.
     * <p>
     * A string for the same reason {@link #action()} is — expect states you do
     * not recognise from newer servers and ignore them rather than throwing.
     *
     * @return the state name
     */
    public String state() {
        return state;
    }

    /**
     * Gets how long the action is expected to take, measured from when the
     * message was sent.
     * <p>
     * Only meaningful on {@link #STATE_STARTED}, and 0 there for an action with
     * no fixed length. Cancelled and finished actions always report 0.
     *
     * @return the milliseconds the action is expected to take, or 0
     */
    public long durationMillis() {
        return durationMillis;
    }

    /**
     * Checks whether the player began the action.
     *
     * @return true if the state is {@link #STATE_STARTED}, false otherwise
     */
    public boolean isStarted() {
        return STATE_STARTED.equals(state);
    }

    /**
     * Checks whether the action ended before completing.
     *
     * @return true if the state is {@link #STATE_CANCELLED}, false otherwise
     */
    public boolean isCancelled() {
        return STATE_CANCELLED.equals(state);
    }

    /**
     * Checks whether the action ran to completion.
     *
     * @return true if the state is {@link #STATE_FINISHED}, false otherwise
     */
    public boolean isFinished() {
        return STATE_FINISHED.equals(state);
    }

    /**
     * Checks whether the action stopped, whether it completed or not.
     *
     * @return true if the state is {@link #STATE_CANCELLED} or
     *         {@link #STATE_FINISHED}, false otherwise
     */
    public boolean hasEnded() {
        return isCancelled() || isFinished();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameplayActionData that = (GameplayActionData) obj;
        return durationMillis == that.durationMillis
                && Objects.equals(action, that.action)
                && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, state, durationMillis);
    }

    @Override
    public String toString() {
        return "GameplayActionData{" + "action=" + action + ", state=" + state
                + ", durationMillis=" + durationMillis + '}';
    }
}
