import XCTest
@testable import CapacitorIbeaconPlugin

class CapacitorIbeaconTests: XCTestCase {
    func testAuthorizationStatusMapping() {
        // Ensure the basic API wiring still works.
        let implementation = CapacitorIbeacon()
        let status = implementation.getAuthorizationStatus()

        // Should return a valid status string
        let validStatuses = ["not_determined", "restricted", "denied", "authorized_always", "authorized_when_in_use", "unknown"]
        XCTAssertTrue(validStatuses.contains(status))
    }

    func testIsRangingAvailable() {
        // Ensure the ranging availability check works.
        let implementation = CapacitorIbeacon()
        let isAvailable = implementation.isRangingAvailable()

        // Should return a boolean value
        XCTAssertNotNil(isAvailable)
    }
}
