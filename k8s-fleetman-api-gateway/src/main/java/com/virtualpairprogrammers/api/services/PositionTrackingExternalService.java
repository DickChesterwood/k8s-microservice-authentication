package com.virtualpairprogrammers.api.services;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.virtualpairprogrammers.api.domain.VehiclePosition;

@Service 
public class PositionTrackingExternalService 
{
	// @Autowired
	// TODO we need to reimplement this
	private RemotePositionMicroserviceCalls remoteService;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public Collection<VehiclePosition> getAllUpdatedPositionsSince(Date since)
	{
		String date = formatter.format(since);
		Collection<VehiclePosition> results = remoteService.getAllLatestPositionsSince(date);
		return results;
	}
	
	public Collection<VehiclePosition> getHistoryFor(String vehicleName) {
		return remoteService.getHistoryFor(vehicleName);
	}
	
	public Collection<VehiclePosition> getHistoryForDown(String vehicleName) {
		return new HashSet<>();
	}
	
}
