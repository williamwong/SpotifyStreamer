package org.williamwong.spotifystreamer.utilities;

/**
 * A helper class to work with timer representations
 * Created by williamwong on 11/29/15.
 */
public class TimerUtilities {

    /**
     * Converts milliseconds into string representation of time
     *
     * @param milliseconds Number of milliseconds to convert to timer string
     * @return String representation of time in the format of hours:minutes:seconds
     */
    public static String milliSecondsToTimeString(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Gets progress percentage of track played
     *
     * @param currentPosition Current point in time of playing track in milliseconds
     * @param totalDuration   Total length of track in milliseconds
     * @return Percentage of track played as integer
     */
    public static int millisecondsToPercentage(long currentPosition, long totalDuration) {
        Double percentage = (((double) currentPosition / totalDuration) * 100);
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      Percentage progress of currently playing track
     * @param totalDuration Total duration of current track in milliseconds
     * @return Current duration in milliseconds
     */
    public static int percentageToMilliseconds(int progress, long totalDuration) {
        return (int) ((((double) progress) / 100) * totalDuration);
    }
}
