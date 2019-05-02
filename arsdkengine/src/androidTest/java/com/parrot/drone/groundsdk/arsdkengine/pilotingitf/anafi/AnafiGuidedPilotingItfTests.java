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

package com.parrot.drone.groundsdk.arsdkengine.pilotingitf.anafi;

import com.parrot.drone.groundsdk.arsdkengine.ArsdkEngineTestBase;
import com.parrot.drone.groundsdk.device.DeviceState;
import com.parrot.drone.groundsdk.device.Drone;
import com.parrot.drone.groundsdk.device.pilotingitf.Activable;
import com.parrot.drone.groundsdk.device.pilotingitf.GuidedPilotingItf;
import com.parrot.drone.groundsdk.device.pilotingitf.GuidedPilotingItf.FinishedLocationFlightInfo;
import com.parrot.drone.groundsdk.device.pilotingitf.GuidedPilotingItf.FinishedRelativeMoveFlightInfo;
import com.parrot.drone.groundsdk.device.pilotingitf.GuidedPilotingItf.LocationDirective;
import com.parrot.drone.groundsdk.device.pilotingitf.GuidedPilotingItf.LocationDirective.Orientation;
import com.parrot.drone.groundsdk.device.pilotingitf.GuidedPilotingItf.RelativeMoveDirective;
import com.parrot.drone.groundsdk.internal.device.DroneCore;
import com.parrot.drone.sdkcore.arsdk.ArsdkEncoder;
import com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingMovetoOrientationMode;
import com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingstateMovetochangedOrientationMode;
import com.parrot.drone.sdkcore.arsdk.Backend;
import com.parrot.drone.sdkcore.arsdk.Expectation;
import com.parrot.drone.sdkcore.arsdk.ExpectedCmd;

import org.junit.Test;

import static com.parrot.drone.groundsdk.FinishedRelativeMoveFlightInfoMatcher.matchesFinishedRelativeMoveFlightInfo;
import static com.parrot.drone.groundsdk.LocationDirectiveMatcher.matchesLocationDirective;
import static com.parrot.drone.groundsdk.RelativeMoveDirectiveMatcher.matchesRelativeMoveDirective;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingeventMovebyendError.INTERRUPTED;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingeventMovebyendError.OK;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingstateFlyingstatechangedState.FLYING;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingstateFlyingstatechangedState.LANDED;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingstateMovetochangedStatus.CANCELED;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingstateMovetochangedStatus.DONE;
import static com.parrot.drone.sdkcore.arsdk.ArsdkFeatureArdrone3.PilotingstateMovetochangedStatus.RUNNING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class AnafiGuidedPilotingItfTests extends ArsdkEngineTestBase {

    private DroneCore mDrone;

    private GuidedPilotingItf mPilotingItf;

    private int mChangeCnt;

    @Override
    public void setUp() {
        super.setUp();
        mArsdkEngine.start();
        mMockArsdkCore.addDevice("123", Drone.Model.ANAFI_4K.id(), "Drone1", 1, Backend.TYPE_NET);
        mDrone = mDroneStore.get("123");
        assert mDrone != null;

        mPilotingItf = mDrone.getPilotingItfStore().get(mMockSession, GuidedPilotingItf.class);
        mDrone.getPilotingItfStore().registerObserver(GuidedPilotingItf.class, () -> {
            mPilotingItf = mDrone.getPilotingItfStore().get(mMockSession, GuidedPilotingItf.class);
            mChangeCnt++;
        });

        mChangeCnt = 0;
    }

    @Override
    public void teardown() {
        if (mDrone.getDeviceStateCore().getConnectionState() != DeviceState.ConnectionState.DISCONNECTED) {
            disconnectDrone(mDrone, 1);
        }
        mArsdkEngine.requestStop(null);
        super.teardown();
    }

    @Test
    public void testPublication() {
        // should be unavailable when the drone is not connected and not known
        assertThat(mPilotingItf, is(nullValue()));
        // connect the drone
        connectDrone(mDrone, 1);
        // interface should be published
        assertThat(mPilotingItf, is(notNullValue()));
        assertThat(mChangeCnt, is(1));
        // disconnect the drone
        disconnectDrone(mDrone, 1);
        // interface should be absent
        assertThat(mPilotingItf, is(nullValue()));
        assertThat(mChangeCnt, is(2));
        // forget the drone
        mDrone.forget();
        // interface should still be absent
        assertThat(mPilotingItf, is(nullValue()));
        assertThat(mChangeCnt, is(2));
    }

    @Test
    public void testLocationMove() {
        connectDrone(mDrone, 1);

        // should be inactive
        assertThat(mChangeCnt, is(1));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // starting a location move in unavailable state should not change anything
        mPilotingItf.moveToLocation(48.88, 2.33, 7, Orientation.NONE);
        assertThat(mChangeCnt, is(1));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // should be idle as the drone is flying now
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(FLYING));
        assertThat(mChangeCnt, is(2));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));

        // start location move, interface becomes immediately active
        mMockArsdkCore.expect(new Expectation.Command(1,
                ExpectedCmd.ardrone3PilotingMoveTo(48.8795, 2.3675, 5,
                        PilotingMovetoOrientationMode.HEADING_DURING, 90), true));
        mPilotingItf.moveToLocation(48.8795, 2.3675, 5, Orientation.headingDuring(90));
        assertThat(mChangeCnt, is(3));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should match the parameters sent
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(LocationDirective.class));
        assertThat((LocationDirective) mPilotingItf.getCurrentDirective(),
                matchesLocationDirective(48.8795, 2.3675, 5, Orientation.headingDuring(90)));

        // notify location move is running, changing directive values
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingStateMoveToChanged(48.0, 2.0, 10,
                        PilotingstateMovetochangedOrientationMode.HEADING_START, 95, RUNNING));
        assertThat(mChangeCnt, is(4));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should match the new parameters
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(LocationDirective.class));
        assertThat((LocationDirective) mPilotingItf.getCurrentDirective(),
                matchesLocationDirective(48.0, 2.0, 10, Orientation.headingStart(95)));

        // notify location move is done, interface becomes idle
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingStateMoveToChanged(48.0, 2.0, 10,
                        PilotingstateMovetochangedOrientationMode.HEADING_START, 95, DONE));
        assertThat(mChangeCnt, is(5));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));
        // the current directive should be null, and the latest flight info should be updated accordingly
        assertThat(mPilotingItf.getCurrentDirective(), nullValue());
        assertThat(mPilotingItf.getLatestFinishedFlightInfo(), instanceOf(FinishedLocationFlightInfo.class));
        FinishedLocationFlightInfo flightInfo = (FinishedLocationFlightInfo) mPilotingItf.getLatestFinishedFlightInfo();
        assert flightInfo != null;
        assertThat(flightInfo.getDirective(),
                matchesLocationDirective(48.0, 2.0, 10, Orientation.headingStart(95)));
        assertThat(flightInfo.wasSuccessful(), is(true));

        // interface becomes unavailable as the drone is landed
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(LANDED));
        assertThat(mChangeCnt, is(6));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // put back as available
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(FLYING));
        assertThat(mChangeCnt, is(7));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));

        // reconnect drone and say that it is still flying
        disconnectDrone(mDrone, 1);
        assertThat(mChangeCnt, is(8));
        connectDrone(mDrone, 1, () -> mMockArsdkCore.commandReceived(
                1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(FLYING)));

        assertThat(mChangeCnt, is(9));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));
    }

    @Test
    public void testLocationMoveInterrupted() {
        connectDrone(mDrone, 1);

        // should be inactive
        assertThat(mChangeCnt, is(1));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // should be idle as the drone is flying now
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(FLYING));
        assertThat(mChangeCnt, is(2));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));

        // start location move, interface becomes immediately active
        mMockArsdkCore.expect(new Expectation.Command(1,
                ExpectedCmd.ardrone3PilotingMoveTo(48.0, 2.0, 10,
                        PilotingMovetoOrientationMode.TO_TARGET, 0), true));
        mPilotingItf.moveToLocation(48.0, 2.0, 10, Orientation.TO_TARGET);
        assertThat(mChangeCnt, is(3));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should match the parameters sent
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(LocationDirective.class));
        assertThat((LocationDirective) mPilotingItf.getCurrentDirective(),
                matchesLocationDirective(48.0, 2.0, 10, Orientation.TO_TARGET));

        // notify location move is running, nothing should change
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingStateMoveToChanged(48.0, 2.0, 10,
                        PilotingstateMovetochangedOrientationMode.TO_TARGET, 0, RUNNING));
        assertThat(mChangeCnt, is(3));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(LocationDirective.class));
        assertThat((LocationDirective) mPilotingItf.getCurrentDirective(),
                matchesLocationDirective(48.0, 2.0, 10, Orientation.TO_TARGET));

        // stop the current move, nothing should change
        mMockArsdkCore.expect(new Expectation.Command(1, ExpectedCmd.ardrone3PilotingCancelMoveTo(), true));
        mPilotingItf.deactivate();
        assertThat(mChangeCnt, is(3));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should not change
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(LocationDirective.class));
        assertThat((LocationDirective) mPilotingItf.getCurrentDirective(),
                matchesLocationDirective(48.0, 2.0, 10, Orientation.TO_TARGET));

        // notify location move is cancelled, interface becomes idle
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingStateMoveToChanged(48.0, 2.0, 10,
                        PilotingstateMovetochangedOrientationMode.TO_TARGET, 0, CANCELED));
        assertThat(mChangeCnt, is(4));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));
        // the current directive should be null, and the latest flight info should be updated accordingly
        assertThat(mPilotingItf.getCurrentDirective(), nullValue());
        assertThat(mPilotingItf.getLatestFinishedFlightInfo(), instanceOf(FinishedLocationFlightInfo.class));
        FinishedLocationFlightInfo flightInfo = (FinishedLocationFlightInfo) mPilotingItf.getLatestFinishedFlightInfo();
        assert flightInfo != null;
        assertThat(flightInfo.getDirective(),
                matchesLocationDirective(48.0, 2.0, 10, Orientation.TO_TARGET));
        assertThat(flightInfo.wasSuccessful(), is(false));
    }

    @Test
    public void testRelativeMove() {
        connectDrone(mDrone, 1);

        // should be inactive
        assertThat(mChangeCnt, is(1));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // starting a relative move in unavailable state should not change anything
        mPilotingItf.moveToRelativePosition(1.0, 2.0, 3.0, 4.0);
        assertThat(mChangeCnt, is(1));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // should be idle as the drone is flying now
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(FLYING));
        assertThat(mChangeCnt, is(2));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));

        // start relative move, interface becomes immediately active
        mMockArsdkCore.expect(new Expectation.Command(1,
                ExpectedCmd.ardrone3PilotingMoveBy(10.0f, 2.5f, -5.0f, 0.7853982f), true));
        mPilotingItf.moveToRelativePosition(10.0, 2.5, -5.0, 45.0);
        assertThat(mChangeCnt, is(3));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should match the parameters sent
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(RelativeMoveDirective.class));
        assertThat((RelativeMoveDirective) mPilotingItf.getCurrentDirective(),
                matchesRelativeMoveDirective(10.0, 2.5, -5.0, 45.0));

        // notify relative move is done, interface becomes idle
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingEventMoveByEnd(10.01f, 2.48f, -5.03f, 0.7854023f, OK));
        assertThat(mChangeCnt, is(4));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));
        // the current directive should be null, and the latest flight info should be updated accordingly
        assertThat(mPilotingItf.getCurrentDirective(), nullValue());
        assertThat(mPilotingItf.getLatestFinishedFlightInfo(), instanceOf(FinishedRelativeMoveFlightInfo.class));
        FinishedRelativeMoveFlightInfo flightInfo =
                (FinishedRelativeMoveFlightInfo) mPilotingItf.getLatestFinishedFlightInfo();
        assert flightInfo != null;
        assertThat(flightInfo.getDirective(),
                matchesRelativeMoveDirective(10.0, 2.5, -5.0, 45.0));
        assertThat(flightInfo,
                matchesFinishedRelativeMoveFlightInfo(true, 10.01f, 2.48f, -5.03f, 0.7854023f));

        // interface becomes unavailable as the drone is landed
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(LANDED));
        assertThat(mChangeCnt, is(5));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));
    }

    @Test
    public void testRelativeMoveInterrupted() {
        connectDrone(mDrone, 1);

        // should be inactive
        assertThat(mChangeCnt, is(1));
        assertThat(mPilotingItf.getState(), is(Activable.State.UNAVAILABLE));

        // should be idle as the drone is flying now
        mMockArsdkCore.commandReceived(1, ArsdkEncoder.encodeArdrone3PilotingStateFlyingStateChanged(FLYING));
        assertThat(mChangeCnt, is(2));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));

        // start relative move, interface becomes immediately active
        mMockArsdkCore.expect(new Expectation.Command(1,
                ExpectedCmd.ardrone3PilotingMoveBy(10.0f, 2.5f, -5.0f, 0.7853982f), true));
        mPilotingItf.moveToRelativePosition(10.0, 2.5, -5.0, 45.0);
        assertThat(mChangeCnt, is(3));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should match the parameters sent
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(RelativeMoveDirective.class));
        assertThat((RelativeMoveDirective) mPilotingItf.getCurrentDirective(),
                matchesRelativeMoveDirective(10.0, 2.5, -5.0, 45.0));

        // start new relative move
        mMockArsdkCore.expect(new Expectation.Command(1,
                ExpectedCmd.ardrone3PilotingMoveBy(50.0f, -1.0f, 0.5f, 0.0f), true));
        mPilotingItf.moveToRelativePosition(50.0, -1.0, 0.5, 0.0);
        assertThat(mChangeCnt, is(4));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should match the parameters sent
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(RelativeMoveDirective.class));
        assertThat((RelativeMoveDirective) mPilotingItf.getCurrentDirective(),
                matchesRelativeMoveDirective(50.0, -1.0, 0.5, 0.0));

        // notify that the first relative move is interrupted
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingEventMoveByEnd(5.0f, 1.25f, -2.5f, 0.7853982f, INTERRUPTED));
        assertThat(mChangeCnt, is(5));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should not change
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(RelativeMoveDirective.class));
        assertThat((RelativeMoveDirective) mPilotingItf.getCurrentDirective(),
                matchesRelativeMoveDirective(50.0, -1.0, 0.5, 0.0));
        // the latest flight info should be updated accordingly
        assertThat(mPilotingItf.getLatestFinishedFlightInfo(), instanceOf(FinishedRelativeMoveFlightInfo.class));
        FinishedRelativeMoveFlightInfo flightInfo =
                (FinishedRelativeMoveFlightInfo) mPilotingItf.getLatestFinishedFlightInfo();
        assert flightInfo != null;
        assertThat(flightInfo.getDirective(),
                matchesRelativeMoveDirective(10.0, 2.5, -5.0, 45.0));
        assertThat(flightInfo,
                matchesFinishedRelativeMoveFlightInfo(false, 5.0f, 1.25f, -2.5f, 0.7853982f));

        // stop the current move, i.e. the second relative move
        mMockArsdkCore.expect(new Expectation.Command(1,
                ExpectedCmd.ardrone3PilotingMoveBy(0.0f, 0.0f, 0.0f, 0.0f), true));
        mPilotingItf.deactivate();
        assertThat(mChangeCnt, is(5));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should not change
        assertThat(mPilotingItf.getCurrentDirective(), instanceOf(RelativeMoveDirective.class));
        assertThat((RelativeMoveDirective) mPilotingItf.getCurrentDirective(),
                matchesRelativeMoveDirective(50.0, -1.0, 0.5, 0.0));

        // notify that the second relative move is interrupted
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingEventMoveByEnd(43.2f, -0.89f, 0.49f, -0.01f, INTERRUPTED));
        assertThat(mChangeCnt, is(6));
        assertThat(mPilotingItf.getState(), is(Activable.State.ACTIVE));
        // the current directive should be null
        assertThat(mPilotingItf.getCurrentDirective(), nullValue());
        // the latest flight info should be updated accordingly
        assertThat(mPilotingItf.getLatestFinishedFlightInfo(), instanceOf(FinishedRelativeMoveFlightInfo.class));
        flightInfo = (FinishedRelativeMoveFlightInfo) mPilotingItf.getLatestFinishedFlightInfo();
        assert flightInfo != null;
        assertThat(flightInfo.getDirective(),
                matchesRelativeMoveDirective(50.0, -1.0, 0.5, 0.0));
        assertThat(flightInfo,
                matchesFinishedRelativeMoveFlightInfo(false, 43.2f, -0.89f, 0.49f, -0.01f));

        // notify that the second relative move has actually stopped
        mMockArsdkCore.commandReceived(1,
                ArsdkEncoder.encodeArdrone3PilotingEventMoveByEnd(0.01f, -0.01f, 0.02f, 0.0f, OK));
        assertThat(mChangeCnt, is(7));
        assertThat(mPilotingItf.getState(), is(Activable.State.IDLE));
        // the current directive should still be null
        assertThat(mPilotingItf.getCurrentDirective(), nullValue());
        // the latest flight info should not change
        assertThat(mPilotingItf.getLatestFinishedFlightInfo(), instanceOf(FinishedRelativeMoveFlightInfo.class));
        flightInfo = (FinishedRelativeMoveFlightInfo) mPilotingItf.getLatestFinishedFlightInfo();
        assert flightInfo != null;
        assertThat(flightInfo.getDirective(),
                matchesRelativeMoveDirective(50.0, -1.0, 0.5, 0.0));
        assertThat(flightInfo,
                matchesFinishedRelativeMoveFlightInfo(false, 43.2f, -0.89f, 0.49f, -0.01f));
    }
}
