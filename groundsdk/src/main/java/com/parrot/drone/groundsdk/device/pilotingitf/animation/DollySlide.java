/*
 *     Copyright (C) 2019 Parrot Drones SAS
 *
 *     Redistribution and use in source and binary forms, with or without
 *     modification, are permitted provided that the following conditions
 *     are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the name of the Parrot Company nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *     "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *     LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *     FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *     PARROT COMPANY BE LIABLE FOR ANY DIRECT, INDIRECT,
 *     INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *     BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 *     OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 *     AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *     OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *     OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *     SUCH DAMAGE.
 *
 */

package com.parrot.drone.groundsdk.device.pilotingitf.animation;

import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Dolly slide animation interface.
 * <p>
 * This animation instructs the drone to fly horizontally with respect to a specific angle between its original
 * position (before the animation begins), the target, and its destination. <br/>
 * The target in question depends on the currently active
 * {@link com.parrot.drone.groundsdk.device.pilotingitf.PilotingItf piloting interface}.
 */
public interface DollySlide extends Animation {

    /**
     * Dolly slide animation configuration class.
     * <p>
     * Allows to configure the following parameters for this animation:
     * <ul>
     * <li><strong>speed:</strong> animation execution speed, in meters per second. If {@link #withSpeed speed} is
     * not customized, then the drone will apply its own default value for this parameter. </li>
     * <li><strong>angle:</strong> angle between the drone/target axis and the target/destination axis, in degrees.
     * The drone will slide horizontally according to that angle. If {@link #withAngle angle} is not customized,
     * then the drone will apply its own default value for this parameter. </li>
     * <li><strong>horizontal distance:</strong> distance the drone will slide horizontally, in meters. If
     * {@link #withHorizontalDistance horizontal distance} is not customized, then the drone will apply its own
     * default value for this parameter. </li>
     * <li><strong>mode:</strong> animation execution {@link Mode mode}. If {@link #withMode mode} is not
     * customized, then the drone will apply its own default value for this parameter: {@link Mode#ONCE}.</li>
     * </ul>
     */
    final class Config extends Animation.Config {

        /**
         * Constructor.
         */
        public Config() {
            super(Type.DOLLY_SLIDE);
        }

        /** {@code true} when {@link #withSpeed} has been called once. */
        private boolean mCustomSpeed;

        /** Configured custom animation speed, in meters per second. */
        private double mSpeed;

        /**
         * Configures a custom animation speed.
         *
         * @param speed custom animation speed, in meters per second
         *
         * @return this, to allow call chaining
         */
        @NonNull
        public Config withSpeed(@FloatRange(from = 0) double speed) {
            mCustomSpeed = true;
            mSpeed = speed;
            return this;
        }

        /**
         * Tells whether animation speed parameter has been customized in this configuration.
         *
         * @return {@code true} if animation speed parameter has been customized, otherwise {@code false}
         */
        public boolean usesCustomSpeed() {
            return mCustomSpeed;
        }

        /**
         * Retrieves customized animation speed.
         * <p>
         * Value is meaningless if {@link #withSpeed} has not been called previously.
         *
         * @return customized animation speed, in meters per second
         */
        @FloatRange(from = 0)
        public double getSpeed() {
            return mSpeed;
        }

        /** {@code true} when {@link #withAngle} has been called once. */
        private boolean mCustomAngle;

        /** Configured custom animation angle, in degrees. */
        private double mAngle;

        /**
         * Configures a custom animation angle.
         *
         * @param angle custom animation angle, in degrees
         *
         * @return this, to allow call chaining
         */
        @NonNull
        public Config withAngle(double angle) {
            mCustomAngle = true;
            mAngle = angle;
            return this;
        }

        /**
         * Tells whether animation angle parameter has been customized in this configuration.
         *
         * @return {@code true} if animation angle parameter has been customized, otherwise {@code false}
         */
        public boolean usesCustomAngle() {
            return mCustomAngle;
        }

        /**
         * Retrieves customized animation angle.
         * <p>
         * Value is meaningless if {@link #withAngle(double)} has not been called previously.
         *
         * @return customized animation angle, in degrees
         */
        public double getAngle() {
            return mAngle;
        }

        /** {@code true} when {@link #withHorizontalDistance} has been called once. */
        private boolean mCustomHorizontalDistance;

        /** Configured custom animation horizontal distance, in meters. */
        private double mHorizontalDistance;

        /**
         * Configures a custom animation horizontal distance.
         *
         * @param distance custom horizontal distance, in meters
         *
         * @return this, to allow call chaining
         */
        @NonNull
        public Config withHorizontalDistance(@FloatRange(from = 0) double distance) {
            mCustomHorizontalDistance = true;
            mHorizontalDistance = distance;
            return this;
        }

        /**
         * Tells whether animation horizontal distance parameter has been customized in this configuration.
         *
         * @return {@code true} if animation horizontal distance parameter has been customized, otherwise {@code false}
         */
        public boolean usesCustomHorizontalDistance() {
            return mCustomHorizontalDistance;
        }

        /**
         * Retrieves customized animation horizontal distance.
         * <p>
         * Value is meaningless if {@link #withHorizontalDistance} has not been called previously.
         *
         * @return customized animation horizontal distance, in meters
         */
        @FloatRange(from = 0)
        public double getHorizontalDistance() {
            return mHorizontalDistance;
        }

        /** Configured custom animation execution mode. {@code null} if not customized. */
        @Nullable
        private Mode mMode;

        /**
         * Configures a custom animation execution mode.
         *
         * @param mode custom execution mode
         *
         * @return this, to allow call chaining
         */
        @NonNull
        public Config withMode(@NonNull Mode mode) {
            mMode = mode;
            return this;
        }

        /**
         * Retrieves customized animation execution mode.
         * <p>
         * Value is {@code null} if {@link #withMode} has not been called previously.
         *
         * @return customized animation execution mode
         */
        @Nullable
        public Mode getMode() {
            return mMode;
        }
    }

    /**
     * Retrieves currently applied animation speed.
     *
     * @return current animation speed, in meters per second
     */
    @FloatRange(from = 0)
    double getSpeed();

    /**
     * Retrieves currently applied animation angle.
     *
     * @return current animation angle, in degrees
     */
    double getAngle();

    /**
     * Retrieves currently applied animation horizontal distance.
     *
     * @return current animation horizontal distance, in meters
     */
    @FloatRange(from = 0)
    double getHorizontalDistance();

    /**
     * Retrieves currently applied animation execution mode.
     *
     * @return current animation execution mode
     */
    @NonNull
    Mode getMode();
}
