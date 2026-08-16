package com.minescape.mod.api.channel.general.action;

import java.util.Objects;
import java.util.UUID;

/**
 * Data class representing a change in what another visible player is doing.
 * <p>
 * This is the onlooker's view of {@link GameplayActionData}: while that message
 * tells a player about their own actions, this one is broadcast to every player
 * that can see the acting player, so nearby clients can follow along too. The
 * acting player does not receive it — they get {@link GameplayActionData}
 * instead.
 * <p>
 * The action and state values are the same ones {@link GameplayActionData}
 * carries, and follow the same replacement rules: a new action for the same
 * player sends {@link GameplayActionData#STATE_CANCELLED} for the old action
 * before {@link GameplayActionData#STATE_STARTED} for the new one. Expect a
 * dangling action with no terminal state when the acting player moves out of
 * view or logs out mid-action.
 */
public class PlayerActionData {

    private final UUID uuid;
    private final String action;
    private final String state;
    private final long durationMillis;

    /**
     * Creates a new PlayerActionData instance with no duration.
     *
     * @param uuid   the UUID of the acting player
     * @param action the name of the action
     * @param state  the state the action moved into
     */
    public PlayerActionData(UUID uuid, String action, String state) {
        this(uuid, action, state, 0L);
    }

    /**
     * Creates a new PlayerActionData instance.
     *
     * @param uuid           the UUID of the acting player
     * @param action         the name of the action
     * @param state          the state the action moved into
     * @param durationMillis the milliseconds the action is expected to take, or
     *                       0 when it is not timed or has already ended
     */
    public PlayerActionData(UUID uuid, String action, String state, long durationMillis) {
        this.uuid = uuid;
        this.action = action;
        this.state = state;
        this.durationMillis = durationMillis;
    }

    /**
     * Gets the UUID of the player performing the action.
     *
     * @return the UUID of the acting player
     */
    public UUID uuid() {
        return uuid;
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
     * Gets the state the action moved into:
     * {@link GameplayActionData#STATE_STARTED},
     * {@link GameplayActionData#STATE_CANCELLED} or
     * {@link GameplayActionData#STATE_FINISHED}.
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
     * Only meaningful on {@link GameplayActionData#STATE_STARTED}, and 0 there
     * for an action with no fixed length. Cancelled and finished actions always
     * report 0.
     *
     * @return the milliseconds the action is expected to take, or 0
     */
    public long durationMillis() {
        return durationMillis;
    }

    /**
     * Checks whether the acting player began the action.
     *
     * @return true if the state is {@link GameplayActionData#STATE_STARTED},
     *         false otherwise
     */
    public boolean isStarted() {
        return GameplayActionData.STATE_STARTED.equals(state);
    }

    /**
     * Checks whether the action ended before completing.
     *
     * @return true if the state is {@link GameplayActionData#STATE_CANCELLED},
     *         false otherwise
     */
    public boolean isCancelled() {
        return GameplayActionData.STATE_CANCELLED.equals(state);
    }

    /**
     * Checks whether the action ran to completion.
     *
     * @return true if the state is {@link GameplayActionData#STATE_FINISHED},
     *         false otherwise
     */
    public boolean isFinished() {
        return GameplayActionData.STATE_FINISHED.equals(state);
    }

    /**
     * Checks whether the action stopped, whether it completed or not.
     *
     * @return true if the state is {@link GameplayActionData#STATE_CANCELLED}
     *         or {@link GameplayActionData#STATE_FINISHED}, false otherwise
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
        PlayerActionData that = (PlayerActionData) obj;
        return durationMillis == that.durationMillis
                && Objects.equals(uuid, that.uuid)
                && Objects.equals(action, that.action)
                && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, action, state, durationMillis);
    }

    @Override
    public String toString() {
        return "PlayerActionData{" + "uuid=" + uuid + ", action=" + action
                + ", state=" + state + ", durationMillis=" + durationMillis + '}';
    }
}
