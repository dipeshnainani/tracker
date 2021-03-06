package com.dipeshimpl.service;

import com.dipeshimpl.entity.Alert;
import com.dipeshimpl.entity.Reading;
import com.dipeshimpl.entity.Vehicle;
import com.dipeshimpl.repository.AlertRepository;
import com.dipeshimpl.repository.ReadRepository;
import com.dipeshimpl.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The class is an implementation of read service interface.
 * It helps in creating the readings and also helps in defining
 * the alerts.
 *
 * @author Dipesh Nainani
 */

@Service
public class ReadServiceImpl implements ReadService{

    @Autowired
    private ReadRepository readRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AlertRepository alertRepository;

    /**
     * The method creates the readings and checks the conditions
     * for alerts.
     *
     * @param readings details of the reading entered by the user
     * @return reading
     */

    @Transactional
    public Reading create(Reading readings) {

        Vehicle vehicle = vehicleRepository.findOne(readings.getVin());
        if(vehicle != null) {
            readRepository.create(readings);

            if (readings.getEngineRpm() > vehicle.getRedlineRpm()) {
                Alert alert = new Alert();
                alert.setType("HIGH");
                alert.setVehicle(vehicle);
                alertRepository.create(alert);
            }

            if(readings.getFuelVolume() < (0.10 * vehicle.getMaxFuelVolume())) {
                Alert alert = new Alert();
                alert.setType("MEDIUM");
                alert.setVehicle(vehicle);
                alertRepository.create(alert);
            }

            if((readings.getTires().getFrontLeft() < 32 || readings.getTires().getFrontLeft() > 36) ||
                    (readings.getTires().getFrontRight() < 32 || readings.getTires().getFrontRight() > 36) ||
                    (readings.getTires().getRearLeft() < 32 || readings.getTires().getRearLeft() > 36) ||
                    (readings.getTires().getRearRight() < 32 || readings.getTires().getRearRight() > 36)) {

                Alert alert = new Alert();
                alert.setType("LOW");
                alert.setVehicle(vehicle);
                alertRepository.create(alert);
            }

            if(readings.isEngineCoolantLow() || readings.isCheckEngineLightOn()) {
                Alert alert = new Alert();
                alert.setType("LOW");
                alert.setVehicle(vehicle);
                alertRepository.create(alert);
            }

            return readings;
        } else {
            return null;
        }
    }

    /**
     * This method helps in getting the reading of the car
     *
     * @param vin vin number of the car
     * @return reading of the car
     */
    @Transactional(readOnly = true)
    public List<Reading> findReadings(String vin) {
        if(vin!=null || vin!="") {
            return readRepository.findAll(vin);
        }
        return null;
    }

    /**
     * Helps in returning list of alerts with type
     * @param type type of alert
     * @return list of alerts
     */
    @Transactional(readOnly = true)
    public List<Alert> findAll(String type) {
        if(type!=null) {
            alertRepository.findAlert(type);
        }
        return null;
    }
}
