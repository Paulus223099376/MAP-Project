package com.valentinesgarage.app

import com.valentinesgarage.app.domain.model.VehicleCondition
import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleConditionTest {
    @Test fun `unknown name falls back to GOOD`() {
        assertEquals(VehicleCondition.GOOD, VehicleCondition.fromName("does_not_exist"))
        assertEquals(VehicleCondition.GOOD, VehicleCondition.fromName(null))
    }

    @Test fun `valid names round trip`() {
        VehicleCondition.entries.forEach {
            assertEquals(it, VehicleCondition.fromName(it.name))
        }
    }
}
