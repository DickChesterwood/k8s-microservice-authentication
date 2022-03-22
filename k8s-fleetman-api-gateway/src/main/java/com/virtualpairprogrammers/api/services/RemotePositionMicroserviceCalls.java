package com.virtualpairprogrammers.api.services;

import java.util.Collection;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.virtualpairprogrammers.api.domain.VehiclePosition;

// TODO need to refactor out the cloud stuff. No more websockets!
public interface RemotePositionMicroserviceCalls 
{
	@RequestMapping(method=RequestMethod.GET, value="/vehicles/")
	public Collection<VehiclePosition> getAllLatestPositionsSince(@RequestParam(value="since",required=false) String since);

	@RequestMapping(method=RequestMethod.GET, value="/history/{vehicleName}")
	public Collection<VehiclePosition> getHistoryFor(@PathVariable("vehicleName") String vehicleName);
}
