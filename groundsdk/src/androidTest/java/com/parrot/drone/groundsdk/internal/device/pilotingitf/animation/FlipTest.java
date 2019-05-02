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

package com.parrot.drone.groundsdk.internal.device.pilotingitf.animation;

import com.parrot.drone.groundsdk.device.pilotingitf.animation.Animation;
import com.parrot.drone.groundsdk.device.pilotingitf.animation.Flip;

import org.junit.Test;

import java.util.EnumSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FlipTest {

    @Test
    public void testFlipCoreType() {
        assertThat(new FlipCore(Flip.Direction.BACK).getType(), is(Animation.Type.FLIP));
    }

    @Test
    public void testFlipConfigType() {
        assertThat(new Flip.Config(Flip.Direction.LEFT).getAnimationType(), is(Animation.Type.FLIP));
    }

    @Test
    public void testFlipCoreDirection() {
        for (Flip.Direction direction : Flip.Direction.values()) {
            FlipCore flipCore = new FlipCore(direction);
            assertThat(flipCore.getDirection(), is(direction));
        }
    }

    @Test
    public void testFlipConfigDirection() {
        for (Flip.Direction direction : Flip.Direction.values()) {
            Flip.Config config = new Flip.Config(direction);
            assertThat(config.getDirection(), is(direction));
        }
    }

    @Test
    public void testMatchesConfig() {
        for (Flip.Direction direction : Flip.Direction.values()) {
            EnumSet<Flip.Direction> matchingDirection = EnumSet.of(direction);
            FlipCore flipCore = new FlipCore(direction);
            assertThat(flipCore.matchesConfig(new Flip.Config(direction)), is(true));
            for (Flip.Direction nonMatchingDirection : EnumSet.complementOf(matchingDirection)) {
                assertThat(flipCore.matchesConfig(new Flip.Config(nonMatchingDirection)), is(false));
            }
        }
    }
}
