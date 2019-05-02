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

package com.parrot.drone.groundsdk.internal.device.pilotingitf;

import android.support.annotation.NonNull;

import com.parrot.drone.groundsdk.device.pilotingitf.LookAtPilotingItf;
import com.parrot.drone.groundsdk.device.pilotingitf.PilotingItf;
import com.parrot.drone.groundsdk.internal.component.ComponentDescriptor;
import com.parrot.drone.groundsdk.internal.component.ComponentStore;

/**
 * Core class for LookAtPilotingItf.
 */
public final class LookAtPilotingItfCore extends TrackingPilotingItfCore implements LookAtPilotingItf {

    /** Description of LookAtPilotingItf. */
    private static final ComponentDescriptor<PilotingItf, LookAtPilotingItf> DESC =
            ComponentDescriptor.of(LookAtPilotingItf.class);

    /**
     * Constructor.
     *
     * @param pilotingItfStore store where this piloting interface belongs.
     * @param backend          backend used to forward actions to the engine
     */
    public LookAtPilotingItfCore(@NonNull ComponentStore<PilotingItf> pilotingItfStore, @NonNull Backend backend) {
        super(DESC, pilotingItfStore, backend);
    }
}
