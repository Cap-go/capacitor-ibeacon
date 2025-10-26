import Foundation
import CoreLocation
import CoreBluetooth

public class CapacitorIbeacon: NSObject, CLLocationManagerDelegate, CBPeripheralManagerDelegate {
    private var locationManager: CLLocationManager!
    private var peripheralManager: CBPeripheralManager!
    private weak var plugin: CapacitorIbeaconPlugin?
    private var monitoredRegions: [String: CLBeaconRegion] = [:]
    private var rangedRegions: [String: CLBeaconRegion] = [:]

    public override init() {
        super.init()
        locationManager = CLLocationManager()
        locationManager.delegate = self
        peripheralManager = CBPeripheralManager(delegate: self, queue: nil)
    }

    public func setPlugin(_ plugin: CapacitorIbeaconPlugin) {
        self.plugin = plugin
    }

    public func startMonitoringForRegion(identifier: String, uuid: String, major: Int?, minor: Int?, completion: @escaping (Result<Void, Error>) -> Void) {
        guard let beaconUUID = UUID(uuidString: uuid) else {
            completion(.failure(NSError(domain: "CapacitorIbeacon", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid UUID"])))
            return
        }

        let beaconRegion: CLBeaconRegion
        if let major = major {
            if let minor = minor {
                beaconRegion = CLBeaconRegion(uuid: beaconUUID, major: CLBeaconMajorValue(major), minor: CLBeaconMinorValue(minor), identifier: identifier)
            } else {
                beaconRegion = CLBeaconRegion(uuid: beaconUUID, major: CLBeaconMajorValue(major), identifier: identifier)
            }
        } else {
            beaconRegion = CLBeaconRegion(uuid: beaconUUID, identifier: identifier)
        }

        beaconRegion.notifyEntryStateOnDisplay = true
        beaconRegion.notifyOnEntry = true
        beaconRegion.notifyOnExit = true

        monitoredRegions[identifier] = beaconRegion
        locationManager.startMonitoring(for: beaconRegion)
        completion(.success(()))
    }

    public func stopMonitoringForRegion(identifier: String, uuid: String) {
        if let region = monitoredRegions[identifier] {
            locationManager.stopMonitoring(for: region)
            monitoredRegions.removeValue(forKey: identifier)
        }
    }

    public func startRangingBeaconsInRegion(identifier: String, uuid: String, major: Int?, minor: Int?, completion: @escaping (Result<Void, Error>) -> Void) {
        guard let beaconUUID = UUID(uuidString: uuid) else {
            completion(.failure(NSError(domain: "CapacitorIbeacon", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid UUID"])))
            return
        }

        let beaconRegion: CLBeaconRegion
        if let major = major {
            if let minor = minor {
                beaconRegion = CLBeaconRegion(uuid: beaconUUID, major: CLBeaconMajorValue(major), minor: CLBeaconMinorValue(minor), identifier: identifier)
            } else {
                beaconRegion = CLBeaconRegion(uuid: beaconUUID, major: CLBeaconMajorValue(major), identifier: identifier)
            }
        } else {
            beaconRegion = CLBeaconRegion(uuid: beaconUUID, identifier: identifier)
        }

        rangedRegions[identifier] = beaconRegion
        locationManager.startRangingBeacons(in: beaconRegion)
        completion(.success(()))
    }

    public func stopRangingBeaconsInRegion(identifier: String, uuid: String) {
        if let region = rangedRegions[identifier] {
            locationManager.stopRangingBeacons(in: region)
            rangedRegions.removeValue(forKey: identifier)
        }
    }

    public func startAdvertising(uuid: String, major: Int, minor: Int, identifier: String, measuredPower: Int?, completion: @escaping (Result<Void, Error>) -> Void) {
        guard let beaconUUID = UUID(uuidString: uuid) else {
            completion(.failure(NSError(domain: "CapacitorIbeacon", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid UUID"])))
            return
        }

        let beaconRegion = CLBeaconRegion(uuid: beaconUUID, major: CLBeaconMajorValue(major), minor: CLBeaconMinorValue(minor), identifier: identifier)

        if let power = measuredPower {
            peripheralManager.startAdvertising(beaconRegion.peripheralData(withMeasuredPower: NSNumber(value: power)) as? [String: Any])
        } else {
            peripheralManager.startAdvertising(beaconRegion.peripheralData(withMeasuredPower: nil) as? [String: Any])
        }

        completion(.success(()))
    }

    public func stopAdvertising() {
        peripheralManager.stopAdvertising()
    }

    public func requestWhenInUseAuthorization() {
        locationManager.requestWhenInUseAuthorization()
    }

    public func requestAlwaysAuthorization() {
        locationManager.requestAlwaysAuthorization()
    }

    public func getAuthorizationStatus() -> String {
        let status = CLLocationManager.authorizationStatus()
        switch status {
        case .notDetermined:
            return "not_determined"
        case .restricted:
            return "restricted"
        case .denied:
            return "denied"
        case .authorizedAlways:
            return "authorized_always"
        case .authorizedWhenInUse:
            return "authorized_when_in_use"
        @unknown default:
            return "unknown"
        }
    }

    public func isBluetoothEnabled() -> Bool {
        return peripheralManager.state == .poweredOn
    }

    public func isRangingAvailable() -> Bool {
        return CLLocationManager.isRangingAvailable()
    }

    // MARK: - CLLocationManagerDelegate

    public func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        var beaconsArray: [[String: Any]] = []

        for beacon in beacons {
            var proximityString = "unknown"
            switch beacon.proximity {
            case .immediate:
                proximityString = "immediate"
            case .near:
                proximityString = "near"
            case .far:
                proximityString = "far"
            case .unknown:
                proximityString = "unknown"
            @unknown default:
                proximityString = "unknown"
            }

            beaconsArray.append([
                "uuid": beacon.uuid.uuidString,
                "major": beacon.major.intValue,
                "minor": beacon.minor.intValue,
                "rssi": beacon.rssi,
                "proximity": proximityString,
                "accuracy": beacon.accuracy
            ])
        }

        plugin?.notifyListeners("didRangeBeacons", data: [
            "region": [
                "identifier": region.identifier,
                "uuid": region.uuid.uuidString
            ],
            "beacons": beaconsArray
        ])
    }

    public func locationManager(_ manager: CLLocationManager, didEnterRegion region: CLRegion) {
        if let beaconRegion = region as? CLBeaconRegion {
            plugin?.notifyListeners("didEnterRegion", data: [
                "region": [
                    "identifier": beaconRegion.identifier,
                    "uuid": beaconRegion.uuid.uuidString
                ]
            ])

            plugin?.notifyListeners("didDetermineStateForRegion", data: [
                "region": [
                    "identifier": beaconRegion.identifier,
                    "uuid": beaconRegion.uuid.uuidString
                ],
                "state": "enter"
            ])
        }
    }

    public func locationManager(_ manager: CLLocationManager, didExitRegion region: CLRegion) {
        if let beaconRegion = region as? CLBeaconRegion {
            plugin?.notifyListeners("didExitRegion", data: [
                "region": [
                    "identifier": beaconRegion.identifier,
                    "uuid": beaconRegion.uuid.uuidString
                ]
            ])

            plugin?.notifyListeners("didDetermineStateForRegion", data: [
                "region": [
                    "identifier": beaconRegion.identifier,
                    "uuid": beaconRegion.uuid.uuidString
                ],
                "state": "exit"
            ])
        }
    }

    public func locationManager(_ manager: CLLocationManager, monitoringDidFailFor region: CLRegion?, withError error: Error) {
        if let beaconRegion = region as? CLBeaconRegion {
            plugin?.notifyListeners("monitoringDidFailForRegion", data: [
                "region": [
                    "identifier": beaconRegion.identifier,
                    "uuid": beaconRegion.uuid.uuidString
                ],
                "error": error.localizedDescription
            ])
        }
    }

    // MARK: - CBPeripheralManagerDelegate

    public func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        // Handle Bluetooth state changes if needed
    }
}
