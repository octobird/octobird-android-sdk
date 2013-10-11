package com.octobird.advertising;

public interface OnAdEventHandler {
    /**
     * Raised when the AdControl receives a new ad and ready to transition
     * animation.
     *
     * @param sender The AdControl that received the event.
     */
    public void refresh(AdControl sender);

    /**
     * Raised when the AdControl encounters an error in operations.
     *
     * @param sender The AdControl that received the event.
     * @param error  Error text.
     */
    public void error(AdControl sender, String error);

    /**
     * When the Boolean isEngaged changes from true to false or back. IsEngaged
     * is true when the user is interacting with the ad.
     *
     * @param sender  The AdControl that received the event.
     * @param engaged Engaged is true when the user is interacting with the ad.
     */
    public void engagedChange(AdControl sender, boolean engaged);
}
